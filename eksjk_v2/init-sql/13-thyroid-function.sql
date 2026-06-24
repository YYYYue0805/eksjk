-- ============================================================
-- EKSJK V2 — 添加甲状腺功能评估字段
-- 格式：1=正常, 2|描述=异常
-- ============================================================
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS thyroid_function VARCHAR(500) DEFAULT NULL COMMENT '甲状腺功能';
