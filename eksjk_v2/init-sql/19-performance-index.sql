-- ============================================================
-- 病例列表查询性能优化 — 添加复合索引
-- 查询条件: del_flg + dis_class + hospital_name  ORDER BY c_time DESC
-- 执行方式: mysql -u eksjk -peksjk123 eksjk_db < 19-performance-index.sql
-- ============================================================

-- 病例列表查询专用索引（覆盖 WHERE + ORDER BY 条件）
CREATE INDEX idx_patient_list ON datamain_patient (del_flg, dis_class, hospital_name, c_time DESC);

-- 审核列表查询也用到相同字段组合
-- CREATE INDEX 已覆盖 audit list 查询（auditServiceImpl.applyDataScope 使用相同条件）
