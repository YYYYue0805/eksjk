package com.eksjk.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 学生数据传输对象（新增/编辑）
 *
 * @author eksjk
 */
@Data
public class StudentDTO {

    /** 编号 */
    private String num;
    /** 班级 */
    private String sclass;
    /** 姓名 */
    private String name;
    /** 性别 */
    private String sex;
    /** 出生日期 */
    private String birthTime;
    /** 手机号 */
    private String phone;
    /** 填表人和孩子的关系 */
    private String hhzgx;
    /** 当前身高 */
    private String height;
    /** 当前体重 */
    private String weight;
    /** 母亲受教育程度 */
    private String mqjycd;
    /** 父亲受教育程度 */
    private String fqjycd;
    /** 家庭年收入 */
    private String jtnsr;
    /** 主要照护人 */
    private String zyjhr;
    /** 主要照护人受教育程度 */
    private String zhrjycd;
    /** 是否有兄弟姐妹 */
    private String isxdjm;
    /** 妊娠期糖尿病 */
    private String yszdrstlb;
    /** 妊娠期高血压 */
    private String yszdrsgxy;
    /** 精神压力 */
    private String yldisbz;
    /** 营养不良 */
    private String yszdyybl;
    /** 分娩方式 */
    private String fmfs;
    /** 出生体重 */
    private String bweight;
    /** 出生孕周 */
    private String bweek;
    /** 窒息或抢救 */
    private String csiszxqj;
    /** 出生后喂养方式 */
    private String cswyfs;
    /** 断母乳时间 */
    private String dmrsj;
    /** 添加辅食时间 */
    private String jfssj;

    /** 7张问卷数据（key为问卷类型：cchkn/cbq/mqzyfs/qzhd/pmbl/sthd/smxg） */
    private Map<String, Map<String, Object>> questionnaires;
}
