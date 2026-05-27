package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学校健康筛查 - 学生主表实体类
 * <p>
 * 兼容 V1 表结构（表名 school_student）
 * </p>
 *
 * @author eksjk
 */
@Data
@TableName("school_student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 主责医生 */
    private String doctor;

    /** 手机号 */
    private String phone;

    /** 填表人和孩子的关系 */
    private String hhzgx;

    /** 编号 */
    private String num;

    /** 班级 */
    private String sclass;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 出生日期 */
    private LocalDateTime birthTime;

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

    /** 孩子的主要照护人 */
    private String zyjhr;

    /** 主要照护人受教育程度 */
    private String zhrjycd;

    /** 是否有兄弟姐妹 */
    private String isxdjm;

    /** 医生诊断为妊娠期糖尿病 */
    private String yszdrstlb;

    /** 医生诊断为妊娠期高血压 */
    private String yszdrsgxy;

    /** 精神压力大或者情绪问题且需要专业人员帮助 */
    private String yldisbz;

    /** 医生诊断为营养不良 */
    private String yszdyybl;

    /** 分娩方式 */
    private String fmfs;

    /** 出生体重 */
    private String bweight;

    /** 出生孕周 */
    private String bweek;

    /** 出生时是否发生窒息或抢救 */
    private String csiszxqj;

    /** 出生后喂养方式 */
    private String cswyfs;

    /** 断母乳时间 */
    private String dmrsj;

    /** 添加辅食时间 */
    private String jfssj;

    /** 导入人员 */
    private String impPer;

    /** 上传机构 */
    private String upMec;

    /** 导入时间 */
    private LocalDateTime cTime;

    /** 修改时间 */
    private LocalDateTime modifyTime;

    /** 删除标志（'0'=已删除, '1'=有效数据） */
    private String delFlg;

    /** 内容/备注 */
    private String count;
}
