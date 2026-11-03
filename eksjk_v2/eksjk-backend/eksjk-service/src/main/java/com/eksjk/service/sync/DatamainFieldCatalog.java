package com.eksjk.service.sync;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * datamain 接口字段目录
 *
 * <p>集中维护已知字段集合，采集器据此识别接口新增/未知字段。</p>
 *
 * @author eksjk
 */
public final class DatamainFieldCatalog {

    public static final int[] UNKNOWN_CX_COLUMNS = {22, 23, 26, 28, 36, 40, 44, 48};

    public static final Map<Integer, String> CX_COLUMN_LABELS = new LinkedHashMap<>();

    static {
        CX_COLUMN_LABELS.put(22, "列 22（疑似旧系统患者/用户 ID，含义待确认）");
        CX_COLUMN_LABELS.put(23, "列 23（疑似旧系统患者/用户 ID，含义待确认）");
        CX_COLUMN_LABELS.put(26, "列 26（数量类字段，含义待确认）");
        CX_COLUMN_LABELS.put(28, "列 28（数量类字段，含义待确认）");
        CX_COLUMN_LABELS.put(36, "列 36（数量类字段，含义待确认）");
        CX_COLUMN_LABELS.put(40, "列 40（数量类字段，含义待确认）");
        CX_COLUMN_LABELS.put(44, "列 44（数量类字段，含义待确认）");
        CX_COLUMN_LABELS.put(48, "列 48（数量类字段，含义待确认）");
    }

    public static final Set<String> EXTEND_DATA_KEYS = Set.of(
            "name", "sex", "registerSource", "birthday", "document_types", "age",
            "MTP", "MTPTR", "remarks", "height", "fht", "mht", "nation", "class");

    public static final Set<String> HOSPITAL_BIND_KEYS = Set.of(
            "doctor_id", "doctor_name", "hospital_id", "hospital_name",
            "province", "city", "district", "assignment_source");

    public static final Set<String> PROJECT_KEYS = Set.of(
            "project_id", "project_name", "relation_sources", "first_relation_time");

    public static final Set<String> FORM_KEYS = Set.of(
            "form_data_id", "form_key", "form_name", "form_type", "project_id", "project_name",
            "source_type", "serial_number", "to_patient_id", "patient_plan_id", "third_patient_id",
            "submitted_at", "complete_time", "complete_person_type", "score", "evaluation_result",
            "result_description", "latitude_compute_score", "original_data");

    public static final Set<String> PIC_KEYS = Set.of(
            "pic_id", "image_url", "image_type", "diagnosis_date", "medical_record_name",
            "remark", "pic_source_type", "type_content", "create_time", "update_time");

    public static final Set<String> PHYSICAL_KEYS = Set.of(
            "archive_id", "form_key", "attribute_id", "attribute_code", "attribute_value",
            "measure_time", "data_source", "three_level", "archives_type",
            "create_time", "update_time");

    public static final Set<String> REPORT_KEYS = Set.of(
            "report_id", "model_id", "model_name", "form_key", "form_name", "project_id",
            "project_name", "user_form_data_id", "score", "total_score", "evaluation_result",
            "result_description", "report_remark_value", "report_suggest_value",
            "base_check_date_value", "base_next_check_date_value", "create_time", "update_time");

    public static final Set<String> EXERCISE_KEYS = Set.of(
            "record_id", "exercise_item_id", "exercise_name", "category_name", "subcategory_name",
            "duration_seconds", "calories_burned", "met_value", "weight_kg", "weight_source",
            "intensity_level", "age_group", "baby_age_days", "record_date", "create_time", "update_time");

    public static final Set<String> EXERCISE_SUMMARY_KEYS = Set.of(
            "summary_id", "summary_date", "total_count", "total_duration_seconds", "total_calories",
            "mvpa_minutes", "exercise_types", "is_target_reached", "baby_age_days",
            "create_time", "update_time");

    public static final Set<String> SLEEP_KEYS = Set.of(
            "record_id", "record_date", "sleep_start_time", "sleep_end_time",
            "sleep_duration_hours", "sleep_duration_minutes", "awake_times", "sleep_way",
            "sleep_habits", "sleep_posture", "sleep_regularly", "sleep_env_is_quiet",
            "age_group", "baby_age_days", "create_time", "update_time");

    public static final Set<String> SLEEP_SUMMARY_KEYS = Set.of(
            "summary_id", "summary_date", "total_duration_hours", "is_target_reached",
            "baby_age_days", "create_time", "update_time");

    public static final Set<String> CONSENT_KEYS = Set.of(
            "sign_id", "project_id", "group_id", "informed_consent_id", "doctor_id",
            "sign_status", "doctor_sign_time", "patient_sign_time", "guardian_sign_time",
            "create_time", "update_time");

    public static final Set<String> KNOWN_PHYSICAL_ATTRIBUTES = Set.of(
            "height:Tab", "weight:Tab", "bmi", "fatherHeight", "motherHeight",
            "expectHeight", "heightP", "standardDeviation");

    public static final Map<Integer, String> UNKNOWN_GETTP_COLUMNS = new LinkedHashMap<>();

    static {
        UNKNOWN_GETTP_COLUMNS.put(2, "待确认（长整型 ID）");
        UNKNOWN_GETTP_COLUMNS.put(8, "疑似上传医生 ID");
        UNKNOWN_GETTP_COLUMNS.put(9, "疑似上传医生 ID");
        UNKNOWN_GETTP_COLUMNS.put(12, "待确认（0/1/3 字典）");
        UNKNOWN_GETTP_COLUMNS.put(13, "待确认（关联 ID）");
        UNKNOWN_GETTP_COLUMNS.put(15, "疑似 remark（字段名待确认）");
    }

    public static final Map<Integer, String> UNKNOWN_GETZQ_COLUMNS = new LinkedHashMap<>();

    static {
        UNKNOWN_GETZQ_COLUMNS.put(8, "医生 ID（语义待确认）");
        UNKNOWN_GETZQ_COLUMNS.put(21, "PDF 字段（语义与列序待确认）");
        UNKNOWN_GETZQ_COLUMNS.put(23, "PDF 字段（语义与列序待确认）");
    }

    private DatamainFieldCatalog() {
    }
}
