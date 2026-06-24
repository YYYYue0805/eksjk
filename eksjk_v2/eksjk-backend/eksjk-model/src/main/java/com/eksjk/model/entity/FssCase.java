package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 遗传性骨病 (FSS) 疾病子表实体类
 * <p>对应数据库表 datamain_short</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_short")
public class FssCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 骨龄 */
    private String boneAge;

    /** 身高标准差 */
    private String heightSds;

    /** 基因检测 */
    private String geneticTest;

    /** 诊断 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    // ==================== 结构化既往史 ====================

    /** 运动发育落后 1=无 2=有 */
    private String motDevBack;

    /** 运动发育落后描述 */
    private String sport;

    /** 语言发育落后 1=无 2=有 */
    private String lanDevBack;

    /** 语言发育落后描述 */
    private String language;

    /** 智力发育落后 1=无 2=有 */
    private String intDevBack;

    /** 智力发育落后描述 */
    private String intelligence;

    /** 听力异常 1=无 2=有 */
    private String abnHear;

    /** 听力异常描述 */
    private String hear;

    /** 反复感染史 1=无 2=有 */
    private String recInfHis;

    /** 反复感染史描述 */
    private String infection;

    /** 抽搐史 1=无 2=有 */
    private String conHis;

    /** 其他既往史 */
    private String pastOther;

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
