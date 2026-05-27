package com.eksjk.model.dto;

import lombok.Data;

/**
 * 小程序宝宝信息 DTO（新增/编辑）
 *
 * @author eksjk
 */
@Data
public class BabyDTO {

    /** 宝宝ID（编辑时传入） */
    private String id;

    /** 姓名 */
    private String name;

    /** 性别（1=男, 2=女） */
    private String sex;

    /** 出生日期（yyyy-MM-dd） */
    private String birthTime;

    /** 与孩子关系 */
    private String relation;

    /** 联系电话 */
    private String selfTel;

    /** 绑定医生ID */
    private String doctorId;

    /** 父亲身高 */
    private String fht;

    /** 母亲身高 */
    private String mht;

    /** 疾病分类 */
    private String disClass;

    /** 期望身高 */
    private String expectedHeight;

    /** 当前城市 */
    private String currentCity;

    /** 当前身高 */
    private String height;

    /** 当前体重 */
    private String weight;

    /** R型骨龄（年-月） */
    private String rboneAge;

    /** C型骨龄（年-月） */
    private String cboneAge;

    /** 上次测量时间（yyyy-MM-dd） */
    private String pastTime;

    /** 上次身高 */
    private String pastHeight;

    /** 上次体重 */
    private String pastWeight;
}
