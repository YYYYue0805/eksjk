package com.eksjk.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 随访记录 VO
 *
 * @author eksjk
 */
@Data
public class FollowUpVO {

    /** 随访ID（Hashids编码） */
    private String id;

    /** 病例主表ID（Hashids编码） */
    private String patientId;

    // ==================== 关联患者信息（审核列表展示用） ====================

    /** 患者病例编号 */
    private String patientCaseNum;

    /** 患者姓名 */
    private String patientName;

    /** 患者性别 */
    private String patientSex;

    /** 患者审核状态 */
    private String patientAuditStatus;

    /** 患者所属医院（数据隔离用） */
    private String patientHospitalName;

    /** 患者导入人员（数据隔离用） */
    private String patientImpPer;

    /** 随访日期 */
    private LocalDateTime follTime;

    /** 上传日期 */
    private LocalDateTime upTime;

    /** 年龄 */
    private String age;

    // ==================== 基础测量 ====================

    /** 身高 cm */
    private String ht;

    /** 体重 kg */
    private String wt;

    /** BMI */
    private String bmi;

    /** 体脂率 */
    private String bodyFat;

    /** 腰围 */
    private String waistline;

    /** 臀围 */
    private String hips;

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

    /** IGF-1 */
    private String igf1;

    /** IGFBP-3 */
    private String igfbp3;

    // ==================== 甲状腺功能 ====================

    /** 甲功（V1兼容） */
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
    private String lh;

    /** FSH */
    private String fsh;

    /** E2 */
    private String e2;

    /** T */
    private String t;

    /** DHT */
    private String dht;

    /** SHBG */
    private String shbg;

    // ==================== 影像检查 ====================

    /** 性腺B超 */
    private String gonBUlt;

    /** 骨密度 */
    private String bonMinDen;

    // ==================== 诊疗方案 ====================

    /** 诊疗方案 */
    private String diaTreaPlan;

    /** 其他 */
    private String other;

    /** 是否达终身高 */
    private String isFinalhei;

    /** 图像 */
    private String image;

    /** 删除标志 */
    private String delFlg;

    // ==================== 性激素扩展 ====================

    private String prl;
    private String ft;
    private String amh;
    private String inhb;

    // ==================== 生长因子补充 ====================

    private String glyHemA;

    // ==================== 肾上腺激素 ====================

    private String acth;
    private String cortisol;
    private String ohp;
    private String dheas;
    private String androstenedione;
    private String afp;
    private String cea;

    // ==================== 激发试验 ====================

    private String hcg;
    private String hcgt;
    private String hcgdht;
    private String hcgad;
    private String lhMax;
    private String fshMax;
    private String gh;

    // ==================== 常规实验室检查 ====================

    private String bloodRoutine;
    private String urineRoutine;
    private String hepatitisB;

    // ==================== 影像检查补充 ====================

    private String pituitaryMri;
    private String thyroidUlt;
    private String gonBUltDetail;

    // ==================== 甲状腺补充 ====================

    private String thyroidFunction;

    // ==================== 各项检查日期 ====================

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
    private String igf1CheckDate;
    private String igfbp3CheckDate;
    private String fasBloodGluCheckDate;
    private String fasInsulinCheckDate;
    private String glyHemCheckDate;
    private String glyHemACheckDate;
    private String acthCheckDate;
    private String cortisolCheckDate;
    private String ohpCheckDate;
    private String dheasCheckDate;
    private String androstenedioneCheckDate;
    private String afpCheckDate;
    private String ceaCheckDate;
    private String hcgCheckDate;
    private String hcgtCheckDate;
    private String hcgdhtCheckDate;
    private String hcgadCheckDate;
    private String lhMaxCheckDate;
    private String fshMaxCheckDate;
    private String ghCheckDate;
    private String tshCheckDate;
    private String ft3CheckDate;
    private String ft4CheckDate;
    private String tpoabCheckDate;
    private String tgabCheckDate;

    /** MAS专用随访数据 */
    private Map<String, Object> masFollowUpData;

    // ==================== 眼科检查 ====================

    /** 眼科检查数据(JSON) */
    private String eyeExam;

    // ==================== 审核发放字段 ====================

    /** 审核状态 */
    private String auditStatus;

    /** 审核人 */
    private String auditBy;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 发放人 */
    private String releaseBy;

    /** 发放时间 */
    private LocalDateTime releaseTime;

    /** 审核意见 */
    private String auditRemark;
}
