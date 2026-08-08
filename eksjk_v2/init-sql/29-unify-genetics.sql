-- ============================================================
-- 统一遗传学检查模块：染色体核型独立表 patient_genetics
-- 日期：2026-08-08
-- 说明：
--   1. Patient 主表及多数病种子表行大小已超 InnoDB 8126 限制，无法新增列
--   2. 染色体核型多选(chrom/chrom_other)统一存储于独立表 patient_genetics
--   3. 所有病种共用此表，保证遗传学检查设计一致
-- ============================================================

CREATE TABLE IF NOT EXISTS patient_genetics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE COMMENT '病例主表ID',
    chrom TEXT COMMENT '染色体核型(JSON数组)',
    chrom_other TEXT COMMENT '其它异常核型',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='患者遗传学染色体核型独立表';

-- ==================== 兼容迁移已有 ELTM 数据 ====================
-- 将已有 ELTM 子表的染色体核型数据迁入独立表（当前无数据，防御性保留）

INSERT IGNORE INTO patient_genetics (patient_id, chrom, chrom_other)
SELECT e.patient_id, e.chrom, e.chrom_other
FROM datamain_szfyeltm e
WHERE e.chrom IS NOT NULL AND e.chrom != '';
