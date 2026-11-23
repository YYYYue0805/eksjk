-- 外部同步（ELTM / datamain）病例不再直接进入审核发放流程，
-- 统一标记为「未上传」，需在病例管理中对单个病例点击「上传」后再进入审核。
-- 仅迁移尚未进入审核流转（pending_review）的同步数据；已发放/已驳回等保持原状。
UPDATE datamain_patient
SET audit_status = 'pending_upload',
    modify_time = NOW()
WHERE del_flg = '1'
  AND sync_status = 'synced'
  AND audit_status = 'pending_review';
