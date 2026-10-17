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
 * ELTM 测评报告
 *
 * @author eksjk
 */
@Data
@TableName("eltm_assessment_report")
public class EltmAssessmentReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部报告ID */
    private Long reportId;

    /** 量表ID */
    private Long modelId;

    /** 量表名称 */
    private String modelName;

    /** 表单key */
    private String formKey;

    /** 表单名称 */
    private String formName;

    /** 项目ID */
    private String projectId;

    /** 项目名称 */
    private String projectName;

    /** 用户表单数据ID */
    private Long userFormDataId;

    /** 得分 */
    private String score;

    /** 总分 */
    private String totalScore;

    /** 评估结果 */
    private String evaluationResult;

    /** 结果描述 */
    private String resultDescription;

    /** 报告备注 */
    private String reportRemarkValue;

    /** 报告建议 */
    private String reportSuggestValue;

    /** 检查日期 */
    private LocalDateTime baseCheckDateValue;

    /** 下次检查日期 */
    private LocalDateTime baseNextCheckDateValue;

    /** 报告时间 */
    private LocalDateTime reportTime;

    /** 外部创建时间 */
    private LocalDateTime externalCreateTime;

    /** 外部更新时间 */
    private LocalDateTime externalUpdateTime;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
