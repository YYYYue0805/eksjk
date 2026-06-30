-- ============================================================
-- EKSJK V2 — ELTM 检验检查模块补全
-- 日期：2026-06-30
-- 说明：
--   1. 新增 GH 药物激发试验字段（V1 中 lab_exa_other.gh 对应字段）
--   2. 新增糖化血红蛋白 A1c 字段（V1 中 lab_exa_other.glyHemA 对应字段）
--   3. 所有新增字段均为可选（Nullable），不影响现有数据
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN gh VARCHAR(20) COMMENT 'GH药物激发试验峰值(ng/mL)',
    ADD COLUMN gh_check_date VARCHAR(20) COMMENT 'GH激发试验检查日期',
    ADD COLUMN gly_hem_a VARCHAR(20) COMMENT '糖化血红蛋白A1c(%)',
    ADD COLUMN gly_hem_a_check_date VARCHAR(20) COMMENT '糖化血红蛋白A1c检查日期';
