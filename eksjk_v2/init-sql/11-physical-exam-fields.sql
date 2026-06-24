-- ============================================================
-- EKSJK V2 — 体格检查字段扩展
-- 说明：在患者主表 datamain_patient 中新增 9 个体格检查字段
-- ============================================================

-- 臂长
ALTER TABLE datamain_patient ADD COLUMN arm_length VARCHAR(20) NULL COMMENT '臂长(cm)';

-- 特殊面容（1=无, 2=有）
ALTER TABLE datamain_patient ADD COLUMN special_face VARCHAR(10) NULL COMMENT '特殊面容 1=无 2=有';
ALTER TABLE datamain_patient ADD COLUMN special_face_desc VARCHAR(500) NULL COMMENT '特殊面容描述';

-- 脊柱侧弯（1=无, 2=有）
ALTER TABLE datamain_patient ADD COLUMN scoliosis VARCHAR(10) NULL COMMENT '脊柱侧弯 1=无 2=有';
ALTER TABLE datamain_patient ADD COLUMN scoliosis_desc VARCHAR(500) NULL COMMENT '脊柱侧弯描述';

-- 皮疹（1=无, 2=有）
ALTER TABLE datamain_patient ADD COLUMN rash VARCHAR(10) NULL COMMENT '皮疹 1=无 2=有';
ALTER TABLE datamain_patient ADD COLUMN rash_desc VARCHAR(500) NULL COMMENT '皮疹描述';

-- 乳腺发育（女）
ALTER TABLE datamain_patient ADD COLUMN breast_dev_left VARCHAR(10) NULL COMMENT '左侧乳腺发育 B1-B5';
ALTER TABLE datamain_patient ADD COLUMN breast_dev_right VARCHAR(10) NULL COMMENT '右侧乳腺发育 B1-B5';
