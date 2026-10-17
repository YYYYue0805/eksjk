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
 * ELTM 医患医院绑定
 *
 * @author eksjk
 */
@Data
@TableName("eltm_hospital_bind")
public class EltmHospitalBind implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部医生ID */
    private String externalDoctorId;

    /** 医生姓名 */
    private String doctorName;

    /** 外部医院ID */
    private String externalHospitalId;

    /** 医院名称 */
    private String hospitalName;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区县 */
    private String district;

    /** 绑定来源 */
    private String bindSource;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
