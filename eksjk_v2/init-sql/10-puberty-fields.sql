-- ============================================================
-- EKSJK V2 — 一般检查描述 + 初次遗精/月经初潮 字段增强
-- 说明：在患者主表 datamain_patient 中新增3个字段
-- ============================================================

-- 1. 一般检查描述（选择"有一般检查"时填写具体情况）
ALTER TABLE datamain_patient ADD COLUMN general_exam_desc VARCHAR(500) NULL COMMENT '一般检查描述';

-- 2. 是否有初次遗精/月经初潮（1=有, 0=无）
ALTER TABLE datamain_patient ADD COLUMN has_first_ejaculation VARCHAR(10) NULL COMMENT '是否有初次遗精/月经初潮 1=有 0=无';

-- 3. 初次遗精/月经初潮 发生时间（年月，格式 YYYY-MM）
ALTER TABLE datamain_patient ADD COLUMN first_ejaculation_date VARCHAR(10) NULL COMMENT '初次遗精/月经初潮时间（年月）';
