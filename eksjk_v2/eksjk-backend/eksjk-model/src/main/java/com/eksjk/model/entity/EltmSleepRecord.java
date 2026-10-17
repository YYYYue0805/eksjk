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
 * ELTM 睡眠记录
 *
 * @author eksjk
 */
@Data
@TableName("eltm_sleep_record")
public class EltmSleepRecord implements Serializable {

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

    /** 入睡时间 */
    private LocalDateTime sleepStartTime;

    /** 醒来时间 */
    private LocalDateTime sleepEndTime;

    /** 时长小时 */
    private Integer sleepDurationHours;

    /** 时长分钟 */
    private Integer sleepDurationMinutes;

    /** 夜醒次数 */
    private Integer awakeTimes;

    /** 入睡方式 */
    private String sleepWay;

    /** 睡眠习惯 */
    private String sleepHabits;

    /** 睡姿 */
    private String sleepPosture;

    /** 是否规律 */
    private Integer sleepRegularly;

    /** 环境是否安静 */
    private Integer sleepEnvIsQuiet;

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
