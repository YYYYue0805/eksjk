CREATE TABLE IF NOT EXISTS `informed_consent` (
  `id`               BIGINT AUTO_INCREMENT PRIMARY KEY,
  `patient_id`       BIGINT       NOT NULL COMMENT '本系统患者ID',
  `eltm_id`          VARCHAR(64)  DEFAULT NULL COMMENT 'E路童萌患者唯一标识',
  `external_file_id` VARCHAR(128) DEFAULT NULL COMMENT 'E路童萌外部文件ID',
  `file_name`        VARCHAR(255) NOT NULL COMMENT '文件名称',
  `oss_url`          VARCHAR(512) NOT NULL COMMENT 'OSS 文件地址',
  `sign_time`        DATETIME     DEFAULT NULL COMMENT '签署时间',
  `patient_name`     VARCHAR(64)  DEFAULT NULL COMMENT '患者姓名',
  `doctor_name`      VARCHAR(64)  DEFAULT NULL COMMENT '公众号端医生姓名',
  `source`           VARCHAR(32)  DEFAULT 'ELTM' COMMENT '来源',
  `sync_time`        DATETIME     DEFAULT NULL COMMENT '同步时间',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `is_deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_eltm_id` (`eltm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知情同意书';
