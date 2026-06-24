-- GH不良事件模块
-- 用于记录生长激素治疗相关不良事件

CREATE TABLE IF NOT EXISTS datamain_gh_adverse_event (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  patient_id BIGINT NOT NULL COMMENT '关联患者ID',
  occurrence_date DATE DEFAULT NULL COMMENT '发生时间',
  symptoms VARCHAR(500) DEFAULT NULL COMMENT '发生症状（患者端）',
  cause VARCHAR(500) DEFAULT NULL COMMENT '发生原因（患者端）',
  measures_taken VARCHAR(500) DEFAULT NULL COMMENT '采取的措施（患者端）',
  severity VARCHAR(20) DEFAULT NULL COMMENT '严重程度：轻度/中度/重度/危及生命/致残/住院',
  gh_causality VARCHAR(20) DEFAULT NULL COMMENT 'GH关联性评价：肯定相关/很可能相关/可能相关/可能无关/无关/待评价',
  local_reactions VARCHAR(200) DEFAULT NULL COMMENT '局部不良反应（逗号分隔）',
  local_other VARCHAR(200) DEFAULT NULL COMMENT '局部-其他补充描述',
  systemic_reactions VARCHAR(200) DEFAULT NULL COMMENT '全身一般反应（逗号分隔）',
  systemic_other VARCHAR(200) DEFAULT NULL COMMENT '全身-其他补充描述',
  endocrine_reactions VARCHAR(200) DEFAULT NULL COMMENT '内分泌/代谢相关（逗号分隔）',
  endocrine_other VARCHAR(200) DEFAULT NULL COMMENT '内分泌-其他补充描述',
  neuro_reactions VARCHAR(200) DEFAULT NULL COMMENT '神经系统/眼部（逗号分隔）',
  neuro_other VARCHAR(200) DEFAULT NULL COMMENT '神经系统-其他补充描述',
  skin_reactions VARCHAR(200) DEFAULT NULL COMMENT '皮肤过敏反应（逗号分隔）',
  skin_other VARCHAR(200) DEFAULT NULL COMMENT '皮肤-其他补充描述',
  other_rare_reaction VARCHAR(500) DEFAULT NULL COMMENT '其他少见不良反应',
  medical_intervention VARCHAR(20) DEFAULT NULL COMMENT '医疗措施：未干预/对症处理/GH用药调整',
  medication_name VARCHAR(200) DEFAULT NULL COMMENT '对症处理药品名',
  gh_dose_adjustment VARCHAR(20) DEFAULT NULL COMMENT 'GH用药调整：继续用药/减量/暂停用药/永久停药',
  adjustment_reason VARCHAR(500) DEFAULT NULL COMMENT '用药调整原因',
  outcome VARCHAR(20) DEFAULT NULL COMMENT '结局：痊愈/好转/未好转/加重/死亡/后遗症',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  is_deleted INT DEFAULT 0 COMMENT '逻辑删除：0=正常 1=已删除',
  INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GH不良事件记录表';
