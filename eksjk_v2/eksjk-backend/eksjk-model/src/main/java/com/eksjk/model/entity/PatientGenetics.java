package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者遗传学染色体核型独立表
 * <p>对应数据库表 patient_genetics</p>
 * <p>主表行大小超限无法新增列，染色体核型多选统一存储于此表，所有病种共用</p>
 *
 * @author eksjk
 */
@Data
@TableName("patient_genetics")
public class PatientGenetics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 染色体核型(JSON数组) */
    private String chrom;

    /** 其它异常核型 */
    private String chromOther;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
