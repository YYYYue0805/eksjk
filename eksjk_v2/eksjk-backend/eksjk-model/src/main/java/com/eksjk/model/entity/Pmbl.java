package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 屏幕暴露问卷实体类
 * <p>兼容 V1 表结构（表名 school_pmbl）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_pmbl")
public class Pmbl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    /** 第一次接触电子屏幕的月龄 */
    private String dycyn;
    /** 平均每天接触电子屏幕时间 */
    private String mtjcsj;
    /** 矩阵填写 */
    private String jztx;
    /** 陪同观看的时间 */
    private String ptgksj;
    /** 与其交流电视内容的时间 */
    private String ptgkjlsj;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
