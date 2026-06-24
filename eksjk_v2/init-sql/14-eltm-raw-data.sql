-- 14-eltm-raw-data.sql
-- ELTM 模块：添加原始同步数据字段
-- 用于存储从外部 E路童萌 系统导入的完整原始记录（JSON格式）

ALTER TABLE datamain_szfyeltm ADD COLUMN raw_data TEXT COMMENT '原始同步数据(JSON)';
