-- ============================================================
-- 17-audit-fields.sql
-- 新增审核发放字段（Patient + PatientFollowUp）
-- 存量数据默认 released，视为历史已生效数据
-- ============================================================

ALTER TABLE datamain_patient
  ADD COLUMN audit_status VARCHAR(20) DEFAULT 'released' COMMENT '审核状态: pending_review/pending_release/released/rejected',
  ADD COLUMN audit_by    VARCHAR(100) DEFAULT NULL COMMENT '审核人(用户名)',
  ADD COLUMN audit_time  DATETIME     DEFAULT NULL COMMENT '审核时间',
  ADD COLUMN release_by  VARCHAR(100) DEFAULT NULL COMMENT '发放人(用户名)',
  ADD COLUMN release_time DATETIME    DEFAULT NULL COMMENT '发放时间',
  ADD COLUMN audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核意见';

ALTER TABLE datamain_patfoll
  ADD COLUMN audit_status VARCHAR(20) DEFAULT 'released' COMMENT '审核状态: pending_review/pending_release/released/rejected',
  ADD COLUMN audit_by    VARCHAR(100) DEFAULT NULL COMMENT '审核人(用户名)',
  ADD COLUMN audit_time  DATETIME     DEFAULT NULL COMMENT '审核时间',
  ADD COLUMN release_by  VARCHAR(100) DEFAULT NULL COMMENT '发放人(用户名)',
  ADD COLUMN release_time DATETIME    DEFAULT NULL COMMENT '发放时间',
  ADD COLUMN audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核意见';
