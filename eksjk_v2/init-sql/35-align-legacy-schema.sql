-- EKSJK staging 旧库结构对齐（V1/V2 混合 -> 当前 V2），2026-11-03
-- 执行环境：RDS eksjk_staging；脚本为一次性迁移，已包含数据表备份
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- ========== 1. 备份有数据的表 ==========
CREATE TABLE `_bak_20261103_datamain_case` LIKE `datamain_case`;
INSERT INTO `_bak_20261103_datamain_case` SELECT * FROM `datamain_case`;
CREATE TABLE `_bak_20261103_datamain_gh_adverse_event` LIKE `datamain_gh_adverse_event`;
INSERT INTO `_bak_20261103_datamain_gh_adverse_event` SELECT * FROM `datamain_gh_adverse_event`;
CREATE TABLE `_bak_20261103_datamain_jzxshort` LIKE `datamain_jzxshort`;
INSERT INTO `_bak_20261103_datamain_jzxshort` SELECT * FROM `datamain_jzxshort`;
CREATE TABLE `_bak_20261103_datamain_mas` LIKE `datamain_mas`;
INSERT INTO `_bak_20261103_datamain_mas` SELECT * FROM `datamain_mas`;
CREATE TABLE `_bak_20261103_datamain_patfoll` LIKE `datamain_patfoll`;
INSERT INTO `_bak_20261103_datamain_patfoll` SELECT * FROM `datamain_patfoll`;
CREATE TABLE `_bak_20261103_datamain_patient` LIKE `datamain_patient`;
INSERT INTO `_bak_20261103_datamain_patient` SELECT * FROM `datamain_patient`;
CREATE TABLE `_bak_20261103_datamain_sexprecocity` LIKE `datamain_sexprecocity`;
INSERT INTO `_bak_20261103_datamain_sexprecocity` SELECT * FROM `datamain_sexprecocity`;
CREATE TABLE `_bak_20261103_datamain_sga` LIKE `datamain_sga`;
INSERT INTO `_bak_20261103_datamain_sga` SELECT * FROM `datamain_sga`;
CREATE TABLE `_bak_20261103_datamain_short` LIKE `datamain_short`;
INSERT INTO `_bak_20261103_datamain_short` SELECT * FROM `datamain_short`;
CREATE TABLE `_bak_20261103_datamain_szfyeltm` LIKE `datamain_szfyeltm`;
INSERT INTO `_bak_20261103_datamain_szfyeltm` SELECT * FROM `datamain_szfyeltm`;
CREATE TABLE `_bak_20261103_file_note` LIKE `file_note`;
INSERT INTO `_bak_20261103_file_note` SELECT * FROM `file_note`;
CREATE TABLE `_bak_20261103_login_unit` LIKE `login_unit`;
INSERT INTO `_bak_20261103_login_unit` SELECT * FROM `login_unit`;
CREATE TABLE `_bak_20261103_login_user` LIKE `login_user`;
INSERT INTO `_bak_20261103_login_user` SELECT * FROM `login_user`;
CREATE TABLE `_bak_20261103_patient_genetics` LIKE `patient_genetics`;
INSERT INTO `_bak_20261103_patient_genetics` SELECT * FROM `patient_genetics`;

-- ========== 2. 归档旧结构表（数据保留在 _bak_* 中） ==========
RENAME TABLE `datamain_masfoll` TO `_bak_20261103_datamain_masfoll`;
RENAME TABLE `informed_consent` TO `_bak_20261103_informed_consent`;
RENAME TABLE `school_cbq` TO `_bak_20261103_school_cbq`;
RENAME TABLE `school_cchkn` TO `_bak_20261103_school_cchkn`;
RENAME TABLE `school_mqzyfs` TO `_bak_20261103_school_mqzyfs`;
RENAME TABLE `school_pmbl` TO `_bak_20261103_school_pmbl`;
RENAME TABLE `school_qzhd` TO `_bak_20261103_school_qzhd`;
RENAME TABLE `school_smxg` TO `_bak_20261103_school_smxg`;
RENAME TABLE `school_sthd` TO `_bak_20261103_school_sthd`;
RENAME TABLE `school_student` TO `_bak_20261103_school_student`;
RENAME TABLE `xcx_chartuser` TO `_bak_20261103_xcx_chartuser`;

