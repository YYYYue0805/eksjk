-- ============================================================
-- EKSJK V2 Mock 数据 — 医院/单位
-- 说明：创建 2 家测试医院，用于验证多租户数据隔离
-- ============================================================
USE eksjk;

-- 测试医院A：有 2 名医生，多名患者
INSERT IGNORE INTO login_unit (id, unit_name, unit_code, del_flg, unit_short_name, contact_name, contact_phone, province, city, district, status, created_at)
VALUES (1, '测试医院A', 'HOSPITAL_A', '1', '医院A', '张主任', '13800000001', '广东省', '深圳市', '南山区', 1, NOW());

-- 测试医院B：有 1 名医生，少量患者
INSERT IGNORE INTO login_unit (id, unit_name, unit_code, del_flg, unit_short_name, contact_name, contact_phone, province, city, district, status, created_at)
VALUES (2, '测试医院B', 'HOSPITAL_B', '1', '医院B', '李主任', '13800000002', '广东省', '广州市', '天河区', 1, NOW());
