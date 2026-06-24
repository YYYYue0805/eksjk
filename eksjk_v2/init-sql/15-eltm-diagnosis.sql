-- ELTM 智能诊断字段
-- 为 datamain_szfyeltm 表添加诊断状态相关字段

ALTER TABLE datamain_szfyeltm
  ADD COLUMN IF NOT EXISTS diagnosis_status VARCHAR(20) DEFAULT 'unclassified'
    COMMENT '诊断状态: unclassified/suggested/auto_classified/uncertain',
  ADD COLUMN IF NOT EXISTS suggested_dis_class VARCHAR(8) DEFAULT NULL
    COMMENT '建议的疾病分类代码 (10000001-10000006)',
  ADD COLUMN IF NOT EXISTS diagnosis_note TEXT DEFAULT NULL
    COMMENT '诊断说明（匹配的指标和得分详情）';

-- 为 datamain_patient 表添加诊断状态字段
ALTER TABLE datamain_patient
  ADD COLUMN IF NOT EXISTS diagnosis_status VARCHAR(20) DEFAULT NULL
    COMMENT 'ELTM诊断状态';