-- ========== 3. 按当前 V2 结构重建 ==========
CREATE TABLE IF NOT EXISTS `datamain_masfoll` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mas_id` bigint DEFAULT NULL,
  `is_finalhei` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_per_pre` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `per_pre_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_hyper` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hyper_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hyper_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_gro_hor` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gro_hor_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gro_hor_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_tre_hpy` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tre_hpy_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tre_hpy_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_inc_cor` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inc_cor_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inc_cor_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_int_sur` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_bil_adr` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_bon_pai` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bon_pai_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bon_pai_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hypop` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hypop_sf` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hypop_jc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_ske_sur` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_cafe_spot` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_psy_cou` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sur_sta` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `eltm_assessment_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `report_id` bigint NOT NULL,
  `model_id` bigint DEFAULT NULL,
  `model_name` varchar(200) DEFAULT NULL,
  `form_key` varchar(64) DEFAULT NULL,
  `form_name` varchar(200) DEFAULT NULL,
  `project_id` varchar(64) DEFAULT NULL,
  `project_name` varchar(500) DEFAULT NULL,
  `user_form_data_id` bigint DEFAULT NULL,
  `score` varchar(32) DEFAULT NULL,
  `total_score` varchar(32) DEFAULT NULL,
  `evaluation_result` text,
  `result_description` text,
  `report_remark_value` text,
  `report_suggest_value` text,
  `base_check_date_value` datetime DEFAULT NULL,
  `base_next_check_date_value` datetime DEFAULT NULL,
  `report_time` datetime DEFAULT NULL COMMENT 'create_time',
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_id` (`report_id`),
  KEY `idx_report_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 测评报告';

CREATE TABLE IF NOT EXISTS `eltm_exercise_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `record_id` bigint NOT NULL,
  `record_date` date DEFAULT NULL,
  `category_name` varchar(100) DEFAULT NULL,
  `subcategory_name` varchar(100) DEFAULT NULL,
  `exercise_item_id` bigint DEFAULT NULL,
  `exercise_name` varchar(200) DEFAULT NULL,
  `duration_seconds` int DEFAULT NULL,
  `calories_burned` decimal(10,2) DEFAULT NULL,
  `met_value` decimal(6,2) DEFAULT NULL,
  `weight_kg` decimal(6,2) DEFAULT NULL,
  `weight_source` int DEFAULT NULL,
  `intensity_level` int DEFAULT NULL,
  `age_group` varchar(32) DEFAULT NULL,
  `baby_age_days` int DEFAULT NULL,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exercise_record_id` (`record_id`),
  KEY `idx_exercise_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 运动记录';

CREATE TABLE IF NOT EXISTS `eltm_exercise_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `summary_id` bigint NOT NULL,
  `summary_date` date DEFAULT NULL,
  `total_count` int DEFAULT NULL,
  `total_duration_seconds` int DEFAULT NULL,
  `total_calories` decimal(10,2) DEFAULT NULL,
  `mvpa_minutes` int DEFAULT NULL,
  `exercise_types` text,
  `is_target_reached` tinyint DEFAULT NULL,
  `baby_age_days` int DEFAULT NULL,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `summary_json` longtext,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exercise_summary_id` (`summary_id`),
  KEY `idx_exercise_summary_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 运动汇总';

CREATE TABLE IF NOT EXISTS `eltm_form_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submission_id` bigint NOT NULL,
  `field_key` varchar(128) DEFAULT NULL,
  `field_label` varchar(200) DEFAULT NULL,
  `field_value` text,
  `raw_json` longtext,
  PRIMARY KEY (`id`),
  KEY `idx_answer_submission_id` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 表单答题明细';

CREATE TABLE IF NOT EXISTS `eltm_form_field_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `form_key` varchar(64) NOT NULL COMMENT '外部表单模板 key',
  `form_name` varchar(200) DEFAULT NULL COMMENT '表单名称（便于维护）',
  `field_key` varchar(128) NOT NULL COMMENT '外部答案字段 key',
  `target_scope` varchar(32) NOT NULL COMMENT 'PATIENT/ELTM_CASE/GENETICS/FOLLOWUP/ADVERSE',
  `target_field` varchar(64) NOT NULL COMMENT '目标字段名',
  `value_type` varchar(16) NOT NULL DEFAULT 'RAW' COMMENT 'RAW/LABEL/MULTI_LABEL',
  `transform_rule` varchar(64) DEFAULT NULL COMMENT 'WEIGHT_G_TO_KG/BIRTH_HEIGHT_CM/GEST_WEEK 等',
  `priority` int NOT NULL DEFAULT '0',
  `enabled` tinyint NOT NULL DEFAULT '1',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_form_field` (`form_key`,`field_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 表单字段映射配置';

