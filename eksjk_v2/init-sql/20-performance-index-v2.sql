-- ============================================================
-- 病例列表查询性能优化 — 补充医生角色索引
-- 查询条件: del_flg + imp_per + dis_class  ORDER BY c_time DESC
-- 执行方式: mysql -u eksjk -peksjk123 eksjk_db < 20-performance-index-v2.sql
-- ============================================================

-- 普通医生角色（最常用）的列表查询专用索引
-- 覆盖: WHERE del_flg='1' AND imp_per=? AND dis_class=? ORDER BY c_time DESC
CREATE INDEX idx_patient_doctor ON datamain_patient (del_flg, imp_per, dis_class, c_time DESC);

-- 医院管理员角色查询索引
-- 覆盖: WHERE del_flg='1' AND hospital_name=? ORDER BY c_time DESC
CREATE INDEX idx_patient_hospital ON datamain_patient (del_flg, hospital_name, dis_class, c_time DESC);
