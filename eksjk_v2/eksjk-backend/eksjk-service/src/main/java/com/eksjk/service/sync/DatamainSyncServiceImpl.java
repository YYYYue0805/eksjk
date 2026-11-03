package com.eksjk.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.EltmFormFieldMappingMapper;
import com.eksjk.mapper.EltmPatientRawMapper;
import com.eksjk.mapper.EltmSyncErrorMapper;
import com.eksjk.mapper.EltmSyncLogMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.EltmFormFieldMapping;
import com.eksjk.model.entity.EltmPatientRaw;
import com.eksjk.model.entity.EltmSyncError;
import com.eksjk.model.entity.EltmSyncLog;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.eltm.EltmProjectionService;
import com.eksjk.service.eltm.EltmUnprocessedService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * datamain 数据同步实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatamainSyncServiceImpl implements DatamainSyncService {

    private final DatamainApiClient apiClient;
    private final DatamainSyncTxService txService;
    private final DatamainSyncLockService lockService;
    private final PatientMapper patientMapper;
    private final EltmPatientRawMapper rawMapper;
    private final EltmSyncLogMapper syncLogMapper;
    private final EltmSyncErrorMapper syncErrorMapper;
    private final EltmProjectionService projectionService;
    private final EltmFormFieldMappingMapper mappingMapper;
    private final EltmUnprocessedService unprocessedService;

    @Value("${eksjk.datamain.sync-enabled:false}")
    private boolean syncEnabled;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "datamain-sync");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public Map<String, Object> syncFromExternal(String triggerType) {
        return syncFromExternal(triggerType, false, 0);
    }

    @Override
    public Map<String, Object> syncFromExternal(String triggerType, boolean force, int limit) {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("同步进行中，请稍后再试");
        }
        if (!lockService.acquire()) {
            running.set(false);
            throw new IllegalStateException("同步进行中（未获取到数据库锁）");
        }

        EltmSyncLog syncLog = new EltmSyncLog();
        syncLog.setSyncType("FULL");
        syncLog.setTriggerType(triggerType);
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        syncLog.setTotalCount(0);
        syncLog.setNewCount(0);
        syncLog.setUpdatedCount(0);
        syncLog.setSkippedCount(0);
        syncLog.setFailedCount(0);
        syncLogMapper.insert(syncLog);

        AtomicInteger total = new AtomicInteger();
        AtomicInteger newCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        AtomicInteger skippedCount = new AtomicInteger();
        AtomicInteger failedCount = new AtomicInteger();
        long startedAt = System.currentTimeMillis();

        try {
            Map<Long, EltmPatientRaw> rawByExternalId = rawMapper.selectList(
                            new LambdaQueryWrapper<EltmPatientRaw>()
                                    .select(EltmPatientRaw::getExternalId, EltmPatientRaw::getRowHash)
                                    .isNotNull(EltmPatientRaw::getExternalId))
                    .stream()
                    .collect(Collectors.toMap(EltmPatientRaw::getExternalId, Function.identity(), (a, b) -> a));
            Map<Long, Patient> patientByExternalId = patientMapper.selectList(
                            new LambdaQueryWrapper<Patient>()
                                    .select(Patient::getId, Patient::getExternalId)
                                    .isNotNull(Patient::getExternalId))
                    .stream()
                    .collect(Collectors.toMap(Patient::getExternalId, Function.identity(), (a, b) -> a));
            Map<String, Set<String>> mappingIndex = loadMappingIndex();

            DatamainApiClient.FetchResult result = apiClient.streamCxList(row -> {
                if (limit > 0 && total.get() >= limit) {
                    return;
                }
                total.incrementAndGet();
                try {
                    String rowHash = DatamainParseUtil.sha256(row.rawJson());
                    EltmPatientRaw raw = rawByExternalId.get(row.id());
                    Patient patient = patientByExternalId.get(row.id());
                    if (!force && raw != null && patient != null && rowHash.equals(raw.getRowHash())) {
                        skippedCount.incrementAndGet();
                        return;
                    }
                    DatamainSyncTxService.SyncOutcome outcome =
                            txService.syncPatient(row, rowHash, mappingIndex);
                    try {
                        unprocessedService.replacePending(outcome.patientId(),
                                EltmUnprocessedCollector.API_CX_LIST, outcome.unprocessed());
                    } catch (Exception e) {
                        log.error("保存未处理数据失败 externalId={}", row.id(), e);
                    }
                    try {
                        projectionService.projectByExternalId(row.id());
                    } catch (Exception e) {
                        log.warn("ELTM 数据投影失败 externalId={}", row.id(), e);
                    }
                    if (outcome.isNew()) {
                        newCount.incrementAndGet();
                    } else {
                        updatedCount.incrementAndGet();
                    }
                    if (total.get() % 100 == 0) {
                        updateProgress(syncLog, total, newCount, updatedCount, skippedCount, failedCount);
                        log.info("datamain 同步进度：{} 行，新增 {}，更新 {}，跳过 {}，失败 {}",
                                total.get(), newCount.get(), updatedCount.get(),
                                skippedCount.get(), failedCount.get());
                    }
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    log.error("同步患者失败 externalId={}", row.id(), e);
                    saveError(syncLog.getId(), row.id(), e, row.rawJson());
                }
            });

            if (result.code() != 0) {
                throw new IllegalStateException("外部接口返回 code=" + result.code());
            }
            syncLog.setStatus(failedCount.get() > 0 ? "PARTIAL" : "SUCCESS");
        } catch (Exception e) {
            syncLog.setStatus("FAILED");
            syncLog.setErrorMessage(DatamainParseUtil.truncate(e.getMessage(), 2000));
            throw new RuntimeException("datamain 同步失败: " + e.getMessage(), e);
        } finally {
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setTotalCount(total.get());
            syncLog.setNewCount(newCount.get());
            syncLog.setUpdatedCount(updatedCount.get());
            syncLog.setSkippedCount(skippedCount.get());
            syncLog.setFailedCount(failedCount.get());
            syncLogMapper.updateById(syncLog);
            lockService.release();
            running.set(false);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("syncLogId", syncLog.getId());
        result.put("totalRecords", total.get());
        result.put("newCount", newCount.get());
        result.put("updatedCount", updatedCount.get());
        result.put("skippedCount", skippedCount.get());
        result.put("failedCount", failedCount.get());
        result.put("elapsedSeconds", (System.currentTimeMillis() - startedAt) / 1000);
        result.put("status", syncLog.getStatus());
        return result;
    }

    @Override
    public Map<String, Object> startAsync(String triggerType, boolean force, int limit) {
        if (running.get()) {
            throw new IllegalStateException("同步进行中，请稍后再试");
        }
        asyncExecutor.submit(() -> {
            try {
                syncFromExternal(triggerType, force, limit);
            } catch (Exception e) {
                log.error("datamain 异步同步失败", e);
            }
        });
        Map<String, Object> result = new HashMap<>();
        result.put("started", true);
        result.put("triggerType", triggerType);
        result.put("force", force);
        result.put("limit", limit);
        return result;
    }

    private Map<String, Set<String>> loadMappingIndex() {
        return mappingMapper.selectList(new LambdaQueryWrapper<EltmFormFieldMapping>()
                        .eq(EltmFormFieldMapping::getEnabled, 1))
                .stream()
                .filter(mapping -> mapping.getFormKey() != null && mapping.getFieldKey() != null)
                .collect(Collectors.groupingBy(EltmFormFieldMapping::getFormKey,
                        Collectors.mapping(EltmFormFieldMapping::getFieldKey, Collectors.toSet())));
    }

    private void updateProgress(EltmSyncLog syncLog, AtomicInteger total, AtomicInteger newCount,
                                AtomicInteger updatedCount, AtomicInteger skippedCount,
                                AtomicInteger failedCount) {
        syncLog.setTotalCount(total.get());
        syncLog.setNewCount(newCount.get());
        syncLog.setUpdatedCount(updatedCount.get());
        syncLog.setSkippedCount(skippedCount.get());
        syncLog.setFailedCount(failedCount.get());
        syncLogMapper.updateById(syncLog);
    }

    @PreDestroy
    public void shutdownExecutor() {
        asyncExecutor.shutdownNow();
    }

    @Override
    public Map<String, Object> getSyncStatus() {
        Long totalEltmCount = patientMapper.selectCount(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDisClass, "10000007")
                .eq(Patient::getDelFlg, "1"));
        Long syncedCount = patientMapper.selectCount(new LambdaQueryWrapper<Patient>()
                .isNotNull(Patient::getExternalId)
                .eq(Patient::getDelFlg, "1"));
        EltmSyncLog lastLog = syncLogMapper.selectOne(new LambdaQueryWrapper<EltmSyncLog>()
                .orderByDesc(EltmSyncLog::getStartTime).last("LIMIT 1"));

        Map<String, Object> status = new HashMap<>();
        status.put("syncedCount", syncedCount);
        status.put("totalEltmCount", totalEltmCount);
        status.put("lastSyncTime", lastLog != null ? lastLog.getStartTime() : null);
        status.put("lastSyncLog", lastLog);
        return status;
    }

    @Override
    public List<EltmSyncLog> listRecentLogs(int limit) {
        int size = Math.max(1, Math.min(limit, 100));
        return syncLogMapper.selectList(new LambdaQueryWrapper<EltmSyncLog>()
                .orderByDesc(EltmSyncLog::getStartTime)
                .last("LIMIT " + size));
    }

    /** 每日定时同步（由 eksjk.datamain.sync-enabled 控制） */
    @Scheduled(cron = "${eksjk.datamain.sync-cron:0 10 2 * * ?}")
    public void scheduledSync() {
        if (!syncEnabled) {
            log.info("datamain 定时同步未开启（eksjk.datamain.sync-enabled=false），跳过");
            return;
        }
        try {
            syncFromExternal("SCHEDULED");
        } catch (Exception e) {
            log.error("datamain 定时同步失败", e);
        }
    }

    private void saveError(Long syncLogId, long externalId, Exception e, String rawJson) {
        try {
            EltmSyncError error = new EltmSyncError();
            error.setSyncLogId(syncLogId);
            error.setExternalId(externalId);
            error.setStage("PATIENT");
            error.setErrorMessage(DatamainParseUtil.truncate(e.getMessage(), 2000));
            error.setRawJson(DatamainParseUtil.truncate(rawJson, 8000));
            error.setCreateTime(LocalDateTime.now());
            syncErrorMapper.insert(error);
        } catch (Exception ex) {
            log.error("记录同步失败明细时出错", ex);
        }
    }
}