CREATE TABLE IF NOT EXISTS `eltm_form_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `form_data_id` bigint NOT NULL,
  `form_key` varchar(128) DEFAULT NULL,
  `form_name` varchar(200) DEFAULT NULL,
  `form_type` varchar(50) DEFAULT NULL,
  `project_id` varchar(64) DEFAULT NULL,
  `project_name` varchar(500) DEFAULT NULL,
  `source_type` varchar(32) DEFAULT NULL,
  `serial_number` int DEFAULT NULL,
  `to_patient_id` bigint DEFAULT NULL,
  `patient_plan_id` bigint DEFAULT NULL,
  `third_patient_id` bigint DEFAULT NULL,
  `submitted_at` datetime DEFAULT NULL,
  `complete_time` bigint DEFAULT NULL COMMENT '外部时间值，口径待确认',
  `complete_person_type` int DEFAULT NULL,
  `score` varchar(32) DEFAULT NULL,
  `evaluation_result` text,
  `result_description` text,
  `latitude_compute_score` longtext,
  `original_data` longtext COMMENT '原始答题数据 JSON',
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_form_data_id` (`form_data_id`),
  KEY `idx_form_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 表单提交';

CREATE TABLE IF NOT EXISTS `eltm_hospital_bind` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `external_doctor_id` varchar(64) DEFAULT NULL,
  `doctor_name` varchar(100) DEFAULT NULL,
  `external_hospital_id` varchar(64) DEFAULT NULL,
  `hospital_name` varchar(200) DEFAULT NULL,
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `district` varchar(50) DEFAULT NULL,
  `bind_source` varchar(50) DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bind_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 医患医院绑定';

CREATE TABLE IF NOT EXISTS `eltm_patient_pic` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `pic_id` varchar(64) NOT NULL,
  `record_date` datetime DEFAULT NULL COMMENT 'pic_json.diagnosis_date / gettpbyid.record_date',
  `record_name` varchar(200) DEFAULT NULL COMMENT 'pic_json.medical_record_name / gettpbyid.record_name',
  `type_content` varchar(128) DEFAULT NULL,
  `image_url` varchar(512) DEFAULT NULL,
  `image_type` varchar(10) DEFAULT NULL COMMENT '0检验/1检查/2影像/3病历/4处方/5其它/6出院小结',
  `pic_source_type` varchar(10) DEFAULT NULL,
  `source` varchar(32) DEFAULT NULL,
  `remark` text,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pic_id` (`pic_id`),
  KEY `idx_pic_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 病历图片';

CREATE TABLE IF NOT EXISTS `eltm_patient_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_id` bigint NOT NULL,
  `register_source` varchar(32) DEFAULT NULL,
  `id_type` varchar(32) DEFAULT NULL,
  `external_created_at` datetime DEFAULT NULL,
  `external_doctor_id` varchar(64) DEFAULT NULL,
  `external_hospital_id` varchar(64) DEFAULT NULL,
  `first_doctor_names` varchar(500) DEFAULT NULL,
  `first_doctor_ids` varchar(500) DEFAULT NULL,
  `first_hospital_names` varchar(500) DEFAULT NULL,
  `first_hospital_ids` varchar(500) DEFAULT NULL,
  `assign_type` varchar(100) DEFAULT NULL,
  `project_count` int DEFAULT NULL,
  `project_name` varchar(500) DEFAULT NULL,
  `patient_type` varchar(32) DEFAULT NULL,
  `data_scope` varchar(64) DEFAULT NULL,
  `form_count` int DEFAULT NULL,
  `pic_count` int DEFAULT NULL,
  `physical_sign_count` int DEFAULT NULL,
  `report_count` int DEFAULT NULL,
  `exercise_flag` varchar(10) DEFAULT NULL,
  `sleep_flag` varchar(10) DEFAULT NULL,
  `consent_count` int DEFAULT NULL,
  `latest_form_time` datetime DEFAULT NULL,
  `latest_form_update` datetime DEFAULT NULL,
  `latest_form_name` varchar(500) DEFAULT NULL,
  `latest_pic_time` datetime DEFAULT NULL,
  `latest_pic_update` datetime DEFAULT NULL,
  `latest_sign_time` datetime DEFAULT NULL,
  `latest_sign_update` datetime DEFAULT NULL,
  `latest_report_time` datetime DEFAULT NULL,
  `latest_report_update` datetime DEFAULT NULL,
  `latest_exercise_time` datetime DEFAULT NULL,
  `latest_exercise_update` datetime DEFAULT NULL,
  `latest_sleep_time` datetime DEFAULT NULL,
  `latest_sleep_update` datetime DEFAULT NULL,
  `latest_consent_time` datetime DEFAULT NULL,
  `latest_consent_update` datetime DEFAULT NULL,
  `sync_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_profile_patient_id` (`patient_id`),
  UNIQUE KEY `uk_profile_external_id` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 外部患者扩展属性';

CREATE TABLE IF NOT EXISTS `eltm_patient_raw` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_id` bigint NOT NULL,
  `external_updated_at` datetime DEFAULT NULL,
  `row_hash` varchar(64) DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_raw_patient_id` (`patient_id`),
  UNIQUE KEY `uk_raw_external_id` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 患者原始行快照';

CREATE TABLE IF NOT EXISTS `eltm_physical_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `archive_id` bigint DEFAULT NULL,
  `form_key` varchar(64) DEFAULT NULL,
  `attribute_id` bigint DEFAULT NULL,
  `attribute_code` varchar(128) DEFAULT NULL,
  `attribute_value` text,
  `measure_time` datetime DEFAULT NULL,
  `data_source` varchar(32) DEFAULT NULL,
  `three_level` varchar(64) DEFAULT NULL,
  `archives_type` varchar(32) DEFAULT NULL,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_physical_archive_id` (`archive_id`),
  KEY `idx_physical_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 体征属性明细';

