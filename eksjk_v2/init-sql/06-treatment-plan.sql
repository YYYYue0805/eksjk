-- ============================================================
-- EKSJK V2 — 诊疗方案 升级 DDL
-- 日期：2026-06-02
-- 说明：
--   FSS/SGA/SSS/CPP 疾病子表新增诊疗方案JSON字段
--   (pastTime/pastHeight/pastWeight 已在 01-schema.sql 中存在)
-- ============================================================

ALTER TABLE datamain_short
    ADD COLUMN IF NOT EXISTS dia_trea_plan TEXT COMMENT '诊疗方案JSON';

ALTER TABLE datamain_sga
    ADD COLUMN IF NOT EXISTS dia_trea_plan TEXT COMMENT '诊疗方案JSON';

ALTER TABLE datamain_jzxshort
    ADD COLUMN IF NOT EXISTS dia_trea_plan TEXT COMMENT '诊疗方案JSON';

ALTER TABLE datamain_sexprecocity
    ADD COLUMN IF NOT EXISTS dia_trea_plan TEXT COMMENT '诊疗方案JSON';
