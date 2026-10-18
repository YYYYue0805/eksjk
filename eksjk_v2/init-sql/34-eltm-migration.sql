-- ============================================================
-- 34-eltm-migration.sql
-- ELTM「暂未处理数据」迁移：来源标记、幂等键、首批映射配置
-- 可重复执行
-- ============================================================

-- ------------------------------------------------------------
-- 1. GH 不良事件：外部来源标记与幂等键
-- ------------------------------------------------------------
SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'datamain_gh_adverse_event'
    AND COLUMN_NAME = 'data_source'
);
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE datamain_gh_adverse_event ADD COLUMN data_source VARCHAR(32) NULL DEFAULT ''LOCAL'' COMMENT ''数据来源 LOCAL/ELTM_SYNC''',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'datamain_gh_adverse_event'
    AND COLUMN_NAME = 'source_form_data_id'
);
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE datamain_gh_adverse_event ADD COLUMN source_form_data_id BIGINT NULL COMMENT ''外部表单 form_data_id（幂等键）''',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'datamain_gh_adverse_event'
    AND INDEX_NAME = 'uk_ae_source_form'
);
SET @ddl = IF(@idx_exists = 0,
  'ALTER TABLE datamain_gh_adverse_event ADD UNIQUE KEY uk_ae_source_form (source_form_data_id)',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------
-- 2. 随访记录：外部表单幂等键
-- ------------------------------------------------------------
SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'datamain_patfoll'
    AND COLUMN_NAME = 'source_form_data_id'
);
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE datamain_patfoll ADD COLUMN source_form_data_id BIGINT NULL COMMENT ''外部表单 form_data_id（幂等键）''',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'datamain_patfoll'
    AND INDEX_NAME = 'uk_foll_source_form'
);
SET @ddl = IF(@idx_exists = 0,
  'ALTER TABLE datamain_patfoll ADD UNIQUE KEY uk_foll_source_form (source_form_data_id)',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------
-- 3. 知情同意书：外部文件幂等查询索引
-- ------------------------------------------------------------
SET @idx_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'informed_consent'
    AND INDEX_NAME = 'idx_external_file_id'
);
SET @ddl = IF(@idx_exists = 0,
  'ALTER TABLE informed_consent ADD KEY idx_external_file_id (external_file_id)',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------
-- 4. 首批字段映射：产妇信息收集（单模板）
--    目标：基本信息 + 家族史
-- ------------------------------------------------------------
INSERT INTO eltm_form_field_mapping
  (form_key, form_name, field_key, target_scope, target_field, value_type, transform_rule, remark)
VALUES
  ('LN5LubZ9','产妇信息收集','number1776061420180','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('LN5LubZ9','产妇信息收集','number1776061429718','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('LN5LubZ9','产妇信息收集','number1776061350356','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  ('LN5LubZ9','产妇信息收集','single1776061363565','PATIENT','cesaSec','LABEL','ENUM:自然分娩=0;剖宫产=1','分娩方式'),
  ('LN5LubZ9','产妇信息收集','number1776060934920','PATIENT','parity','RAW',NULL,'胎次（推断）'),
  ('LN5LubZ9','产妇信息收集','number1776061063207','PATIENT','pronum','RAW',NULL,'产次（推断）'),
  ('LN5LubZ9','产妇信息收集','input1776060746468','PATIENT','ethnic','RAW','TEXT_CLEAN','民族'),
  ('LN5LubZ9','产妇信息收集','number1776061581221','FAMILY','fatherHeight','RAW',NULL,'父亲身高（推断）'),
  ('LN5LubZ9','产妇信息收集','number1776061587001','FAMILY','motherHeight','RAW',NULL,'母亲身高（推断）'),
  ('LN5LubZ9','产妇信息收集','input1776060557073','FAMILY','motherName','RAW','TEXT_CLEAN','母亲姓名（候选）'),
  ('LN5LubZ9','产妇信息收集','input1776060858353','FAMILY','fatherName','RAW','TEXT_CLEAN','父亲姓名（候选）'),
  ('LN5LubZ9','产妇信息收集','date1776060738788','FAMILY','motherBirthYear','RAW',NULL,'母亲出生年（推断）')
ON DUPLICATE KEY UPDATE
  form_name = VALUES(form_name), target_scope = VALUES(target_scope),
  target_field = VALUES(target_field), value_type = VALUES(value_type),
  transform_rule = VALUES(transform_rule), remark = VALUES(remark);

-- ------------------------------------------------------------
-- 5. 首批字段映射：出生史
-- ------------------------------------------------------------
INSERT INTO eltm_form_field_mapping
  (form_key, form_name, field_key, target_scope, target_field, value_type, transform_rule, remark)
VALUES
  ('QcA9Ts21','出生史','number1773801080613','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重(kg)'),
  ('QcA9Ts21','出生史','number1773801061412','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('QcA9Ts21','出生史','input1773800303331','PATIENT','gesWeek','RAW','GEST_WEEK','孕周')
ON DUPLICATE KEY UPDATE
  form_name = VALUES(form_name), target_scope = VALUES(target_scope),
  target_field = VALUES(target_field), value_type = VALUES(value_type),
  transform_rule = VALUES(transform_rule), remark = VALUES(remark);

-- ------------------------------------------------------------
-- 6. 首批字段映射：基因结果（仅真实基因名称模板）
--    说明：bXWU6GCK 为测试模板，不写入基因字段，避免污染。
-- ------------------------------------------------------------
-- 无新增（现有 categoryDescribe 映射保留）

-- ------------------------------------------------------------
-- 7. 标记已有外部随访来源（历史数据兜底）
-- ------------------------------------------------------------
UPDATE datamain_patfoll SET data_source = 'ELTM_SYNC'
WHERE data_source IS NULL AND source_form_data_id IS NOT NULL;
