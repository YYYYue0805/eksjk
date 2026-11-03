-- ============================================================
-- 37-eltm-unprocessed-module.sql
-- 未处理数据增加「建议模块」归类，供前端按目标模块筛选
-- 可重复执行
-- ============================================================

SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'eltm_unprocessed_data'
    AND COLUMN_NAME = 'suggested_module'
);
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE eltm_unprocessed_data ADD COLUMN suggested_module VARCHAR(32) NULL COMMENT ''建议归入的目标模块'' AFTER source_name',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'eltm_unprocessed_data'
    AND INDEX_NAME = 'idx_unprocessed_module'
);
SET @ddl = IF(@idx_exists = 0,
  'ALTER TABLE eltm_unprocessed_data ADD KEY idx_unprocessed_module (patient_id, suggested_module, status)',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 存量数据回填：按来源名称粗略归类，后续 rebuild / 同步会重算
UPDATE eltm_unprocessed_data
SET suggested_module = CASE
  WHEN source_name LIKE '%知情同意%' THEN '知情同意书'
  WHEN source_name LIKE '%不良事件%' OR source_name LIKE '%不良反应%' THEN 'GH不良事件'
  WHEN source_name LIKE '%主要转归%' OR source_name LIKE '%术后辅助治疗%' OR source_name LIKE '%诊断%' THEN '诊断'
  WHEN source_name LIKE '%染色体%' OR source_name LIKE '%基因%' THEN '遗传学检查'
  WHEN source_name LIKE '%实验室检查%' OR source_name LIKE '%影像学%' OR source_name LIKE '%检查记录%'
       OR source_name LIKE '%骨龄%' OR source_name LIKE '%垂体%' OR source_name LIKE '%超声%'
       OR source_name LIKE '%血常规%' OR source_name LIKE '%尿常规%' OR source_name LIKE '%血糖%'
       OR source_name LIKE '%糖尿病%' THEN '基线-辅助检查'
  WHEN source_name LIKE '%GH用药%' OR source_name LIKE '%合并用药%' OR source_name LIKE '%随访%'
       OR source_name LIKE '%基础测量%' OR source_name LIKE '%体重记录%' OR source_name LIKE '%长高记录%' THEN '随访记录'
  WHEN source_name LIKE '%体格检查%' OR source_name LIKE '%病史%' OR source_name LIKE '%临床症状%'
       OR source_name LIKE '%体征%' OR source_name LIKE '%家族史%' THEN '基线-临床信息'
  WHEN source_name LIKE '%人口学%' OR source_name LIKE '%基本信息%' OR source_name LIKE '%患者基线信息%'
       OR source_name LIKE '%出生史%' OR source_name LIKE '%产妇信息%' THEN '基本信息'
  WHEN source_name LIKE 'physical_json%' THEN '基线-临床信息'
  WHEN source_name LIKE 'extend_data_json%' THEN '基本信息'
  ELSE '其他/保留'
END
WHERE suggested_module IS NULL;
