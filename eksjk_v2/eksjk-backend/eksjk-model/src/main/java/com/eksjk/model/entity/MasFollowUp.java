package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * MAS专用随访实体类
 * <p>兼容 V1 表结构（表名 datamain_masfoll）</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_masfoll")
public class MasFollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** MAS病例ID */
    private Long masId;

    /** 是否达终身高 */
    private String isFinalhei;

    // ==================== 治疗随访 ====================

    /** 有无对外周性性早熟进行治疗 */
    private String isPerPre;

    /** 外周性性早熟随访情况 */
    private String perPreSf;

    /** 有无对甲状腺功能亢进进行治疗 */
    private String isHyper;

    /** 甲亢随访情况 */
    private String hyperSf;

    /** 甲亢监测指标 */
    private String hyperJc;

    /** 有无对生长激素分泌过多进行治疗 */
    private String isGroHor;

    /** 生长激素随访情况 */
    private String groHorSf;

    /** 生长激素监测指标 */
    private String groHorJc;

    /** 有无对高泌乳素血症进行治疗 */
    private String isTreHpy;

    /** 高泌乳素随访情况 */
    private String treHpySf;

    /** 高泌乳素监测指标 */
    private String treHpyJc;

    /** 有无对皮质醇增多症进行治疗 */
    private String isIncCor;

    /** 皮质醇随访情况 */
    private String incCorSf;

    /** 皮质醇监测指标 */
    private String incCorJc;

    // ==================== 手术 ====================

    /** 是否行颅内手术 */
    private String isIntSur;

    /** 是否行双侧肾上腺切除术 */
    private String isBilAdr;

    // ==================== 骨痛治疗 ====================

    /** 是否对骨痛进行治疗 */
    private String isBonPai;

    /** 骨痛随访情况 */
    private String bonPaiSf;

    /** 骨痛监测指标 */
    private String bonPaiJc;

    // ==================== 低磷酸盐血症 ====================

    /** 是否对低磷酸盐血症进行治疗 */
    private String hypop;

    /** 低磷酸盐随访情况 */
    private String hypopSf;

    /** 低磷酸盐监测指标 */
    private String hypopJc;

    // ==================== 其他 ====================

    /** 是否行骨骼外科手术 */
    private String isSkeSur;

    /** 是否对牛奶咖啡斑进行激光治疗 */
    private String isCafeSpot;

    /** 是否进行心理疏导 */
    private String isPsyCou;

    /** 生存状态 */
    private String surSta;

    /** 删除标志 */
    private String delFlg;
}
