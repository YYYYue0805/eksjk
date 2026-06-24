-- ============================================================
-- EKSJK V2 — 基线辅助检查：每个检验项目独立检查日期
-- 日期：2026-06-04
-- 说明：
--   1. 每个激素/生长因子/肾上腺/激发试验/甲状腺检验项增加独立检查日期列
--   2. 保留旧的分类级日期列以兼容旧数据
--   3. 迁移现有数据：将旧分类日期复制到对应各类别下每一个项目的新列
-- ============================================================

ALTER TABLE datamain_patient
    -- 性激素及相关 (10 items)
    ADD COLUMN IF NOT EXISTS lh_check_date VARCHAR(20) COMMENT 'LH检查日期',
    ADD COLUMN IF NOT EXISTS fsh_check_date VARCHAR(20) COMMENT 'FSH检查日期',
    ADD COLUMN IF NOT EXISTS e2_check_date VARCHAR(20) COMMENT 'E2检查日期',
    ADD COLUMN IF NOT EXISTS t_check_date VARCHAR(20) COMMENT 'T检查日期',
    ADD COLUMN IF NOT EXISTS prl_check_date VARCHAR(20) COMMENT 'PRL检查日期',
    ADD COLUMN IF NOT EXISTS dht_check_date VARCHAR(20) COMMENT 'DHT检查日期',
    ADD COLUMN IF NOT EXISTS ft_check_date VARCHAR(20) COMMENT 'FT检查日期',
    ADD COLUMN IF NOT EXISTS shbg_check_date VARCHAR(20) COMMENT 'SHBG检查日期',
    ADD COLUMN IF NOT EXISTS amh_check_date VARCHAR(20) COMMENT 'AMH检查日期',
    ADD COLUMN IF NOT EXISTS inhb_check_date VARCHAR(20) COMMENT 'INHB检查日期',
    -- 生长因子与代谢 (5 items)
    ADD COLUMN IF NOT EXISTS igf1_check_date VARCHAR(20) COMMENT 'IGF-1检查日期',
    ADD COLUMN IF NOT EXISTS igfbp3_check_date VARCHAR(20) COMMENT 'IGFBP-3检查日期',
    ADD COLUMN IF NOT EXISTS fas_blood_glu_check_date VARCHAR(20) COMMENT '空腹血糖检查日期',
    ADD COLUMN IF NOT EXISTS fas_insulin_check_date VARCHAR(20) COMMENT '空腹胰岛素检查日期',
    ADD COLUMN IF NOT EXISTS gly_hem_check_date VARCHAR(20) COMMENT '糖化血红蛋白检查日期',
    -- 肾上腺激素 (5 items)
    ADD COLUMN IF NOT EXISTS acth_check_date VARCHAR(20) COMMENT 'ACTH检查日期',
    ADD COLUMN IF NOT EXISTS cortisol_check_date VARCHAR(20) COMMENT '皮质醇检查日期',
    ADD COLUMN IF NOT EXISTS ohp_check_date VARCHAR(20) COMMENT '17-OHP检查日期',
    ADD COLUMN IF NOT EXISTS dheas_check_date VARCHAR(20) COMMENT 'DHEA-S检查日期',
    ADD COLUMN IF NOT EXISTS androstenedione_check_date VARCHAR(20) COMMENT '雄烯二酮检查日期',
    -- 激发试验 (6 items)
    ADD COLUMN IF NOT EXISTS hcg_check_date VARCHAR(20) COMMENT 'HCG激发前T检查日期',
    ADD COLUMN IF NOT EXISTS hcgt_check_date VARCHAR(20) COMMENT 'HCG激发后T检查日期',
    ADD COLUMN IF NOT EXISTS hcgdht_check_date VARCHAR(20) COMMENT 'HCG激发后DHT检查日期',
    ADD COLUMN IF NOT EXISTS hcgad_check_date VARCHAR(20) COMMENT 'HCG激发后AD检查日期',
    ADD COLUMN IF NOT EXISTS lh_max_check_date VARCHAR(20) COMMENT 'GnRH激发LHmax检查日期',
    ADD COLUMN IF NOT EXISTS fsh_max_check_date VARCHAR(20) COMMENT 'GnRH激发FSHmax检查日期',
    -- 甲状腺功能 (5 items)
    ADD COLUMN IF NOT EXISTS tsh_check_date VARCHAR(20) COMMENT 'TSH检查日期',
    ADD COLUMN IF NOT EXISTS ft3_check_date VARCHAR(20) COMMENT 'FT3检查日期',
    ADD COLUMN IF NOT EXISTS ft4_check_date VARCHAR(20) COMMENT 'FT4检查日期',
    ADD COLUMN IF NOT EXISTS tpoab_check_date VARCHAR(20) COMMENT 'TPOAb检查日期',
    ADD COLUMN IF NOT EXISTS tgab_check_date VARCHAR(20) COMMENT 'TgAb检查日期';

-- ============================================================
-- 数据迁移：将旧分类检查日期复制到对应类别下的每个检验项
-- ============================================================

-- 性激素：hormone_check_date -> 10个激素项
UPDATE datamain_patient SET
    lh_check_date = hormone_check_date,
    fsh_check_date = hormone_check_date,
    e2_check_date = hormone_check_date,
    t_check_date = hormone_check_date,
    prl_check_date = hormone_check_date,
    dht_check_date = hormone_check_date,
    ft_check_date = hormone_check_date,
    shbg_check_date = hormone_check_date,
    amh_check_date = hormone_check_date,
    inhb_check_date = hormone_check_date
WHERE hormone_check_date IS NOT NULL AND hormone_check_date != '';

-- 生长因子：growth_factor_check_date -> 5个代谢项
UPDATE datamain_patient SET
    igf1_check_date = growth_factor_check_date,
    igfbp3_check_date = growth_factor_check_date,
    fas_blood_glu_check_date = growth_factor_check_date,
    fas_insulin_check_date = growth_factor_check_date,
    gly_hem_check_date = growth_factor_check_date
WHERE growth_factor_check_date IS NOT NULL AND growth_factor_check_date != '';

-- 肾上腺：adrenal_check_date -> 5个肾上腺项
UPDATE datamain_patient SET
    acth_check_date = adrenal_check_date,
    cortisol_check_date = adrenal_check_date,
    ohp_check_date = adrenal_check_date,
    dheas_check_date = adrenal_check_date,
    androstenedione_check_date = adrenal_check_date
WHERE adrenal_check_date IS NOT NULL AND adrenal_check_date != '';

-- 激发试验：provocation_check_date -> 6个激发项
UPDATE datamain_patient SET
    hcg_check_date = provocation_check_date,
    hcgt_check_date = provocation_check_date,
    hcgdht_check_date = provocation_check_date,
    hcgad_check_date = provocation_check_date,
    lh_max_check_date = provocation_check_date,
    fsh_max_check_date = provocation_check_date
WHERE provocation_check_date IS NOT NULL AND provocation_check_date != '';

-- 甲状腺：thyroid_check_date -> 5个甲状腺项
UPDATE datamain_patient SET
    tsh_check_date = thyroid_check_date,
    ft3_check_date = thyroid_check_date,
    ft4_check_date = thyroid_check_date,
    tpoab_check_date = thyroid_check_date,
    tgab_check_date = thyroid_check_date
WHERE thyroid_check_date IS NOT NULL AND thyroid_check_date != '';
