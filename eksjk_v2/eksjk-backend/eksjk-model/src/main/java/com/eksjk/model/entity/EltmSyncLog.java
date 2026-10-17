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
 * ELTM 数据同步日志
 *
 * @author eksjk
 */
@Data
@TableName("eltm_sync_log")
public class EltmSyncLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 同步类型 */
    private String syncType;

    /** 触发方式 */
    private String triggerType;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 总记录数 */
    private Integer totalCount;

    /** 新增数 */
    private Integer newCount;

    /** 更新数 */
    private Integer updatedCount;

    /** 跳过数 */
    private Integer skippedCount;

    /** 失败数 */
    private Integer failedCount;

    /** 状态 */
    private String status;

    /** 错误信息 */
    private String errorMessage;

    /** 创建时间 */
    private LocalDateTime createTime;
}
