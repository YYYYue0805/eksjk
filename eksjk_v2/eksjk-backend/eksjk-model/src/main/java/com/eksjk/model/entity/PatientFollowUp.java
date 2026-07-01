package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通用随访实体类
 * <p>兼容 V1 表结构（表名 datamain_patfoll）</p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_patfoll")
public class PatientFollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 病例主表ID */
    private Long patientId;

    // ==================== 基础测量 ====================

    /** 随访日期 */
    private LocalDateTime follTime;

    /** 上传日期 */
    private LocalDateTime upTime;

    /** 年龄 */
    private String age;

    /** 现身高 cm */
    @TableField("Ht")
    private String ht;

    /** 现体重 kg */
    @TableField("Wt")
    private String wt;

    /** BMI值 */
    private String bmi;

    /** 体脂率 % */
    private String bodyFat;

    /** 腰围 cm */
    private String waistline;

    /** 臀围 cm */
    private String hips;

    /** 腰臀比 */
    private String waistToHipRatio;

    // ==================== 骨龄评估 ====================

    /** R系列骨龄 */
    private String rboneAge;

    /** C系列骨龄 */
    private String cboneAge;

    // ==================== 发育分期 ====================

    /** 生殖器分期 */
    private String genStag;

    /** 阴毛分期 */
    private String pubStag;

    // ==================== 生长因子 ====================

    /** IGF-1 ng/ml */
    @TableField("IGF1")
    private String igf1;

    /** IGFBP-3 ug/ml */
    @TableField("IGFBP3")
    private String igfbp3;

    // ==================== 甲状腺功能 ====================

    /** 甲功（V1兼容，正常/异常 + 描述） */
    @TableField("Jiagong")
    private String jiagong;

    /** TSH (uIU/mL) */
    private String tsh;

    /** FT3 (pg/mL) */
    private String ft3;

    /** FT4 (ng/dL) */
    private String ft4;

    /** TPOAb (IU/mL) */
    private String tpoab;

    /** TgAb (IU/mL) */
    private String tgab;

    // ==================== 血糖/胰岛素 ====================

    /** 空腹血糖 */
    private String fasBloodGlu;

    /** 空腹胰岛素 */
    private String fasInsulin;

    /** 糖化血红蛋白 */
    private String glyHem;

    // ==================== 肝肾功能 ====================

    /** 肝肾脂电解质 */
    private String livKidLip;

    // ==================== 性激素 ====================

    /** LH */
    @TableField("LH")
    private String lh;

    /** FSH */
    @TableField("FSH")
    private String fsh;

    /** E2 */
    @TableField("E2")
    private String e2;

    /** T */
    @TableField("T")
    private String t;

    /** DHT */
    @TableField("DHT")
    private String dht;

    /** 游离睾酮 */
    private String yltg;

    /** SHBG */
    @TableField("SHBG")
    private String shbg;

    // ==================== 影像检查 ====================

    /** 性腺B超 */
    private String gonBUlt;

    /** 睾丸大小 */
    private String tesSize;

    /** 骨密度 */
    private String bonMinDen;

    // ==================== 诊疗方案 ====================

    /** 诊疗方案 */
    private String diaTreaPlan;

    /** 诊疗方案其他字段 */
    private String otherMedicine;

    // ==================== 行为发育评估 ====================

    /** 是否行为发育评估 */
    private String behDevAss;

    /** Peabody运动发育评估 */
    private String pedMotDevAss;

    /** 粗大运动 */
    private String groMot;

    /** 个人社会 */
    private String indSoc;

    /** 听力语言 */
    private String lisLan;

    /** 手眼协调 */
    private String hanEyeCoo;

    /** 视觉表现 */
    private String visRep;

    /** 实际推理 */
    private String praRea;

    /** 韦氏智力量表 */
    private String wecSca;

    // ==================== 其他 ====================

    /** 是否达终身高 */
    private String isFinalhei;

    /** 其他 */
    private String other;

    /** 图像 */
    private String image;

    /** 实验室检查其它字段 */
    private String labExaOther;

    /** MAS实验室检查 */
    private String labExaMas;

    /** 疾病 */
    private String disease;

    /** 地舒单抗 */
    private String dsdk;

    /** 唑来膦酸 */
    private String clls;

    /** 其他用量 */
    private String qtyl;

    /** 其他检查 */
    private String otherExam;

    /** EOS */
    private String eos;

    /** 其他图片名称 */
    private String otherImaName;

    /** 删除标志 */
    private String delFlg;

    // ==================== 性激素扩展 ====================

    /** PRL (ng/mL) */
    private String prl;

    /** FT (ng/dL) */
    private String ft;

    /** AMH (ng/mL) */
    private String amh;

    /** INHB (pg/mL) */
    private String inhb;

    // ==================== 生长因子补充 ====================

    /** 糖化血红蛋白A1c (%) */
    private String glyHemA;

    // ==================== 肾上腺激素 ====================

    /** ACTH (8am, pg/mL) */
    private String acth;

    /** 皮质醇 (8am, ug/dL) */
    private String cortisol;

    /** 17-OHP (nmol/L) */
    private String ohp;

    /** DHEA-S (ug/dL) */
    private String dheas;

    /** 雄烯二酮 (ng/mL) */
    private String androstenedione;

    /** 甲胎蛋白 (ng/mL) */
    private String afp;

    /** 癌胚抗原 (ng/mL) */
    private String cea;

    // ==================== 激发试验 ====================

    /** HCG 激发前 T */
    private String hcg;

    /** HCG 激发后 T */
    private String hcgt;

    /** HCG 激发后 DHT */
    private String hcgdht;

    /** HCG 激发后 AD */
    private String hcgad;

    /** GnRH 激发 LHmax */
    private String lhMax;

    /** GnRH 激发 FSHmax */
    private String fshMax;

    /** GH 药物激发试验 - GH 峰值 (ng/mL) */
    private String gh;

    // ==================== 常规实验室检查 ====================

    /** 血常规（编码格式） */
    private String bloodRoutine;

    /** 尿常规（编码格式） */
    private String urineRoutine;

    /** 乙肝三系 */
    private String hepatitisB;

    // ==================== 影像检查补充 ====================

    /** 垂体MRI */
    private String pituitaryMri;

    /** 甲状腺B超 */
    private String thyroidUlt;

    /** 性腺B超详情(JSON) */
    private String gonBUltDetail;

    // ==================== 甲状腺补充 ====================

    /** 甲状腺功能评估 */
    private String thyroidFunction;

    // ==================== 各项检查日期 ====================

    // 性激素日期
    private String lhCheckDate;
    private String fshCheckDate;
    private String e2CheckDate;
    private String tCheckDate;
    private String prlCheckDate;
    private String dhtCheckDate;
    private String ftCheckDate;
    private String shbgCheckDate;
    private String amhCheckDate;
    private String inhbCheckDate;
    // 生长因子日期
    private String igf1CheckDate;
    private String igfbp3CheckDate;
    private String fasBloodGluCheckDate;
    private String fasInsulinCheckDate;
    private String glyHemCheckDate;
    private String glyHemACheckDate;
    // 肾上腺日期
    private String acthCheckDate;
    private String cortisolCheckDate;
    private String ohpCheckDate;
    private String dheasCheckDate;
    private String androstenedioneCheckDate;
    private String afpCheckDate;
    private String ceaCheckDate;
    // 激发试验日期
    private String hcgCheckDate;
    private String hcgtCheckDate;
    private String hcgdhtCheckDate;
    private String hcgadCheckDate;
    private String lhMaxCheckDate;
    private String fshMaxCheckDate;
    private String ghCheckDate;
    // 甲状腺日期
    private String tshCheckDate;
    private String ft3CheckDate;
    private String ft4CheckDate;
    private String tpoabCheckDate;
    private String tgabCheckDate;

    // ==================== 眼科检查 ====================

    /** 眼科检查数据(JSON) */
    private String eyeExam;

    // ==================== 审核发放字段 ====================

    /** 审核状态: pending_review/pending_release/released/rejected */
    private String auditStatus;

    /** 审核人(用户名) */
    private String auditBy;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 发放人(用户名) */
    private String releaseBy;

    /** 发放时间 */
    private LocalDateTime releaseTime;

    /** 审核意见 */
    private String auditRemark;
}