CREATE TABLE IF NOT EXISTS `eltm_physical_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `measure_time` datetime NOT NULL,
  `data_source` varchar(32) DEFAULT NULL,
  `archives_type` varchar(32) DEFAULT NULL,
  `item_count` int DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_physical_patient_time` (`patient_id`,`measure_time`),
  KEY `idx_physical_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 体征测量记录';

CREATE TABLE IF NOT EXISTS `eltm_project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `external_project_id` varchar(64) DEFAULT NULL,
  `project_name` varchar(500) DEFAULT NULL,
  `relation_source` varchar(100) DEFAULT NULL,
  `first_relation_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_project_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 患者所属项目';

CREATE TABLE IF NOT EXISTS `eltm_projection_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_id` bigint DEFAULT NULL,
  `status` varchar(20) NOT NULL COMMENT 'SUCCESS/SKIPPED/FAILED',
  `field_count` int DEFAULT '0',
  `message` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 数据投影日志';

CREATE TABLE IF NOT EXISTS `eltm_sleep_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `record_id` bigint NOT NULL,
  `record_date` date DEFAULT NULL,
  `sleep_start_time` datetime DEFAULT NULL,
  `sleep_end_time` datetime DEFAULT NULL,
  `sleep_duration_hours` int DEFAULT NULL,
  `sleep_duration_minutes` int DEFAULT NULL,
  `awake_times` int DEFAULT NULL,
  `sleep_way` varchar(100) DEFAULT NULL,
  `sleep_habits` text,
  `sleep_posture` varchar(100) DEFAULT NULL,
  `sleep_regularly` tinyint DEFAULT NULL,
  `sleep_env_is_quiet` tinyint DEFAULT NULL,
  `age_group` varchar(32) DEFAULT NULL,
  `baby_age_days` int DEFAULT NULL,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sleep_record_id` (`record_id`),
  KEY `idx_sleep_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 睡眠记录';

