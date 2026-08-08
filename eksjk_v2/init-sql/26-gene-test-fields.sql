-- ============================================================
-- EKSJK V2 — 遗传学检查新增基因检测方法和结果字段
-- 日期：2026-07-06
-- 说明：
--   1. 患者主表新增基因检测方法（单选：先证者WES/Trio-WES/CNV测序/CMA/WGS/其他）
--   2. 患者主表新增基因检测结果（阴性/阳性，阳性时展示基因突变检测表）
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN gene_test_method VARCHAR(255) COMMENT '基因检测方法：先证者WES/Trio-WES/CNV测序/CMA/WGS/其他',
    ADD COLUMN gene_test_result VARCHAR(10) COMMENT '基因检测结果：阴性/阳性';
