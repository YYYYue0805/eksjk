package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SDQ 长处和困难问卷实体类
 * <p>兼容 V1 表结构（表名 school_cchkn）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_cchkn")
public class Cchkn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联学生ID */
    @TableField("student_id")
    private Long studentId;

    /** 能体谅到别人的感受 */
    private String ntlbrgs;
    /** 不安定、过分活跃、不能长久安静 */
    private String bad;
    /** 经常抱怨头痛、肚子痛或身体不舒服 */
    private String jcdzt;
    /** 很乐意与别的小孩分享东西 */
    private String lyfx;
    /** 经常发脾气或大吵大闹 */
    private String jcfpq;
    /** 比较孤独，喜欢自己一个人玩 */
    private String bjgd;
    /** 一般来说，比较顺从 */
    private String bjsc;
    /** 有很多担忧，经常表现出忧虑 */
    private String hdyy;
    /** 如果有人受伤，都很乐意提供帮助 */
    private String lybz;
    /** 经常的坐立不安或躁动 */
    private String jczlba;
    /** 有一个或一个以上的好朋友 */
    private String yhpy;
    /** 经常与别的小孩吵架或欺负其他小孩子 */
    private String cjqfbr;
    /** 经常不高兴、情绪低落或哭泣 */
    private String jcbgx;
    /** 一般来说，受别的小孩所喜欢 */
    private String sxpyxh;
    /** 容易分心，注意力不集中 */
    private String ryfx;
    /** 在新环境下，会紧张或粘住大人 */
    private String xhjjz;
    /** 爱对年纪小的儿童和善 */
    private String dljxyh;
    /** 经常撒谎或欺骗 */
    private String jcshqp;
    /** 受别的小孩捉弄或欺负 */
    private String sbrzn;
    /** 经常自愿的帮助别人 */
    private String zybzbr;
    /** 做事前会想清楚 */
    private String zsqxqc;
    /** 会从事家里、学校或其他地方偷东西 */
    private String htdx;
    /** 跟大人相处比跟小孩子相处融洽 */
    private String hdrrq;
    /** 对很多事情容易感到害怕 */
    private String ryjx;
    /** 做事情能做到底，注意力持久 */
    private String zylcj;

    /** 导入时间 */
    private LocalDateTime cTime;
    /** 修改时间 */
    private LocalDateTime modifyTime;
    /** 删除标志 */
    private String delFlg;
    /** 内容/备注 */
    private String count;
}
