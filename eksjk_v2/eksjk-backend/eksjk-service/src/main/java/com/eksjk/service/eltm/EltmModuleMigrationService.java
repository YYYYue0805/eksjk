package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.*;
import com.eksjk.model.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ELTM 暂未处理数据迁移
 *
 * <p>把外部表单/体征中的可结构化数据迁移到病例各模块。
 * 只填空、不覆盖本地已有值；多记录目标使用 form_data_id 幂等。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmModuleMigrationService {

    private static final String SOURCE_ELTM = "ELTM_SYNC";
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    private static final Set<String> DEMOGRAPHIC_FORMS = Set.of(
            "人口学信息", "人口学资料_医生填写", "儿童基本信息", "儿童基础信息 ",
            "病例基本信息采集（研究对象）", "人口学及基线资料", "患者基线信息");

    private static final Set<String> FOLLOWUP_FORMS = Set.of(
            "GH用药记录", "合并用药记录", "合并用药&非药物治疗记录（随访期）",
            "生长激素治疗相关病史：临床症状和体征", "随访检查记录", "治疗随访-实验室检查",
            "基础测量（身高、体重、头围、BMI）", "体重记录", "长高记录");

    private static final Set<String> AE_FORMS = Set.of("不良事件", "不良反应收集");
    private static final Set<String> CONSENT_FORMS = Set.of("知情同意书上传");
    private static final Set<String> STAGE_LABELS = Set.of("Ⅰ期", "Ⅱ期", "Ⅲ期", "Ⅳ期", "Ⅴ期");
    private static final Set<String> SEVERITY_LABELS = Set.of("轻度", "中度", "重度", "危及生命", "致残", "住院");
    private static final Set<String> CAUSALITY_LABELS = Set.of("肯定相关", "很可能相关", "可能相关", "可能无关", "无关", "待评价");
    private static final Set<String> OUTCOME_LABELS = Set.of("痊愈", "好转", "未好转", "加重", "死亡", "后遗症");
    private static final Set<String> DOSE_ADJUST_LABELS = Set.of("继续用药", "减量", "暂停用药", "永久停药");
    private static final Set<String> INTERVENTION_LABELS = Set.of("未干预", "对症处理", "GH用药调整");
    private static final Set<String> LOCAL_LABELS = Set.of("红肿", "硬结", "瘙痒", "疼痛", "皮下脂肪萎缩", "局部出血或瘀斑");
    private static final Set<String> SYSTEMIC_LABELS = Set.of("发热", "乏力", "头痛", "关节痛", "肌肉痛", "水肿(眼睑/下肢)", "全身荨麻疹", "全身一般反应", "严重过敏反应");
    private static final Set<String> ENDOCRINE_LABELS = Set.of("血糖异常", "甲状腺功能异常", "水钠潴留电解质紊乱", "肢端肥大样表现(长期用药)");
    private static final Set<String> NEURO_LABELS = Set.of("抽搐头晕嗜睡等", "良性颅内高压(假性脑瘤)");
    private static final Set<String> SKIN_LABELS = Set.of("皮疹", "过敏性皮炎", "瘙痒", "皮肤过敏反应");

    private final PatientMapper patientMapper;
    private final EltmFormSubmissionMapper formSubmissionMapper;
    private final EltmCaseMapper eltmCaseMapper;
    private final EltmPhysicalRecordMapper physicalRecordMapper;
    private final EltmPhysicalItemMapper physicalItemMapper;
    private final PatientFollowUpMapper followUpMapper;
    private final GhAdverseEventMapper adverseEventMapper;
    private final InformedConsentMapper informedConsentMapper;
    private final ObjectMapper objectMapper;

    /**
     * 对单个患者执行扩展迁移
     */
    public int projectPatient(Patient patient) {
        int changed = 0;
        changed += projectDemographics(patient);
        changed += projectPhysicalExtras(patient);
        changed += projectPhysicalExam(patient);
        changed += projectFormFollowUps(patient);
        changed += projectAdverseEvents(patient);
        changed += projectConsentUploads(patient);
        changed += projectStructuredExams(patient);
        changed += projectDiagnosis(patient);
        return changed;
    }

    // ==================== 人口学信息 -> 基本信息 ====================

    private int projectDemographics(Patient patient) {
        List<EltmFormSubmission> submissions = loadSubmissions(patient.getId(), DEMOGRAPHIC_FORMS);
        if (submissions.isEmpty()) {
            return 0;
        }
        boolean dirty = false;
        for (EltmFormSubmission submission : submissions) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) {
                continue;
            }
            if (blank(patient.getSex())) {
                String sex = detectSex(data);
                if (sex != null) {
                    patient.setSex(sex);
                    dirty = true;
                }
            }
            if (blank(patient.getEthnic())) {
                String ethnic = detectEthnic(data);
                if (ethnic != null) {
                    patient.setEthnic(ethnic);
                    dirty = true;
                }
            }
            if (blank(patient.getFamAdr())) {
                String address = detectAddress(data);
                if (address != null) {
                    patient.setFamAdr(address);
                    dirty = true;
                }
            }
        }
        if (dirty) {
            patientMapper.updateById(patient);
        }
        return dirty ? 1 : 0;
    }

    private String detectSex(JsonNode data) {
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            JsonNode node = entry.getValue();
            Set<String> options = optionLabels(node);
            if (options.contains("男") && options.contains("女")) {
                for (String selected : selectedLabels(node)) {
                    if ("男".equals(selected)) return "1";
                    if ("女".equals(selected)) return "2";
                }
            }
        }
        return null;
    }

    private String detectEthnic(JsonNode data) {
        String candidate = null;
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            String text = textOf(entry.getValue());
            if (text == null || text.length() > 20) {
                continue;
            }
            if (text.endsWith("族") || "汉".equals(text)) {
                if (candidate == null || "汉族".equals(text)) {
                    candidate = text;
                }
            }
        }
        return candidate;
    }

    private String detectAddress(JsonNode data) {
        String best = null;
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            JsonNode node = entry.getValue();
            if (node.isObject() && node.has("cascadeValue")) {
                List<String> parts = new ArrayList<>();
                for (JsonNode item : node.get("cascadeValue")) {
                    if (item.isValueNode() && !item.asText().isBlank()) parts.add(item.asText());
                }
                if (node.has("detailAddr") && !node.get("detailAddr").asText().isBlank()) {
                    parts.add(node.get("detailAddr").asText());
                }
                String joined = String.join("", parts);
                if (joined.length() > 5 && (best == null || joined.length() > best.length())) {
                    best = joined;
                }
                continue;
            }
            String text = textOf(node);
            if (text == null || text.length() < 5 || text.length() > 200) {
                continue;
            }
            if (looksLikeAddress(text) && (best == null || text.length() > best.length())) {
                best = text;
            }
        }
        return best;
    }

    private boolean looksLikeAddress(String text) {
        return text.contains("省") || text.contains("市") || text.contains("区")
                || text.contains("县") || text.contains("街道") || text.contains("镇")
                || text.contains("乡") || text.contains("村") || text.contains("路")
                || text.contains("号") || text.contains("小区") || text.contains("室");
    }

    // ==================== 体征扩展 -> 基线-临床信息 ====================

    private int projectPhysicalExtras(Patient patient) {
        EltmPhysicalRecord latest = physicalRecordMapper.selectOne(new LambdaQueryWrapper<EltmPhysicalRecord>()
                .eq(EltmPhysicalRecord::getPatientId, patient.getId())
                .isNotNull(EltmPhysicalRecord::getMeasureTime)
                .orderByDesc(EltmPhysicalRecord::getMeasureTime)
                .last("LIMIT 1"));
        if (latest == null) {
            return 0;
        }
        Map<String, String> values = physicalItems(latest.getId());
        Map<String, String> family = new HashMap<>();
        family.put("fatherHeight", values.get("fatherHeight"));
        family.put("motherHeight", values.get("motherHeight"));

        boolean dirty = false;
        dirty |= mergeFamily(patient, family);
        if (blank(patient.getHeightSds()) && !blank(values.get("standardDeviation"))) {
            patient.setHeightSds(values.get("standardDeviation"));
            dirty = true;
        }
        if (blank(patient.getFht()) && !blank(values.get("fatherHeight"))) {
            patient.setFht(values.get("fatherHeight"));
            dirty = true;
        }
        if (blank(patient.getMht()) && !blank(values.get("motherHeight"))) {
            patient.setMht(values.get("motherHeight"));
            dirty = true;
        }
        if (dirty) {
            patientMapper.updateById(patient);
        }
        return dirty ? 1 : 0;
    }

    private Map<String, String> physicalItems(Long recordId) {
        Map<String, String> map = new HashMap<>();
        List<EltmPhysicalItem> items = physicalItemMapper.selectList(new LambdaQueryWrapper<EltmPhysicalItem>()
                .eq(EltmPhysicalItem::getRecordId, recordId));
        for (EltmPhysicalItem item : items) {
            if (item.getAttributeCode() != null) {
                map.put(item.getAttributeCode(), item.getAttributeValue());
            }
        }
        return map;
    }

    // ==================== 体格检查 -> 基线-临床信息 ====================

    private int projectPhysicalExam(Patient patient) {
        List<EltmFormSubmission> submissions = loadSubmissions(patient.getId(), Set.of("体格检查", "儿童体格检查"));
        if (submissions.isEmpty()) {
            return 0;
        }
        boolean dirty = false;
        int examined = 0;
        for (EltmFormSubmission submission : submissions) {
            if (examined >= 5) {
                break;
            }
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) {
                continue;
            }
            Map<String, Double> numbers = numericValues(data);
            Double height = pickHeight(numbers);
            Double weight = pickWeight(numbers, height);
            if (blank(patient.getHeight()) && height != null) {
                patient.setHeight(trim(height, 1));
                dirty = true;
            }
            if (blank(patient.getWeight()) && weight != null) {
                patient.setWeight(trim(weight, 2));
                dirty = true;
            }
            if (blank(patient.getBmi()) && height != null && weight != null && height > 0) {
                patient.setBmi(trim(weight / Math.pow(height / 100.0, 2), 2));
                dirty = true;
            }
            dirty |= applyStageFields(patient, data);
            examined++;
            if (!blank(patient.getHeight()) && !blank(patient.getWeight()) && !blank(patient.getGenStag()) && !blank(patient.getPubStag())) {
                break;
            }
        }
        if (dirty) {
            patientMapper.updateById(patient);
        }
        return dirty ? 1 : 0;
    }

    private Double pickHeight(Map<String, Double> numbers) {
        Double known = numbers.get("number1401782290169");
        if (known != null && known >= 30 && known <= 220) {
            return known;
        }
        Double best = null;
        for (Double value : numbers.values()) {
            if (value != null && value >= 30 && value <= 220 && (best == null || value > best)) {
                best = value;
            }
        }
        return best;
    }

    private Double pickWeight(Map<String, Double> numbers, Double height) {
        Double known = numbers.get("number3614223399579");
        if (known != null && known > 0.5 && known <= 150) {
            return known;
        }
        Double best = null;
        for (Double value : numbers.values()) {
            if (value == null || value <= 0.5 || value > 150) {
                continue;
            }
            if (height != null && value >= height - 5) {
                continue;
            }
            if (best == null || value > best) {
                best = value;
            }
        }
        return best;
    }

    private boolean applyStageFields(Patient patient, JsonNode data) {
        List<String> stages = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            Set<String> options = optionLabels(entry.getValue());
            if (!options.isEmpty() && STAGE_LABELS.containsAll(options)) {
                List<String> selected = selectedLabels(entry.getValue());
                if (!selected.isEmpty()) {
                    stages.add(selected.get(0));
                }
            }
        }
        if (stages.isEmpty()) {
            return false;
        }
        boolean dirty = false;
        int first = stageNumber(stages.get(0));
        if ("2".equals(patient.getSex())) {
            if (blank(patient.getBreastDevLeft()) && first > 0) {
                patient.setBreastDevLeft("B" + first);
                dirty = true;
            }
            if (blank(patient.getBreastDevRight()) && first > 0) {
                patient.setBreastDevRight("B" + first);
                dirty = true;
            }
        } else if (blank(patient.getGenStag()) && first > 0) {
            patient.setGenStag("G" + first);
            dirty = true;
        }
        if (stages.size() > 1 && blank(patient.getPubStag())) {
            int second = stageNumber(stages.get(1));
            if (second > 0) {
                patient.setPubStag(String.valueOf(second));
                dirty = true;
            }
        }
        return dirty;
    }

    private int stageNumber(String label) {
        if (label == null) return 0;
        return switch (label) {
            case "Ⅰ期", "1期", "1" -> 1;
            case "Ⅱ期", "2期", "2" -> 2;
            case "Ⅲ期", "3期", "3" -> 3;
            case "Ⅳ期", "4期", "4" -> 4;
            case "Ⅴ期", "5期", "5" -> 5;
            default -> 0;
        };
    }

    // ==================== 表单 -> 随访记录 ====================

    private int projectFormFollowUps(Patient patient) {
        List<EltmFormSubmission> submissions = loadSubmissions(patient.getId(), FOLLOWUP_FORMS);
        int created = 0;
        for (EltmFormSubmission submission : submissions) {
            if (followUpExists(submission.getFormDataId())) {
                continue;
            }
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) {
                continue;
            }
            LocalDateTime follTime = latestDate(data, submission.getSubmittedAt());
            String formName = submission.getFormName();
            PatientFollowUp followUp = new PatientFollowUp();
            followUp.setPatientId(patient.getId());
            followUp.setFollTime(follTime);
            followUp.setUpTime(LocalDateTime.now());
            followUp.setDataSource(SOURCE_ELTM);
            followUp.setDelFlg("1");
            followUp.setSourceFormDataId(submission.getFormDataId());

            switch (formName) {
                case "GH用药记录" -> {
                    String summary = summarizeGhMedication(data);
                    if (summary == null) continue;
                    followUp.setDiaTreaPlan(summary);
                }
                case "合并用药记录", "合并用药&非药物治疗记录（随访期）" -> {
                    String summary = summarizeConcomitant(data);
                    if (summary == null) continue;
                    followUp.setOtherMedicine(summary);
                }
                case "生长激素治疗相关病史：临床症状和体征" -> {
                    String summary = joinTexts(data, 500);
                    if (summary == null) continue;
                    followUp.setOther("GH治疗症状：" + summary);
                }
                case "随访检查记录" -> {
                    String summary = summarizeFollowupForm(data);
                    if (summary == null) continue;
                    followUp.setOther(summary);
                }
                case "治疗随访-实验室检查" -> {
                    List<String> names = imageNames(data);
                    if (names.isEmpty()) continue;
                    followUp.setLabExaOther(String.join(",", names));
                }
                case "基础测量（身高、体重、头围、BMI）", "体重记录", "长高记录" -> {
                    Map<String, Double> numbers = numericValues(data);
                    Double height = pickHeight(numbers);
                    Double weight = pickWeight(numbers, height);
                    if (height == null && weight == null) continue;
                    if (height != null) followUp.setHt(trim(height, 1));
                    if (weight != null) followUp.setWt(trim(weight, 2));
                    if (height != null && weight != null && height > 0) {
                        followUp.setBmi(trim(weight / Math.pow(height / 100.0, 2), 2));
                    }
                }
                default -> {
                    continue;
                }
            }
            followUpMapper.insert(followUp);
            created++;
        }
        return created;
    }

    private String summarizeGhMedication(JsonNode data) {
        List<String> drugs = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            for (String selected : selectedLabels(entry.getValue())) {
                if ("赛增".equals(selected) || "金赛增".equals(selected)) {
                    drugs.add(selected);
                }
            }
        }
        Map<String, Double> numbers = numericValues(data);
        Double dose = numbers.get("input8415589063118");
        Double doseKg = numbers.get("input6710939989618");
        if (dose == null) dose = numbers.get("input3928013950723");
        if (drugs.isEmpty() && dose == null && doseKg == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("GH用药");
        if (!drugs.isEmpty()) sb.append("：").append(String.join("/", new LinkedHashSet<>(drugs)));
        if (dose != null) sb.append("，剂量 ").append(trim(dose, 2)).append(" IU/d");
        if (doseKg != null) sb.append("，剂量/体重 ").append(trim(doseKg, 4)).append(" mg/kg/d");
        return sb.toString();
    }

    private String summarizeConcomitant(JsonNode data) {
        LinkedHashSet<String> texts = new LinkedHashSet<>();
        collectSubFormTexts(data, texts);
        if (texts.isEmpty()) {
            collectTexts(data, texts);
        }
        texts.removeIf(t -> t.length() < 2 || t.matches("\\d+(\\.\\d+)?"));
        if (texts.isEmpty()) {
            return null;
        }
        return "合并用药：" + String.join("、", texts);
    }

    private String summarizeFollowupForm(JsonNode data) {
        LinkedHashSet<String> texts = new LinkedHashSet<>();
        collectTexts(data, texts);
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            for (String selected : selectedLabels(entry.getValue())) {
                if (!selected.isBlank()) texts.add(selected);
            }
        }
        texts.removeIf(t -> t.length() < 2 || t.matches("\\d+(\\.\\d+)?"));
        if (texts.isEmpty()) {
            return null;
        }
        return "随访检查：" + String.join("、", texts);
    }

    private void collectSubFormTexts(JsonNode node, Set<String> out) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode item : node) collectSubFormTexts(item, out);
            return;
        }
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (entry.getKey().startsWith("date")) continue;
                JsonNode value = entry.getValue();
                if (value.isObject() && value.has("value")) {
                    out.addAll(selectedLabels(value));
                } else if (value.isValueNode() && !value.isNull()) {
                    String text = value.asText().trim();
                    if (!text.isBlank() && !DATE_PATTERN.matcher(text).matches()) out.add(text);
                } else {
                    collectSubFormTexts(value, out);
                }
            }
        }
    }

    private void collectTexts(JsonNode node, Set<String> out) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode item : node) collectTexts(item, out);
            return;
        }
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (entry.getKey().startsWith("date") || entry.getKey().startsWith("image_upload")) continue;
                JsonNode value = entry.getValue();
                if (value.isValueNode() && !value.isNull()) {
                    String text = value.asText().trim();
                    if (!text.isBlank() && !DATE_PATTERN.matcher(text).matches()) out.add(text);
                } else if (value.isObject() && value.has("value")) {
                    for (String selected : selectedLabels(value)) {
                        if (!selected.isBlank()) out.add(selected);
                    }
                } else {
                    collectTexts(value, out);
                }
            }
        }
    }

    private boolean followUpExists(Long formDataId) {
        if (formDataId == null) return false;
        return followUpMapper.selectCount(new LambdaQueryWrapper<PatientFollowUp>()
                .eq(PatientFollowUp::getSourceFormDataId, formDataId)) > 0;
    }

    // ==================== 表单 -> GH 不良事件 ====================

    private int projectAdverseEvents(Patient patient) {
        List<EltmFormSubmission> submissions = loadSubmissions(patient.getId(), AE_FORMS);
        int created = 0;
        for (EltmFormSubmission submission : submissions) {
            if (submission.getFormDataId() == null || adverseEventExists(submission.getFormDataId())) {
                continue;
            }
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) {
                continue;
            }
            GhAdverseEvent event = buildAdverseEvent(patient, submission, data);
            if (event == null) {
                continue;
            }
            adverseEventMapper.insert(event);
            created++;
        }
        return created;
    }

    private boolean adverseEventExists(Long formDataId) {
        return adverseEventMapper.selectCount(new LambdaQueryWrapper<GhAdverseEvent>()
                .eq(GhAdverseEvent::getSourceFormDataId, formDataId)) > 0;
    }

    private GhAdverseEvent buildAdverseEvent(Patient patient, EltmFormSubmission submission, JsonNode data) {
        List<String> local = new ArrayList<>();
        List<String> systemic = new ArrayList<>();
        List<String> endocrine = new ArrayList<>();
        List<String> neuro = new ArrayList<>();
        List<String> skin = new ArrayList<>();
        List<String> rare = new ArrayList<>();
        String severity = null;
        String causality = null;
        String outcome = null;
        String doseAdjustment = null;
        String intervention = null;

        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            List<String> selected = selectedLabels(entry.getValue());
            if (selected.isEmpty()) continue;
            Set<String> options = optionLabels(entry.getValue());
            Set<String> all = options.isEmpty() ? new HashSet<>(selected) : options;
            if (all.containsAll(SEVERITY_LABELS) || intersects(all, SEVERITY_LABELS)) {
                severity = firstMatch(selected, SEVERITY_LABELS);
                if (severity != null) continue;
            }
            if (all.containsAll(CAUSALITY_LABELS) || intersects(all, CAUSALITY_LABELS)) {
                causality = firstMatch(selected, CAUSALITY_LABELS);
                if (causality != null) continue;
            }
            if (all.containsAll(OUTCOME_LABELS) || intersects(all, OUTCOME_LABELS)) {
                outcome = firstMatch(selected, OUTCOME_LABELS);
                if (outcome != null) continue;
            }
            if (all.containsAll(DOSE_ADJUST_LABELS) || intersects(all, DOSE_ADJUST_LABELS)) {
                doseAdjustment = firstMatch(selected, DOSE_ADJUST_LABELS);
                if (doseAdjustment != null) continue;
            }
            if (all.containsAll(INTERVENTION_LABELS) || intersects(all, INTERVENTION_LABELS)) {
                intervention = firstMatch(selected, INTERVENTION_LABELS);
                if (intervention != null) continue;
            }
            for (String label : selected) {
                if (isGenericOption(label)) continue;
                if (LOCAL_LABELS.contains(label)) local.add(label);
                else if (SYSTEMIC_LABELS.contains(label)) systemic.add(label);
                else if (ENDOCRINE_LABELS.contains(label)) endocrine.add(label);
                else if (NEURO_LABELS.contains(label)) neuro.add(label);
                else if (SKIN_LABELS.contains(label)) skin.add(label);
                else if (!"其他".equals(label)) rare.add(label);
            }
        }

        List<String> texts = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            JsonNode node = entry.getValue();
            if (!node.isValueNode() || node.isNull()) continue;
            String text = node.asText().trim();
            if (text.length() < 2 || DATE_PATTERN.matcher(text).matches() || isNumber(text)) continue;
            if (Set.of("无", "有", "是", "否", "不详").contains(text)) continue;
            texts.add(text);
        }
        String symptoms = null;
        String cause = null;
        String measures = null;
        for (String text : texts) {
            if (text.contains("采取") || text.contains("处理") || text.contains("就医")
                    || text.contains("停药") || text.contains("减量") || text.contains("住院")) {
                measures = append(measures, text);
            } else if (text.contains("原因") || text.contains("诱因") || text.contains("不清楚")) {
                cause = append(cause, text);
            } else {
                symptoms = append(symptoms, text);
            }
        }

        if (severity == null && causality == null && outcome == null
                && local.isEmpty() && systemic.isEmpty() && endocrine.isEmpty()
                && neuro.isEmpty() && skin.isEmpty() && rare.isEmpty()
                && symptoms == null && cause == null && measures == null) {
            return null;
        }

        GhAdverseEvent event = new GhAdverseEvent();
        event.setPatientId(patient.getId());
        event.setOccurrenceDate(earliestDate(data, submission.getSubmittedAt()));
        event.setSymptoms(symptoms);
        event.setCause(cause);
        event.setMeasuresTaken(measures);
        event.setSeverity(severity);
        event.setGhCausality(causality);
        event.setLocalReactions(join(local));
        event.setSystemicReactions(join(systemic));
        event.setEndocrineReactions(join(endocrine));
        event.setNeuroReactions(join(neuro));
        event.setSkinReactions(join(skin));
        event.setOtherRareReaction(join(rare));
        event.setMedicalIntervention(intervention);
        event.setGhDoseAdjustment(doseAdjustment);
        event.setOutcome(outcome);
        event.setDataSource(SOURCE_ELTM);
        event.setSourceFormDataId(submission.getFormDataId());
        event.setCreateTime(LocalDateTime.now());
        event.setUpdateTime(LocalDateTime.now());
        event.setIsDeleted(0);
        return event;
    }

    private String firstMatch(List<String> selected, Set<String> allowed) {
        for (String value : selected) {
            if (allowed.contains(value)) return value;
        }
        return null;
    }

    private boolean intersects(Set<String> values, Set<String> allowed) {
        for (String value : values) {
            if (allowed.contains(value)) return true;
        }
        return false;
    }

    private boolean isGenericOption(String label) {
        return label == null || label.isBlank()
                || Set.of("是", "否", "无", "有", "不详", "未查", "其他").contains(label)
                || label.startsWith("选项");
    }

    // ==================== 知情同意书上传 -> 知情同意书 ====================

    private int projectConsentUploads(Patient patient) {
        List<EltmFormSubmission> submissions = loadSubmissions(patient.getId(), CONSENT_FORMS);
        int created = 0;
        for (EltmFormSubmission submission : submissions) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            int index = 0;
            for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
                if (!entry.getKey().startsWith("image_upload")) continue;
                JsonNode files = entry.getValue();
                if (!files.isArray()) continue;
                for (JsonNode file : files) {
                    if (!file.isObject() || !file.hasNonNull("url")) continue;
                    String url = file.get("url").asText("");
                    if (url.isBlank()) continue;
                    String name = file.hasNonNull("name") ? file.get("name").asText() : "知情同意书";
                    String externalFileId = submission.getFormDataId() + "_" + index++;
                    if (consentExists(externalFileId)) continue;
                    InformedConsent consent = new InformedConsent();
                    consent.setPatientId(patient.getId());
                    consent.setExternalFileId(externalFileId);
                    consent.setFileName(name);
                    consent.setOssUrl(url);
                    consent.setSignTime(submission.getSubmittedAt());
                    consent.setPatientName(patient.getName());
                    consent.setDoctorName(patient.getDoctorName());
                    consent.setExternalPatientId(patient.getExternalId());
                    consent.setSource("ELTM_FORM_UPLOAD");
                    consent.setSignStatus("uploaded");
                    consent.setCreateTime(LocalDateTime.now());
                    consent.setSyncTime(LocalDateTime.now());
                    consent.setIsDeleted(0);
                    informedConsentMapper.insert(consent);
                    created++;
                }
            }
        }
        return created;
    }

    private boolean consentExists(String externalFileId) {
        return informedConsentMapper.selectCount(new LambdaQueryWrapper<InformedConsent>()
                .eq(InformedConsent::getExternalFileId, externalFileId)) > 0;
    }

    // ==================== 其他结构化检查 ====================

    private int projectStructuredExams(Patient patient) {
        int changed = 0;
        boolean dirty = false;

        // 骨龄 X 线
        for (EltmFormSubmission submission : loadSubmissions(patient.getId(), Set.of("其他检查（骨龄X线诊断）"))) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            List<Double> numbers = new ArrayList<>();
            for (Double value : numericValues(data).values()) {
                if (value != null && value >= 0 && value <= 30) numbers.add(value);
            }
            if (!numbers.isEmpty() && blank(patient.getRboneAge())) {
                patient.setRboneAge(trim(numbers.get(0), 1));
                dirty = true;
                if (numbers.size() > 1 && !numbers.get(1).equals(numbers.get(0)) && blank(patient.getCboneAge())) {
                    patient.setCboneAge(trim(numbers.get(1), 1));
                }
                break;
            }
        }

        // 垂体 MRI/CT
        for (EltmFormSubmission submission : loadSubmissions(patient.getId(), Set.of("其他检查（垂体MRI/CT）"))) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            if (!blank(patient.getPituitaryMri())) break;
            List<String> labels = new ArrayList<>();
            for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
                labels.addAll(selectedLabels(entry.getValue()));
            }
            String text = joinTexts(data, 200);
            String summary = String.join("；", new LinkedHashSet<>(labels));
            if (text != null) summary = summary.isBlank() ? text : summary + "；" + text;
            if (!summary.isBlank()) {
                patient.setPituitaryMri(summary);
                dirty = true;
            }
            break;
        }

        if (dirty) {
            patientMapper.updateById(patient);
            changed++;
        }
        return changed;
    }

    private int projectDiagnosis(Patient patient) {
        int changed = 0;
        EltmCase eltmCase = null;

        for (EltmFormSubmission submission : loadSubmissions(patient.getId(), Set.of("主要转归和诊断"))) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            String text = joinTexts(data, 1000);
            if (blank(text)) continue;
            if (eltmCase == null) eltmCase = findOrCreateEltmCase(patient.getId());
            if (blank(eltmCase.getDiagnosis())) {
                eltmCase.setDiagnosis(text);
                changed++;
            } else if (blank(eltmCase.getTreatmentPlan())) {
                eltmCase.setTreatmentPlan(text);
                changed++;
            }
        }

        for (EltmFormSubmission submission : loadSubmissions(patient.getId(), Set.of("术后辅助治疗"))) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            String text = joinTexts(data, 1000);
            if (blank(text)) continue;
            if (eltmCase == null) eltmCase = findOrCreateEltmCase(patient.getId());
            if (blank(eltmCase.getTreatmentPlan())) {
                eltmCase.setTreatmentPlan(text);
                changed++;
            }
        }

        if (eltmCase != null && changed > 0) {
            eltmCase.setUpdateTime(LocalDateTime.now());
            if (eltmCase.getId() == null) {
                eltmCaseMapper.insert(eltmCase);
            } else {
                eltmCaseMapper.updateById(eltmCase);
            }
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
        }
        return entity;
    }

    // ==================== 家族史 JSON 合并 ====================

    /**
     * 合并父母身高/姓名/出生年到 Patient.familyMembers JSON
     */
    public boolean mergeFamily(Patient patient, Map<String, String> family) {
        if (family == null || family.values().stream().allMatch(this::blank)) {
            return false;
        }
        List<Map<String, Object>> members = new ArrayList<>();
        if (!blank(patient.getFamilyMembers())) {
            try {
                JsonNode array = objectMapper.readTree(patient.getFamilyMembers());
                if (array.isArray()) {
                    for (JsonNode node : array) {
                        members.add(objectMapper.convertValue(node, Map.class));
                    }
                }
            } catch (Exception e) {
                log.warn("解析患者家族史 JSON 失败 patientId={}", patient.getId(), e);
            }
        }
        Map<String, Object> father = findOrCreateMember(members, "父亲", "1");
        Map<String, Object> mother = findOrCreateMember(members, "母亲", "2");
        boolean changed = false;
        changed |= putIfBlank(father, "height", family.get("fatherHeight"));
        changed |= putIfBlank(mother, "height", family.get("motherHeight"));
        changed |= putNote(father, family.get("fatherName"));
        changed |= putNote(mother, family.get("motherName"));
        changed |= putIfBlank(father, "birthYear", extractYear(family.get("fatherBirthYear")));
        changed |= putIfBlank(mother, "birthYear", extractYear(family.get("motherBirthYear")));
        if (changed) {
            try {
                patient.setFamilyMembers(objectMapper.writeValueAsString(members));
            } catch (Exception e) {
                log.warn("序列化患者家族史 JSON 失败 patientId={}", patient.getId(), e);
                return false;
            }
        }
        return changed;
    }

    private Map<String, Object> findOrCreateMember(List<Map<String, Object>> members, String relationship, String sex) {
        for (Map<String, Object> member : members) {
            if (relationship.equals(String.valueOf(member.get("relationship")))) {
                return member;
            }
        }
        Map<String, Object> member = new LinkedHashMap<>();
        member.put("relationship", relationship);
        member.put("sex", sex);
        member.put("birthYear", "");
        member.put("height", "");
        member.put("weight", "");
        member.put("isAffected", "0");
        member.put("isDeceased", "0");
        member.put("generation", 0);
        member.put("hasSimilarDisease", "否");
        member.put("notes", "");
        member.put("linkedPatientId", null);
        member.put("linkedMedrecNum", "");
        member.put("linkedDiseaseType", "");
        members.add(member);
        return member;
    }

    private boolean putIfBlank(Map<String, Object> map, String key, String value) {
        if (blank(value)) return false;
        Object current = map.get(key);
        if (current != null && !String.valueOf(current).isBlank()) return false;
        map.put(key, value);
        return true;
    }

    private boolean putNote(Map<String, Object> map, String name) {
        if (blank(name)) return false;
        Object current = map.get("notes");
        String notes = current == null ? "" : String.valueOf(current);
        if (notes.contains("姓名：")) return false;
        map.put("notes", notes.isBlank() ? "姓名：" + name : notes + "；姓名：" + name);
        return true;
    }

    private String extractYear(String value) {
        if (blank(value)) return null;
        Matcher matcher = YEAR_PATTERN.matcher(value);
        return matcher.find() ? matcher.group(1) : value;
    }

    // ==================== 通用工具 ====================

    private List<EltmFormSubmission> loadSubmissions(Long patientId, Collection<String> formNames) {
        return formSubmissionMapper.selectList(new LambdaQueryWrapper<EltmFormSubmission>()
                .eq(EltmFormSubmission::getPatientId, patientId)
                .in(EltmFormSubmission::getFormName, formNames)
                .isNotNull(EltmFormSubmission::getOriginalData)
                .orderByDesc(EltmFormSubmission::getSubmittedAt)
                .orderByDesc(EltmFormSubmission::getId));
    }

    private JsonNode parse(String json) {
        if (blank(json)) return null;
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map.Entry<String, JsonNode>> dataFields(JsonNode data) {
        List<Map.Entry<String, JsonNode>> list = new ArrayList<>();
        if (data != null && data.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext(); ) {
                list.add(it.next());
            }
        }
        return list;
    }

    private String textOf(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isValueNode()) {
            String text = node.asText().trim();
            return text.isBlank() ? null : text;
        }
        List<String> selected = selectedLabels(node);
        return selected.isEmpty() ? null : selected.get(0);
    }

    private List<String> selectedLabels(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node == null || node.isNull()) return result;
        if (node.isValueNode()) {
            String text = node.asText().trim();
            if (!text.isBlank()) result.add(text);
            return result;
        }
        if (!node.isObject() || !node.has("value")) return result;
        JsonNode value = node.get("value");
        JsonNode labels = node.get("labelList");
        if (value.isArray()) {
            for (JsonNode item : value) {
                String key = item.asText();
                result.add(labels != null && labels.has(key) ? labels.get(key).asText() : key);
            }
        } else if (!value.isNull()) {
            String key = value.asText();
            if (!key.isBlank()) {
                result.add(labels != null && labels.has(key) ? labels.get(key).asText() : key);
            }
        }
        return result;
    }

    private Set<String> optionLabels(JsonNode node) {
        Set<String> result = new LinkedHashSet<>();
        if (node == null || !node.isObject() || !node.has("labelList")) return result;
        JsonNode labels = node.get("labelList");
        if (labels.isObject()) {
            for (Iterator<JsonNode> it = labels.elements(); it.hasNext(); ) {
                JsonNode value = it.next();
                if (value.isValueNode() && !value.asText().isBlank()) {
                    result.add(value.asText());
                }
            }
        }
        return result;
    }

    private Map<String, Double> numericValues(JsonNode data) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            JsonNode node = entry.getValue();
            if (node.isNumber()) {
                result.put(entry.getKey(), node.asDouble());
            } else if (node.isValueNode() && node.isTextual()) {
                String text = node.asText().trim();
                if (text.matches("-?\\d+(\\.\\d+)?")) {
                    try {
                        result.put(entry.getKey(), Double.parseDouble(text));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return result;
    }

    private List<String> imageNames(JsonNode data) {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : dataFields(data)) {
            if (!entry.getKey().startsWith("image_upload")) continue;
            JsonNode node = entry.getValue();
            if (!node.isArray()) continue;
            for (JsonNode file : node) {
                String name = file.hasNonNull("name") ? file.get("name").asText() : null;
                if (name != null && !name.isBlank()) names.add(name);
            }
        }
        return names;
    }

    private LocalDateTime latestDate(JsonNode data, LocalDateTime fallback) {
        LocalDate latest = null;
        for (String text : textValues(data)) {
            LocalDate date = parseDate(text);
            if (date != null && (latest == null || date.isAfter(latest))) latest = date;
        }
        return latest != null ? latest.atStartOfDay() : fallback;
    }

    private LocalDate earliestDate(JsonNode data, LocalDateTime fallback) {
        LocalDate earliest = null;
        for (String text : textValues(data)) {
            LocalDate date = parseDate(text);
            if (date != null && (earliest == null || date.isBefore(earliest))) earliest = date;
        }
        return earliest != null ? earliest : (fallback == null ? null : fallback.toLocalDate());
    }

    private List<String> textValues(JsonNode data) {
        return new ArrayList<>(collectTextsToList(data));
    }

    private Set<String> collectTextsToList(JsonNode data) {
        Set<String> out = new LinkedHashSet<>();
        collectTexts(data, out);
        return out;
    }

    private LocalDate parseDate(String text) {
        if (text == null) return null;
        Matcher matcher = DATE_PATTERN.matcher(text);
        if (!matcher.find()) return null;
        try {
            return LocalDate.of(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isNumber(String text) {
        return text != null && text.matches("-?\\d+(\\.\\d+)?");
    }

    private String join(List<String> values) {
        if (values == null || values.isEmpty()) return null;
        return String.join(",", new LinkedHashSet<>(values));
    }

    private String append(String current, String text) {
        return current == null || current.isBlank() ? text : current + "；" + text;
    }

    private String joinTexts(JsonNode data, int maxLength) {
        LinkedHashSet<String> texts = new LinkedHashSet<>();
        collectTexts(data, texts);
        texts.removeIf(t -> t.length() < 2 || isNumber(t) || DATE_PATTERN.matcher(t).matches());
        if (texts.isEmpty()) return null;
        String joined = String.join("；", texts);
        return joined.length() > maxLength ? joined.substring(0, maxLength) : joined;
    }

    private String trim(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
