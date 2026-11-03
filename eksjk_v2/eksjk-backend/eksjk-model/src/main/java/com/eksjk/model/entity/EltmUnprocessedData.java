package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ELTM 外部接口未处理数据
 *
 * @author eksjk
 */
@Data
@TableName("eltm_unprocessed_data")
public class EltmUnprocessedData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** 外部患者ID */
    private Long externalPatientId;

    /** 来源接口：CXLIST/GETTPBYID/GETZQBYID */
    private String apiCode;

    /** 分类：UNKNOWN_COLUMN/UNPARSED_JSON/UNHANDLED_FORM/UNMAPPED_FIELD/UNKNOWN_INTERFACE_FIELD */
    private String category;

    /** 精确来源键：列号/JSON路径/form_data_id:fieldKey/pic_id:colN */
    private String sourceKey;

    /** 便于阅读的来源名称 */
    private String sourceName;

    /** 建议归入的目标模块（基本信息/基线-临床信息/...） */
    private String suggestedModule;

    /** 未处理原因 */
    private String reason;

    /** 原始值 */
    private String rawValue;

    /** 原始 JSON 片段 */
    private String rawJson;

    /** 内容指纹 */
    private String contentHash;

    /** PENDING/MAPPED/IGNORED */
    private String status;

    /** 同步时间 */
    private LocalDateTime syncTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
