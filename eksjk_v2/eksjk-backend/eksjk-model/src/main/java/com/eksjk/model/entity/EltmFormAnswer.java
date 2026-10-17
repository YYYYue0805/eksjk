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
 * ELTM 表单答题明细
 *
 * @author eksjk
 */
@Data
@TableName("eltm_form_answer")
public class EltmFormAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 表单提交ID */
    private Long submissionId;

    /** 字段key */
    private String fieldKey;

    /** 字段名称 */
    private String fieldLabel;

    /** 字段值 */
    private String fieldValue;

    /** 原始 JSON */
    private String rawJson;
}
