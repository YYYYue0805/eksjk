-- EKSJK V2 家族成员扩展字段
-- 将家族史从 7 个静态字段改为 JSON 数组，支持动态添加多名家族成员
ALTER TABLE datamain_patient ADD COLUMN family_members TEXT COMMENT '家族成员信息（JSON数组）';
