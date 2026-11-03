package com.eksjk.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.EltmPatientPicMapper;
import com.eksjk.mapper.EltmPatientProfileMapper;
import com.eksjk.mapper.EltmSyncErrorMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.EltmPatientPic;
import com.eksjk.model.entity.EltmPatientProfile;
import com.eksjk.model.entity.EltmSyncError;
import com.eksjk.model.entity.EltmUnprocessedData;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.eltm.EltmUnprocessedService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * gettpbyid 病历图片补拉
 *
 * <p>仅对 profile.pic_count > 0 的患者补拉，避免对全部 5354 名患者逐一请求。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatamainPicBackfillService {

    private static final long RATE_LIMIT_MILLIS = 200;

    private final DatamainApiClient apiClient;
    private final PatientMapper patientMapper;
    private final EltmPatientProfileMapper profileMapper;
    private final EltmPatientPicMapper picMapper;
    private final EltmUnprocessedService unprocessedService;
    private final EltmUnprocessedCollector unprocessedCollector;
    private final EltmSyncErrorMapper syncErrorMapper;

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 补拉单个患者
     */
    public Map<String, Object> backfillPatient(long externalId) {
        Patient patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getExternalId, externalId).last("LIMIT 1"));
        if (patient == null) {
            throw new IllegalArgumentException("未找到外部患者: " + externalId);
        }

        List<JsonNode> rows = apiClient.fetchPicRecords(externalId);
        List<EltmUnprocessedData> unprocessed = new ArrayList<>();
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (JsonNode row : rows) {
            String picId = DatamainParseUtil.text(row.get(0));
            if (picId == null || picId.isBlank()) {
                skipped++;
                continue;
            }
            EltmPatientPic pic = picMapper.selectOne(new LambdaQueryWrapper<EltmPatientPic>()
                    .eq(EltmPatientPic::getPicId, picId).last("LIMIT 1"));
            boolean isNew = pic == null;
            if (isNew) {
                pic = new EltmPatientPic();
                pic.setPicId(picId);
                created++;
            } else {
                updated++;
            }
            pic.setPatientId(patient.getId());
            pic.setExternalPatientId(externalId);
            setIfNotNull(pic::setRecordDate, DatamainParseUtil.parseDateTime(row.get(3)));
            setIfNotNull(pic::setRecordName, DatamainParseUtil.text(row.get(4)));
            setIfNotNull(pic::setImageUrl, DatamainParseUtil.text(row.get(5)));
            setIfNotNull(pic::setExternalCreateTime, DatamainParseUtil.parseDateTime(row.get(6)));
            setIfNotNull(pic::setExternalUpdateTime, DatamainParseUtil.parseDateTime(row.get(7)));
            setIfNotNull(pic::setPicSourceType, DatamainParseUtil.text(row.get(10)));
            setIfNotNull(pic::setSource, DatamainParseUtil.text(row.get(11)));
            setIfNotNull(pic::setImageType, DatamainParseUtil.text(row.get(14)));
            setIfNotNull(pic::setRemark, DatamainParseUtil.text(row.get(15)));
            pic.setRawJson(row.toString());
            pic.setSyncTime(LocalDateTime.now());
            if (isNew) {
                picMapper.insert(pic);
            } else {
                picMapper.updateById(pic);
            }
            unprocessed.addAll(unprocessedCollector.collectGetTpById(
                    patient.getId(), externalId, picId, List.of(row)));
        }

        unprocessedService.replacePending(patient.getId(),
                EltmUnprocessedCollector.API_GETTPBYID, unprocessed);

        Map<String, Object> result = new HashMap<>();
        result.put("externalId", externalId);
        result.put("total", rows.size());
        result.put("created", created);
        result.put("updated", updated);
        result.put("skipped", skipped);
        result.put("unprocessed", unprocessed.size());
        return result;
    }

    /**
     * 批量补拉 pic_count > 0 的患者
     *
     * @param limit 0 表示全部
     */
    public Map<String, Object> backfillAll(int limit) {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("图片补拉进行中，请稍后再试");
        }
        try {
            List<EltmPatientProfile> profiles = profileMapper.selectList(
                    new LambdaQueryWrapper<EltmPatientProfile>()
                            .select(EltmPatientProfile::getExternalId, EltmPatientProfile::getPicCount)
                            .gt(EltmPatientProfile::getPicCount, 0)
                            .isNotNull(EltmPatientProfile::getExternalId)
                            .orderByAsc(EltmPatientProfile::getExternalId));
            int total = profiles.size();
            int processed = 0;
            int success = 0;
            int failed = 0;
            for (EltmPatientProfile profile : profiles) {
                if (limit > 0 && processed >= limit) {
                    break;
                }
                processed++;
                try {
                    backfillPatient(profile.getExternalId());
                    success++;
                } catch (Exception e) {
                    failed++;
                    log.error("补拉病历图片失败 externalId={}", profile.getExternalId(), e);
                    saveError(profile.getExternalId(), e);
                }
                sleepQuietly();
            }
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("processed", processed);
            result.put("success", success);
            result.put("failed", failed);
            return result;
        } finally {
            running.set(false);
        }
    }

    private void saveError(long externalId, Exception e) {
        try {
            EltmSyncError error = new EltmSyncError();
            error.setExternalId(externalId);
            error.setStage("PIC");
            error.setErrorMessage(DatamainParseUtil.truncate(e.getMessage(), 2000));
            error.setCreateTime(LocalDateTime.now());
            syncErrorMapper.insert(error);
        } catch (Exception ex) {
            log.error("记录图片补拉失败明细时出错", ex);
        }
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(RATE_LIMIT_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private <T> void setIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
