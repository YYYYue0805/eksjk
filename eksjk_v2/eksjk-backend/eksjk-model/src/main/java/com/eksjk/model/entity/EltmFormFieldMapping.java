package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ELTM 表单字段映射配置
 * <p>外部表单答案只有随机字段 key，没有题目名称，通过本表配置映射到本地业务字段。</p>
 *
 * @author eksjk
 */
@Data
@TableName("eltm_form_field_mapping")
public class EltmFormFieldMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 外部表单模板 key */
    private String formKey;

    /** 表单名称（便于维护） */
    private String formName;

    /** 外部答案字段 key */
    private String fieldKey;

    /** 目标范围：PATIENT/ELTM_CASE/GENETICS/FOLLOWUP/ADVERSE */
    private String targetScope;

    /** 目标字段名 */
    private String targetField;

    /** 取值方式：RAW/LABEL/MULTI_LABEL */
    private String valueType;

    /** 转换规则：WEIGHT_G_TO_KG/BIRTH_HEIGHT_CM/GEST_WEEK */
    private String transformRule;

    /** 优先级 */
    private Integer priority;

    /** 是否启用 */
    private Integer enabled;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