CREATE TABLE IF NOT EXISTS `eltm_sleep_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `external_patient_id` bigint NOT NULL,
  `summary_id` bigint NOT NULL,
  `summary_date` date DEFAULT NULL,
  `total_duration_hours` decimal(6,2) DEFAULT NULL,
  `is_target_reached` tinyint DEFAULT NULL,
  `baby_age_days` int DEFAULT NULL,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  `summary_json` longtext,
  `raw_json` longtext,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sleep_summary_id` (`summary_id`),
  KEY `idx_sleep_summary_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 睡眠汇总';

CREATE TABLE IF NOT EXISTS `eltm_sync_error` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sync_log_id` bigint DEFAULT NULL,
  `external_id` bigint DEFAULT NULL,
  `stage` varchar(50) DEFAULT NULL COMMENT 'PATIENT/FORM/PIC/PHYSICAL/REPORT/EXERCISE/SLEEP/CONSENT',
  `error_message` text,
  `raw_json` longtext,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sync_error_log` (`sync_log_id`),
  KEY `idx_sync_error_external` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 同步失败明细';

CREATE TABLE IF NOT EXISTS `eltm_sync_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sync_type` varchar(20) NOT NULL COMMENT 'FULL / INCREMENTAL',
  `trigger_type` varchar(20) NOT NULL COMMENT 'MANUAL / SCHEDULED',
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `total_count` int DEFAULT '0',
  `new_count` int DEFAULT '0',
  `updated_count` int DEFAULT '0',
  `skipped_count` int DEFAULT '0',
  `failed_count` int DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'RUNNING',
  `error_message` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sync_log_start` (`start_time`),
  KEY `idx_sync_log_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ELTM 数据同步日志';

CREATE TABLE IF NOT EXISTS `informed_consent` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL COMMENT '鏈?郴缁熸偅鑰匢D',
  `eltm_id` varchar(64) DEFAULT NULL COMMENT 'E璺??钀屾偅鑰呭敮涓?爣璇',
  `external_file_id` varchar(128) DEFAULT NULL COMMENT 'E璺??钀屽?閮ㄦ枃浠禝D',
  `file_name` varchar(255) DEFAULT NULL,
  `oss_url` varchar(512) DEFAULT NULL,
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `patient_name` varchar(64) DEFAULT NULL COMMENT '鎮ｈ?濮撳悕',
  `doctor_name` varchar(64) DEFAULT NULL COMMENT '鍏?紬鍙风?鍖荤敓濮撳悕',
  `source` varchar(32) DEFAULT 'ELTM' COMMENT '鏉ユ簮',
  `sync_time` datetime DEFAULT NULL COMMENT '鍚屾?鏃堕棿',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0' COMMENT '閫昏緫鍒犻櫎',
  `sign_id` varchar(64) DEFAULT NULL COMMENT '外部签署记录ID',
  `external_patient_id` bigint DEFAULT NULL COMMENT '外部患者ID',
  `external_doctor_id` bigint DEFAULT NULL COMMENT '外部归属医生ID',
  `project_id` varchar(64) DEFAULT NULL,
  `group_id` varchar(64) DEFAULT NULL,
  `external_consent_id` varchar(64) DEFAULT NULL COMMENT '外部知情同意书模板ID',
  `content` longtext COMMENT 'HTML 正文',
  `sign_doctor_id` varchar(64) DEFAULT NULL,
  `patient_sign_image` varchar(512) DEFAULT NULL COMMENT '签名图片地址',
  `patient_sign_time` datetime DEFAULT NULL,
  `guardian_sign_image` varchar(512) DEFAULT NULL,
  `guardian_sign_time` datetime DEFAULT NULL,
  `doctor_sign_image` varchar(512) DEFAULT NULL,
  `doctor_sign_time` datetime DEFAULT NULL,
  `sign_status` varchar(10) DEFAULT NULL COMMENT '签署状态，字典待确认',
  `pdf_path` varchar(512) DEFAULT NULL,
  `pdf_file_name` varchar(255) DEFAULT NULL,
  `pdf_error_msg` varchar(500) DEFAULT NULL,
  `pdf_status` varchar(10) DEFAULT NULL COMMENT '0待生成/1成功/2失败/3跳过',
  `pdf_retry_count` int DEFAULT NULL,
  `pdf_generate_time` datetime DEFAULT NULL,
  `raw_json` longtext,
  `external_create_time` datetime DEFAULT NULL,
  `external_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sign_id` (`sign_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_eltm_id` (`eltm_id`),
  KEY `idx_external_file_id` (`external_file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐭ユ儏鍚屾剰涔';

