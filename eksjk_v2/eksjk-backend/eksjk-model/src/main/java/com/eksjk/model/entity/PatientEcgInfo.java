package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 病例心电图信息（备注 + 检查时间）
 * <p>与病例 1:1，patient_id 作为主键</p>
 *
 * @author eksjk
 */
@Data
@TableName("patient_ecg_info")
public class PatientEcgInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 病例 ID（主键） */
    @TableId(type = IdType.INPUT)
    private Long patientId;

    /** 心电图备注 */
    private String ecgNote;

    /** 心电图检查时间 */
    private String ecgCheckDate;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
