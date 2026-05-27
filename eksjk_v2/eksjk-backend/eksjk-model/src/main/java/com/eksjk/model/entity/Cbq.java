package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CBQ 儿童气质问卷实体类
 * <p>兼容 V1 表结构（表名 school_cbq）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_cbq")
public class Cbq implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    private String zscm;
    private String bzzsl;
    private String htzz;
    private String xhmxhd;
    private String xcsba;
    private String nwczb;
    private String mrjrxdf;
    private String jtjhmdx;
    private String xhdtrcg;
    private String drhrbjs;
    private String hpyz;
    private String nzyfmxyf;
    private String gxhaj;
    private String sqgcsj;
    private String trjccsj;
    private String dqqgk;
    private String bwcjs;
    private String syayqxd;
    private String hcsjsyxhj;
    private String gmssby;
    private String xhgq;
    private String hhx;
    private String ryaf;
    private String zyktxsw;
    private String wsjlcp;
    private String bhphy;
    private String zztscsj;
    private String bxhcyyx;
    private String qwcsbxf;
    private String zywx;
    private String hmzs;
    private String bnzdhsc;
    private String xhrhhd;
    private String xrshx;
    private String pyzhfz;
    private String dfmwbpj;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
