-- EKSJK V2 测试环境数据库 Schema（H2 兼容 MySQL 模式）
-- 用于单元测试和集成测试

-- 用户表（兼容 V1 Django AbstractUser 结构）
CREATE TABLE IF NOT EXISTS login_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(255),
    last_login TIMESTAMP,
    is_superuser BOOLEAN DEFAULT FALSE,
    username VARCHAR(150) NOT NULL UNIQUE,
    first_name VARCHAR(150) DEFAULT '',
    last_name VARCHAR(150) DEFAULT '',
    email VARCHAR(254) DEFAULT '',
    is_staff BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(100),
    sex VARCHAR(10),
    unit VARCHAR(50),
    level INT DEFAULT 0,
    professional VARCHAR(50),
    date_update TIMESTAMP,
    department VARCHAR(100),
    role_code VARCHAR(50),
    password_changed_at TIMESTAMP,
    wx_openid VARCHAR(100),
    phone VARCHAR(20),
    job_number VARCHAR(50),
    is_deleted INT DEFAULT 0
);

-- 医院/单位表
CREATE TABLE IF NOT EXISTS unit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200),
    code VARCHAR(50),
    address VARCHAR(500),
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- 患者主表
CREATE TABLE IF NOT EXISTS datamain_patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dis_class VARCHAR(20),
    case_num VARCHAR(50),
    medrec_num VARCHAR(50),
    user_num VARCHAR(50),
    name VARCHAR(100),
    sex VARCHAR(10),
    birth_time TIMESTAMP,
    relation VARCHAR(50),
    self_tel VARCHAR(20),
    doctor_name VARCHAR(100),
    gonadal_sex VARCHAR(10),
    `AGEy` VARCHAR(10),
    `AGEm` VARCHAR(10),
    chi_com TEXT,
    age VARCHAR(20),
    ethnic VARCHAR(50),
    height VARCHAR(20),
    weight VARCHAR(20),
    bmi VARCHAR(20),
    rbone_age VARCHAR(20),
    cbone_age VARCHAR(20),
    `FHt` VARCHAR(20),
    `MHt` VARCHAR(20),
    `FHw` VARCHAR(20),
    `MHw` VARCHAR(20),
    men_age VARCHAR(20),
    is_bot VARCHAR(10),
    family_his TEXT,
    ges_week VARCHAR(10),
    `BWt` VARCHAR(20),
    `BL` VARCHAR(20),
    cesa_sec VARCHAR(50),
    fet_pro_his TEXT,
    past_his TEXT,
    cesa_asphyxia VARCHAR(50),
    parity VARCHAR(10),
    pronum VARCHAR(10),
    pregnancy_infection TEXT,
    fir_vis_time TIMESTAMP,
    fir_vis_age VARCHAR(20),
    `ICD` VARCHAR(50),
    is_finalhei VARCHAR(10),
    xcx_card VARCHAR(100),
    expected_height VARCHAR(20),
    current_city VARCHAR(100),
    past_time TIMESTAMP,
    past_height VARCHAR(20),
    past_weight VARCHAR(20),
    baby_flag VARCHAR(10),
    myself_picture VARCHAR(500),
    contacts_name VARCHAR(100),
    contacts_num VARCHAR(20),
    p_emial VARCHAR(100),
    idcard VARCHAR(50),
    nat_pla VARCHAR(100),
    fam_adr VARCHAR(500),
    card VARCHAR(50),
    imp_per VARCHAR(100),
    up_mec VARCHAR(200),
    hospital_name VARCHAR(200),
    c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modify_time TIMESTAMP,
    modify_per VARCHAR(100),
    del_flg VARCHAR(2) DEFAULT '1',
    tags VARCHAR(500),
    confuse_name VARCHAR(100),
    upper_case VARCHAR(100),
    photo VARCHAR(500),
    address VARCHAR(500),
    category_describe TEXT,
    enrollment_num VARCHAR(50),
    enrollment_time TIMESTAMP,
    one_time TIMESTAMP,
    eltm_id VARCHAR(50),
    sync_status VARCHAR(20),
    sync_time TIMESTAMP
);

