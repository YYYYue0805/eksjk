-- ============================================================
-- 遗传学检查模块完整升级 DDL
-- 为 Patient/MasCase/EltmCase 表新增遗传学相关字段
-- 所有字段均为 Nullable，不影响现有数据
-- 使用 ADD COLUMN IF NOT EXISTS 确保幂等性 (MySQL 8.0+)
-- 执行方式: mysql -u eksjk -peksjk123 eksjk_db < 18-genetics-exam-upgrade.sql
-- ============================================================

-- ==================== Patient 主表 ====================

-- 补充缺失的 gen_data 列（Java Entity 已定义但 SQL 迁移中遗漏）
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS gen_data TEXT COMMENT '基因突变检测数据(JSON数组)';

-- 新增遗传学检查字段
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS surgery_note TEXT COMMENT '手术情况';
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS pathology_result TEXT COMMENT '病理结果';
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS treatment_opinion TEXT COMMENT '处理意见';
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS genetics_other TEXT COMMENT '遗传学其他';
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS biolog_bank_data TEXT COMMENT '生物样本库详情(JSON数组)';

-- ==================== MAS 子表 (datamain_mas) ====================

ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS gnas VARCHAR(20) COMMENT 'GNAS基因测定:1=是,2=否,3=不详';
ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS gnas_sam_loc VARCHAR(500) COMMENT '标本采样类型或部位';
ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS gen_tes_met VARCHAR(200) COMMENT '遗传学检测方法';
ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS det_res VARCHAR(500) COMMENT '检测结果';
ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS det_ver VARCHAR(100) COMMENT '检测版本';
ALTER TABLE datamain_mas ADD COLUMN IF NOT EXISTS mut_sit VARCHAR(500) COMMENT '突变位点';

-- ==================== ELTM 子表 (datamain_szfyeltm) ====================

ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_method VARCHAR(200) COMMENT '基因检测方法';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_res VARCHAR(20) COMMENT '基因结果:阴性/阳性';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_name VARCHAR(200) COMMENT '基因名称';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_point VARCHAR(500) COMMENT '突变位点';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_type VARCHAR(200) COMMENT '突变类型';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS gene_mode VARCHAR(200) COMMENT '遗传模式';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS chrom VARCHAR(500) COMMENT '染色体核型(JSON数组)';
ALTER TABLE datamain_szfyeltm ADD COLUMN IF NOT EXISTS chrom_other VARCHAR(200) COMMENT '其它异常核型';
