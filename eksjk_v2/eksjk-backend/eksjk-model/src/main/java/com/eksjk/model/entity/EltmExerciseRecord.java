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
 * ELTM 运动记录
 *
 * @author eksjk
 */
@Data
@TableName("eltm_exercise_record")
public class EltmExerciseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部记录ID */
    private Long recordId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 分类名称 */
    private String categoryName;

    /** 子分类名称 */
    private String subcategoryName;

    /** 运动项目ID */
    private Long exerciseItemId;

    /** 运动名称 */
    private String exerciseName;

    /** 时长（秒） */
    private Integer durationSeconds;

    /** 消耗卡路里 */
    private BigDecimal caloriesBurned;

    /** MET值 */
    private BigDecimal metValue;

    /** 体重 */
    private BigDecimal weightKg;

    /** 体重来源 */
    private Integer weightSource;

    /** 强度等级 */
    private Integer intensityLevel;

    /** 年龄组 */
    private String ageGroup;

    /** 年龄天数 */
    private Integer babyAgeDays;

    /** 外部创建时间 */
    private LocalDateTime externalCreateTime;

    /** 外部更新时间 */
    private LocalDateTime externalUpdateTime;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
