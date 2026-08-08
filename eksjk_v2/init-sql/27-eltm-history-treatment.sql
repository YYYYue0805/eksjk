-- ============================================================================
-- 27-eltm-history-treatment: ELTM 表新增结构化既往史和诊疗方案字段
-- 对齐 SSS/FSS/SGA 的基线-临床信息模块功能
-- ============================================================================

-- 检查并添加列（MySQL 8.0 兼容，幂等）
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

DELIMITER ;;
CREATE PROCEDURE add_column_if_not_exists(
    IN tbl_name VARCHAR(128),
    IN col_name VARCHAR(128),
    IN col_def VARCHAR(512)
)
BEGIN
    DECLARE col_count INT;
    SELECT COUNT(*) INTO col_count
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND COLUMN_NAME = col_name;
    IF col_count = 0 THEN
        SET @ddl = CONCAT('ALTER TABLE `', tbl_name, '` ADD COLUMN `', col_name, '` ', col_def);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;;
DELIMITER ;

CALL add_column_if_not_exists('datamain_szfyeltm', 'mot_dev_back',  "VARCHAR(32)   DEFAULT NULL COMMENT '运动发育落后 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'sport',        "VARCHAR(255)  DEFAULT NULL COMMENT '运动发育落后描述'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'lan_dev_back', "VARCHAR(32)   DEFAULT NULL COMMENT '语言发育落后 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'language',     "VARCHAR(255)  DEFAULT NULL COMMENT '语言发育落后描述'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'int_dev_back', "VARCHAR(32)   DEFAULT NULL COMMENT '智力发育落后 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'intelligence', "VARCHAR(255)  DEFAULT NULL COMMENT '智力发育落后描述'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'abn_hear',     "VARCHAR(32)   DEFAULT NULL COMMENT '听力异常 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'hear',         "VARCHAR(255)  DEFAULT NULL COMMENT '听力异常描述'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'rec_inf_his',  "VARCHAR(32)   DEFAULT NULL COMMENT '反复感染史 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'infection',    "VARCHAR(255)  DEFAULT NULL COMMENT '反复感染史描述'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'con_his',      "VARCHAR(32)   DEFAULT NULL COMMENT '抽搐史 1=无 2=有'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'past_other',   "VARCHAR(500)  DEFAULT NULL COMMENT '其他既往史'");
CALL add_column_if_not_exists('datamain_szfyeltm', 'dia_trea_plan', "TEXT         DEFAULT NULL COMMENT '诊疗方案JSON'");

DROP PROCEDURE IF EXISTS add_column_if_not_exists;
