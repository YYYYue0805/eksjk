-- ============================================================
-- EKSJK V2 — 新增甲状腺B超详细模块
-- 日期：2026-07-06
-- 说明：
--   1. 患者主表新增甲状腺B超详情字段（JSON格式）
--   2. JSON结构：
--      {
--        "leftResult": "1|2",
--        "leftNoduleGrade": "1-5",
--        "leftSize": "",
--        "leftDiffuseLesion": "",
--        "leftOther": "",
--        "rightResult": "1|2",
--        "rightNoduleGrade": "1-5",
--        "rightSize": "",
--        "rightDiffuseLesion": "",
--        "rightOther": ""
--      }
-- ============================================================

ALTER TABLE datamain_patient
    ADD COLUMN thyroid_ult_detail TEXT COMMENT '甲状腺B超详情（JSON，含左右侧结节分级、大小、弥漫性病变等）';
