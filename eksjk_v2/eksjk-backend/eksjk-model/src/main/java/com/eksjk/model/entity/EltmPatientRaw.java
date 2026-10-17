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
 * ELTM 患者原始行快照
 *
 * @author eksjk
 */
@Data
@TableName("eltm_patient_raw")
public class EltmPatientRaw implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalId;

    /** 外部最近更新时间 */
    private LocalDateTime externalUpdatedAt;

    /** 整行指纹 */
    private String rowHash;

    /** 原始行 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
