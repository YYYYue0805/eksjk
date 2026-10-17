-- ============================================================
-- 32-eltm-backfill-summary.sql
-- ELTM 同步数据摘要回填（可重复执行）
-- 1. 患者主表：最近一次体征测量的身高/体重/BMI
-- 2. 知情同意书：用患者归属医生补齐 doctor_name
-- 执行日期：2026-10-17
-- ============================================================

-- ------------------------------------------------------------
-- 1. 回填患者最新身高/体重/BMI
-- ------------------------------------------------------------
UPDATE datamain_patient p
JOIN (
    SELECT r.patient_id,
           MAX(CASE WHEN i.attribute_code = 'height:Tab' THEN i.attribute_value END) AS height,
           MAX(CASE WHEN i.attribute_code = 'weight:Tab' THEN i.attribute_value END) AS weight,
           MAX(CASE WHEN i.attribute_code = 'bmi'        THEN i.attribute_value END) AS bmi
    FROM eltm_physical_record r
    JOIN eltm_physical_item i ON i.record_id = r.id
    JOIN (
        SELECT patient_id, MAX(measure_time) AS mt
        FROM eltm_physical_record
        GROUP BY patient_id
    ) latest ON latest.patient_id = r.patient_id AND latest.mt = r.measure_time
    GROUP BY r.patient_id
) x ON x.patient_id = p.id
SET p.height = COALESCE(x.height, p.height),
    p.weight = COALESCE(x.weight, p.weight),
    p.bmi    = COALESCE(x.bmi, p.bmi)
WHERE p.external_id IS NOT NULL;

-- ------------------------------------------------------------
-- 2. 回填知情同意书的归属医生
-- ------------------------------------------------------------
UPDATE informed_consent ic
JOIN datamain_patient p ON ic.patient_id = p.id
SET ic.doctor_name = p.doctor_name
WHERE ic.sign_id IS NOT NULL
  AND (ic.doctor_name IS NULL OR ic.doctor_name = '')
  AND p.doctor_name IS NOT NULL
  AND p.doctor_name <> '';
