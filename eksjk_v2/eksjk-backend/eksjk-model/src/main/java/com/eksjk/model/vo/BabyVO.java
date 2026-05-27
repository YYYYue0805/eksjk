package com.eksjk.model.vo;

import lombok.Data;

/**
 * 小程序宝宝信息 VO
 *
 * @author eksjk
 */
@Data
public class BabyVO {

    /** 加密后的ID */
    private String id;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 性别名称 */
    private String sexName;

    /** 出生日期 */
    private String birthTime;

    /** 年龄描述（如"3岁2个月"） */
    private String ageDesc;

    /** 与孩子关系 */
    private String relation;

    /** 当前身高 */
    private String height;

    /** 当前体重 */
    private String weight;

    /** 父亲身高 */
    private String fht;

    /** 母亲身高 */
    private String mht;

    /** 期望身高 */
    private String expectedHeight;

    /** 当前城市 */
    private String currentCity;

    /** 疾病分类 */
    private String disClass;

    /** R型骨龄 */
    private String rboneAge;

    /** C型骨龄 */
    private String cboneAge;
}
