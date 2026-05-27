package com.eksjk.model.vo;

import lombok.Data;

import java.util.Map;

/**
 * 学生视图对象
 *
 * @author eksjk
 */
@Data
public class StudentVO {

    /** 加密后的ID */
    private String id;
    /** 编号 */
    private String num;
    /** 班级 */
    private String sclass;
    /** 姓名 */
    private String name;
    /** 性别 */
    private String sex;
    /** 性别名称 */
    private String sexName;
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
    /** 导入人员 */
    private String impPer;
    /** 上传机构 */
    private String upMec;
    /** 导入时间 */
    private String cTime;

    /** 问卷填写状态（key为问卷类型，value为是否已填） */
    private Map<String, Boolean> questionnaireStatus;

    /** 问卷数据（详情时返回） */
    private Map<String, Object> questionnaires;
}
