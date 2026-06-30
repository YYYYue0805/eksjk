-- ============================================================
-- EKSJK V2 — 新增眼科检查模块
-- 日期：2026-06-30
-- 说明：
--   1. 患者主表新增眼科检查数据字段（JSON格式）
--   2. 随访表新增眼科检查数据字段（JSON格式）
--   3. JSON结构：{"hasExam":"0|1","examDate":"","nakedVisionRight":"","nakedVisionLeft":"","correctedVisionRight":"","correctedVisionLeft":"","axialLengthRight":"","axialLengthLeft":""}
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN eye_exam VARCHAR(500) COMMENT '眼科检查数据(JSON)';

ALTER TABLE datamain_patfoll
    ADD COLUMN eye_exam VARCHAR(500) COMMENT '眼科检查数据(JSON)';
