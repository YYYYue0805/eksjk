-- 基线-辅助检查：心电图备注与检查时间（独立小表，避免主表 datamain_patient 行大小超限）
CREATE TABLE IF NOT EXISTS patient_ecg_info (
    patient_id      BIGINT      NOT NULL COMMENT '病例ID',
    ecg_note        TEXT        NULL COMMENT '心电图备注',
    ecg_check_date  VARCHAR(50) NULL COMMENT '心电图检查时间',
    update_time     DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (patient_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='病例心电图信息';
