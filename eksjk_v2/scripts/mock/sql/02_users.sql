-- ============================================================
-- EKSJK V2 Mock 数据 — 用户与权限
-- 说明：创建 7 个测试账号，覆盖所有角色
-- 密码统一为 Test@1234（BCrypt 加密）
-- BCrypt hash: $2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe
-- ============================================================
USE eksjk;

-- 超级管理员：可管理所有数据
INSERT IGNORE INTO login_user (id, username, password, name, role_code, is_superuser, is_active, is_staff, level, date_joined, is_deleted)
VALUES (1, 'super_admin', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '超级管理员', 'super_admin', TRUE, TRUE, TRUE, 1, NOW(), 0);

-- 医院管理员1：管理测试医院A
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, is_staff, level, date_joined, is_deleted)
VALUES (2, 'hospital_admin_1', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '医院A管理员', 'hospital_admin', '1', TRUE, TRUE, 1, NOW(), 0);

-- 医院管理员2：管理测试医院B
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, is_staff, level, date_joined, is_deleted)
VALUES (3, 'hospital_admin_2', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '医院B管理员', 'hospital_admin', '2', TRUE, TRUE, 1, NOW(), 0);

-- 普通医生1：测试医院A，管理自己的病例
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (4, 'doctor_1', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '张医生', 'doctor', '1', TRUE, 0, NOW(), 0);

-- 普通医生2：测试医院A，管理自己的病例
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (5, 'doctor_2', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '李医生', 'doctor', '1', TRUE, 0, NOW(), 0);

-- 普通医生3：测试医院B，管理自己的病例
INSERT IGNORE INTO login_user (id, username, password, name, role_code, unit, is_active, level, date_joined, is_deleted)
VALUES (6, 'doctor_3', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '王医生', 'doctor', '2', TRUE, 0, NOW(), 0);

-- 家长用户：小程序端访问
INSERT IGNORE INTO login_user (id, username, password, name, role_code, phone, is_active, level, date_joined, is_deleted)
VALUES (7, 'parent_1', '$2a$10$/aDcvyHseo9my0Z0yhOD7uaoWONYfghND0VU2UoESAcz2ECJZZUbe', '测试家长', 'parent', '13800001111', TRUE, 0, NOW(), 0);