CREATE TABLE IF NOT EXISTS `school_cbq` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `zscm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bzzsl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `htzz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xhmxhd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xcsba` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nwczb` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mrjrxdf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jtjhmdx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xhdtrcg` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `drhrbjs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hpyz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nzyfmxyf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gxhaj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sqgcsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trjccsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dqqgk` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bwcjs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `syayqxd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hcsjsyxhj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gmssby` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xhgq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hhx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ryaf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zyktxsw` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wsjlcp` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bhphy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zztscsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bxhcyyx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qwcsbxf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zywx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hmzs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bnzdhsc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xhrhhd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xrshx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pyzhfz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dfmwbpj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_cchkn` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `ntlbrgs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bad` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jcdzt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lyfx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jcfpq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bjgd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bjsc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hdyy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lybz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jczlba` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yhpy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cjqfbr` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jcbgx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sxpyxh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ryfx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xhjjz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dljxyh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jcshqp` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sbrzn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zybzbr` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zsqxqc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `htdx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hdrrq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ryjx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zylcj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_mqzyfs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `hzbgxbpp` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzysmgsm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzsffcwsw` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzgxbxyq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzbfcdm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzfcbzh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzyddycgfzj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `glhzzs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thzzs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gjhzxqpytc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhzknysdc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yxchtw` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bxbgjz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wmyyzpp` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bnfwzwn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bxhzzcn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rhzmbwsm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhzshysgx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bzyhzzsm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zjmblchz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzxzmyjzy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bqsjbyhz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bljhzhfuzsm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ysdfsbyhz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wlyqysmz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhzmyjc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bhzjjkn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `khzzs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhzrzhd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tyjsysyx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhzbjsf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hsyqyty` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thzyqyx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yqhzjyy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzzcyspp` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bgxxs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `slshzsc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ysqz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zjmjtyq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pyhzjzjd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_pmbl` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `dycyn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mtjcsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jztx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ptgksj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ptgkjlsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_qzhd` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `yhzyqks` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jhzsdgl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tthh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yhzyqyx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yqsz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yqcg` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jgs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zsg` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zyd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jhzzl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yhztlzw` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yhzrsdzr` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_smxg` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `psscsj` datetime DEFAULT NULL,
  `zmscsj` datetime DEFAULT NULL,
  `psszsj` datetime DEFAULT NULL,
  `zmszsj` datetime DEFAULT NULL,
  `gdsjscsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `esfzrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dzrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ztrcsrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rsyb` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tdwrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xypbrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzbrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hkjrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzhzz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzhpha` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzhpyr` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hzmtsm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sdts` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sdtd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smsl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mtsmlyy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wsnc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smsmh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smbaw` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hmy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hdtrcs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fytt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ttbw` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mybzcxrs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `myxx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dhl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hxzt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bxz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jzbzjywt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bysmwt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yjxljj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emjx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `psxlsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zmxlsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xlyc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xlycys` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pszcxlsj` datetime DEFAULT NULL,
  `zmzcxlsj` datetime DEFAULT NULL,
  `psqcsj` datetime DEFAULT NULL,
  `zmqcsj` datetime DEFAULT NULL,
  `zjxl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nzjx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xlqxbj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yjrjx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qckn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `csjcnqx` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xlhz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wkhh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rjhxs` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xfhdzsz` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kqlhpj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dzws` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kds` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zc` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cf` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_sthd` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `cjzqdpl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dssjzqd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cjdqdpl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dssjdqd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jzpl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jzsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fkpmjspv` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fkpmjzsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pmjzpl` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pmjzsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `school_student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `doctor` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hhzgx` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `num` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sclass` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sex` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `birth_time` datetime DEFAULT NULL,
  `height` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `weight` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mqjycd` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fqjycd` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jtnsr` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zyjhr` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zhrjycd` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isxdjm` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yszdrstlb` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yszdrsgxy` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yldisbz` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `yszdyybl` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fmfs` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bweight` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bweek` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `csiszxqj` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cswyfs` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dmrsj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jfssj` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imp_per` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `up_mec` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime DEFAULT NULL,
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `count` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `xcx_chartuser` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `medrec_num` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_num` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `doctor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_tongb` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '0',
  `del_flg` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT '1',
  `new_user_flag` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `myself_picture` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacts_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacts_num` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `p_emial` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idcard` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nat_pla` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== 4. 公共表补齐缺失列 ==========
ALTER TABLE `datamain_case` ADD COLUMN `dia_trea_plan` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE `datamain_gh_adverse_event` ADD COLUMN `data_source` varchar(32) DEFAULT 'LOCAL' COMMENT '数据来源 LOCAL/ELTM_SYNC';
ALTER TABLE `datamain_gh_adverse_event` ADD COLUMN `source_form_data_id` bigint DEFAULT NULL COMMENT '外部表单 form_data_id（幂等键）';
ALTER TABLE `datamain_mas` ADD COLUMN `dia_trea_plan` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE `datamain_patfoll` ADD COLUMN `is_deleted` int DEFAULT '0';
ALTER TABLE `datamain_patfoll` ADD COLUMN `data_source` varchar(32) DEFAULT 'LOCAL' COMMENT '数据来源 LOCAL/ELTM_SYNC';
ALTER TABLE `datamain_patfoll` ADD COLUMN `source_form_data_id` bigint DEFAULT NULL COMMENT '外部表单 form_data_id（幂等键）';
ALTER TABLE `datamain_patient` ADD COLUMN `is_deleted` int DEFAULT '0';
ALTER TABLE `datamain_patient` ADD COLUMN `external_id` bigint DEFAULT NULL COMMENT 'datamain 外部患者主键(cxList 列0)';
ALTER TABLE `login_unit` ADD COLUMN `address` varchar(500) DEFAULT NULL;
ALTER TABLE `login_unit` ADD COLUMN `is_deleted` int DEFAULT '0';

-- ========== 5. 公共表补齐缺失索引 ==========
ALTER TABLE `datamain_gh_adverse_event` ADD UNIQUE KEY `uk_ae_source_form` (`source_form_data_id`);
ALTER TABLE `datamain_patfoll` ADD UNIQUE KEY `uk_foll_source_form` (`source_form_data_id`);
ALTER TABLE `datamain_patient` ADD UNIQUE KEY `uk_patient_external_id` (`external_id`);

-- ========== 6. 放宽字段长度（仅 varchar->更长/text，避免截断） ==========
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `tsh` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `ft3` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `ft4` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `tpoab` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `tgab` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patfoll` MODIFY COLUMN `eye_exam` text COMMENT '眼科检查数据(JSON)';
ALTER TABLE `datamain_patient` MODIFY COLUMN `myself_picture` text COMMENT '个人头像';
ALTER TABLE `datamain_patient` MODIFY COLUMN `fam_adr` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `up_mec` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `tags` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `photo` text COMMENT '照片';
ALTER TABLE `datamain_patient` MODIFY COLUMN `address` text COMMENT '地址';
ALTER TABLE `datamain_patient` MODIFY COLUMN `karyotype` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `biolog_bank` text COMMENT '患者生物样本库';
ALTER TABLE `datamain_patient` MODIFY COLUMN `biolog_bank_fa` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `biolog_bank_mo` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `general_exam_desc` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `special_face_desc` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `scoliosis_desc` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `rash_desc` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `family_id` varchar(50) DEFAULT NULL;
ALTER TABLE `datamain_patient` MODIFY COLUMN `blood_routine` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `urine_routine` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `liver_kidney_electrolyte` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `thyroid_function` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `audit_remark` text;
ALTER TABLE `datamain_patient` MODIFY COLUMN `eye_exam` text COMMENT '眼科检查数据(JSON)';
ALTER TABLE `datamain_patient` MODIFY COLUMN `gene_test_method` text;

