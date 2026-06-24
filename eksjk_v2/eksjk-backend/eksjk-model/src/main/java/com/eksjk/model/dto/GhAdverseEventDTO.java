package com.eksjk.model.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * GH不良事件保存 DTO
 *
 * @author eksjk
 */
@Data
public class GhAdverseEventDTO {

    /** 病例主表ID（Hashids编码） */
    private String patientId;

    /** 发生时间 */
    private LocalDate occurrenceDate;

    /** 发生症状（患者端） */
    private String symptoms;

    /** 发生原因（患者端） */
    private String cause;

    /** 采取的措施（患者端） */
    private String measuresTaken;

    /** 严重程度 */
    private String severity;

    /** GH关联性评价 */
    private String ghCausality;

    /** 局部不良反应（逗号分隔） */
    private String localReactions;

    /** 局部-其他补充描述 */
    private String localOther;

    /** 全身一般反应（逗号分隔） */
    private String systemicReactions;

    /** 全身-其他补充描述 */
    private String systemicOther;

    /** 内分泌/代谢相关（逗号分隔） */
    private String endocrineReactions;

    /** 内分泌-其他补充描述 */
    private String endocrineOther;

    /** 神经系统/眼部（逗号分隔） */
    private String neuroReactions;

    /** 神经系统-其他补充描述 */
    private String neuroOther;

    /** 皮肤过敏反应（逗号分隔） */
    private String skinReactions;

    /** 皮肤-其他补充描述 */
    private String skinOther;

    /** 其他少见不良反应 */
    private String otherRareReaction;

    /** 医疗措施 */
    private String medicalIntervention;

    /** 对症处理药品名 */
    private String medicationName;

    /** GH用药调整 */
    private String ghDoseAdjustment;

    /** 用药调整原因 */
    private String adjustmentReason;

    /** 不良事件结局 */
    private String outcome;
}
