-- ============================================================
-- EKSJK V2 本地开发环境 — 种子数据
-- 密码统一为 Test@1234（BCrypt 加密）
-- ============================================================

-- 测试医院/单位
INSERT IGNORE INTO login_unit (id, unit_name, unit_code, del_flg, unit_short_name, contact_name, contact_phone, province, city, district, status, created_at)
VALUES (1, '测试医院A', 'HOSPITAL_A', '1', '医院A', '张主任', '13800000001', '广东省', '深圳市', '南山区', 1, NOW());

INSERT IGNORE INTO login_unit (id, unit_name, unit_code, del_flg, unit_short_name, contact_name, contact_phone, province, city, district, status, created_at)
VALUES (2, '测试医院B', 'HOSPITAL_B', '1', '医院B', '李主任', '13800000002', '广东省', '广州市', '天河区', 1, NOW());

-- 测试用户（密码 Test@1234）
-- BCrypt: $2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2

-- 超级管理员
INSERT IGNORE INTO login_user (id, username, password, name, role_code, is_superuser, is_active, is_staff, level, date_joined, is_deleted)
VALUES (1, 'super_admin', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '超级管理员', 'super_admin', 1, 1, 1, 1, NOW(), 0);

-- 医院管理员1（管理测试医院A）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, is_staff, level, date_joined, is_deleted)
VALUES (2, 'hospital_admin_1', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '医院A管理员', 'hospital_admin', '1', 1, 1, 1, NOW(), 0);

-- 医院管理员2（管理测试医院B）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, is_staff, level, date_joined, is_deleted)
VALUES (3, 'hospital_admin_2', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '医院B管理员', 'hospital_admin', '2', 1, 1, 1, NOW(), 0);

-- 普通医生1（测试医院A）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (4, 'doctor_1', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '张医生', 'doctor', '1', 1, 0, NOW(), 0);

-- 普通医生2（测试医院A）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (5, 'doctor_2', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '李医生', 'doctor', '1', 1, 0, NOW(), 0);

-- 普通医生3（测试医院B）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (6, 'doctor_3', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '王医生', 'doctor', '2', 1, 0, NOW(), 0);

-- 家长用户（小程序端）
INSERT IGNORE INTO login_user (id, username, password, name, role_code, phone, is_active, level, date_joined, is_deleted)
VALUES (7, 'parent_1', '$2a$10$0tUv1PbqjogBXA8DCSSvv.CpKWZ1MeCOhh.r6F0cjfKsC4xef2Wp2', '测试家长', 'parent', '13800001111', 1, 0, NOW(), 0);
