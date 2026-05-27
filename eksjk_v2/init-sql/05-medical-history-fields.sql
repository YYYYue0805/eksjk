-- ============================================================
-- 05-medical-history-fields.sql
-- 病史 & 结构化既往史字段补充
-- 对应 Tab2 基线-临床信息 增强
-- ============================================================

-- 1. Patient 主表：生长速率
ALTER TABLE datamain_patient ADD COLUMN grow_rate VARCHAR(32) NULL COMMENT '生长速率选择 1=不详 2=已选择';
ALTER TABLE datamain_patient ADD COLUMN rate VARCHAR(32) NULL COMMENT '生长速率数值 cm/年';

-- 2. FSS (datamain_short)：结构化既往史
ALTER TABLE datamain_short ADD COLUMN mot_dev_back VARCHAR(32) NULL COMMENT '运动发育落后 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN sport VARCHAR(255) NULL COMMENT '运动发育落后描述';
ALTER TABLE datamain_short ADD COLUMN lan_dev_back VARCHAR(32) NULL COMMENT '语言发育落后 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN language VARCHAR(255) NULL COMMENT '语言发育落后描述';
ALTER TABLE datamain_short ADD COLUMN int_dev_back VARCHAR(32) NULL COMMENT '智力发育落后 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN intelligence VARCHAR(255) NULL COMMENT '智力发育落后描述';
ALTER TABLE datamain_short ADD COLUMN abn_hear VARCHAR(32) NULL COMMENT '听力异常 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN hear VARCHAR(255) NULL COMMENT '听力异常描述';
ALTER TABLE datamain_short ADD COLUMN rec_inf_his VARCHAR(32) NULL COMMENT '反复感染史 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN infection VARCHAR(255) NULL COMMENT '反复感染史描述';
ALTER TABLE datamain_short ADD COLUMN con_his VARCHAR(32) NULL COMMENT '抽搐史 1=无 2=有';
ALTER TABLE datamain_short ADD COLUMN past_other VARCHAR(500) NULL COMMENT '其他既往史';

-- 3. SGA (datamain_sga)：结构化既往史
ALTER TABLE datamain_sga ADD COLUMN mot_dev_back VARCHAR(32) NULL COMMENT '运动发育落后 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN sport VARCHAR(255) NULL COMMENT '运动发育落后描述';
ALTER TABLE datamain_sga ADD COLUMN lan_dev_back VARCHAR(32) NULL COMMENT '语言发育落后 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN language VARCHAR(255) NULL COMMENT '语言发育落后描述';
ALTER TABLE datamain_sga ADD COLUMN int_dev_back VARCHAR(32) NULL COMMENT '智力发育落后 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN intelligence VARCHAR(255) NULL COMMENT '智力发育落后描述';
ALTER TABLE datamain_sga ADD COLUMN abn_hear VARCHAR(32) NULL COMMENT '听力异常 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN hear VARCHAR(255) NULL COMMENT '听力异常描述';
ALTER TABLE datamain_sga ADD COLUMN rec_inf_his VARCHAR(32) NULL COMMENT '反复感染史 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN infection VARCHAR(255) NULL COMMENT '反复感染史描述';
ALTER TABLE datamain_sga ADD COLUMN con_his VARCHAR(32) NULL COMMENT '抽搐史 1=无 2=有';
ALTER TABLE datamain_sga ADD COLUMN past_other VARCHAR(500) NULL COMMENT '其他既往史';

-- 4. SSS (datamain_jzxshort)：结构化既往史
ALTER TABLE datamain_jzxshort ADD COLUMN mot_dev_back VARCHAR(32) NULL COMMENT '运动发育落后 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN sport VARCHAR(255) NULL COMMENT '运动发育落后描述';
ALTER TABLE datamain_jzxshort ADD COLUMN lan_dev_back VARCHAR(32) NULL COMMENT '语言发育落后 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN language VARCHAR(255) NULL COMMENT '语言发育落后描述';
ALTER TABLE datamain_jzxshort ADD COLUMN int_dev_back VARCHAR(32) NULL COMMENT '智力发育落后 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN intelligence VARCHAR(255) NULL COMMENT '智力发育落后描述';
ALTER TABLE datamain_jzxshort ADD COLUMN abn_hear VARCHAR(32) NULL COMMENT '听力异常 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN hear VARCHAR(255) NULL COMMENT '听力异常描述';
ALTER TABLE datamain_jzxshort ADD COLUMN rec_inf_his VARCHAR(32) NULL COMMENT '反复感染史 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN infection VARCHAR(255) NULL COMMENT '反复感染史描述';
ALTER TABLE datamain_jzxshort ADD COLUMN con_his VARCHAR(32) NULL COMMENT '抽搐史 1=无 2=有';
ALTER TABLE datamain_jzxshort ADD COLUMN past_other VARCHAR(500) NULL COMMENT '其他既往史';

-- 5. CPP (datamain_sexprecocity)：简化既往史
ALTER TABLE datamain_sexprecocity ADD COLUMN is_his VARCHAR(32) NULL COMMENT '既往史 1=健康 2=异常';
ALTER TABLE datamain_sexprecocity ADD COLUMN old_his VARCHAR(1500) NULL COMMENT '既往疾病及治疗情况';

-- 6. MAS (datamain_mas)：身高增长速率
ALTER TABLE datamain_mas ADD COLUMN height_rate VARCHAR(32) NULL COMMENT '身高增长速度 cm/年';

-- 7. ELTM (datamain_szfyeltm)：既往用药史
ALTER TABLE datamain_szfyeltm ADD COLUMN has_history VARCHAR(32) NULL COMMENT '有无既往用药史';
