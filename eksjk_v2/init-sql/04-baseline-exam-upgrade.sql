-- ============================================================
-- EKSJK V2 — 基线辅助检查升级 DDL（性腺B超 + 甲状腺功能）
-- 日期：2026-06-02
-- 说明：
--   1. Patient 主表新增 B超尺寸详情 + 激素检查日期字段
--   2. PatientFollowUp 随访表新增甲状腺结构化字段
--   3. 所有新增字段均为可选，不影响现有数据
-- ============================================================

-- ==================== Patient 主表 ====================

ALTER TABLE datamain_patient
    ADD COLUMN IF NOT EXISTS hormone_check_date VARCHAR(20) COMMENT '性激素检查日期',
    ADD COLUMN IF NOT EXISTS thyroid_check_date VARCHAR(20) COMMENT '甲状腺检查日期',
    ADD COLUMN IF NOT EXISTS adrenal_check_date VARCHAR(20) COMMENT '肾上腺检查日期',
    ADD COLUMN IF NOT EXISTS growth_factor_check_date VARCHAR(20) COMMENT '生长因子检查日期',
    ADD COLUMN IF NOT EXISTS provocation_check_date VARCHAR(20) COMMENT '激发试验检查日期',
    ADD COLUMN IF NOT EXISTS blood_routine VARCHAR(500) COMMENT '血常规（编码格式）',
    ADD COLUMN IF NOT EXISTS urine_routine VARCHAR(500) COMMENT '尿常规（编码格式）',
    ADD COLUMN IF NOT EXISTS hepatitis_b VARCHAR(50) COMMENT '乙肝三系',
    ADD COLUMN IF NOT EXISTS liver_kidney_electrolyte VARCHAR(2000) COMMENT '肝肾糖电解质',
    ADD COLUMN IF NOT EXISTS gon_b_ult_detail TEXT COMMENT '性腺B超尺寸详情（JSON，含子宫/卵巢/睾丸三维尺寸）';

-- ==================== PatientFollowUp 随访表 ====================

ALTER TABLE datamain_patfoll
    ADD COLUMN IF NOT EXISTS tsh VARCHAR(20) COMMENT 'TSH (uIU/mL)',
    ADD COLUMN IF NOT EXISTS ft3 VARCHAR(20) COMMENT 'FT3 (pg/mL)',
    ADD COLUMN IF NOT EXISTS ft4 VARCHAR(20) COMMENT 'FT4 (ng/dL)',
    ADD COLUMN IF NOT EXISTS tpoab VARCHAR(20) COMMENT 'TPOAb (IU/mL)',
    ADD COLUMN IF NOT EXISTS tgab VARCHAR(20) COMMENT 'TgAb (IU/mL)';
