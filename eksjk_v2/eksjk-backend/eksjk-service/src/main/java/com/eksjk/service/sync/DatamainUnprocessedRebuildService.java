package com.eksjk.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.EltmFormFieldMappingMapper;
import com.eksjk.mapper.EltmPatientRawMapper;
import com.eksjk.model.entity.EltmFormFieldMapping;
import com.eksjk.model.entity.EltmPatientRaw;
import com.eksjk.model.entity.EltmUnprocessedData;
import com.eksjk.service.eltm.EltmUnprocessedService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从 eltm_patient_raw 重放采集未处理数据
 *
 * <p>用于新增分类规则或映射配置后，无需重新下载 50MB cxList 即可刷新归桶结果。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatamainUnprocessedRebuildService {

    private static final int PAGE_SIZE = 200;
    private static final int EXPECTED_COLUMNS = 66;

    private final EltmPatientRawMapper rawMapper;
    private final EltmFormFieldMappingMapper mappingMapper;
    private final EltmUnprocessedCollector unprocessedCollector;
    private final EltmUnprocessedService unprocessedService;
    private final ObjectMapper objectMapper;

    public Map<String, Object> rebuildAll() {
        Map<String, Set<String>> mappingIndex = mappingMapper.selectList(
                        new LambdaQueryWrapper<EltmFormFieldMapping>()
                                .eq(EltmFormFieldMapping::getEnabled, 1))
                .stream()
                .filter(mapping -> mapping.getFormKey() != null && mapping.getFieldKey() != null)
                .collect(Collectors.groupingBy(EltmFormFieldMapping::getFormKey,
                        Collectors.mapping(EltmFormFieldMapping::getFieldKey, Collectors.toSet())));

        long lastId = 0;
        int total = 0;
        int success = 0;
        int failed = 0;
        while (true) {
            List<EltmPatientRaw> batch = rawMapper.selectList(
                    new LambdaQueryWrapper<EltmPatientRaw>()
                            .select(EltmPatientRaw::getId, EltmPatientRaw::getPatientId,
                                    EltmPatientRaw::getExternalId, EltmPatientRaw::getRawJson)
                            .gt(EltmPatientRaw::getId, lastId)
                            .orderByAsc(EltmPatientRaw::getId)
                            .last("LIMIT " + PAGE_SIZE));
            if (batch.isEmpty()) {
                break;
            }
            for (EltmPatientRaw raw : batch) {
                lastId = raw.getId();
                total++;
                if (raw.getPatientId() == null || raw.getRawJson() == null) {
                    failed++;
                    continue;
                }
                try {
                    JsonNode node = objectMapper.readTree(raw.getRawJson());
                    if (!node.isArray() || node.size() != EXPECTED_COLUMNS) {
                        failed++;
                        continue;
                    }
                    CxListRow row = new CxListRow((ArrayNode) node, objectMapper);
                    List<EltmUnprocessedData> records = unprocessedCollector.collectCxList(
                            row, raw.getPatientId(), mappingIndex);
                    unprocessedService.replacePending(raw.getPatientId(),
                            EltmUnprocessedCollector.API_CX_LIST, records);
                    success++;
                } catch (Exception e) {
                    failed++;
                    log.warn("重放未处理数据失败 externalId={}", raw.getExternalId(), e);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }
}
