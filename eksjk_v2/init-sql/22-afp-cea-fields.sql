-- ============================================================
-- EKSJK V2 — 新增甲胎蛋白、癌胚抗原检查项
-- 日期：2026-06-30
-- 说明：
--   1. 新增甲胎蛋白 (AFP) 检查字段
--   2. 新增癌胚抗原 (CEA) 检查字段
--   3. 所有新增字段均为可选（Nullable），不影响现有数据
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN afp VARCHAR(20) COMMENT '甲胎蛋白(ng/ml)',
    ADD COLUMN afp_check_date VARCHAR(20) COMMENT '甲胎蛋白检查日期',
    ADD COLUMN cea VARCHAR(20) COMMENT '癌胚抗原(ng/ml)',
    ADD COLUMN cea_check_date VARCHAR(20) COMMENT '癌胚抗原检查日期';
