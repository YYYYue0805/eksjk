package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 中枢性性早熟 (CPP) 疾病子表实体类
 * <p>对应数据库表 datamain_sexprecocity</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_sexprecocity")
public class CppCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 发病年龄 */
    private String onsetAge;

    /** 骨龄提前量 */
    private String boneAgeAdvance;

    /** LH峰值 */
    private String lhPeak;

    /** FSH峰值 */
    private String fshPeak;

    /** 诊断 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    // ==================== 既往史 ====================

    /** 既往史 1=健康 2=异常 */
    private String isHis;

    /** 既往疾病及治疗情况 */
    private String oldHis;

    /** 诊疗方案（JSON） */
    @TableField("dia_trea_plan")
    private String diaTreaPlan;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标志 */
    private Integer isDeleted;
}
