-- ============================================================
-- 病例详情页 Tab 重构 — 新增字段 DDL
-- 为 datamain_patient 表新增约 35 个辅助检查和遗传学字段
-- 所有字段均为 Nullable VARCHAR/TEXT，不影响现有数据
-- 执行方式: mysql -u eksjk -peksjk123 eksjk_db < 04-case-detail-fields.sql
-- ============================================================

-- Tanner 分期
ALTER TABLE datamain_patient ADD COLUMN gen_stag VARCHAR(10) COMMENT 'Tanner_stage_genital';
ALTER TABLE datamain_patient ADD COLUMN pub_stag VARCHAR(10) COMMENT 'Tanner_stage_pubic';

-- 体格扩展
ALTER TABLE datamain_patient ADD COLUMN height_sds VARCHAR(20) COMMENT 'height_SDS';
ALTER TABLE datamain_patient ADD COLUMN weight_sds VARCHAR(20) COMMENT 'weight_SDS';
ALTER TABLE datamain_patient ADD COLUMN lower_measure VARCHAR(20) COMMENT 'lower_measure_cm';

-- 性激素
ALTER TABLE datamain_patient ADD COLUMN lh VARCHAR(20) COMMENT 'LH_mIU_mL';
ALTER TABLE datamain_patient ADD COLUMN fsh VARCHAR(20) COMMENT 'FSH_mIU_mL';
ALTER TABLE datamain_patient ADD COLUMN e2_pgml VARCHAR(20) COMMENT 'E2_pg_mL';
ALTER TABLE datamain_patient ADD COLUMN t_ngdl VARCHAR(20) COMMENT 'T_ng_dL';
ALTER TABLE datamain_patient ADD COLUMN prl VARCHAR(20) COMMENT 'PRL_ng_mL';
ALTER TABLE datamain_patient ADD COLUMN dht VARCHAR(20) COMMENT 'DHT_ng_dL';
ALTER TABLE datamain_patient ADD COLUMN ft VARCHAR(20) COMMENT 'FT_ng_dL';
ALTER TABLE datamain_patient ADD COLUMN shbg VARCHAR(20) COMMENT 'SHBG_nmol_L';
ALTER TABLE datamain_patient ADD COLUMN amh VARCHAR(20) COMMENT 'AMH_ng_mL';
ALTER TABLE datamain_patient ADD COLUMN inhb VARCHAR(20) COMMENT 'INHB_pg_mL';

-- 生长因子/代谢
ALTER TABLE datamain_patient ADD COLUMN igf1 VARCHAR(20) COMMENT 'IGF1_ng_mL';
ALTER TABLE datamain_patient ADD COLUMN igfbp3 VARCHAR(20) COMMENT 'IGFBP3_ug_mL';
ALTER TABLE datamain_patient ADD COLUMN fas_blood_glu VARCHAR(20) COMMENT 'FBG_mmol_L';
ALTER TABLE datamain_patient ADD COLUMN fas_insulin VARCHAR(20) COMMENT 'FINS_uIU_mL';
ALTER TABLE datamain_patient ADD COLUMN gly_hem VARCHAR(20) COMMENT 'HbA1c_percent';

-- 肾上腺
ALTER TABLE datamain_patient ADD COLUMN acth VARCHAR(20) COMMENT 'ACTH_pg_mL';
ALTER TABLE datamain_patient ADD COLUMN cortisol VARCHAR(20) COMMENT 'cortisol_ug_dL';
ALTER TABLE datamain_patient ADD COLUMN ohp VARCHAR(20) COMMENT 'OHP17_ng_mL';
ALTER TABLE datamain_patient ADD COLUMN dheas VARCHAR(20) COMMENT 'DHEAS_ug_dL';
ALTER TABLE datamain_patient ADD COLUMN ad VARCHAR(20) COMMENT 'androstenedione_ng_mL';

-- 激发试验
ALTER TABLE datamain_patient ADD COLUMN hcg VARCHAR(20) COMMENT 'HCG_basal';
ALTER TABLE datamain_patient ADD COLUMN hcgt VARCHAR(20) COMMENT 'HCG_stimulated_T';
ALTER TABLE datamain_patient ADD COLUMN hcgdht VARCHAR(20) COMMENT 'HCG_stimulated_DHT';
ALTER TABLE datamain_patient ADD COLUMN hcgad VARCHAR(20) COMMENT 'HCG_stimulated_AD';
ALTER TABLE datamain_patient ADD COLUMN lh_max VARCHAR(20) COMMENT 'GnRH_LHmax';
ALTER TABLE datamain_patient ADD COLUMN fsh_max VARCHAR(20) COMMENT 'GnRH_FSHmax';

-- 甲状腺
ALTER TABLE datamain_patient ADD COLUMN tsh VARCHAR(20) COMMENT 'TSH_uIU_mL';
ALTER TABLE datamain_patient ADD COLUMN ft3 VARCHAR(20) COMMENT 'FT3_pg_mL';
ALTER TABLE datamain_patient ADD COLUMN ft4 VARCHAR(20) COMMENT 'FT4_ng_dL';
ALTER TABLE datamain_patient ADD COLUMN tpoab VARCHAR(20) COMMENT 'TPOAb_IU_mL';
ALTER TABLE datamain_patient ADD COLUMN tgab VARCHAR(20) COMMENT 'TgAb_IU_mL';

-- 影像描述
ALTER TABLE datamain_patient ADD COLUMN gon_b_ult TEXT COMMENT 'gonadal_B_ultrasound';
ALTER TABLE datamain_patient ADD COLUMN pituitary_mri TEXT COMMENT 'pituitary_MRI';
ALTER TABLE datamain_patient ADD COLUMN thyroid_ult TEXT COMMENT 'thyroid_ultrasound';
ALTER TABLE datamain_patient ADD COLUMN bon_min_den TEXT COMMENT 'bone_mineral_density';

-- 遗传学
ALTER TABLE datamain_patient ADD COLUMN karyotype VARCHAR(200) COMMENT 'karyotype';
ALTER TABLE datamain_patient ADD COLUMN biolog_bank VARCHAR(500) COMMENT 'patient_bio_bank';
ALTER TABLE datamain_patient ADD COLUMN biolog_bank_fa VARCHAR(500) COMMENT 'father_bio_bank';
ALTER TABLE datamain_patient ADD COLUMN biolog_bank_mo VARCHAR(500) COMMENT 'mother_bio_bank';
