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
 * ELTM 体征属性明细
 *
 * @author eksjk
 */
@Data
@TableName("eltm_physical_item")
public class EltmPhysicalItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 测量记录ID */
    private Long recordId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部档案ID */
    private Long archiveId;

    /** 表单key */
    private String formKey;

    /** 属性ID */
    private Long attributeId;

    /** 属性编码 */
    private String attributeCode;

    /** 属性值 */
    private String attributeValue;

    /** 测量时间 */
    private LocalDateTime measureTime;

    /** 数据来源 */
    private String dataSource;

    /** 三级分类 */
    private String threeLevel;

    /** 档案类型 */
    private String archivesType;

    /** 外部创建时间 */
    private LocalDateTime externalCreateTime;

    /** 外部更新时间 */
    private LocalDateTime externalUpdateTime;

    /** 原始 JSON */
    private String rawJson;

    /** 同步时间 */
    private LocalDateTime syncTime;
}
