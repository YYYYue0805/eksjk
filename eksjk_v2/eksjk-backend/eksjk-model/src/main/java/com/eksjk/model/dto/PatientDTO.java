package com.eksjk.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 患者病例保存 DTO
 *
 * @author eksjk
 */
@Data
public class PatientDTO {

    // ==================== 基本信息 ====================

    /** 疾病分类编码 */
    private String disClass;

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

    /** 出生日期 */
    private LocalDateTime birthTime;

    /** 与患者关系 */
    private String relation;

    /** 本人电话 */
    private String selfTel;

    /** 性腺性别 */
    private String gonadalSex;

    /** 主诉 */
    private String chiCom;

    /** 民族 */
    private String ethnic;

    /** 身份证号码（患者本人身份证号） */
    private String card;

    /** 家庭地址 */
    private String famAdr;

    /** 联系人姓名 */
    private String contactsName;

    /** 致病基因（复用疾病描述字段） */
    private String categoryDescribe;

    // ==================== 生长数据 ====================

    /** 身高 */
    private String height;

    /** 体重 */
    private String weight;

    /** R型骨龄 */
    private String rboneAge;

    /** C型骨龄 */
    private String cboneAge;

    // ==================== 家庭信息 ====================

    /** 父亲身高 */
    private String fht;

    /** 母亲身高 */
    private String mht;

    /** 父亲体重 */
    private String fhw;

    /** 母亲体重 */
    private String mhw;

    /** 初潮年龄 */
    private String menAge;

    /** 有无兄弟姐妹 */
    private String isBot;

    /** 家族史 */
    private String familyHis;

    /** 家族成员信息（JSON数组） */
    private String familyMembers;

    // ==================== 出生信息 ====================

    /** 胎龄周 */
    private String gesWeek;

    /** 出生体重 */
    private String bwt;

    /** 出生身长 */
    private String bl;

    /** 分娩方式 */
    private String cesaSec;

    /** 保胎史 */
    private String fetProHis;

    /** 既往史 */
    private String pastHis;

    /** 既往测量时间 */
    private LocalDateTime pastTime;


    /** 窒息抢救史 */
    private String cesaAsphyxia;

    /** 胎次 */
    private String parity;

    /** 产次 */
    private String pronum;

    // ==================== 就诊信息 ====================

    /** 初诊时间 */
    private LocalDateTime firVisTime;

    /** 确诊年龄 */
    private String firVisAge;

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

    /** ICD编码 */
    private String icd;

    // ==================== Tanner 分期 ====================

    /** 生殖器发育(Tanner) */
    private String genStag;

    /** 阴毛发育(Tanner) */
    private String pubStag;

    // ==================== 体格扩展 ====================

    private String heightSds;
    private String weightSds;
    private String lowerMeasure;

    /** 臂长(cm) */
    private String armLength;

    /** 特殊面容（1=无, 2=有） */
    private String specialFace;

    /** 特殊面容描述 */
    private String specialFaceDesc;

    /** 脊柱侧弯（1=无, 2=有） */
    private String scoliosis;

    /** 脊柱侧弯描述 */
    private String scoliosisDesc;

    /** 皮疹（1=无, 2=有） */
    private String rash;

    /** 皮疹描述 */
    private String rashDesc;

    /** 左侧乳腺发育 B1-B5 */
    private String breastDevLeft;

    /** 右侧乳腺发育 B1-B5 */
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
    private String thyroidUltDetail;
    private String bonMinDen;

    // ==================== 遗传学 ====================

    private String karyotype;

    /** 染色体核型多选(JSON数组) */
    private String chrom;

    /** 其它异常核型 */
    private String chromOther;

    private String biologBank;
    private String biologBankFa;
    private String biologBankMo;

    /** 基因检测方法 */
    private String geneTestMethod;

    /** 基因检测结果（阴性/阳性） */
    private String geneTestResult;

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

    // ==================== 眼科检查 ====================

    /** 眼科检查数据(JSON) */
    private String eyeExam;

    // ==================== 疾病专项数据（动态字段） ====================

    /**
     * 疾病子表数据，以 Map 形式传递
     * key 为字段名，value 为字段值
     */
    private Map<String, Object> diseaseData;
}
