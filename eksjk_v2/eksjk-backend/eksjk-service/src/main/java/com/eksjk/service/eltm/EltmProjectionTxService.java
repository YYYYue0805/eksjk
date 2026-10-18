package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.*;
import com.eksjk.model.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ELTM 外部数据投影（单患者事务）
 *
 * <p>按 eltm_form_field_mapping 配置，把外部表单答案投影到现有业务表；
 * 只填空、不覆盖本地已有值，可重复执行。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmProjectionTxService {

    private static final String SOURCE_LOCAL = "LOCAL";
    private static final String SOURCE_ELTM = "ELTM_SYNC";
    private static final Pattern FIRST_NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

    private final PatientMapper patientMapper;
    private final EltmFormSubmissionMapper formSubmissionMapper;
    private final EltmFormFieldMappingMapper mappingMapper;
    private final EltmCaseMapper eltmCaseMapper;
    private final PatientGeneticsMapper patientGeneticsMapper;
    private final EltmPhysicalRecordMapper physicalRecordMapper;
    private final EltmPhysicalItemMapper physicalItemMapper;
    private final PatientFollowUpMapper followUpMapper;
    private final EltmProjectionLogMapper projectionLogMapper;
    private final ObjectMapper objectMapper;
    private final EltmModuleMigrationService moduleMigrationService;

    /**
     * 对单个患者执行投影
     *
     * @return 本次新增/写入的记录数
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int projectPatient(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || patient.getExternalId() == null) {
            return 0;
        }

        int changed = 0;
        changed += applyFormMappings(patient);
        changed += projectPhysicalFollowUps(patient);
        changed += fillLatestPhysicalSummary(patient);
        changed += moduleMigrationService.projectPatient(patient);
        writeLog(patient, "SUCCESS", changed, null);
        return changed;
    }

    // ==================== 表单映射 ====================

    private int applyFormMappings(Patient patient) {
        List<EltmFormFieldMapping> mappings = mappingMapper.selectList(
                new LambdaQueryWrapper<EltmFormFieldMapping>()
                        .eq(EltmFormFieldMapping::getEnabled, 1)
                        .orderByAsc(EltmFormFieldMapping::getPriority));
        if (mappings.isEmpty()) {
            return 0;
        }

        List<EltmFormSubmission> submissions = formSubmissionMapper.selectList(
                new LambdaQueryWrapper<EltmFormSubmission>()
                        .eq(EltmFormSubmission::getPatientId, patient.getId())
                        .isNotNull(EltmFormSubmission::getOriginalData)
                        .orderByDesc(EltmFormSubmission::getSubmittedAt)
                        .orderByDesc(EltmFormSubmission::getId));
        if (submissions.isEmpty()) {
            return 0;
        }

        Map<String, List<EltmFormFieldMapping>> byFormKey = mappings.stream()
                .collect(Collectors.groupingBy(EltmFormFieldMapping::getFormKey,
                        LinkedHashMap::new, Collectors.toList()));

        // 同一目标字段只取最近一次非空提交
        Map<String, String> targets = new LinkedHashMap<>();
        for (EltmFormSubmission submission : submissions) {
            List<EltmFormFieldMapping> formMappings = byFormKey.get(submission.getFormKey());
            if (formMappings == null || formMappings.isEmpty()) {
                continue;
            }
            JsonNode data;
            try {
                data = objectMapper.readTree(submission.getOriginalData());
            } catch (Exception e) {
                log.warn("ELTM 表单 JSON 解析失败 patientId={} formDataId={}", patient.getId(), submission.getFormDataId());
                continue;
            }
            for (EltmFormFieldMapping mapping : formMappings) {
                String key = mapping.getTargetScope() + "." + mapping.getTargetField();
                if (targets.containsKey(key)) {
                    continue;
                }
                String value = extractValue(data, mapping);
                value = applyTransform(value, mapping.getTransformRule());
                if (value != null && !value.isBlank()) {
                    targets.put(key, value);
                }
            }
        }

        int changed = 0;
        boolean patientDirty = false;
        EltmCase eltmCase = null;
        PatientGenetics genetics = null;
        Map<String, String> familyTargets = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : targets.entrySet()) {
            String[] parts = entry.getKey().split("\\.", 2);
            String scope = parts[0];
            String field = parts[1];
            String value = entry.getValue();
            switch (scope) {
                case "PATIENT" -> {
                    if (setIfBlank(patient, field, value)) {
                        patientDirty = true;
                        changed++;
                    }
                }
                case "ELTM_CASE" -> {
                    if (eltmCase == null) {
                        eltmCase = findOrCreateEltmCase(patient.getId());
                    }
                    changed += setIfBlank(eltmCase, field, value) ? 1 : 0;
                }
                case "GENETICS" -> {
                    if (genetics == null) {
                        genetics = findOrCreateGenetics(patient.getId());
                    }
                    changed += setIfBlank(genetics, field, value) ? 1 : 0;
                }
                case "FAMILY" -> familyTargets.put(field, value);
                default -> {
                    // FOLLOWUP/ADVERSE 由 EltmModuleMigrationService 处理
                }
            }
        }

        if (!familyTargets.isEmpty() && moduleMigrationService.mergeFamily(patient, familyTargets)) {
            patientDirty = true;
            changed++;
        }
        if (patientDirty) {
            patientMapper.updateById(patient);
        }
        if (eltmCase != null && eltmCase.getId() != null) {
            eltmCaseMapper.updateById(eltmCase);
        }
        if (genetics != null && genetics.getId() != null) {
            patientGeneticsMapper.updateById(genetics);
        }
        return changed;
    }

    private EltmCase findOrCreateEltmCase(Long patientId) {
        EltmCase entity = eltmCaseMapper.selectOne(new LambdaQueryWrapper<EltmCase>()
                .eq(EltmCase::getPatientId, patientId).last("LIMIT 1"));
        if (entity == null) {
            entity = new EltmCase();
            entity.setPatientId(patientId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            eltmCaseMapper.insert(entity);
        }
        return entity;
    }

    private PatientGenetics findOrCreateGenetics(Long patientId) {
        PatientGenetics entity = patientGeneticsMapper.selectOne(new LambdaQueryWrapper<PatientGenetics>()
                .eq(PatientGenetics::getPatientId, patientId).last("LIMIT 1"));
        if (entity == null) {
            entity = new PatientGenetics();
            entity.setPatientId(patientId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            patientGeneticsMapper.insert(entity);
        }
        return entity;
    }

    private String extractValue(JsonNode data, EltmFormFieldMapping mapping) {
        JsonNode node = data.get(mapping.getFieldKey());
        if (node == null || node.isNull()) {
            return null;
        }
        String valueType = mapping.getValueType() == null ? "RAW" : mapping.getValueType();
        return switch (valueType) {
            case "LABEL" -> labelOf(node);
            case "MULTI_LABEL" -> multiLabelsAsJson(node);
            default -> node.isValueNode() ? node.asText() : null;
        };
    }

    private String labelOf(JsonNode node) {
        if (!node.isObject()) {
            return node.isValueNode() ? node.asText() : null;
        }
        JsonNode value = node.get("value");
        if (value == null || value.isNull()) {
            return null;
        }
        JsonNode labels = node.get("labelList");
        if (value.isArray()) {
            return multiLabelsAsJson(node);
        }
        String key = value.asText();
        return labels != null && labels.has(key) ? labels.get(key).asText() : key;
    }

    private String multiLabelsAsJson(JsonNode node) {
        if (!node.isObject()) {
            return null;
        }
        JsonNode value = node.get("value");
        if (value == null || value.isNull()) {
            return null;
        }
        JsonNode labels = node.get("labelList");
        List<String> result = new ArrayList<>();
        if (value.isArray()) {
            for (JsonNode item : value) {
                String key = item.asText();
                String label = labels != null && labels.has(key) ? labels.get(key).asText() : key;
                if (!label.isBlank()) {
                    result.add(label);
                }
            }
        } else {
            String key = value.asText();
            String label = labels != null && labels.has(key) ? labels.get(key).asText() : key;
            if (!label.isBlank()) {
                result.add(label);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return null;
        }
    }

    private String applyTransform(String value, String rule) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.isEmpty()) {
            return null;
        }
        if (rule == null || rule.isBlank()) {
            return text;
        }
        if (rule.startsWith("ENUM:")) {
            return enumValue(text, rule.substring(5));
        }
        return switch (rule) {
            case "WEIGHT_G_TO_KG" -> {
                Double number = firstNumber(text);
                if (number == null) {
                    yield null;
                }
                double kg = number > 20 ? number / 1000.0 : number;
                if (kg < 0.5 || kg > 12) {
                    yield null;
                }
                yield trimNumber(kg, 2);
            }
            case "BIRTH_HEIGHT_CM" -> {
                Double number = firstNumber(text);
                if (number == null || number < 30 || number > 70) {
                    yield null;
                }
                yield trimNumber(number, 1);
            }
            case "GEST_WEEK" -> {
                Double number = firstNumber(text);
                if (number == null || number < 20 || number > 45) {
                    yield null;
                }
                yield String.valueOf(Math.round(number));
            }
            case "TEXT_CLEAN" -> text.replaceFirst("^[:：;；\\s]+", "").trim();
            default -> text;
        };
    }

    /**
     * 枚举文案转换，规则格式：ENUM:自然分娩=0;剖宫产=1
     */
    private String enumValue(String text, String rule) {
        if (text == null || rule == null) {
            return null;
        }
        for (String pair : rule.split(";")) {
            int idx = pair.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String label = pair.substring(0, idx).trim();
            String code = pair.substring(idx + 1).trim();
            if (label.equals(text)) {
                return code;
            }
        }
        return null;
    }

    private Double firstNumber(String text) {
        Matcher matcher = FIRST_NUMBER.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        try {
            return Double.parseDouble(matcher.group());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trimNumber(double value, int maxScale) {
        java.math.BigDecimal decimal = java.math.BigDecimal.valueOf(value)
                .setScale(maxScale, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return decimal.toPlainString();
    }

    private boolean setIfBlank(Object target, String fieldName, String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        Field field = findField(target.getClass(), fieldName);
        if (field == null || field.getType() != String.class) {
            log.warn("ELTM 投影目标字段不存在或类型不支持: {}.{}", target.getClass().getSimpleName(), fieldName);
            return false;
        }
        try {
            field.setAccessible(true);
            Object current = field.get(target);
            if (current != null && !String.valueOf(current).isBlank()) {
                return false;
            }
            field.set(target, value);
            return true;
        } catch (Exception e) {
            log.warn("ELTM 投影字段写入失败: {}.{}", target.getClass().getSimpleName(), fieldName, e);
            return false;
        }
    }

    private Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    // ==================== 体征 -> 随访记录 ====================

    private int projectPhysicalFollowUps(Patient patient) {
        List<EltmPhysicalRecord> records = physicalRecordMapper.selectList(
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patient.getId())
                        .isNotNull(EltmPhysicalRecord::getMeasureTime)
                        .orderByAsc(EltmPhysicalRecord::getMeasureTime));
        if (records.isEmpty()) {
            return 0;
        }

        Set<LocalDateTime> existingTimes = followUpMapper.selectList(
                        new LambdaQueryWrapper<PatientFollowUp>()
                                .eq(PatientFollowUp::getPatientId, patient.getId())
                                .eq(PatientFollowUp::getDataSource, SOURCE_ELTM))
                .stream()
                .map(PatientFollowUp::getFollTime)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> recordIds = records.stream().map(EltmPhysicalRecord::getId).toList();
        List<EltmPhysicalItem> items = physicalItemMapper.selectList(
                new LambdaQueryWrapper<EltmPhysicalItem>()
                        .in(EltmPhysicalItem::getRecordId, recordIds));
        Map<Long, Map<String, String>> itemMap = new HashMap<>();
        for (EltmPhysicalItem item : items) {
            itemMap.computeIfAbsent(item.getRecordId(), key -> new HashMap<>())
                    .put(item.getAttributeCode(), item.getAttributeValue());
        }

        int created = 0;
        for (EltmPhysicalRecord record : records) {
            if (existingTimes.contains(record.getMeasureTime())) {
                continue;
            }
            Map<String, String> values = itemMap.getOrDefault(record.getId(), Collections.emptyMap());
            String height = blankToNull(values.get("height:Tab"));
            String weight = blankToNull(values.get("weight:Tab"));
            String bmi = blankToNull(values.get("bmi"));
            if (height == null && weight == null && bmi == null) {
                continue;
            }
            PatientFollowUp followUp = new PatientFollowUp();
            followUp.setPatientId(patient.getId());
            followUp.setFollTime(record.getMeasureTime());
            followUp.setUpTime(LocalDateTime.now());
            followUp.setHt(height);
            followUp.setWt(weight);
            followUp.setBmi(bmi);
            followUp.setDataSource(SOURCE_ELTM);
            followUp.setDelFlg("1");
            followUpMapper.insert(followUp);
            created++;
        }
        return created;
    }

    /**
     * 最新一次体征回填患者主表身高/体重/BMI（只填空）
     */
    private int fillLatestPhysicalSummary(Patient patient) {
        EltmPhysicalRecord latest = physicalRecordMapper.selectOne(
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patient.getId())
                        .orderByDesc(EltmPhysicalRecord::getMeasureTime)
                        .last("LIMIT 1"));
        if (latest == null) {
            return 0;
        }
        Map<String, String> values = physicalItemMapper.selectList(
                        new LambdaQueryWrapper<EltmPhysicalItem>()
                                .eq(EltmPhysicalItem::getRecordId, latest.getId()))
                .stream()
                .collect(Collectors.toMap(EltmPhysicalItem::getAttributeCode,
                        item -> item.getAttributeValue() == null ? "" : item.getAttributeValue(),
                        (a, b) -> a));
        int changed = 0;
        changed += setIfBlank(patient, "height", blankToNull(values.get("height:Tab"))) ? 1 : 0;
        changed += setIfBlank(patient, "weight", blankToNull(values.get("weight:Tab"))) ? 1 : 0;
        changed += setIfBlank(patient, "bmi", blankToNull(values.get("bmi"))) ? 1 : 0;
        if (changed > 0) {
            patientMapper.updateById(patient);
        }
        return changed;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void writeLog(Patient patient, String status, int fieldCount, String message) {
        try {
            EltmProjectionLog entry = new EltmProjectionLog();
            entry.setPatientId(patient.getId());
            entry.setExternalId(patient.getExternalId());
            entry.setStatus(status);
            entry.setFieldCount(fieldCount);
            entry.setMessage(message);
            entry.setCreateTime(LocalDateTime.now());
            projectionLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写入 ELTM 投影日志失败 patientId={}", patient.getId(), e);
        }
    }
}
