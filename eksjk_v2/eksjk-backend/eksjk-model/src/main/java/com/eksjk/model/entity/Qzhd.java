package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 亲子活动问卷实体类
 * <p>兼容 V1 表结构（表名 school_qzhd）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_qzhd")
public class Qzhd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    /** 与孩子一起阅读、看图画书 */
    private String yhzyqks;
    /** 在生活中教孩子数的概念 */
    private String jhzsdgl;
    /** 涂涂画画 */
    private String tthh;
    /** 跟孩子一起玩开发智力的游戏 */
    private String yhzyqyx;
    /** 结合日常生活与孩子一起识字 */
    private String yqsz;
    /** 一起听唱歌曲、诗歌、童谣 */
    private String yqcg;
    /** 讲故事 */
    private String jgs;
    /** 做手工 */
    private String zsg;
    /** 做运动 */
    private String zyd;
    /** 教孩子生活自理技能 */
    private String jhzzl;
    /** 与孩子谈论周围发生的一些事 */
    private String yhztlzw;
    /** 与孩子一起认识大自然的动植物 */
    private String yhzrsdzr;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
