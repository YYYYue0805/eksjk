-- ============================================================
-- 31-datamain-sync.sql
-- datamain 第三方数据同步：患者表扩展 + 16 张新表 + 知情同意扩展
-- 编写日期：2026-10-17
-- ============================================================

-- ------------------------------------------------------------
-- 1. 患者主表扩展
-- 注意：datamain_patient 已有 210 列，接近 InnoDB 行大小上限，
--       只允许新增 external_id 一个列；其余同步字段放在 eltm_patient_raw。
-- ------------------------------------------------------------
ALTER TABLE datamain_patient
    ADD COLUMN external_id BIGINT NULL COMMENT 'datamain 外部患者主键(cxList 列0)',
    ADD UNIQUE KEY uk_patient_external_id (external_id);

-- 外部数据存在 1970 年前的出生日期，TIMESTAMP 无法存储，改为 DATETIME
ALTER TABLE datamain_patient
    MODIFY COLUMN birth_time DATETIME NULL;

-- ------------------------------------------------------------
-- 2. 外部患者扩展属性（1:1）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_patient_profile (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id             BIGINT NOT NULL,
    external_id            BIGINT NOT NULL,
    register_source        VARCHAR(32),
    id_type                VARCHAR(32),
    external_created_at    DATETIME,
    external_doctor_id     VARCHAR(64),
    external_hospital_id   VARCHAR(64),
    first_doctor_names     VARCHAR(500),
    first_doctor_ids       VARCHAR(500),
    first_hospital_names   VARCHAR(500),
    first_hospital_ids     VARCHAR(500),
    assign_type            VARCHAR(100),
    project_count          INT,
    project_name           VARCHAR(500),
    patient_type           VARCHAR(32),
    data_scope             VARCHAR(64),
    form_count             INT,
    pic_count              INT,
    physical_sign_count    INT,
    report_count           INT,
    exercise_flag          VARCHAR(10),
    sleep_flag             VARCHAR(10),
    consent_count          INT,
    latest_form_time       DATETIME,
    latest_form_update     DATETIME,
    latest_form_name       VARCHAR(500),
    latest_pic_time        DATETIME,
    latest_pic_update      DATETIME,
    latest_sign_time       DATETIME,
    latest_sign_update     DATETIME,
    latest_report_time     DATETIME,
    latest_report_update   DATETIME,
    latest_exercise_time   DATETIME,
    latest_exercise_update DATETIME,
    latest_sleep_time      DATETIME,
    latest_sleep_update    DATETIME,
    latest_consent_time    DATETIME,
    latest_consent_update  DATETIME,
    sync_time              DATETIME,
    create_time            DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_profile_patient_id (patient_id),
    UNIQUE KEY uk_profile_external_id (external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 外部患者扩展属性';

-- ------------------------------------------------------------
-- 3. 66 列原始快照（1:1）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_patient_raw (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    external_id         BIGINT NOT NULL,
    external_updated_at DATETIME,
    row_hash            VARCHAR(64),
    raw_json            LONGTEXT,
    sync_time           DATETIME,
    UNIQUE KEY uk_raw_patient_id (patient_id),
    UNIQUE KEY uk_raw_external_id (external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 患者原始行快照';

-- ------------------------------------------------------------
-- 4. 医患-医院绑定
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_hospital_bind (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id           BIGINT NOT NULL,
    external_patient_id  BIGINT NOT NULL,
    external_doctor_id   VARCHAR(64),
    doctor_name          VARCHAR(100),
    external_hospital_id VARCHAR(64),
    hospital_name        VARCHAR(200),
    province             VARCHAR(50),
    city                 VARCHAR(50),
    district             VARCHAR(50),
    bind_source          VARCHAR(50),
    raw_json             LONGTEXT,
    sync_time            DATETIME,
    KEY idx_bind_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 医患医院绑定';

-- ------------------------------------------------------------
-- 5. 所属项目
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_project (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    external_patient_id BIGINT NOT NULL,
    external_project_id VARCHAR(64),
    project_name        VARCHAR(500),
    relation_source     VARCHAR(100),
    first_relation_time DATETIME,
    raw_json            LONGTEXT,
    sync_time           DATETIME,
    KEY idx_project_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 患者所属项目';

-- ------------------------------------------------------------
-- 6. 表单提交
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_form_submission (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id             BIGINT NOT NULL,
    external_patient_id    BIGINT NOT NULL,
    form_data_id           BIGINT NOT NULL,
    form_key               VARCHAR(128),
    form_name              VARCHAR(200),
    form_type              VARCHAR(50),
    project_id             VARCHAR(64),
    project_name           VARCHAR(500),
    source_type            VARCHAR(32),
    serial_number          INT,
    to_patient_id          BIGINT,
    patient_plan_id        BIGINT,
    third_patient_id       BIGINT,
    submitted_at           DATETIME,
    complete_time          BIGINT COMMENT '外部时间值，口径待确认',
    complete_person_type   INT,
    score                  VARCHAR(32),
    evaluation_result      TEXT,
    result_description     TEXT,
    latitude_compute_score LONGTEXT,
    original_data          LONGTEXT COMMENT '原始答题数据 JSON',
    raw_json               LONGTEXT,
    sync_time              DATETIME,
    UNIQUE KEY uk_form_data_id (form_data_id),
    KEY idx_form_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 表单提交';

-- ------------------------------------------------------------
-- 7. 表单答案（尽力拆分）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_form_answer (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    field_key     VARCHAR(128),
    field_label   VARCHAR(200),
    field_value   TEXT,
    raw_json      LONGTEXT,
    KEY idx_answer_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 表单答题明细';

-- ------------------------------------------------------------
-- 8. 病历图片
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_patient_pic (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id           BIGINT NOT NULL,
    external_patient_id  BIGINT NOT NULL,
    pic_id               VARCHAR(64) NOT NULL,
    record_date          DATETIME COMMENT 'pic_json.diagnosis_date / gettpbyid.record_date',
    record_name          VARCHAR(200) COMMENT 'pic_json.medical_record_name / gettpbyid.record_name',
    type_content         VARCHAR(128),
    image_url            VARCHAR(512),
    image_type           VARCHAR(10) COMMENT '0检验/1检查/2影像/3病历/4处方/5其它/6出院小结',
    pic_source_type      VARCHAR(10),
    source               VARCHAR(32),
    remark               TEXT,
    external_create_time DATETIME,
    external_update_time DATETIME,
    raw_json             LONGTEXT,
    sync_time            DATETIME,
    UNIQUE KEY uk_pic_id (pic_id),
    KEY idx_pic_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 病历图片';

-- ------------------------------------------------------------
-- 9. 体征测量记录（EAV 表头）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_physical_record (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    external_patient_id BIGINT NOT NULL,
    measure_time        DATETIME NOT NULL,
    data_source         VARCHAR(32),
    archives_type       VARCHAR(32),
    item_count          INT,
    raw_json            LONGTEXT,
    sync_time           DATETIME,
    UNIQUE KEY uk_physical_patient_time (patient_id, measure_time),
    KEY idx_physical_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 体征测量记录';

-- ------------------------------------------------------------
-- 10. 体征属性明细（EAV 明细）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_physical_item (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id            BIGINT NOT NULL,
    external_patient_id  BIGINT NOT NULL,
    archive_id           BIGINT,
    form_key             VARCHAR(64),
    attribute_id         BIGINT,
    attribute_code       VARCHAR(128),
    attribute_value      TEXT,
    measure_time         DATETIME,
    data_source          VARCHAR(32),
    three_level          VARCHAR(64),
    archives_type        VARCHAR(32),
    external_create_time DATETIME,
    external_update_time DATETIME,
    raw_json             LONGTEXT,
    sync_time            DATETIME,
    UNIQUE KEY uk_physical_archive_id (archive_id),
    KEY idx_physical_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 体征属性明细';

-- ------------------------------------------------------------
-- 11. 测评报告
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_assessment_report (
    id                         BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id                 BIGINT NOT NULL,
    external_patient_id        BIGINT NOT NULL,
    report_id                  BIGINT NOT NULL,
    model_id                   BIGINT,
    model_name                 VARCHAR(200),
    form_key                   VARCHAR(64),
    form_name                  VARCHAR(200),
    project_id                 VARCHAR(64),
    project_name               VARCHAR(500),
    user_form_data_id          BIGINT,
    score                      VARCHAR(32),
    total_score                VARCHAR(32),
    evaluation_result          TEXT,
    result_description         TEXT,
    report_remark_value        TEXT,
    report_suggest_value       TEXT,
    base_check_date_value      DATETIME,
    base_next_check_date_value DATETIME,
    report_time                DATETIME COMMENT 'create_time',
    external_create_time       DATETIME,
    external_update_time       DATETIME,
    raw_json                   LONGTEXT,
    sync_time                  DATETIME,
    UNIQUE KEY uk_report_id (report_id),
    KEY idx_report_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 测评报告';

-- ------------------------------------------------------------
-- 12. 运动记录
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_exercise_record (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id           BIGINT NOT NULL,
    external_patient_id  BIGINT NOT NULL,
    record_id            BIGINT NOT NULL,
    record_date          DATE,
    category_name        VARCHAR(100),
    subcategory_name     VARCHAR(100),
    exercise_item_id     BIGINT,
    exercise_name        VARCHAR(200),
    duration_seconds     INT,
    calories_burned      DECIMAL(10,2),
    met_value            DECIMAL(6,2),
    weight_kg            DECIMAL(6,2),
    weight_source        INT,
    intensity_level      INT,
    age_group            VARCHAR(32),
    baby_age_days        INT,
    external_create_time DATETIME,
    external_update_time DATETIME,
    raw_json             LONGTEXT,
    sync_time            DATETIME,
    UNIQUE KEY uk_exercise_record_id (record_id),
    KEY idx_exercise_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 运动记录';

-- ------------------------------------------------------------
-- 13. 运动汇总
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_exercise_summary (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id             BIGINT NOT NULL,
    external_patient_id    BIGINT NOT NULL,
    summary_id             BIGINT NOT NULL,
    summary_date           DATE,
    total_count            INT,
    total_duration_seconds INT,
    total_calories         DECIMAL(10,2),
    mvpa_minutes           INT,
    exercise_types         TEXT,
    is_target_reached      TINYINT,
    baby_age_days          INT,
    external_create_time   DATETIME,
    external_update_time   DATETIME,
    summary_json           LONGTEXT,
    raw_json               LONGTEXT,
    sync_time              DATETIME,
    UNIQUE KEY uk_exercise_summary_id (summary_id),
    KEY idx_exercise_summary_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 运动汇总';

-- ------------------------------------------------------------
-- 14. 睡眠记录
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_sleep_record (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id             BIGINT NOT NULL,
    external_patient_id    BIGINT NOT NULL,
    record_id              BIGINT NOT NULL,
    record_date            DATE,
    sleep_start_time       DATETIME,
    sleep_end_time         DATETIME,
    sleep_duration_hours   INT,
    sleep_duration_minutes INT,
    awake_times            INT,
    sleep_way              VARCHAR(100),
    sleep_habits           TEXT,
    sleep_posture          VARCHAR(100),
    sleep_regularly        TINYINT,
    sleep_env_is_quiet     TINYINT,
    age_group              VARCHAR(32),
    baby_age_days          INT,
    external_create_time   DATETIME,
    external_update_time   DATETIME,
    raw_json               LONGTEXT,
    sync_time              DATETIME,
    UNIQUE KEY uk_sleep_record_id (record_id),
    KEY idx_sleep_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 睡眠记录';

-- ------------------------------------------------------------
-- 15. 睡眠汇总
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_sleep_summary (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id           BIGINT NOT NULL,
    external_patient_id  BIGINT NOT NULL,
    summary_id           BIGINT NOT NULL,
    summary_date         DATE,
    total_duration_hours DECIMAL(6,2),
    is_target_reached    TINYINT,
    baby_age_days        INT,
    external_create_time DATETIME,
    external_update_time DATETIME,
    summary_json         LONGTEXT,
    raw_json             LONGTEXT,
    sync_time            DATETIME,
    UNIQUE KEY uk_sleep_summary_id (summary_id),
    KEY idx_sleep_summary_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 睡眠汇总';

-- ------------------------------------------------------------
-- 16. 同步日志
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_sync_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sync_type     VARCHAR(20) NOT NULL COMMENT 'FULL / INCREMENTAL',
    trigger_type  VARCHAR(20) NOT NULL COMMENT 'MANUAL / SCHEDULED',
    start_time    DATETIME NOT NULL,
    end_time      DATETIME,
    total_count   INT DEFAULT 0,
    new_count     INT DEFAULT 0,
    updated_count INT DEFAULT 0,
    skipped_count INT DEFAULT 0,
    failed_count  INT DEFAULT 0,
    status        VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    error_message TEXT,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_sync_log_start (start_time),
    KEY idx_sync_log_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 数据同步日志';

-- ------------------------------------------------------------
-- 17. 同步失败明细
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_sync_error (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sync_log_id   BIGINT,
    external_id   BIGINT,
    stage         VARCHAR(50) COMMENT 'PATIENT/FORM/PIC/PHYSICAL/REPORT/EXERCISE/SLEEP/CONSENT',
    error_message TEXT,
    raw_json      LONGTEXT,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_sync_error_log (sync_log_id),
    KEY idx_sync_error_external (external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 同步失败明细';

-- ------------------------------------------------------------
-- 18. 知情同意书表扩展（承载签署记录）
-- ------------------------------------------------------------
ALTER TABLE informed_consent
    MODIFY COLUMN file_name VARCHAR(255) NULL,
    MODIFY COLUMN oss_url VARCHAR(512) NULL,
    ADD COLUMN sign_id               VARCHAR(64)  NULL COMMENT '外部签署记录ID',
    ADD COLUMN external_patient_id   BIGINT       NULL COMMENT '外部患者ID',
    ADD COLUMN external_doctor_id    BIGINT       NULL COMMENT '外部归属医生ID',
    ADD COLUMN project_id            VARCHAR(64)  NULL,
    ADD COLUMN group_id              VARCHAR(64)  NULL,
    ADD COLUMN external_consent_id   VARCHAR(64)  NULL COMMENT '外部知情同意书模板ID',
    ADD COLUMN content               LONGTEXT     NULL COMMENT 'HTML 正文',
    ADD COLUMN sign_doctor_id        VARCHAR(64)  NULL,
    ADD COLUMN patient_sign_image    VARCHAR(512) NULL COMMENT '签名图片地址',
    ADD COLUMN patient_sign_time     DATETIME     NULL,
    ADD COLUMN guardian_sign_image   VARCHAR(512) NULL,
    ADD COLUMN guardian_sign_time    DATETIME     NULL,
    ADD COLUMN doctor_sign_image     VARCHAR(512) NULL,
    ADD COLUMN doctor_sign_time      DATETIME     NULL,
    ADD COLUMN sign_status           VARCHAR(10)  NULL COMMENT '签署状态，字典待确认',
    ADD COLUMN pdf_path              VARCHAR(512) NULL,
    ADD COLUMN pdf_file_name         VARCHAR(255) NULL,
    ADD COLUMN pdf_error_msg         VARCHAR(500) NULL,
    ADD COLUMN pdf_status            VARCHAR(10)  NULL COMMENT '0待生成/1成功/2失败/3跳过',
    ADD COLUMN pdf_retry_count       INT          NULL,
    ADD COLUMN pdf_generate_time     DATETIME     NULL,
    ADD COLUMN raw_json              LONGTEXT     NULL,
    ADD COLUMN external_create_time  DATETIME     NULL,
    ADD COLUMN external_update_time  DATETIME     NULL,
    ADD UNIQUE KEY uk_sign_id (sign_id);
