package com.eksjk.model.vo;

import lombok.Data;

/**
 * 小程序身高评测结果 VO
 *
 * @author eksjk
 */
@Data
public class HeightAssessVO {

    /** 评测记录ID */
    private String id;

    /** 宝宝ID */
    private String babyId;

    /** 测量日期 */
    private String measureDate;

    /** 身高（cm） */
    private String height;

    /** 体重（kg） */
    private String weight;

    /** BMI */
    private String bmi;

    /** SDS 值 */
    private String sds;

    /** 百分位排名 */
    private String percentile;

    /** 评测结论 */
    private String conclusion;

    /** 与上次对比：身高增长值 */
    private String heightGrowth;

    /** 与上次对比：时间间隔（天） */
    private Integer daysSinceLast;
}
