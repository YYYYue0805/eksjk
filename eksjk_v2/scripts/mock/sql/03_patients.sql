-- ============================================================
-- EKSJK V2 Mock 数据 — 患者基础信息
-- 说明：7 种疾病类型各 3 名患者，共 21 名
-- 疾病编码：DSD=10000001, FSS=10000002, CPP=10000003, MAS=10000004, SGA=10000005, SSS=10000006, ELTM=10000007
-- ============================================================
USE eksjk;

-- ==================== DSD 患者（doctor_1，医院A）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (1, '10000001', 'DSD202604001', '测试患者_DSD_001', '男', '2015-03-15', '13800010001', '110101201503150011', '张医生', '测试医院A', '4', '120.5', '25.3', '17.4', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (2, '10000001', 'DSD202604002', '测试患者_DSD_002', '女', '2016-07-22', '13800010002', '110101201607220022', '张医生', '测试医院A', '4', '115.2', '22.1', '16.7', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (3, '10000001', 'DSD202604003', '测试患者_DSD_003', '男', '2014-11-08', '13800010003', '110101201411080033', '张医生', '测试医院A', '4', '130.8', '30.5', '17.8', '1', NOW());

-- ==================== FSS 患者（doctor_1，医院A）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (4, '10000002', 'FSS202604001', '测试患者_FSS_001', '女', '2013-05-10', '13800020001', '110101201305100041', '张医生', '测试医院A', '4', '125.0', '24.0', '15.4', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (5, '10000002', 'FSS202604002', '测试患者_FSS_002', '男', '2015-09-18', '13800020002', '110101201509180052', '张医生', '测试医院A', '4', '118.3', '21.5', '15.4', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (6, '10000002', 'FSS202604003', '测试患者_FSS_003', '女', '2017-01-25', '13800020003', '110101201701250063', '张医生', '测试医院A', '4', '105.6', '17.8', '16.0', '1', NOW());

-- ==================== CPP 患者（doctor_2，医院A）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (7, '10000003', 'CPP202604001', '测试患者_CPP_001', '女', '2014-02-14', '13800030001', '110101201402140071', '李医生', '测试医院A', '5', '140.2', '35.0', '17.8', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (8, '10000003', 'CPP202604002', '测试患者_CPP_002', '女', '2015-06-30', '13800030002', '110101201506300082', '李医生', '测试医院A', '5', '135.5', '32.0', '17.4', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (9, '10000003', 'CPP202604003', '测试患者_CPP_003', '男', '2016-10-05', '13800030003', '110101201610050093', '李医生', '测试医院A', '5', '128.0', '28.5', '17.4', '1', NOW());

-- ==================== MAS 患者（doctor_2，医院A）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (10, '10000004', 'MAS202604001', '测试患者_MAS_001', '女', '2013-08-20', '13800040001', '110101201308200101', '李医生', '测试医院A', '5', '138.0', '33.0', '17.3', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (11, '10000004', 'MAS202604002', '测试患者_MAS_002', '男', '2015-12-12', '13800040002', '110101201512120112', '李医生', '测试医院A', '5', '122.5', '24.8', '16.5', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (12, '10000004', 'MAS202604003', '测试患者_MAS_003', '女', '2017-04-03', '13800040003', '110101201704030123', '李医生', '测试医院A', '5', '108.0', '18.5', '15.9', '1', NOW());

-- ==================== SGA 患者（doctor_3，医院B）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (13, '10000005', 'SGA202604001', '测试患者_SGA_001', '男', '2018-01-10', '13800050001', '110101201801100131', '王医生', '测试医院B', '6', '95.0', '13.5', '15.0', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (14, '10000005', 'SGA202604002', '测试患者_SGA_002', '女', '2019-05-22', '13800050002', '110101201905220142', '王医生', '测试医院B', '6', '88.0', '11.8', '15.2', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (15, '10000005', 'SGA202604003', '测试患者_SGA_003', '男', '2017-09-15', '13800050003', '110101201709150153', '王医生', '测试医院B', '6', '100.5', '15.0', '14.9', '1', NOW());

-- ==================== SSS 患者（doctor_3，医院B）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (16, '10000006', 'SSS202604001', '测试患者_SSS_001', '女', '2014-07-08', '13800060001', '110101201407080161', '王医生', '测试医院B', '6', '128.0', '26.0', '15.9', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (17, '10000006', 'SSS202604002', '测试患者_SSS_002', '男', '2016-03-20', '13800060002', '110101201603200172', '王医生', '测试医院B', '6', '112.0', '19.5', '15.5', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (18, '10000006', 'SSS202604003', '测试患者_SSS_003', '女', '2015-11-30', '13800060003', '110101201511300183', '王医生', '测试医院B', '6', '120.0', '22.0', '15.3', '1', NOW());

-- ==================== ELTM 患者（doctor_1，医院A）====================
INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (19, '10000007', 'ELTM202604001', '测试患者_ELTM_001', '男', '2019-02-28', '13800070001', '110101201902280191', '张医生', '测试医院A', '4', '92.0', '13.0', '15.4', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (20, '10000007', 'ELTM202604002', '测试患者_ELTM_002', '女', '2020-06-15', '13800070002', '110101202006150202', '张医生', '测试医院A', '4', '85.0', '11.0', '15.2', '1', NOW());

INSERT IGNORE INTO datamain_patient (id, dis_class, case_num, name, sex, birth_time, self_tel, idcard, doctor_name, hospital_name, imp_per, height, weight, bmi, del_flg, c_time)
VALUES (21, '10000007', 'ELTM202604003', '测试患者_ELTM_003', '男', '2018-10-10', '13800070003', '110101201810100213', '张医生', '测试医院A', '4', '98.0', '14.5', '15.1', '1', NOW());
