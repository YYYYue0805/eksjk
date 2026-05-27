package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 身体活动问卷实体类
 * <p>兼容 V1 表结构（表名 school_sthd）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_sthd")
public class Sthd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    /** 中强度身体活动频率 */
    private String cjzqdpl;
    /** 中高强度身体活动时间 */
    private String dssjzqd;
    /** 低强度身体活动频率 */
    private String cjdqdpl;
    /** 低强度身体活动时间 */
    private String dssjdqd;
    /** 静坐频率 */
    private String jzpl;
    /** 每天静坐时间 */
    private String jzsj;
    /** 非看屏幕的静坐频率 */
    private String fkpmjspv;
    /** 非看屏幕的静坐时间 */
    private String fkpmjzsj;
    /** 屏幕前静坐频率 */
    private String pmjzpl;
    /** 屏幕前静坐时间 */
    private String pmjzsj;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
