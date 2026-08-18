package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知情同意书实体
 *
 * @author eksjk
 */
@Data
@TableName("informed_consent")
public class InformedConsent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** E路童萌患者唯一标识 */
    private String eltmId;

    /** E路童萌外部文件ID */
    private String externalFileId;

    /** 文件名称 */
    private String fileName;

    /** OSS 文件地址 */
    private String ossUrl;

    /** 签署时间 */
    private LocalDateTime signTime;

    /** 患者姓名 */
    private String patientName;

    /** 公众号端医生姓名 */
    private String doctorName;

    /** 来源 */
    private String source;

    /** 同步时间 */
    private LocalDateTime syncTime;

    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
