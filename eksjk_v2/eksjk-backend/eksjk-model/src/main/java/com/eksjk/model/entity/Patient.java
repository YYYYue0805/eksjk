package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者主表实体类
 * <p>
 * 兼容 V1 表结构（表名 datamain_patient），
 * 包含患者基本信息、家庭信息、出生信息、就诊信息等。
 * </p>
 *
 * @author eksjk
 */
@Data
@TableName("datamain_patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    // ==================== 疾病分类 ====================

    /** 疾病分类编码（10000001~10000007） */
    private String disClass;

    // ==================== 编号体系 ====================

    /** 病例编号（系统自动生成，唯一） */
    private String caseNum;

    /** 病历号（医院病历号） */
    private String medrecNum;

    /** 家庭分组ID（同一家庭的患者共享） */
    @TableField("family_id")
    private String familyId;

    /** 患者编号 */
    private String userNum;

    // ==================== 基本信息 ====================

    /** 患者姓名 */
    private String name;

    /** 社会性别 */
    private String sex;

    /** 出生日期 */
    private LocalDateTime birthTime;

    /** 与患者关系 */
    private String relation;

    /** 本人电话 */
    private String selfTel;

    /** 医生姓名 */
    private String doctorName;

    /** 性腺性别 */
    private String gonadalSex;

    /** 年龄（年） */
    @TableField("AGEy")
    private String ageY;

    /** 年龄（月） */
    @TableField("AGEm")
    private String ageM;

    /** 主诉 */
    private String chiCom;

    /** 年龄 */
    private String age;

    /** 民族 */
    private String ethnic;

    // ==================== 生长数据 ====================

    /** 身高 */
    private String height;

    /** 体重 */
    private String weight;

    /** BMI值 */
    private String bmi;

    /** R型骨龄 */
    private String rboneAge;

    /** C型骨龄 */
    private String cboneAge;

    // ==================== 家庭信息 ====================

    /** 父亲身高 */
    @TableField("FHt")
    private String fht;

    /** 母亲身高 */
    @TableField("MHt")
    private String mht;

    /** 父亲体重 */
    @TableField("FHw")
    private String fhw;

    /** 母亲体重 */
    @TableField("MHw")
    private String mhw;

    /** 初潮年龄 */
    private String menAge;

    /** 有无兄弟姐妹 */
    private String isBot;

    /** 家族史 */
    private String familyHis;

    /** 家族成员信息（JSON数组） */
    @TableField("family_members")
    private String familyMembers;

    // ==================== 出生信息 ====================

    /** 胎龄周 */
    private String gesWeek;

    /** 出生体重 */
    @TableField("BWt")
    private String bwt;

    /** 出生身长 */
    @TableField("BL")
    private String bl;

    /** 分娩方式 */
    private String cesaSec;

    /** 保胎史 */
    private String fetProHis;

    /** 既往史 */
    private String pastHis;

    /** 窒息抢救史 */
    private String cesaAsphyxia;

    /** 胎次 */
    private String parity;

    /** 产次 */
    private String pronum;

    /** 孕期感染 */
    private String pregnancyInfection;

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

    /** 国际疾病分类 ICD */
    @TableField("ICD")
    private String icd;

    /** 是否达终身高 */
    private String isFinalhei;

    // ==================== Tanner 分期 ====================

    /** 生殖器发育(Tanner分期 I-V) */
    private String genStag;

    /** 阴毛发育(Tanner分期 I-V) */
    private String pubStag;

    // ==================== 体格扩展 ====================

    /** 身高 SDS */
    private String heightSds;

    /** 体重 SDS */
    private String weightSds;

    /** 下部量(cm) */
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

    /** LH (mIU/mL) */
    private String lh;

    /** FSH (mIU/mL) */
    private String fsh;

    /** E2 (pg/mL) */
    @TableField("e2_pgml")
    private String e2;

    /** T (ng/dL) */
    @TableField("t_ngdl")
    private String t;

    /** PRL (ng/mL) */
    private String prl;

    /** DHT (ng/dL) */
    private String dht;

    /** FT (ng/dL) */
    private String ft;

    /** SHBG (nmol/L) */
    private String shbg;

    /** AMH (ng/mL) */
    private String amh;

    /** INHB (pg/mL) */
    private String inhb;

    // ==================== 生长因子/代谢 ====================

    /** IGF-1 (ng/mL) */
    private String igf1;

    /** IGFBP-3 (ug/mL) */
    private String igfbp3;

    /** 空腹血糖 (mmol/L) */
    private String fasBloodGlu;

    /** 空腹胰岛素 (uIU/mL) */
    private String fasInsulin;

    /** 糖化血红蛋白 (%) */
    private String glyHem;

    /** 糖化血红蛋白 A1c (%) */
    private String glyHemA;

    // ==================== 肾上腺激素 ====================

    /** ACTH (pg/mL) */
    private String acth;

    /** 皮质醇 (ug/dL) */
    private String cortisol;

    /** 17-OHP (ng/mL) */
    private String ohp;

    /** DHEA-S (ug/dL) */
    private String dheas;

    /** 雄烯二酮 (ng/mL) */
    @TableField("ad")
    private String androstenedione;

    /** 甲胎蛋白 (ng/ml) */
    private String afp;

    /** 癌胚抗原 (ng/ml) */
    private String cea;

    // ==================== 激发试验 ====================

    /** HCG 激发前 */
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

    /** GH 药物激发试验峰值 (ng/mL) */
    private String gh;

    // ==================== 甲状腺功能 ====================

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

    /** 甲状腺功能（1=正常, 2|描述=异常） */
    private String thyroidFunction;

    // ==================== 检查日期（每个检验项独立日期） ====================

    /** LH检查日期 */
    private String lhCheckDate;

    /** FSH检查日期 */
    private String fshCheckDate;

    /** E2检查日期 */
    private String e2CheckDate;

    /** T检查日期 */
    private String tCheckDate;

    /** PRL检查日期 */
    private String prlCheckDate;

    /** DHT检查日期 */
    private String dhtCheckDate;

    /** FT检查日期 */
    private String ftCheckDate;

    /** SHBG检查日期 */
    private String shbgCheckDate;

    /** AMH检查日期 */
    private String amhCheckDate;

    /** INHB检查日期 */
    private String inhbCheckDate;

    /** IGF-1检查日期 */
    private String igf1CheckDate;

    /** IGFBP-3检查日期 */
    private String igfbp3CheckDate;

    /** 空腹血糖检查日期 */
    private String fasBloodGluCheckDate;

    /** 空腹胰岛素检查日期 */
    private String fasInsulinCheckDate;

    /** 糖化血红蛋白检查日期 */
    private String glyHemCheckDate;

    /** 糖化血红蛋白A1c检查日期 */
    private String glyHemACheckDate;

    /** ACTH检查日期 */
    private String acthCheckDate;

    /** 皮质醇检查日期 */
    private String cortisolCheckDate;

    /** 17-OHP检查日期 */
    private String ohpCheckDate;

    /** DHEA-S检查日期 */
    private String dheasCheckDate;

    /** 雄烯二酮检查日期 */
    private String androstenedioneCheckDate;

    /** 甲胎蛋白检查日期 */
    private String afpCheckDate;

    /** 癌胚抗原检查日期 */
    private String ceaCheckDate;

    /** HCG激发前T检查日期 */
    private String hcgCheckDate;

    /** HCG激发后T检查日期 */
    private String hcgtCheckDate;

    /** HCG激发后DHT检查日期 */
    private String hcgdhtCheckDate;

    /** HCG激发后AD检查日期 */
    private String hcgadCheckDate;

    /** GnRH激发LHmax检查日期 */
    private String lhMaxCheckDate;

    /** GnRH激发FSHmax检查日期 */
    private String fshMaxCheckDate;

    /** GH激发试验检查日期 */
    private String ghCheckDate;

    /** TSH检查日期 */
    private String tshCheckDate;

    /** FT3检查日期 */
    private String ft3CheckDate;

    /** FT4检查日期 */
    private String ft4CheckDate;

    /** TPOAb检查日期 */
    private String tpoabCheckDate;

    /** TgAb检查日期 */
    private String tgabCheckDate;

    // ==================== 旧分类级检查日期（保留兼容，不再使用，数据库中不存在） ====================

    /** @deprecated 使用各检验项独立日期 */
    @Deprecated
    @TableField(exist = false)
    private String hormoneCheckDate;

    /** @deprecated 使用各检验项独立日期 */
    @Deprecated
    @TableField(exist = false)
    private String thyroidCheckDate;

    /** @deprecated 使用各检验项独立日期 */
    @Deprecated
    @TableField(exist = false)
    private String adrenalCheckDate;

    /** @deprecated 使用各检验项独立日期 */
    @Deprecated
    @TableField(exist = false)
    private String growthFactorCheckDate;

    /** @deprecated 使用各检验项独立日期 */
    @Deprecated
    @TableField(exist = false)
    private String provocationCheckDate;

    // ==================== 常规实验室检查 ====================

    /** 血常规（编码格式: status|description） */
    private String bloodRoutine;

    /** 尿常规（编码格式: status|description） */
    private String urineRoutine;

    /** 乙肝三系（阴性/HBSAb阳性/小三阳/大三阳） */
    private String hepatitisB;

    /** 肝肾糖电解质（支持OCR识别文本） */
    private String liverKidneyElectrolyte;

    // ==================== 影像描述 ====================

    /** 性腺B超描述 */
    private String gonBUlt;

    /** 性腺B超尺寸详情（JSON格式，含子宫/卵巢/睾丸三维尺寸等） */
    private String gonBUltDetail;

    /** 垂体MRI描述 */
    private String pituitaryMri;

    /** 甲状腺B超描述 */
    private String thyroidUlt;

    /** 骨密度描述 */
    private String bonMinDen;

    // ==================== 遗传学 ====================

    /** 染色体核型 */
    private String karyotype;

    /** 患者生物样本库 */
    private String biologBank;

    /** 父亲生物样本库 */
    private String biologBankFa;

    /** 母亲生物样本库 */
    private String biologBankMo;

    /** 基因突变检测数据(JSON数组) */
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

    // ==================== 小程序关联 ====================

    /** 小程序身份标识 */
    private String xcxCard;

    /** 期望身高 */
    private String expectedHeight;

    /** 当前城市 */
    private String currentCity;

    /** 既往测量时间 */
    private LocalDateTime pastTime;

    /** 既往身高 */
    private String pastHeight;

    /** 既往体重 */
    private String pastWeight;

    /** 宝宝删除标记 */
    private String babyFlag;

    // ==================== 联系人信息 ====================

    /** 个人头像 */
    private String myselfPicture;

    /** 联系人姓名 */
    private String contactsName;

    /** 联系电话 */
    private String contactsNum;

    /** 邮箱 */
    private String pEmial;

    /** 身份证 */
    private String idcard;

    /** 籍贯 */
    private String natPla;

    /** 家庭住址 */
    private String famAdr;

    /** 患者身份证号码 */
    private String card;

    // ==================== 管理字段 ====================

    /** 导入人员 */
    private String impPer;

    /** 上传机构 */
    private String upMec;

    /** 所在医院名称 */
    private String hospitalName;

    /** 导入时间（创建时间） */
    private LocalDateTime cTime;

    /** 修改时间 */
    private LocalDateTime modifyTime;

    /** 修改人员 */
    private String modifyPer;

    /** 删除标志（'0'=已删除, '1'=有效数据） */
    private String delFlg;

    // ==================== 其他字段 ====================

    /** 标签 */
    private String tags;

    /** 混淆姓名 */
    private String confuseName;

    /** 姓名大写 */
    private String upperCase;

    /** 患者照片 */
    private String photo;

    /** 网格地址 */
    private String address;

    /** 疾病描述 */
    private String categoryDescribe;

    /** 入组序号 */
    private String enrollmentNum;

    /** 入组时间 */
    private LocalDateTime enrollmentTime;

    /** 首次提交时间 */
    private LocalDateTime oneTime;

    /** E路童萌id */
    private String eltmId;

    // ==================== V2 新增字段 ====================

    /** 数据同步状态 */
    private String syncStatus;

    /** 同步时间 */
    private LocalDateTime syncTime;

    /** ELTM诊断状态 */
    private String diagnosisStatus;

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