-- 随访记录表（兼容 V1 表名 datamain_patfoll）
CREATE TABLE IF NOT EXISTS datamain_patfoll (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    foll_time TIMESTAMP,
    up_time TIMESTAMP,
    age VARCHAR(20),
    `Ht` VARCHAR(20),
    `Wt` VARCHAR(20),
    bmi VARCHAR(20),
    body_fat VARCHAR(20),
    waistline VARCHAR(20),
    hips VARCHAR(20),
    waist_to_hip_ratio VARCHAR(20),
    rbone_age VARCHAR(20),
    cbone_age VARCHAR(20),
    gen_stag VARCHAR(50),
    pub_stag VARCHAR(50),
    `IGF1` VARCHAR(20),
    `IGFBP3` VARCHAR(20),
    `Jiagong` TEXT,
    fas_blood_glu VARCHAR(20),
    fas_insulin VARCHAR(20),
    gly_hem VARCHAR(20),
    liv_kid_lip TEXT,
    `LH` VARCHAR(20),
    `FSH` VARCHAR(20),
    `E2` VARCHAR(20),
    `T` VARCHAR(20),
    `DHT` VARCHAR(20),
    yltg VARCHAR(20),
    `SHBG` VARCHAR(20),
    gon_b_ult TEXT,
    tes_size VARCHAR(50),
    bon_min_den VARCHAR(50),
    dia_trea_plan TEXT,
    other_medicine TEXT,
    beh_dev_ass VARCHAR(50),
    ped_mot_dev_ass TEXT,
    gro_mot VARCHAR(50),
    ind_soc VARCHAR(50),
    lis_lan VARCHAR(50),
    han_eye_coo VARCHAR(50),
    vis_rep VARCHAR(50),
    pra_rea VARCHAR(50),
    wec_sca VARCHAR(50),
    is_finalhei VARCHAR(10),
    other TEXT,
    image TEXT,
    lab_exa_other TEXT,
    lab_exa_mas TEXT,
    disease VARCHAR(200),
    dsdk VARCHAR(200),
    clls VARCHAR(200),
    qtyl VARCHAR(200),
    other_exam TEXT,
    eos VARCHAR(200),
    other_ima_name VARCHAR(500),
    del_flg VARCHAR(2) DEFAULT '1'
);

-- MAS 随访记录表
CREATE TABLE IF NOT EXISTS mas_follow_up (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follow_up_id BIGINT,
    patient_id BIGINT,
    cafe_au_lait_spots VARCHAR(50),
    fibrous_dysplasia VARCHAR(50),
    precocious_puberty VARCHAR(50),
    thyroid_function VARCHAR(50),
    growth_hormone VARCHAR(50),
    cortisol VARCHAR(50),
    notes TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- DSD 病例子表
CREATE TABLE IF NOT EXISTS dsd_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    karyotype VARCHAR(100),
    gonadal_status VARCHAR(200),
    external_genitalia TEXT,
    internal_genitalia TEXT,
    hormone_levels TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- FSS 病例子表
CREATE TABLE IF NOT EXISTS fss_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    bone_age VARCHAR(20),
    height_sds VARCHAR(20),
    genetic_test TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- CPP 病例子表
CREATE TABLE IF NOT EXISTS cpp_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    onset_age VARCHAR(20),
    bone_age_advance VARCHAR(20),
    lh_peak VARCHAR(20),
    fsh_peak VARCHAR(20),
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- MAS 病例子表
CREATE TABLE IF NOT EXISTS mas_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    cafe_au_lait_spots VARCHAR(200),
    fibrous_dysplasia VARCHAR(200),
    precocious_puberty VARCHAR(200),
    thyroid_abnormality VARCHAR(200),
    gh_excess VARCHAR(200),
    cushing_syndrome VARCHAR(200),
    phosphate_wasting VARCHAR(200),
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- SGA 病例子表
CREATE TABLE IF NOT EXISTS sga_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    birth_weight VARCHAR(20),
    birth_length VARCHAR(20),
    gestational_age VARCHAR(20),
    catch_up_growth VARCHAR(50),
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- SSS 病例子表
CREATE TABLE IF NOT EXISTS sss_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    father_height VARCHAR(20),
    mother_height VARCHAR(20),
    target_height VARCHAR(20),
    genetic_test TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- ELTM 病例子表
CREATE TABLE IF NOT EXISTS eltm_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    screening_result VARCHAR(200),
    assessment_data TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted INT DEFAULT 0
);
