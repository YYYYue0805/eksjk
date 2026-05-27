package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 母亲照养方式问卷实体类
 * <p>兼容 V1 表结构（表名 school_mqzyfs）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_mqzyfs")
public class Mqzyfs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    private String hzbgxbpp;
    private String hzysmgsm;
    private String hzsffcwsw;
    private String hzgxbxyq;
    private String hzbfcdm;
    private String hzfcbzh;
    private String hzyddycgfzj;
    private String glhzzs;
    private String thzzs;
    private String gjhzxqpytc;
    private String dhzknysdc;
    private String yxchtw;
    private String bxbgjz;
    private String wmyyzpp;
    private String bnfwzwn;
    private String bxhzzcn;
    private String rhzmbwsm;
    private String dhzshysgx;
    private String bzyhzzsm;
    private String zjmblchz;
    private String hzxzmyjzy;
    private String bqsjbyhz;
    private String bljhzhfuzsm;
    private String ysdfsbyhz;
    private String wlyqysmz;
    private String dhzmyjc;
    private String bhzjjkn;
    private String khzzs;
    private String dhzrzhd;
    private String tyjsysyx;
    private String dhzbjsf;
    private String hsyqyty;
    private String thzyqyx;
    private String yqhzjyy;
    private String hzzcyspp;
    private String bgxxs;
    private String slshzsc;
    private String ysqz;
    private String zjmjtyq;
    private String pyhzjzjd;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
