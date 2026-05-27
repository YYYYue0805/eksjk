-- ============================================================
-- EKSJK V2 — 一般检查 + 初次遗精 + 第二性征 字段
-- 说明：在患者主表 datamain_patient 中新增4个字段
-- ============================================================

ALTER TABLE datamain_patient ADD COLUMN has_general_exam VARCHAR(10) NULL COMMENT '是否有一般检查 1=有 0=无';
ALTER TABLE datamain_patient ADD COLUMN first_ejaculation VARCHAR(32) NULL COMMENT '初次遗精';
ALTER TABLE datamain_patient ADD COLUMN has_secondary_sexual VARCHAR(10) NULL COMMENT '是否有第二性征 1=有 0=无';
ALTER TABLE datamain_patient ADD COLUMN secondary_sexual_date DATETIME NULL COMMENT '第二性征出现日期';
