package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.PageResult;
import com.eksjk.mapper.EltmPatientRawMapper;
import com.eksjk.mapper.EltmUnprocessedDataMapper;
import com.eksjk.model.entity.EltmPatientRaw;
import com.eksjk.model.entity.EltmUnprocessedData;
import com.eksjk.service.sync.DatamainParseUtil;
import com.eksjk.service.sync.EltmModuleClassifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ELTM 外部接口未处理数据服务
 *
 * <p>同患者同接口的 PENDING 记录每次同步重建；MAPPED/IGNORED 由人工处理结果保留。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmUnprocessedService {

    private static final int BATCH_SIZE = 200;
    private static final int MAX_RAW_VALUE = 20000;
    private static final int MAX_RAW_JSON = 65535;

    private final EltmUnprocessedDataMapper unprocessedMapper;
    private final EltmPatientRawMapper rawMapper;

    /**
     * 重建某患者某接口的 PENDING 未处理数据
     */
    public int replacePending(Long patientId, String apiCode, List<EltmUnprocessedData> records) {
        if (patientId == null || apiCode == null) {
            return 0;
        }
        unprocessedMapper.delete(new LambdaQueryWrapper<EltmUnprocessedData>()
                .eq(EltmUnprocessedData::getPatientId, patientId)
                .eq(EltmUnprocessedData::getApiCode, apiCode)
                .eq(EltmUnprocessedData::getStatus, "PENDING"));
        if (records == null || records.isEmpty()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        List<EltmUnprocessedData> normalized = new ArrayList<>(records.size());
        for (EltmUnprocessedData record : records) {
            if (record.getCategory() == null || record.getSourceKey() == null) {
                continue;
            }
            record.setPatientId(patientId);
            record.setApiCode(apiCode);
            record.setStatus("PENDING");
            record.setSourceKey(DatamainParseUtil.truncate(record.getSourceKey(), 191));
            record.setSourceName(DatamainParseUtil.truncate(record.getSourceName(), 255));
            record.setReason(DatamainParseUtil.truncate(record.getReason(), 500));
            record.setRawValue(DatamainParseUtil.truncate(record.getRawValue(), MAX_RAW_VALUE));
            record.setRawJson(DatamainParseUtil.truncate(record.getRawJson(), MAX_RAW_JSON));
            record.setContentHash(contentHash(record));
            record.setSyncTime(now);
            normalized.add(record);
        }
        if (normalized.isEmpty()) {
            return 0;
        }
        for (int start = 0; start < normalized.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, normalized.size());
            unprocessedMapper.upsertBatch(normalized.subList(start, end));
        }
        return normalized.size();
    }

    public PageResult<EltmUnprocessedData> page(Long patientId, String category, String module,
                                                long pageNum, long pageSize) {
        Page<EltmUnprocessedData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EltmUnprocessedData> wrapper = new LambdaQueryWrapper<EltmUnprocessedData>()
                .select(EltmUnprocessedData::getId, EltmUnprocessedData::getPatientId,
                        EltmUnprocessedData::getExternalPatientId, EltmUnprocessedData::getApiCode,
                        EltmUnprocessedData::getCategory, EltmUnprocessedData::getSourceKey,
                        EltmUnprocessedData::getSourceName, EltmUnprocessedData::getSuggestedModule,
                        EltmUnprocessedData::getReason,
                        EltmUnprocessedData::getRawValue, EltmUnprocessedData::getStatus,
                        EltmUnprocessedData::getSyncTime)
                .eq(EltmUnprocessedData::getPatientId, patientId)
                .eq(category != null && !category.isBlank(), EltmUnprocessedData::getCategory, category)
                .eq(module != null && !module.isBlank(), EltmUnprocessedData::getSuggestedModule, module)
                .orderByDesc(EltmUnprocessedData::getId);
        Page<EltmUnprocessedData> result = unprocessedMapper.selectPage(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    /**
     * 按目标模块统计未处理数据，供前端筛选。
     */
    public List<Map<String, Object>> moduleCounts(Long patientId) {
        List<EltmUnprocessedData> rows = unprocessedMapper.selectList(
                new LambdaQueryWrapper<EltmUnprocessedData>()
                        .select(EltmUnprocessedData::getSuggestedModule)
                        .eq(EltmUnprocessedData::getPatientId, patientId)
                        .eq(EltmUnprocessedData::getStatus, "PENDING"));
        Map<String, Long> counts = new LinkedHashMap<>();
        for (EltmUnprocessedData row : rows) {
            String module = row.getSuggestedModule() == null
                    ? EltmModuleClassifier.OTHER : row.getSuggestedModule();
            counts.merge(module, 1L, Long::sum);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        counts.forEach((module, count) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("module", module);
            item.put("count", count);
            result.add(item);
        });
        result.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));
        return result;
    }

    public EltmUnprocessedData detail(Long patientId, Long recordId) {
        EltmUnprocessedData data = unprocessedMapper.selectOne(
                new LambdaQueryWrapper<EltmUnprocessedData>()
                        .eq(EltmUnprocessedData::getId, recordId)
                        .eq(EltmUnprocessedData::getPatientId, patientId)
                        .last("LIMIT 1"));
        if (data == null) {
            throw new BusinessException("未处理数据不存在");
        }
        return data;
    }

    public EltmPatientRaw rawRow(Long patientId) {
        return rawMapper.selectOne(new LambdaQueryWrapper<EltmPatientRaw>()
                .eq(EltmPatientRaw::getPatientId, patientId)
                .last("LIMIT 1"));
    }

    /**
     * 投影命中后把未映射字段标记为已处理
     */
    public void markMapped(Long patientId, Long formDataId, String fieldKey) {
        if (patientId == null || formDataId == null || fieldKey == null) {
            return;
        }
        unprocessedMapper.update(null, new LambdaUpdateWrapper<EltmUnprocessedData>()
                .eq(EltmUnprocessedData::getPatientId, patientId)
                .eq(EltmUnprocessedData::getApiCode, "CXLIST")
                .eq(EltmUnprocessedData::getCategory, "UNMAPPED_FIELD")
                .eq(EltmUnprocessedData::getSourceKey, formDataId + ":" + fieldKey)
                .eq(EltmUnprocessedData::getStatus, "PENDING")
                .set(EltmUnprocessedData::getStatus, "MAPPED")
                .set(EltmUnprocessedData::getUpdateTime, LocalDateTime.now()));
    }

    public long countPending(Long patientId) {
        return unprocessedMapper.selectCount(new LambdaQueryWrapper<EltmUnprocessedData>()
                .eq(EltmUnprocessedData::getPatientId, patientId)
                .eq(EltmUnprocessedData::getStatus, "PENDING"));
    }

    private String contentHash(EltmUnprocessedData record) {
        return DatamainParseUtil.sha256(String.join("|",
                nullToEmpty(record.getApiCode()),
                String.valueOf(record.getPatientId()),
                nullToEmpty(record.getCategory()),
                nullToEmpty(record.getSourceKey()),
                nullToEmpty(record.getRawValue()),
                nullToEmpty(record.getRawJson())));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
