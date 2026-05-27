package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 性发育异常 (DSD) 疾病子表实体类
 * <p>对应数据库表 datamain_case</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_case")
public class DsdCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 染色体核型 */
    private String karyotype;

    /** 性腺状态 */
    private String gonadalStatus;

    /** 外生殖器 */
    private String externalGenitalia;

    /** 内生殖器 */
    private String internalGenitalia;

    /** 激素水平 */
    private String hormoneLevels;

    /** 诊断 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标志 */
    private Integer isDeleted;
}
