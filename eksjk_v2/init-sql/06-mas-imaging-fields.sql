-- ============================================================
-- 06-mas-imaging-fields.sql
-- MAS (McCune-Albright) 影像学检查字段补充
-- ============================================================

ALTER TABLE datamain_mas ADD COLUMN adrenal_ult VARCHAR(500) NULL COMMENT '肾上腺B超';
ALTER TABLE datamain_mas ADD COLUMN renal_ult VARCHAR(500) NULL COMMENT '肾脏B超';
ALTER TABLE datamain_mas ADD COLUMN bone_x_ray VARCHAR(500) NULL COMMENT '骨骼X线';
ALTER TABLE datamain_mas ADD COLUMN cardiac_ult VARCHAR(500) NULL COMMENT '心脏B超';
ALTER TABLE datamain_mas ADD COLUMN mr_part VARCHAR(100) NULL COMMENT 'MR部位';
ALTER TABLE datamain_mas ADD COLUMN mr_result VARCHAR(500) NULL COMMENT 'MR结果';
ALTER TABLE datamain_mas ADD COLUMN ct_part VARCHAR(100) NULL COMMENT 'CT部位';
ALTER TABLE datamain_mas ADD COLUMN ct_result VARCHAR(500) NULL COMMENT 'CT结果';
