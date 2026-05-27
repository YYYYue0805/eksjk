package com.eksjk.model.dto;

import lombok.Data;

/**
 * 小程序身高评测 DTO
 *
 * @author eksjk
 */
@Data
public class HeightAssessDTO {

    /** 宝宝ID（Hashids编码） */
    private String babyId;

    /** 测量日期（yyyy-MM-dd） */
    private String measureDate;

    /** 身高（cm） */
    private String height;

    /** 体重（kg） */
    private String weight;
}
