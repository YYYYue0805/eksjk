-- ============================================================
-- EKSJK V2 Mock 数据 — 疾病病例详情
-- 说明：为每位患者创建对应疾病类型的病例详情记录
-- ============================================================
USE eksjk;

-- ==================== DSD 病例（datamain_case 表）====================
INSERT IGNORE INTO datamain_case (id, patient_id, karyotype, gonadal_status, external_genitalia, diagnosis, treatment_plan, create_time)
VALUES (1, 1, '46,XY', '双侧睾丸未降', '外生殖器模糊', '46,XY DSD - 雄激素不敏感综合征', '激素替代治疗+定期随访', NOW());
INSERT IGNORE INTO datamain_case (id, patient_id, karyotype, gonadal_status, external_genitalia, diagnosis, treatment_plan, create_time)
VALUES (2, 2, '46,XX', '卵巢正常', '阴蒂肥大', '46,XX DSD - 先天性肾上腺皮质增生', '糖皮质激素替代治疗', NOW());
INSERT IGNORE INTO datamain_case (id, patient_id, karyotype, gonadal_status, external_genitalia, diagnosis, treatment_plan, create_time)
VALUES (3, 3, '45,X/46,XY', '条索状性腺', '外生殖器模糊', '混合性性腺发育不全', '手术+激素治疗', NOW());

-- ==================== FSS 病例（datamain_short 表）====================
INSERT IGNORE INTO datamain_short (id, patient_id, bone_age, height_sds, genetic_test, diagnosis, treatment_plan, create_time)
VALUES (1, 4, '10.5', '-2.5', 'SHOX基因缺失', '遗传性骨病 - SHOX缺陷', '生长激素治疗', NOW());
INSERT IGNORE INTO datamain_short (id, patient_id, bone_age, height_sds, genetic_test, diagnosis, treatment_plan, create_time)
VALUES (2, 5, '8.0', '-3.0', 'FGFR3突变', '软骨发育不全', '对症治疗+定期监测', NOW());
INSERT IGNORE INTO datamain_short (id, patient_id, bone_age, height_sds, genetic_test, diagnosis, treatment_plan, create_time)
VALUES (3, 6, '5.5', '-2.2', '待检测', '疑似遗传性骨病', '遗传咨询+基因检测', NOW());

-- ==================== CPP 病例（datamain_sexprecocity 表）====================
INSERT IGNORE INTO datamain_sexprecocity (id, patient_id, onset_age, bone_age_advance, lh_peak, fsh_peak, diagnosis, treatment_plan, create_time)
VALUES (1, 7, '7.5', '2.5', '15.2', '8.3', '中枢性性早熟', 'GnRHa 治疗', NOW());
INSERT IGNORE INTO datamain_sexprecocity (id, patient_id, onset_age, bone_age_advance, lh_peak, fsh_peak, diagnosis, treatment_plan, create_time)
VALUES (2, 8, '8.0', '1.8', '12.5', '6.8', '中枢性性早熟', 'GnRHa 治疗+定期监测', NOW());
INSERT IGNORE INTO datamain_sexprecocity (id, patient_id, onset_age, bone_age_advance, lh_peak, fsh_peak, diagnosis, treatment_plan, create_time)
VALUES (3, 9, '8.5', '1.5', '10.0', '5.5', '快进展型青春期', '观察随访', NOW());

-- ==================== MAS 病例（datamain_mas 表）====================
INSERT IGNORE INTO datamain_mas (id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_abnormality, diagnosis, treatment_plan, create_time)
VALUES (1, 10, '多发，边界不规则', '左股骨', '有', '甲亢', 'McCune-Albright 综合征', '多学科联合治疗', NOW());
INSERT IGNORE INTO datamain_mas (id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_abnormality, diagnosis, treatment_plan, create_time)
VALUES (2, 11, '单发，右侧躯干', '无', '无', '正常', '疑似 MAS - 不完全型', '定期随访观察', NOW());
INSERT IGNORE INTO datamain_mas (id, patient_id, cafe_au_lait_spots, fibrous_dysplasia, precocious_puberty, thyroid_abnormality, diagnosis, treatment_plan, create_time)
VALUES (3, 12, '多发，双侧', '右胫骨', '有', '正常', 'McCune-Albright 综合征', 'GnRHa+骨科随访', NOW());

-- ==================== SGA 病例（datamain_sga 表）====================
INSERT IGNORE INTO datamain_sga (id, patient_id, birth_weight, birth_length, gestational_age, catch_up_growth, diagnosis, treatment_plan, create_time)
VALUES (1, 13, '2100', '44.0', '38', '未追赶', '小于胎龄儿 - 未追赶生长', '生长激素治疗', NOW());
INSERT IGNORE INTO datamain_sga (id, patient_id, birth_weight, birth_length, gestational_age, catch_up_growth, diagnosis, treatment_plan, create_time)
VALUES (2, 14, '1800', '42.5', '37', '部分追赶', '小于胎龄儿 - 部分追赶', '营养指导+定期监测', NOW());
INSERT IGNORE INTO datamain_sga (id, patient_id, birth_weight, birth_length, gestational_age, catch_up_growth, diagnosis, treatment_plan, create_time)
VALUES (3, 15, '2300', '45.0', '39', '未追赶', '小于胎龄儿 - 未追赶生长', '生长激素治疗评估', NOW());

-- ==================== SSS 病例（datamain_jzxshort 表）====================
INSERT IGNORE INTO datamain_jzxshort (id, patient_id, father_height, mother_height, target_height, diagnosis, treatment_plan, create_time)
VALUES (1, 16, '160.0', '150.0', '148.5', '家族性矮小', '生长激素治疗评估', NOW());
INSERT IGNORE INTO datamain_jzxshort (id, patient_id, father_height, mother_height, target_height, diagnosis, treatment_plan, create_time)
VALUES (2, 17, '162.0', '152.0', '163.5', '家族性矮小', '营养运动指导', NOW());
INSERT IGNORE INTO datamain_jzxshort (id, patient_id, father_height, mother_height, target_height, diagnosis, treatment_plan, create_time)
VALUES (3, 18, '158.0', '148.0', '146.5', '家族性矮小', '生长激素治疗', NOW());

-- ==================== ELTM 病例（datamain_szfyeltm 表）====================
INSERT IGNORE INTO datamain_szfyeltm (id, patient_id, screening_result, assessment_data, diagnosis, treatment_plan, create_time)
VALUES (1, 19, '发育迟缓风险', '大运动发育落后', '发育迟缓 - 大运动', '早期干预训练', NOW());
INSERT IGNORE INTO datamain_szfyeltm (id, patient_id, screening_result, assessment_data, diagnosis, treatment_plan, create_time)
VALUES (2, 20, '正常', '各项发育指标正常', '正常发育', '定期随访', NOW());
INSERT IGNORE INTO datamain_szfyeltm (id, patient_id, screening_result, assessment_data, diagnosis, treatment_plan, create_time)
VALUES (3, 21, '语言发育迟缓风险', '语言理解和表达落后', '语言发育迟缓', '语言训练+家庭指导', NOW());
