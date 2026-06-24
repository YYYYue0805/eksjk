package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E路童萌 (ELTM) 疾病子表实体类
 * <p>对应数据库表 datamain_szfyeltm</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_szfyeltm")
public class EltmCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 筛查结果 */
    private String screeningResult;

    /** 评估数据 */
    private String assessmentData;

    /** 诊断 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    /** 有无既往用药史 */
    private String hasHistory;

    /** 原始同步数据（JSON格式，存储从外部系统导入的完整记录） */
    private String rawData;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 诊断状态: unclassified/suggested/auto_classified/uncertain */
    private String diagnosisStatus;

    /** 建议的疾病分类代码 */
    private String suggestedDisClass;

    /** 诊断说明（匹配的指标和得分详情） */
    private String diagnosisNote;

    // ==================== 遗传学检查 ====================

    /** 基因检测方法 */
    private String geneMethod;

    /** 基因结果 */
    private String geneRes;

    /** 基因名称 */
    private String geneName;

    /** 突变位点 */
    private String genePoint;

    /** 突变类型 */
    private String geneType;

    /** 遗传模式 */
    private String geneMode;

    /** 染色体核型(JSON数组) */
    private String chrom;

    /** 其它异常核型 */
    private String chromOther;

    // ==================== 管理字段 ====================

    /** 删除标志 */
    private Integer isDeleted;
}
