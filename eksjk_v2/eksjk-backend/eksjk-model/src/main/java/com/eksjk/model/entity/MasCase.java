package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * McCune-Albright (MAS) 疾病子表实体类
 * <p>对应数据库表 datamain_mas</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_mas")
public class MasCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    /** 咖啡牛奶斑 */
    private String cafeAuLaitSpots;

    /** 纤维性骨发育不良 */
    private String fibrousDysplasia;

    /** 性早熟 */
    private String precociousPuberty;

    /** 甲状腺异常 */
    private String thyroidAbnormality;

    /** 生长激素过多 */
    private String ghExcess;

    /** 库欣综合征 */
    private String cushingSyndrome;

    /** 磷酸盐消耗 */
    private String phosphateWasting;

    /** 诊断 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    /** 身高增长速度 cm/年 */
    private String heightRate;

    // ==================== 影像学检查 ====================

    /** 肾上腺B超 */
    private String adrenalUlt;

    /** 肾脏B超 */
    private String renalUlt;

    /** 骨骼X线 */
    private String boneXRay;

    /** 心脏B超 */
    private String cardiacUlt;

    /** MR部位 */
    private String mrPart;

    /** MR结果 */
    private String mrResult;

    /** CT部位 */
    private String ctPart;

    /** CT结果 */
    private String ctResult;

    /** 创建时间 */
    private LocalDateTime createTime;

    // ==================== 遗传学检查 ====================

    /** GNAS基因测定: 1=是, 2=否, 3=不详 */
    private String gnas;

    /** 标本采样类型或部位 */
    private String gnasSamLoc;

    /** 遗传学检测方法 */
    private String genTesMet;

    /** 检测结果 */
    private String detRes;

    /** 检测版本 */
    private String detVer;

    /** 突变位点 */
    private String mutSit;

    // ==================== 管理字段 ====================

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标志 */
    private Integer isDeleted;
}
