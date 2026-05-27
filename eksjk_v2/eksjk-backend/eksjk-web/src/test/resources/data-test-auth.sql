-- 集成测试用认证数据
-- 密码均为 Test@1234 的 BCrypt 加密值

-- 超级管理员
INSERT INTO login_user (id, username, password, name, role_code, is_active, is_superuser, level, date_joined)
VALUES (1, 'super_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'super_admin', TRUE, TRUE, 1, CURRENT_TIMESTAMP);

-- 医院管理员
INSERT INTO login_user (id, username, password, name, role_code, unit, is_active, is_superuser, level, date_joined)
VALUES (2, 'hospital_admin_1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '医院管理员A', 'hospital_admin', '100', TRUE, FALSE, 0, CURRENT_TIMESTAMP);

-- 普通医生
INSERT INTO login_user (id, username, password, name, role_code, unit, is_active, is_superuser, level, date_joined)
VALUES (3, 'doctor_1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试医生A', 'doctor', '100', TRUE, FALSE, 0, CURRENT_TIMESTAMP);

-- 另一个医生（医院B）
INSERT INTO login_user (id, username, password, name, role_code, unit, is_active, is_superuser, level, date_joined)
VALUES (4, 'doctor_2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试医生B', 'doctor', '200', TRUE, FALSE, 0, CURRENT_TIMESTAMP);

-- 被禁用的账号
INSERT INTO login_user (id, username, password, name, role_code, is_active, is_superuser, level, date_joined)
VALUES (5, 'disabled_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '禁用用户', 'doctor', FALSE, FALSE, 0, CURRENT_TIMESTAMP);

-- 测试医院
INSERT INTO unit (id, name, code) VALUES (100, '测试医院A', 'HOSPITAL_A');
INSERT INTO unit (id, name, code) VALUES (200, '测试医院B', 'HOSPITAL_B');

-- 测试患者数据（DSD 类型）
INSERT INTO datamain_patient (id, dis_class, case_num, name, sex, height, weight, bmi, doctor_name, hospital_name, up_mec, imp_per, c_time, del_flg)
VALUES (1, '10000001', 'DSD-20240001', '测试患者_DSD_001', '男', '120.5', '25.0', '17.2', '测试医生A', '测试医院A', '100', '3', CURRENT_TIMESTAMP, '1');

INSERT INTO datamain_patient (id, dis_class, case_num, name, sex, height, weight, bmi, doctor_name, hospital_name, up_mec, imp_per, c_time, del_flg)
VALUES (2, '10000001', 'DSD-20240002', '测试患者_DSD_002', '女', '115.0', '22.0', '16.6', '测试医生A', '测试医院A', '100', '3', CURRENT_TIMESTAMP, '1');

-- 测试患者数据（CPP 类型，医院B）
INSERT INTO datamain_patient (id, dis_class, case_num, name, sex, height, weight, bmi, doctor_name, hospital_name, up_mec, imp_per, c_time, del_flg)
VALUES (3, '10000003', 'CPP-20240001', '测试患者_CPP_001', '女', '130.0', '30.0', '17.8', '测试医生B', '测试医院B', '200', '4', CURRENT_TIMESTAMP, '1');

-- 测试随访数据
INSERT INTO datamain_patfoll (id, patient_id, foll_time, age, `Ht`, `Wt`, bmi, rbone_age, del_flg)
VALUES (1, 1, '2024-01-15 10:00:00', '9', '120.5', '25.0', '17.2', '8.5', '1');

INSERT INTO datamain_patfoll (id, patient_id, foll_time, age, `Ht`, `Wt`, bmi, rbone_age, del_flg)
VALUES (2, 1, '2024-07-15 10:00:00', '9.5', '123.0', '26.5', '17.5', '9.0', '1');

INSERT INTO datamain_patfoll (id, patient_id, foll_time, age, `Ht`, `Wt`, bmi, rbone_age, del_flg)
VALUES (3, 2, '2024-03-20 10:00:00', '8', '115.0', '22.0', '16.6', '7.5', '1');
