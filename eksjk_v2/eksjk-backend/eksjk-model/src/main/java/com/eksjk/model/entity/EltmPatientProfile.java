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
 * ELTM 外部患者扩展属性
 *
 * @author eksjk
 */
@Data
@TableName("eltm_patient_profile")
public class EltmPatientProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalId;

    /** 注册来源 */
    private String registerSource;

    /** 证件类型 */
    private String idType;

    /** 外部创建时间 */
    private LocalDateTime externalCreatedAt;

    /** 外部归属医生ID */
    private String externalDoctorId;

    /** 外部归属医院ID */
    private String externalHospitalId;

    /** 历史归属医生姓名 */
    private String firstDoctorNames;

    /** 历史归属医生ID */
    private String firstDoctorIds;

    /** 历史归属医院名称 */
    private String firstHospitalNames;

    /** 历史归属医院ID */
    private String firstHospitalIds;

    /** 归属方式 */
    private String assignType;

    /** 所属项目数 */
    private Integer projectCount;

    /** 所属项目名称 */
    private String projectName;

    /** 患者类型 */
    private String patientType;

    /** 数据范围 */
    private String dataScope;

    /** 表单数 */
    private Integer formCount;

    /** 图片数 */
    private Integer picCount;

    /** 体征数 */
    private Integer physicalSignCount;

    /** 报告数 */
    private Integer reportCount;

    /** 运动标记 */
    private String exerciseFlag;

    /** 睡眠标记 */
    private String sleepFlag;

    /** 知情同意数 */
    private Integer consentCount;

    /** 最近表单时间 */
    private LocalDateTime latestFormTime;

    /** 最近表单更新时间 */
    private LocalDateTime latestFormUpdate;

    /** 最近表单名称 */
    private String latestFormName;

    /** 最近图片时间 */
    private LocalDateTime latestPicTime;

    /** 最近图片更新时间 */
    private LocalDateTime latestPicUpdate;

    /** 最近体征时间 */
    private LocalDateTime latestSignTime;

    /** 最近体征更新时间 */
    private LocalDateTime latestSignUpdate;

    /** 最近报告时间 */
    private LocalDateTime latestReportTime;

    /** 最近报告更新时间 */
    private LocalDateTime latestReportUpdate;

    /** 最近运动时间 */
    private LocalDateTime latestExerciseTime;

    /** 最近运动更新时间 */
    private LocalDateTime latestExerciseUpdate;

    /** 最近睡眠时间 */
    private LocalDateTime latestSleepTime;

    /** 最近睡眠更新时间 */
    private LocalDateTime latestSleepUpdate;

    /** 最近签署时间 */
    private LocalDateTime latestConsentTime;

    /** 最近签署更新时间 */
    private LocalDateTime latestConsentUpdate;

    /** 同步时间 */
    private LocalDateTime syncTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
