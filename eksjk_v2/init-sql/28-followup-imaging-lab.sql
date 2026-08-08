-- 随访影像检查 & 常规实验室检查对齐基线辅助检查
-- 为 datamain_patfoll 表新增卵巢囊肿字段

ALTER TABLE datamain_patfoll
    ADD COLUMN ovarian_cyst VARCHAR(20) DEFAULT NULL COMMENT '卵巢囊肿 0|/1|/2|描述';
