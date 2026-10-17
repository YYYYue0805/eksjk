-- ============================================================
-- 33-eltm-projection.sql
-- ELTM 外部数据投影：表单模板字段映射 + 随访来源标记
-- 可重复执行
-- ============================================================

-- ------------------------------------------------------------
-- 1. 外部表单字段映射配置
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_form_field_mapping (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  form_key      VARCHAR(64)  NOT NULL COMMENT '外部表单模板 key',
  form_name     VARCHAR(200) NULL COMMENT '表单名称（便于维护）',
  field_key     VARCHAR(128) NOT NULL COMMENT '外部答案字段 key',
  target_scope  VARCHAR(32)  NOT NULL COMMENT 'PATIENT/ELTM_CASE/GENETICS/FOLLOWUP/ADVERSE',
  target_field  VARCHAR(64)  NOT NULL COMMENT '目标字段名',
  value_type    VARCHAR(16)  NOT NULL DEFAULT 'RAW' COMMENT 'RAW/LABEL/MULTI_LABEL',
  transform_rule VARCHAR(64) NULL COMMENT 'WEIGHT_G_TO_KG/BIRTH_HEIGHT_CM/GEST_WEEK 等',
  priority      INT          NOT NULL DEFAULT 0,
  enabled       TINYINT      NOT NULL DEFAULT 1,
  remark        VARCHAR(255) NULL,
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_form_field (form_key, field_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 表单字段映射配置';

-- ------------------------------------------------------------
-- 2. 随访记录来源标记（外部同步记录只读）
-- ------------------------------------------------------------
SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'datamain_patfoll'
    AND COLUMN_NAME = 'data_source'
);
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE datamain_patfoll ADD COLUMN data_source VARCHAR(32) NULL DEFAULT ''LOCAL'' COMMENT ''数据来源 LOCAL/ELTM_SYNC''',
  'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------
-- 3. 投影日志
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eltm_projection_log (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id    BIGINT NOT NULL,
  external_id   BIGINT NULL,
  status        VARCHAR(20) NOT NULL COMMENT 'SUCCESS/SKIPPED/FAILED',
  field_count   INT DEFAULT 0,
  message       VARCHAR(1000) NULL,
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_patient_id (patient_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 数据投影日志';

-- ------------------------------------------------------------
-- 4. 首批映射：病史模板
--    主诉/既往史/出生身长/出生体重/孕周
-- ------------------------------------------------------------
INSERT INTO eltm_form_field_mapping
  (form_key, form_name, field_key, target_scope, target_field, value_type, transform_rule, remark)
VALUES
  -- 6q7FEnkX
  ('6q7FEnkX','病史','input7928718414609','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('6q7FEnkX','病史','input0568042355412','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('6q7FEnkX','病史','number2507403372142','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('6q7FEnkX','病史','number3649754232880','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('6q7FEnkX','病史','number9473993544012','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- P1S1Q69J
  ('P1S1Q69J','病史','input5793180866115','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('P1S1Q69J','病史','input4135682979549','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('P1S1Q69J','病史','number0333914903062','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('P1S1Q69J','病史','number1028367739241','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('P1S1Q69J','病史','number1474892780022','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- VpCeck3o
  ('VpCeck3o','病史','input1757343186390','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('VpCeck3o','病史','input1757343197188','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('VpCeck3o','病史','number1757343231377','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('VpCeck3o','病史','number1757343249458','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('VpCeck3o','病史','number1757343266169','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- ZquzBJAe
  ('ZquzBJAe','病史','input8654213123937','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('ZquzBJAe','病史','input8145063119419','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('ZquzBJAe','病史','number0763079523686','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('ZquzBJAe','病史','number9287758469434','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('ZquzBJAe','病史','number3023341578773','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- awWTUawI
  ('awWTUawI','病史','input6017827945917','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('awWTUawI','病史','input9054629893019','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('awWTUawI','病史','number0391210998012','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('awWTUawI','病史','number8718746582140','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('awWTUawI','病史','number7645854007254','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- bVLLxNVd
  ('bVLLxNVd','病史','input8590993692289','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('bVLLxNVd','病史','input1805924143179','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('bVLLxNVd','病史','number8086909532180','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('bVLLxNVd','病史','number9330045522861','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('bVLLxNVd','病史','number0154695919671','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- jXBKDQ0G
  ('jXBKDQ0G','病史','input8676122790766','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('jXBKDQ0G','病史','input4916192605470','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('jXBKDQ0G','病史','number8414425793364','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('jXBKDQ0G','病史','number1885086021867','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('jXBKDQ0G','病史','number9315858344181','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- k7tNv6St
  ('k7tNv6St','病史','input3839353141250','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('k7tNv6St','病史','input6684515994497','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('k7tNv6St','病史','number3942225271473','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('k7tNv6St','病史','number8353585324345','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('k7tNv6St','病史','number1255612002053','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- se7BswNk
  ('se7BswNk','病史','input9278582893038','PATIENT','chiCom','RAW',NULL,'现病史'),
  ('se7BswNk','病史','input6975573136088','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('se7BswNk','病史','number1678218521210','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('se7BswNk','病史','number0420315127596','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('se7BswNk','病史','number4430127467837','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- w4wlPs2y
  ('w4wlPs2y','病史','input6428766566198','PATIENT','chiCom','RAW',NULL,'主诉'),
  ('w4wlPs2y','病史','input8769857735391','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('w4wlPs2y','病史','number3567039860991','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('w4wlPs2y','病史','number3968766230785','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('w4wlPs2y','病史','number9224383227690','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- xce5tqrJ
  ('xce5tqrJ','病史','input3621336518201','PATIENT','chiCom','RAW',NULL,'主诉'),
  ('xce5tqrJ','病史','input8459621020934','PATIENT','pastHis','RAW',NULL,'既往史/家族史'),
  ('xce5tqrJ','病史','number7962549077656','PATIENT','bl','RAW','BIRTH_HEIGHT_CM','出生身长'),
  ('xce5tqrJ','病史','number0523674987345','PATIENT','bwt','RAW','WEIGHT_G_TO_KG','出生体重'),
  ('xce5tqrJ','病史','number2391883587747','PATIENT','gesWeek','RAW','GEST_WEEK','孕周'),
  -- 染色体结果：核型多选直接映射到独立表
  ('BFBYp3Y4','染色体结果','mul_choice9100246084345','GENETICS','chrom','MULTI_LABEL',NULL,'染色体核型'),
  ('SN9UUWoD','染色体结果','mul_choice8010039113766','GENETICS','chrom','MULTI_LABEL',NULL,'染色体核型'),
  ('bBRD8vMR','染色体结果','mul_choice1757344138695','GENETICS','chrom','MULTI_LABEL',NULL,'染色体核型'),
  ('mWFT9FS6','染色体结果','mul_choice7973696500879','GENETICS','chrom','MULTI_LABEL',NULL,'染色体核型'),
  -- 基因结果：基因名称映射到基本信息「致病基因」
  ('bXWU6GCK','基因结果','input1757344256695','PATIENT','categoryDescribe','RAW',NULL,'基因名称')
ON DUPLICATE KEY UPDATE
  form_name = VALUES(form_name),
  target_scope = VALUES(target_scope),
  target_field = VALUES(target_field),
  value_type = VALUES(value_type),
  transform_rule = VALUES(transform_rule),
  remark = VALUES(remark);

-- 文本类字段统一清理开头的冒号/分号/空白
UPDATE eltm_form_field_mapping
SET transform_rule = 'TEXT_CLEAN'
WHERE target_field IN ('chiCom', 'pastHis');
