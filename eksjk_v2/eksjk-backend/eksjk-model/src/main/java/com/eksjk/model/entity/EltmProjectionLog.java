package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ELTM 数据投影日志
 *
 * @author eksjk
 */
@Data
@TableName("eltm_projection_log")
public class EltmProjectionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long patientId;

    private Long externalId;

    /** SUCCESS/SKIPPED/FAILED */
    private String status;

    /** 本次写入/更新的字段数 */
    private Integer fieldCount;

    private String message;

    private LocalDateTime createTime;
}
