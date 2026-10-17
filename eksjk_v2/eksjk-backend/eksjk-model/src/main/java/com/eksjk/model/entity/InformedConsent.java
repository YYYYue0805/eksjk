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

    // ==================== datamain 签署记录字段 ====================

    /** 外部签署记录ID */
    private String signId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 外部归属医生ID */
    private Long externalDoctorId;

    /** 外部项目ID */
    private String projectId;

    /** 外部分组ID */
    private String groupId;

    /** 外部知情同意书模板ID */
    private String externalConsentId;

    /** HTML 正文 */
    private String content;

    /** 签署医生ID */
    private String signDoctorId;

    /** 患者签名图片 */
    private String patientSignImage;

    private LocalDateTime patientSignTime;

    private String guardianSignImage;

    private LocalDateTime guardianSignTime;

    private String doctorSignImage;

    private LocalDateTime doctorSignTime;

    /** 签署状态 */
    private String signStatus;

    private String pdfPath;

    private String pdfFileName;

    private String pdfErrorMsg;

    /** PDF 状态：0待生成/1成功/2失败/3跳过 */
    private String pdfStatus;

    private Integer pdfRetryCount;

    private LocalDateTime pdfGenerateTime;

    /** 外部原始 JSON */
    private String rawJson;

    private LocalDateTime externalCreateTime;

    private LocalDateTime externalUpdateTime;

    /** 来源 */
    private String source;

    /** 同步时间 */
    private LocalDateTime syncTime;

    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
