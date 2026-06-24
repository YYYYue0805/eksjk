-- ============================================================
-- EKSJK V2 — 家庭关联升级 DDL（family_id）
-- 日期：2026-06-02
-- 说明：
--   1. Patient 主表新增 family_id 字段，用于同一家庭多名患者的分组
--   2. 现有数据自动生成独立的 family_id
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN IF NOT EXISTS family_id VARCHAR(36) COMMENT '家庭分组ID（UUID），同一家庭的患者共享';

CREATE INDEX IF NOT EXISTS idx_family_id ON datamain_patient(family_id);

-- 为现有患者生成独立的 family_id
UPDATE datamain_patient SET family_id = UUID() WHERE family_id IS NULL;
