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
 * ELTM 体征测量记录
 *
 * @author eksjk
 */
@Data
@TableName("eltm_physical_record")
public class EltmPhysicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 测量时间 */
    private LocalDateTime measureTime;

    /** 数据来源 */
    private String dataSource;

    /** 档案类型 */
    private String archivesType;

    /** 属性数量 */
    private Integer itemCount;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
