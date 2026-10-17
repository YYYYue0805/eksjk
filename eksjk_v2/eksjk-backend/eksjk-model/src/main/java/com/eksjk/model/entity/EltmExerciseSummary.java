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
 * ELTM 运动汇总
 *
 * @author eksjk
 */
@Data
@TableName("eltm_exercise_summary")
public class EltmExerciseSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部汇总ID */
    private Long summaryId;

    /** 汇总日期 */
    private LocalDate summaryDate;

    /** 总次数 */
    private Integer totalCount;

    /** 总时长（秒） */
    private Integer totalDurationSeconds;

    /** 总消耗 */
    private BigDecimal totalCalories;

    /** 中高强度分钟数 */
    private Integer mvpaMinutes;

    /** 运动类型 */
    private String exerciseTypes;

    /** 是否达标 */
    private Integer isTargetReached;

    /** 年龄天数 */
    private Integer babyAgeDays;

    /** 外部创建时间 */
    private LocalDateTime externalCreateTime;

    /** 外部更新时间 */
    private LocalDateTime externalUpdateTime;

    /** 汇总 JSON */
    private String summaryJson;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