-- ========== 7. 回填兼容字段 ==========
UPDATE datamain_patient SET is_deleted = CASE WHEN del_flg = '0' THEN 1 ELSE 0 END;
UPDATE datamain_patfoll SET is_deleted = CASE WHEN del_flg = '0' THEN 1 ELSE 0 END;
UPDATE datamain_patfoll SET data_source = 'LOCAL' WHERE data_source IS NULL;
UPDATE datamain_gh_adverse_event SET data_source = 'LOCAL' WHERE data_source IS NULL;
UPDATE login_unit SET address = contact_address WHERE (address IS NULL OR address = '') AND contact_address IS NOT NULL;
UPDATE login_unit SET is_deleted = CASE WHEN del_flg = '0' THEN 1 ELSE 0 END;

-- ========== 8. ELTM 字段映射配置 ==========
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (1,'6q7FEnkX','病史','input7928718414609','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (2,'6q7FEnkX','病史','input0568042355412','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (3,'6q7FEnkX','病史','number2507403372142','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (4,'6q7FEnkX','病史','number3649754232880','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (5,'6q7FEnkX','病史','number9473993544012','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (6,'P1S1Q69J','病史','input5793180866115','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (7,'P1S1Q69J','病史','input4135682979549','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (8,'P1S1Q69J','病史','number0333914903062','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (9,'P1S1Q69J','病史','number1028367739241','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (10,'P1S1Q69J','病史','number1474892780022','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (11,'VpCeck3o','病史','input1757343186390','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (12,'VpCeck3o','病史','input1757343197188','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (13,'VpCeck3o','病史','number1757343231377','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (14,'VpCeck3o','病史','number1757343249458','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (15,'VpCeck3o','病史','number1757343266169','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (16,'ZquzBJAe','病史','input8654213123937','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (17,'ZquzBJAe','病史','input8145063119419','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (18,'ZquzBJAe','病史','number0763079523686','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (19,'ZquzBJAe','病史','number9287758469434','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (20,'ZquzBJAe','病史','number3023341578773','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (21,'awWTUawI','病史','input6017827945917','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (22,'awWTUawI','病史','input9054629893019','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (23,'awWTUawI','病史','number0391210998012','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (24,'awWTUawI','病史','number8718746582140','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (25,'awWTUawI','病史','number7645854007254','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (26,'bVLLxNVd','病史','input8590993692289','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (27,'bVLLxNVd','病史','input1805924143179','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (28,'bVLLxNVd','病史','number8086909532180','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (29,'bVLLxNVd','病史','number9330045522861','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (30,'bVLLxNVd','病史','number0154695919671','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (31,'jXBKDQ0G','病史','input8676122790766','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (32,'jXBKDQ0G','病史','input4916192605470','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (33,'jXBKDQ0G','病史','number8414425793364','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (34,'jXBKDQ0G','病史','number1885086021867','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (35,'jXBKDQ0G','病史','number9315858344181','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (36,'k7tNv6St','病史','input3839353141250','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (37,'k7tNv6St','病史','input6684515994497','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (38,'k7tNv6St','病史','number3942225271473','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (39,'k7tNv6St','病史','number8353585324345','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (40,'k7tNv6St','病史','number1255612002053','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (41,'se7BswNk','病史','input9278582893038','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'现病史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (42,'se7BswNk','病史','input6975573136088','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (43,'se7BswNk','病史','number1678218521210','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (44,'se7BswNk','病史','number0420315127596','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (45,'se7BswNk','病史','number4430127467837','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (46,'w4wlPs2y','病史','input6428766566198','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'主诉','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (47,'w4wlPs2y','病史','input8769857735391','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (48,'w4wlPs2y','病史','number3567039860991','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (49,'w4wlPs2y','病史','number3968766230785','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (50,'w4wlPs2y','病史','number9224383227690','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (51,'xce5tqrJ','病史','input3621336518201','PATIENT','chiCom','RAW','TEXT_CLEAN',0,1,'主诉','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (52,'xce5tqrJ','病史','input8459621020934','PATIENT','pastHis','RAW','TEXT_CLEAN',0,1,'既往史/家族史','2026-10-17 17:41:55','2026-10-17 18:36:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (53,'xce5tqrJ','病史','number7962549077656','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (54,'xce5tqrJ','病史','number0523674987345','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (55,'xce5tqrJ','病史','number2391883587747','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (56,'BFBYp3Y4','染色体结果','mul_choice9100246084345','GENETICS','chrom','MULTI_LABEL',NULL,0,1,'染色体核型','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (57,'SN9UUWoD','染色体结果','mul_choice8010039113766','GENETICS','chrom','MULTI_LABEL',NULL,0,1,'染色体核型','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (58,'bBRD8vMR','染色体结果','mul_choice1757344138695','GENETICS','chrom','MULTI_LABEL',NULL,0,1,'染色体核型','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (59,'mWFT9FS6','染色体结果','mul_choice7973696500879','GENETICS','chrom','MULTI_LABEL',NULL,0,1,'染色体核型','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (60,'bXWU6GCK','基因结果','input1757344256695','PATIENT','categoryDescribe','RAW',NULL,0,1,'基因名称','2026-10-17 17:41:55','2026-10-17 17:41:55') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (61,'LN5LubZ9','产妇信息收集','number1776061420180','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (62,'LN5LubZ9','产妇信息收集','number1776061429718','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (63,'LN5LubZ9','产妇信息收集','number1776061350356','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (64,'LN5LubZ9','产妇信息收集','single1776061363565','PATIENT','cesaSec','LABEL','ENUM:自然分娩=0;剖宫产=1',0,1,'分娩方式','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (65,'LN5LubZ9','产妇信息收集','number1776060934920','PATIENT','parity','RAW',NULL,0,1,'胎次（推断）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (66,'LN5LubZ9','产妇信息收集','number1776061063207','PATIENT','pronum','RAW',NULL,0,1,'产次（推断）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (67,'LN5LubZ9','产妇信息收集','input1776060746468','PATIENT','ethnic','RAW','TEXT_CLEAN',0,1,'民族','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (68,'LN5LubZ9','产妇信息收集','number1776061581221','FAMILY','fatherHeight','RAW',NULL,0,1,'父亲身高（推断）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (69,'LN5LubZ9','产妇信息收集','number1776061587001','FAMILY','motherHeight','RAW',NULL,0,1,'母亲身高（推断）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (70,'LN5LubZ9','产妇信息收集','input1776060557073','FAMILY','motherName','RAW','TEXT_CLEAN',0,1,'母亲姓名（候选）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (71,'LN5LubZ9','产妇信息收集','input1776060858353','FAMILY','fatherName','RAW','TEXT_CLEAN',0,1,'父亲姓名（候选）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (72,'LN5LubZ9','产妇信息收集','date1776060738788','FAMILY','motherBirthYear','RAW',NULL,0,1,'母亲出生年（推断）','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (73,'QcA9Ts21','出生史','number1773801080613','PATIENT','bwt','RAW','WEIGHT_G_TO_KG',0,1,'出生体重(kg)','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (74,'QcA9Ts21','出生史','number1773801061412','PATIENT','bl','RAW','BIRTH_HEIGHT_CM',0,1,'出生身长','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);
INSERT INTO eltm_form_field_mapping (id,form_key,form_name,field_key,target_scope,target_field,value_type,transform_rule,priority,enabled,remark,create_time,update_time) VALUES (75,'QcA9Ts21','出生史','input1773800303331','PATIENT','gesWeek','RAW','GEST_WEEK',0,1,'孕周','2026-10-18 21:36:12','2026-10-18 21:36:12') ON DUPLICATE KEY UPDATE form_name=VALUES(form_name), target_scope=VALUES(target_scope), target_field=VALUES(target_field), value_type=VALUES(value_type), transform_rule=VALUES(transform_rule), priority=VALUES(priority), enabled=VALUES(enabled), remark=VALUES(remark);

-- ========== 9. 结果校验 ==========
SELECT 'table_count' AS k, COUNT(*) AS v FROM information_schema.tables WHERE table_schema=DATABASE();
SELECT 'eltm_table_count' AS k, COUNT(*) AS v FROM information_schema.tables WHERE table_schema=DATABASE() AND table_name LIKE 'eltm\_%';
SELECT 'patient_rows' AS k, COUNT(*) AS v FROM datamain_patient;
SELECT 'patient_deleted' AS k, SUM(is_deleted) AS v FROM datamain_patient;
SELECT 'patfoll_rows' AS k, COUNT(*) AS v FROM datamain_patfoll;
SELECT 'mapping_rows' AS k, COUNT(*) AS v FROM eltm_form_field_mapping;
SET FOREIGN_KEY_CHECKS=1;
