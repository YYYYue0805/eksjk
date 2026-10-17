package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ELTM 同步失败明细
 *
 * @author eksjk
 */
@Data
@TableName("eltm_sync_error")
public class EltmSyncError implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 同步日志ID */
    private Long syncLogId;

    /** 外部患者ID */
    private Long externalId;

    /** 失败阶段 */
    private String stage;

    /** 错误信息 */
    private String errorMessage;

    /** 原始 JSON */
    private String rawJson;

    /** 创建时间 */
    private LocalDateTime createTime;
}
