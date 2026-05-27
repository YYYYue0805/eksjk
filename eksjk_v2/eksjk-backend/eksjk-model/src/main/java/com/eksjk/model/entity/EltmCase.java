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

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标志 */
    private Integer isDeleted;
}
