package com.eksjk.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 患者病例 VO（列表展示）
 *
 * @author eksjk
 */
@Data
public class PatientVO {

    /** 患者ID（Hashids编码） */
    private String id;

    /** 疾病分类编码 */
    private String disClass;

    /** 疾病分类名称 */
    private String disClassName;

    /** 病例编号 */
    private String caseNum;

    /** 病历号 */
    private String medrecNum;

    /** 家庭分组ID */
    private String familyId;

    /** 患者编号 */
    private String userNum;

    /** 患者姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 性别名称 */
    private String sexName;

    /** 出生日期 */
    private LocalDateTime birthTime;

    /** 年龄 */
    private String age;

    /** 身高 */
    private String height;

    /** 体重 */
    private String weight;

    /** BMI */
    private String bmi;

    /** 主诉 */
    private String chiCom;

    /** 导入人员 */
    private String impPer;

    /** 上传机构 */
    private String upMec;

    /** 所在医院 */
    private String hospitalName;

    /** 创建时间 */
    private LocalDateTime cTime;

    /** 修改时间 */
    private LocalDateTime modifyTime;

    /** 删除标志 */
    private String delFlg;

    // ==================== 详情时返回的扩展数据 ====================

    /** 家庭信息 */
    private String fht;
    private String mht;
    private String fhw;
    private String mhw;
    private String menAge;
    private String familyHis;

    /** 家族成员信息（JSON数组） */
    private String familyMembers;

    /** 有无兄弟姐妹 */
    private String isBot;

    /** 既往史 */
    private String pastHis;

    /** 既往测量时间 */
    private LocalDateTime pastTime;


    /** 生长速率选择（1=不详, 2=已选择数值） */
    private String growRate;

    /** 生长速率数值（cm/年） */
    private String rate;

    /** 是否有一般检查（1=有, 0=无） */
    private String hasGeneralExam;

    /** 一般检查描述 */
    private String generalExamDesc;

    /** 初次遗精 */
    private String firstEjaculation;

    /** 是否有初次遗精/月经初潮（1=有, 0=无） */
    private String hasFirstEjaculation;

    /** 初次遗精/月经初潮时间（年月） */
    private String firstEjaculationDate;

    /** 是否有第二性征（1=有, 0=无） */
    private String hasSecondarySexual;

    /** 第二性征出现日期 */
    private LocalDateTime secondarySexualDate;

    /** 出生信息 */
    private String gesWeek;
    private String bwt;
    private String bl;
    private String cesaSec;
    private String cesaAsphyxia;

    /** 就诊信息 */
    private LocalDateTime firVisTime;
    private String firVisAge;
    private String icd;

    /** 骨龄 */
    private String rboneAge;
    private String cboneAge;

    /** 联系信息 */
    private String selfTel;
    private String relation;
    private String gonadalSex;
    private String ethnic;

    /** 身份证号码 */
    private String card;

    /** 家庭地址 */
    private String famAdr;

    /** 联系人姓名 */
    private String contactsName;

    /** 致病基因（复用疾病描述字段） */
    private String categoryDescribe;

    // ==================== Tanner 分期 ====================

    private String genStag;
    private String pubStag;

    // ==================== 体格扩展 ====================

    private String heightSds;
    private String weightSds;
    private String lowerMeasure;

    private String armLength;
    private String specialFace;
    private String specialFaceDesc;
    private String scoliosis;
    private String scoliosisDesc;
    private String rash;
    private String rashDesc;
    private String breastDevLeft;
    private String breastDevRight;

    // ==================== 性激素 ====================

    private String lh;
    private String fsh;
    private String e2;
    private String t;
    private String prl;
    private String dht;
    private String ft;
    private String shbg;
    private String amh;
    private String inhb;

    // ==================== 生长因子/代谢 ====================

    private String igf1;
    private String igfbp3;
    private String fasBloodGlu;
    private String fasInsulin;
    private String glyHem;
    private String glyHemA;

    // ==================== 肾上腺 ====================

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

    // ==================== 甲状腺 ====================

    private String tsh;
    private String ft3;
    private String ft4;
    private String tpoab;
    private String tgab;

    /** 甲状腺功能（1=正常, 2|描述=异常） */
    private String thyroidFunction;

    // ==================== 检查日期（每个检验项独立日期） ====================

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

    @Deprecated private String hormoneCheckDate;
    @Deprecated private String thyroidCheckDate;
    @Deprecated private String adrenalCheckDate;
    @Deprecated private String growthFactorCheckDate;
    @Deprecated private String provocationCheckDate;

    // ==================== 常规实验室检查 ====================

    private String bloodRoutine;
    private String urineRoutine;
    private String hepatitisB;
    private String liverKidneyElectrolyte;

    // ==================== 影像描述 ====================

    private String gonBUlt;
    private String gonBUltDetail;
    private String pituitaryMri;
    private String thyroidUlt;
    private String bonMinDen;

    // ==================== 遗传学 ====================

    private String karyotype;
    private String biologBank;
    private String biologBankFa;
    private String biologBankMo;

    /** 基因突变检测数据(JSON) */
    private String genData;

    /** 手术情况 */
    private String surgeryNote;

    /** 病理结果 */
    private String pathologyResult;

    /** 处理意见 */
    private String treatmentOpinion;

    /** 遗传学其他 */
    private String geneticsOther;

    /** 生物样本库详情(JSON数组) */
    private String biologBankData;

    /** 疾病专项数据 */
    private Map<String, Object> diseaseData;

    /** 随访记录数量 */
    private Integer followUpCount;

    /** ELTM诊断状态: unclassified/suggested/auto_classified/uncertain */
    private String diagnosisStatus;

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
