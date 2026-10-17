package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ELTM 表单提交
 *
 * @author eksjk
 */
@Data
@TableName("eltm_form_submission")
public class EltmFormSubmission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部表单数据ID */
    private Long formDataId;

    /** 表单key */
    private String formKey;

    /** 表单名称 */
    private String formName;

    /** 表单类型 */
    private String formType;

    /** 项目ID */
    private String projectId;

    /** 项目名称 */
    private String projectName;

    /** 来源类型 */
    private String sourceType;

    /** 序号 */
    private Integer serialNumber;

    /** 外部患者关联ID */
    private Long toPatientId;

    /** 患者计划ID */
    private Long patientPlanId;

    /** 第三方患者ID */
    private Long thirdPatientId;

    /** 提交时间 */
    private LocalDateTime submittedAt;

    /** 完成时间值 */
    private Long completeTime;

    /** 完成人类型 */
    private Integer completePersonType;

    /** 得分 */
    private String score;

    /** 评估结果 */
    private String evaluationResult;

    /** 结果描述 */
    private String resultDescription;

    /** 维度得分 JSON */
    private String latitudeComputeScore;

    /** 原始答题数据 */
    private String originalData;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
