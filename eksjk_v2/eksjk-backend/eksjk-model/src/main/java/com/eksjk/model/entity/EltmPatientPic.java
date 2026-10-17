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
 * ELTM 病历图片
 *
 * @author eksjk
 */
@Data
@TableName("eltm_patient_pic")
public class EltmPatientPic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部图片ID */
    private String picId;

    /** 病历日期 */
    private LocalDateTime recordDate;

    /** 病历名称 */
    private String recordName;

    /** 类型内容 */
    private String typeContent;

    /** 图片地址 */
    private String imageUrl;

    /** 图片类型 */
    private String imageType;

    /** 图片来源 */
    private String picSourceType;

    /** 来源 */
    private String source;

    /** 备注 */
    private String remark;

    /** 外部创建时间 */
    private LocalDateTime externalCreateTime;

    /** 外部更新时间 */
    private LocalDateTime externalUpdateTime;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
