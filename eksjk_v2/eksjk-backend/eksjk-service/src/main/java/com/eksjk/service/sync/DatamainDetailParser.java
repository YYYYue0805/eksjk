package com.eksjk.service.sync;

import com.eksjk.model.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * cxList 明细列解析器
 *
 * <p>输入为列 55-65 已解析的 JsonNode，输出对应的实体列表。</p>
 *
 * @author eksjk
 */
@Slf4j
@Component
public class DatamainDetailParser {

    public List<EltmHospitalBind> parseHospitalBinds(CxListRow row, Long patientId) {
        List<EltmHospitalBind> list = new ArrayList<>();
        forEachItem(row.json(55), item -> {
            EltmHospitalBind entity = new EltmHospitalBind();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setExternalDoctorId(DatamainParseUtil.text(item.get("doctor_id")));
            entity.setDoctorName(DatamainParseUtil.text(item.get("doctor_name")));
            entity.setExternalHospitalId(DatamainParseUtil.text(item.get("hospital_id")));
            entity.setHospitalName(DatamainParseUtil.text(item.get("hospital_name")));
            entity.setProvince(DatamainParseUtil.text(item.get("province")));
            entity.setCity(DatamainParseUtil.text(item.get("city")));
            entity.setDistrict(DatamainParseUtil.text(item.get("district")));
            entity.setBindSource(DatamainParseUtil.text(item.get("assignment_source")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<EltmProject> parseProjects(CxListRow row, Long patientId) {
        List<EltmProject> list = new ArrayList<>();
        forEachItem(row.json(56), item -> {
            EltmProject entity = new EltmProject();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setExternalProjectId(DatamainParseUtil.text(item.get("project_id")));
            entity.setProjectName(DatamainParseUtil.text(item.get("project_name")));
            entity.setRelationSource(DatamainParseUtil.text(item.get("relation_sources")));
            entity.setFirstRelationTime(DatamainParseUtil.parseDateTime(item.get("first_relation_time")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public FormParseResult parseForms(CxListRow row, Long patientId) {
        FormParseResult result = new FormParseResult();
        forEachItem(row.json(57), item -> {
            EltmFormSubmission submission = new EltmFormSubmission();
            submission.setPatientId(patientId);
            submission.setExternalPatientId(row.id());
            submission.setFormDataId(DatamainParseUtil.longValue(item.get("form_data_id")));
            submission.setFormKey(DatamainParseUtil.text(item.get("form_key")));
            submission.setFormName(DatamainParseUtil.text(item.get("form_name")));
            submission.setFormType(DatamainParseUtil.text(item.get("form_type")));
            submission.setProjectId(DatamainParseUtil.text(item.get("project_id")));
            submission.setProjectName(DatamainParseUtil.text(item.get("project_name")));
            submission.setSourceType(DatamainParseUtil.text(item.get("source_type")));
            submission.setSerialNumber(DatamainParseUtil.intValue(item.get("serial_number")));
            submission.setToPatientId(DatamainParseUtil.longValue(item.get("to_patient_id")));
            submission.setPatientPlanId(DatamainParseUtil.longValue(item.get("patient_plan_id")));
            submission.setThirdPatientId(DatamainParseUtil.longValue(item.get("third_patient_id")));
            submission.setSubmittedAt(DatamainParseUtil.parseDateTime(item.get("submitted_at")));
            submission.setCompleteTime(DatamainParseUtil.longValue(item.get("complete_time")));
            submission.setCompletePersonType(DatamainParseUtil.intValue(item.get("complete_person_type")));
            submission.setScore(DatamainParseUtil.text(item.get("score")));
            submission.setEvaluationResult(DatamainParseUtil.text(item.get("evaluation_result")));
            submission.setResultDescription(DatamainParseUtil.text(item.get("result_description")));
            submission.setLatitudeComputeScore(DatamainParseUtil.text(item.get("latitude_compute_score")));
            submission.setOriginalData(item.hasNonNull("original_data") ? item.get("original_data").toString() : null);
            submission.setRawJson(item.toString());
            submission.setSyncTime(LocalDateTime.now());
            result.submissions.add(submission);

            JsonNode originalData = item.get("original_data");
            if (submission.getFormDataId() != null && originalData != null && originalData.isObject()) {
                List<EltmFormAnswer> answers = new ArrayList<>();
                originalData.fields().forEachRemaining(entry -> {
                    EltmFormAnswer answer = new EltmFormAnswer();
                    answer.setFieldKey(entry.getKey());
                    JsonNode value = entry.getValue();
                    answer.setFieldValue(value.isValueNode() ? value.asText() : value.toString());
                    answer.setRawJson(value.toString());
                    answers.add(answer);
                });
                result.answers.put(submission.getFormDataId(), answers);
            }
        });
        return result;
    }

    public List<EltmPatientPic> parsePics(CxListRow row, Long patientId) {
        List<EltmPatientPic> list = new ArrayList<>();
        forEachItem(row.json(58), item -> {
            EltmPatientPic entity = new EltmPatientPic();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setPicId(DatamainParseUtil.text(item.get("pic_id")));
            entity.setRecordDate(DatamainParseUtil.parseDateTime(item.get("diagnosis_date")));
            entity.setRecordName(DatamainParseUtil.text(item.get("medical_record_name")));
            entity.setTypeContent(DatamainParseUtil.text(item.get("type_content")));
            entity.setImageUrl(DatamainParseUtil.text(item.get("image_url")));
            entity.setImageType(DatamainParseUtil.text(item.get("image_type")));
            entity.setPicSourceType(DatamainParseUtil.text(item.get("pic_source_type")));
            entity.setRemark(DatamainParseUtil.text(item.get("remark")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public PhysicalParseResult parsePhysical(CxListRow row, Long patientId) {
        PhysicalParseResult result = new PhysicalParseResult();
        Map<String, PhysicalSession> sessions = new LinkedHashMap<>();
        int[] index = {0};
        forEachItem(row.json(59), item -> {
            String measureTimeText = DatamainParseUtil.text(item.get("measure_time"));
            String key = measureTimeText != null ? measureTimeText : "unknown-" + (index[0]++);
            PhysicalSession session = sessions.computeIfAbsent(key, k -> new PhysicalSession());
            session.setMeasureTime(DatamainParseUtil.parseDateTime(measureTimeText));
            if (session.getDataSource() == null) {
                session.setDataSource(DatamainParseUtil.text(item.get("data_source")));
            }
            if (session.getArchivesType() == null) {
                session.setArchivesType(DatamainParseUtil.text(item.get("archives_type")));
            }

            EltmPhysicalItem detail = new EltmPhysicalItem();
            detail.setExternalPatientId(row.id());
            detail.setArchiveId(DatamainParseUtil.longValue(item.get("archive_id")));
            detail.setFormKey(DatamainParseUtil.text(item.get("form_key")));
            detail.setAttributeId(DatamainParseUtil.longValue(item.get("attribute_id")));
            detail.setAttributeCode(DatamainParseUtil.text(item.get("attribute_code")));
            detail.setAttributeValue(DatamainParseUtil.text(item.get("attribute_value")));
            detail.setMeasureTime(DatamainParseUtil.parseDateTime(item.get("measure_time")));
            detail.setDataSource(DatamainParseUtil.text(item.get("data_source")));
            detail.setThreeLevel(DatamainParseUtil.text(item.get("three_level")));
            detail.setArchivesType(DatamainParseUtil.text(item.get("archives_type")));
            detail.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            detail.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            detail.setRawJson(item.toString());
            detail.setSyncTime(LocalDateTime.now());
            session.getItems().add(detail);
        });
        result.sessions.addAll(sessions.values());
        return result;
    }

    public List<EltmAssessmentReport> parseReports(CxListRow row, Long patientId) {
        List<EltmAssessmentReport> list = new ArrayList<>();
        forEachItem(row.json(60), item -> {
            EltmAssessmentReport entity = new EltmAssessmentReport();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setReportId(DatamainParseUtil.longValue(item.get("report_id")));
            entity.setModelId(DatamainParseUtil.longValue(item.get("model_id")));
            entity.setModelName(DatamainParseUtil.text(item.get("model_name")));
            entity.setFormKey(DatamainParseUtil.text(item.get("form_key")));
            entity.setFormName(DatamainParseUtil.text(item.get("form_name")));
            entity.setProjectId(DatamainParseUtil.text(item.get("project_id")));
            entity.setProjectName(DatamainParseUtil.text(item.get("project_name")));
            entity.setUserFormDataId(DatamainParseUtil.longValue(item.get("user_form_data_id")));
            entity.setScore(DatamainParseUtil.text(item.get("score")));
            entity.setTotalScore(DatamainParseUtil.text(item.get("total_score")));
            entity.setEvaluationResult(DatamainParseUtil.text(item.get("evaluation_result")));
            entity.setResultDescription(DatamainParseUtil.text(item.get("result_description")));
            entity.setReportRemarkValue(DatamainParseUtil.text(item.get("report_remark_value")));
            entity.setReportSuggestValue(DatamainParseUtil.text(item.get("report_suggest_value")));
            entity.setBaseCheckDateValue(DatamainParseUtil.parseDateTime(item.get("base_check_date_value")));
            entity.setBaseNextCheckDateValue(DatamainParseUtil.parseDateTime(item.get("base_next_check_date_value")));
            entity.setReportTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<EltmExerciseRecord> parseExercises(CxListRow row, Long patientId) {
        List<EltmExerciseRecord> list = new ArrayList<>();
        forEachItem(row.json(61), item -> {
            EltmExerciseRecord entity = new EltmExerciseRecord();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setRecordId(DatamainParseUtil.longValue(item.get("record_id")));
            entity.setRecordDate(DatamainParseUtil.parseDate(item.get("record_date")));
            entity.setCategoryName(DatamainParseUtil.text(item.get("category_name")));
            entity.setSubcategoryName(DatamainParseUtil.text(item.get("subcategory_name")));
            entity.setExerciseItemId(DatamainParseUtil.longValue(item.get("exercise_item_id")));
            entity.setExerciseName(DatamainParseUtil.text(item.get("exercise_name")));
            entity.setDurationSeconds(DatamainParseUtil.intValue(item.get("duration_seconds")));
            entity.setCaloriesBurned(DatamainParseUtil.decimalValue(item.get("calories_burned")));
            entity.setMetValue(DatamainParseUtil.decimalValue(item.get("met_value")));
            entity.setWeightKg(DatamainParseUtil.decimalValue(item.get("weight_kg")));
            entity.setWeightSource(DatamainParseUtil.intValue(item.get("weight_source")));
            entity.setIntensityLevel(DatamainParseUtil.intValue(item.get("intensity_level")));
            entity.setAgeGroup(DatamainParseUtil.text(item.get("age_group")));
            entity.setBabyAgeDays(DatamainParseUtil.intValue(item.get("baby_age_days")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<EltmExerciseSummary> parseExerciseSummaries(CxListRow row, Long patientId) {
        List<EltmExerciseSummary> list = new ArrayList<>();
        forEachItem(row.json(62), item -> {
            EltmExerciseSummary entity = new EltmExerciseSummary();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setSummaryId(DatamainParseUtil.longValue(item.get("summary_id")));
            entity.setSummaryDate(DatamainParseUtil.parseDate(item.get("summary_date")));
            entity.setTotalCount(DatamainParseUtil.intValue(item.get("total_count")));
            entity.setTotalDurationSeconds(DatamainParseUtil.intValue(item.get("total_duration_seconds")));
            entity.setTotalCalories(DatamainParseUtil.decimalValue(item.get("total_calories")));
            entity.setMvpaMinutes(DatamainParseUtil.intValue(item.get("mvpa_minutes")));
            entity.setExerciseTypes(DatamainParseUtil.text(item.get("exercise_types")));
            entity.setIsTargetReached(DatamainParseUtil.intValue(item.get("is_target_reached")));
            entity.setBabyAgeDays(DatamainParseUtil.intValue(item.get("baby_age_days")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setSummaryJson(item.toString());
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<EltmSleepRecord> parseSleeps(CxListRow row, Long patientId) {
        List<EltmSleepRecord> list = new ArrayList<>();
        forEachItem(row.json(63), item -> {
            EltmSleepRecord entity = new EltmSleepRecord();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setRecordId(DatamainParseUtil.longValue(item.get("record_id")));
            entity.setRecordDate(DatamainParseUtil.parseDate(item.get("record_date")));
            entity.setSleepStartTime(DatamainParseUtil.parseDateTime(item.get("sleep_start_time")));
            entity.setSleepEndTime(DatamainParseUtil.parseDateTime(item.get("sleep_end_time")));
            entity.setSleepDurationHours(DatamainParseUtil.intValue(item.get("sleep_duration_hours")));
            entity.setSleepDurationMinutes(DatamainParseUtil.intValue(item.get("sleep_duration_minutes")));
            entity.setAwakeTimes(DatamainParseUtil.intValue(item.get("awake_times")));
            entity.setSleepWay(DatamainParseUtil.text(item.get("sleep_way")));
            entity.setSleepHabits(DatamainParseUtil.text(item.get("sleep_habits")));
            entity.setSleepPosture(DatamainParseUtil.text(item.get("sleep_posture")));
            entity.setSleepRegularly(DatamainParseUtil.intValue(item.get("sleep_regularly")));
            entity.setSleepEnvIsQuiet(DatamainParseUtil.intValue(item.get("sleep_env_is_quiet")));
            entity.setAgeGroup(DatamainParseUtil.text(item.get("age_group")));
            entity.setBabyAgeDays(DatamainParseUtil.intValue(item.get("baby_age_days")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<EltmSleepSummary> parseSleepSummaries(CxListRow row, Long patientId) {
        List<EltmSleepSummary> list = new ArrayList<>();
        forEachItem(row.json(64), item -> {
            EltmSleepSummary entity = new EltmSleepSummary();
            entity.setPatientId(patientId);
            entity.setExternalPatientId(row.id());
            entity.setSummaryId(DatamainParseUtil.longValue(item.get("summary_id")));
            entity.setSummaryDate(DatamainParseUtil.parseDate(item.get("summary_date")));
            entity.setTotalDurationHours(DatamainParseUtil.decimalValue(item.get("total_duration_hours")));
            entity.setIsTargetReached(DatamainParseUtil.intValue(item.get("is_target_reached")));
            entity.setBabyAgeDays(DatamainParseUtil.intValue(item.get("baby_age_days")));
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setSummaryJson(item.toString());
            entity.setRawJson(item.toString());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    public List<InformedConsent> parseConsents(CxListRow row, Long patientId, String patientName, String eltmId) {
        List<InformedConsent> list = new ArrayList<>();
        forEachItem(row.json(65), item -> {
            InformedConsent entity = new InformedConsent();
            entity.setPatientId(patientId);
            entity.setEltmId(eltmId != null ? eltmId : String.valueOf(row.id()));
            entity.setPatientName(patientName);
            entity.setExternalPatientId(row.id());
            entity.setExternalDoctorId(DatamainParseUtil.longValue(item.get("doctor_id")));
            entity.setProjectId(DatamainParseUtil.text(item.get("project_id")));
            entity.setGroupId(DatamainParseUtil.text(item.get("group_id")));
            entity.setExternalConsentId(DatamainParseUtil.text(item.get("informed_consent_id")));
            String signId = DatamainParseUtil.text(item.get("sign_id"));
            entity.setSignId(signId != null ? signId
                    : "H-" + DatamainParseUtil.sha256(item.toString()).substring(0, 32));
            entity.setSignStatus(DatamainParseUtil.text(item.get("sign_status")));
            entity.setPatientSignTime(DatamainParseUtil.parseDateTime(item.get("patient_sign_time")));
            entity.setGuardianSignTime(DatamainParseUtil.parseDateTime(item.get("guardian_sign_time")));
            entity.setDoctorSignTime(DatamainParseUtil.parseDateTime(item.get("doctor_sign_time")));
            entity.setSignTime(entity.getPatientSignTime() != null
                    ? entity.getPatientSignTime() : entity.getDoctorSignTime());
            entity.setSource("ELTM");
            entity.setExternalCreateTime(DatamainParseUtil.parseDateTime(item.get("create_time")));
            entity.setExternalUpdateTime(DatamainParseUtil.parseDateTime(item.get("update_time")));
            entity.setRawJson(item.toString());
            entity.setCreateTime(LocalDateTime.now());
            entity.setSyncTime(LocalDateTime.now());
            list.add(entity);
        });
        return list;
    }

    private void forEachItem(JsonNode node, Consumer<JsonNode> consumer) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isArray()) {
            node.forEach(item -> {
                if (item.isObject()) {
                    consumer.accept(item);
                }
            });
        } else if (node.isObject()) {
            consumer.accept(node);
        }
    }

    public static class FormParseResult {
        private final List<EltmFormSubmission> submissions = new ArrayList<>();
        private final Map<Long, List<EltmFormAnswer>> answers = new HashMap<>();

        public List<EltmFormSubmission> getSubmissions() {
            return submissions;
        }

        public Map<Long, List<EltmFormAnswer>> getAnswers() {
            return answers;
        }
    }

    public static class PhysicalParseResult {
        private final List<PhysicalSession> sessions = new ArrayList<>();

        public List<PhysicalSession> getSessions() {
            return sessions;
        }
    }

    public static class PhysicalSession {
        private LocalDateTime measureTime;
        private String dataSource;
        private String archivesType;
        private final List<EltmPhysicalItem> items = new ArrayList<>();

        public LocalDateTime getMeasureTime() {
            return measureTime;
        }

        public void setMeasureTime(LocalDateTime measureTime) {
            this.measureTime = measureTime;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getArchivesType() {
            return archivesType;
        }

        public void setArchivesType(String archivesType) {
            this.archivesType = archivesType;
        }

        public List<EltmPhysicalItem> getItems() {
            return items;
        }
    }
}
