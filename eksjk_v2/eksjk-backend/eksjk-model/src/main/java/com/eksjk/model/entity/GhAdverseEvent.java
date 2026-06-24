package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GH不良事件实体类
 * <p>对应数据库表 datamain_gh_adverse_event</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_gh_adverse_event")
public class GhAdverseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联患者ID */
    private Long patientId;

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

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标志 */
    private Integer isDeleted;
}
