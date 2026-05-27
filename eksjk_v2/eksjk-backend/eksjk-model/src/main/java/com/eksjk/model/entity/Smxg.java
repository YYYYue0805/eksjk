package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CSHQ 儿童睡眠习惯问卷实体类
 * <p>兼容 V1 表结构（表名 school_smxg）</p>
 *
 * @author eksjk
 */
@Data
@TableName("school_smxg")
public class Smxg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    /** 就寝时间：平时 */
    private LocalDateTime psscsj;
    /** 就寝时间：周末 */
    private LocalDateTime zmscsj;
    /** 睡着时间：平时 */
    private LocalDateTime psszsj;
    /** 睡着时间：周末 */
    private LocalDateTime zmszsj;
    /** 固定时间上床睡觉 */
    private String gdsjscsj;
    /** 上床后20分钟内入睡 */
    private String esfzrs;
    /** 在自己床上独自入睡 */
    private String dzrs;
    /** 在他人床上入睡 */
    private String ztrcsrs;
    /** 入睡时摇摆或节律性动作 */
    private String rsyb;
    /** 需要特定物品入睡 */
    private String tdwrs;
    /** 需要家长陪伴才能入睡 */
    private String xypbrs;
    /** 准备好去睡觉 */
    private String hzbrs;
    /** 抗拒去睡觉 */
    private String hkjrs;
    /** 挣扎（哭闹、拒绝待在床上） */
    private String hzhzz;
    /** 害怕在黑暗中睡觉 */
    private String hzhpha;
    /** 害怕独自一个人睡觉 */
    private String hzhpyr;
    /** 每天的睡眠时间 */
    private String hzmtsm;
    /** 睡得太少 */
    private String sdts;
    /** 睡得太多 */
    private String sdtd;
    /** 睡眠适量 */
    private String smsl;
    /** 每天睡眠量一样 */
    private String mtsmlyy;
    /** 晚上会尿床 */
    private String wsnc;
    /** 睡眠中说梦话 */
    private String smsmh;
    /** 睡眠中不安稳 */
    private String smbaw;
    /** 夜间梦游 */
    private String hmy;
    /** 夜间移动到他人床上 */
    private String hdtrcs;
    /** 睡眠中身体疼痛 */
    private String fytt;
    /** 疼痛部位 */
    private String ttbw;
    /** 夜间醒来后能自主重新入睡 */
    private String mybzcxrs;
    /** 睡眠中磨牙 */
    private String myxx;
    /** 睡眠中打鼾 */
    private String dhl;
    /** 睡眠中呼吸暂停 */
    private String hxzt;
    /** 鼻息重或气急 */
    private String bxz;
    /** 不在家睡觉时有问题 */
    private String jzbzjywt;
    /** 抱怨睡眠问题 */
    private String bysmwt;
    /** 夜间醒来尖叫、出汗 */
    private String yjxljj;
    /** 被噩梦惊醒 */
    private String emjx;
    /** 夜间醒来持续时间：平时 */
    private String psxlsj;
    /** 夜间醒来持续时间：周末 */
    private String zmxlsj;
    /** 夜间醒来一次 */
    private String xlyc;
    /** 夜间醒来一次以上 */
    private String xlycys;
    /** 早晨醒来时间：平时 */
    private LocalDateTime pszcxlsj;
    /** 早晨醒来时间：周末 */
    private LocalDateTime zmzcxlsj;
    /** 早晨起床时间：平时 */
    private LocalDateTime psqcsj;
    /** 早晨起床时间：周末 */
    private LocalDateTime zmqcsj;
    /** 早晨自己醒来 */
    private String zjxl;
    /** 早晨由闹钟叫醒 */
    private String nzjx;
    /** 醒来后情绪不佳 */
    private String xlqxbj;
    /** 由他人叫醒 */
    private String yjrjx;
    /** 早晨起床困难 */
    private String qckn;
    /** 需要很长时间才能清醒 */
    private String csjcnqx;
    /** 早晨醒来很早 */
    private String xlhz;
    /** 早晨胃口很好 */
    private String wkhh;
    /** 日间会小睡 */
    private String rjhxs;
    /** 在兴奋活动中突然睡着 */
    private String xfhdzsz;
    /** 看起来很疲倦 */
    private String kqlhpj;
    /** 独自玩耍 */
    private String dzws;
    /** 看电视 */
    private String kds;
    /** 坐车 */
    private String zc;
    /** 吃饭 */
    private String cf;

    private LocalDateTime cTime;
    private LocalDateTime modifyTime;
    private String delFlg;
    private String count;
}
