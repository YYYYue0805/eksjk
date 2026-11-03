-- ============================================================
-- 36-eltm-unprocessed-data.sql
-- ELTM 外部接口未处理数据（未知列/未解析 JSON/未处理表单/未映射字段）
-- 可重复执行
-- ============================================================

CREATE TABLE IF NOT EXISTS eltm_unprocessed_data (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id           BIGINT NULL COMMENT '本系统患者ID',
  external_patient_id  BIGINT NULL COMMENT '外部患者ID',
  api_code             VARCHAR(32) NOT NULL COMMENT 'CXLIST/GETTPBYID/GETZQBYID',
  category             VARCHAR(32) NOT NULL COMMENT 'UNKNOWN_COLUMN/UNPARSED_JSON/UNHANDLED_FORM/UNMAPPED_FIELD/UNKNOWN_INTERFACE_FIELD',
  source_key           VARCHAR(191) NOT NULL COMMENT '列号/JSON路径/form_data_id:fieldKey/pic_id:colN',
  source_name          VARCHAR(255) NULL COMMENT '便于阅读的来源名称',
  reason               VARCHAR(500) NULL COMMENT '未处理原因',
  raw_value            LONGTEXT NULL COMMENT '原始值',
  raw_json             LONGTEXT NULL COMMENT '原始 JSON 片段（超长截断，全文在来源表）',
  content_hash         CHAR(64) NULL COMMENT '内容指纹，用于变更检测',
  status               VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/MAPPED/IGNORED',
  sync_time            DATETIME NULL COMMENT '同步时间',
  create_time          DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_unprocessed (api_code, patient_id, category, source_key),
  KEY idx_unprocessed_patient (patient_id, status, category),
  KEY idx_unprocessed_api (api_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 外部接口未处理数据';
