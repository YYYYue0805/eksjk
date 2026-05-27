-- ============================================================
-- EKSJK V2 Mock 数据 — 随访记录
-- 说明：为每位患者创建 3 条随访记录，模拟儿童生长趋势
-- MAS 患者额外创建 MAS 专属随访记录
-- ============================================================
USE eksjk;

-- ==================== 患者1 (DSD_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (1, 1, '2024-01-15', '120.5', '25.3', '17.4', '8.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (2, 1, '2024-07-20', '124.0', '27.0', '17.6', '8.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (3, 1, '2025-01-10', '127.5', '28.8', '17.7', '9.0', '1');

-- ==================== 患者4 (FSS_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (4, 4, '2024-02-10', '125.0', '24.0', '15.4', '10.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (5, 4, '2024-08-15', '127.5', '25.5', '15.7', '11.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (6, 4, '2025-02-20', '130.0', '27.0', '16.0', '11.5', '1');

-- ==================== 患者7 (CPP_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (7, 7, '2024-03-05', '140.2', '35.0', '17.8', '11.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (8, 7, '2024-09-10', '142.5', '36.5', '18.0', '11.3', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (9, 7, '2025-03-15', '144.0', '37.8', '18.2', '11.5', '1');

-- ==================== 患者10 (MAS_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (10, 10, '2024-01-20', '138.0', '33.0', '17.3', '12.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (11, 10, '2024-07-25', '140.5', '34.5', '17.5', '12.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (12, 10, '2025-01-30', '143.0', '36.0', '17.6', '13.0', '1');

-- MAS 专属随访记录
INSERT IGNORE INTO datamain_masfoll (id, follow_up_id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_function, create_time)
VALUES (1, 10, 10, '稳定', '无进展', '控制中', '甲亢控制中', NOW());
INSERT IGNORE INTO datamain_masfoll (id, follow_up_id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_function, create_time)
VALUES (2, 11, 10, '稳定', '无进展', '控制中', '甲功正常', NOW());
INSERT IGNORE INTO datamain_masfoll (id, follow_up_id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_function, create_time)
VALUES (3, 12, 10, '稳定', '轻度进展', '控制中', '甲功正常', NOW());

-- ==================== 患者13 (SGA_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (13, 13, '2024-04-10', '95.0', '13.5', '15.0', '4.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (14, 13, '2024-10-15', '98.5', '14.5', '14.9', '5.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (15, 13, '2025-04-20', '102.0', '15.5', '14.9', '5.5', '1');

-- ==================== 患者16 (SSS_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (16, 16, '2024-05-05', '128.0', '26.0', '15.9', '9.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (17, 16, '2024-11-10', '130.5', '27.5', '16.1', '9.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (18, 16, '2025-05-15', '133.0', '29.0', '16.4', '10.0', '1');

-- ==================== 患者19 (ELTM_001) 随访记录 ====================
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (19, 19, '2024-06-01', '92.0', '13.0', '15.4', '3.5', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (20, 19, '2024-12-05', '96.0', '14.0', '15.2', '4.0', '1');
INSERT IGNORE INTO datamain_patfoll (id, patient_id, foll_time, Ht, Wt, bmi, rbone_age, del_flg)
VALUES (21, 19, '2025-06-10', '100.0', '15.0', '15.0', '4.5', '1');
