from django.db import models
import datetime

# Create your models here.

class   Patient(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    disclass = (
        ('10000001', "性发育异常"),
        ('10000002', "家族性矮小"),
        ('10000003', "中枢性性早熟"),
        ('10000004', "mas"),
        ('10000005', "SGA"),
        ('10000006', "家族性矮小"),
        ('10000007', "生长发育(E路童萌)"),
    )

    # 疾病分类（对应小程序：添加宝宝_标签）
    dis_class = models.CharField(max_length=8, choices=disclass, default="1")
    # 病例编号
    case_num = models.CharField(max_length=32, unique=True, null=True)
    # 病历号
    medrec_num = models.CharField(max_length=32, null=True)
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 患者姓名（对应小程序：添加宝宝_宝贝姓名）
    name = models.CharField(max_length=12, null=True, default="")
    # 社会性别（对应小程序：添加宝宝_宝贝性别）
    sex = models.CharField(max_length=4, default="1", null=True)
    # 出生日期（对应小程序：添加宝宝_出生日期）
    birth_time = models.DateTimeField(blank=True, null=True)
    # 与患者关系（对应小程序：添加宝宝_关系）
    relation = models.CharField(max_length=12, null=True)
    # 本人电话（对应小程序：添加宝宝_联系电话）
    self_tel = models.CharField(max_length=32, unique=True, null=True)
    # 医生（对应小程序：添加宝宝_选择医生）
    doctor_name = models.CharField(max_length=32, default="无", null=True)
    # 父亲身高（对应小程序：添加宝宝_父亲身高）
    FHt = models.CharField(max_length=12, null=True)
    # 母亲身高（对应小程序：添加宝宝_母亲身高）
    MHt = models.CharField(max_length=12, null=True)
    # 期望身高（对应小程序：添加宝宝_母亲身高） 【属新增】
    expected_height = models.CharField(max_length=32, null=True)
    # 当前城市（对应小程序：添加宝宝_当前城市） 【属新增】
    current_city = models.CharField(max_length=255, null=True)
    # 身高（对应小程序：添加宝宝_宝贝当前身高）
    height = models.CharField(max_length=32, unique=True, null=True)
    # 体重（对应小程序：添加宝宝_宝贝当前体重）
    weight = models.CharField(max_length=32, unique=True, null=True)
    # r型骨龄（对应小程序：添加宝宝_r型骨龄） 【属新增】
    rbone_age = models.CharField(max_length=32, null=True)
    # c型骨龄（对应小程序：添加宝宝_c型骨龄重） 【属新增】
    cbone_age = models.CharField(max_length=32, null=True)
    # 既往测量时间（对应小程序：添加宝宝_既往测量时间） 【属新增】
    past_time = models.DateTimeField(blank=True, null=True)
    # 既往身高（对应小程序：添加宝宝_既往身高） 【属新增】
    past_height = models.CharField(max_length=32, null=True)
    # 既往体重（对应小程序：添加宝宝_既往体重） 【属新增】
    past_weight = models.CharField(max_length=32, null=True)
    # 删除标记（对应小程序：添加宝宝_删除标记） 【属新增】
    baby_flag = models.CharField(max_length=32, null=True)
    # 性腺性别
    gonadal_sex = models.CharField(max_length=4, default="1", null=True)
    # 初诊时间
    fir_vis_time = models.DateTimeField(auto_now_add=True)
    # AGEy
    AGEy = models.CharField(max_length=64, null=True)
    # AGEm
    AGEm = models.CharField(max_length=64, null=True)
    # 主诉
    chi_com = models.CharField(max_length=256, null=True, default="")
    # 父亲体重
    FHw = models.CharField(max_length=12, null=True)
    # 母亲体重
    MHw = models.CharField(max_length=12, null=True)
    # 初潮年龄
    men_age = models.CharField(max_length=8, null=True)
    # 有无兄弟姐妹
    is_bot = models.CharField(max_length=8, null=True)
    # 家族史（包含兄弟姐妹情况）
    family_his = models.CharField(max_length=512, null=True)
    # 胎龄周
    ges_week = models.CharField(max_length=8, null=True)
    # 出生体重
    BWt = models.CharField(max_length=8, null=True)
    # 出生身长
    BL = models.CharField(max_length=8, null=True)
    # 分娩方式  剖宫产=1、顺产=0、臀围产=2、足先露=3、其他=4、不详=5
    cesa_sec = models.CharField(max_length=4, null=True)
    # 保胎史
    fet_pro_his = models.CharField(max_length=256, null=True)
    # 既往史
    past_his = models.CharField(max_length=256, null=True)
    # 窒息抢救史 1=无 、2=轻度窒息、3=重度窒息、有=4、不详=5
    cesa_asphyxia = models.CharField(max_length=4, null=True)
    # 个人头像  （对应小程序：个人信息_头像）   【属未有新增:变更TextField用于大文本存储】
    myself_picture = models.TextField(null=True)
    # 联系人姓名 （对应小程序：个人信息_姓名）
    contacts_name = models.CharField(max_length=12, null=True)
    # 联系电话 （对应小程序：个人信息_手机号）
    contacts_num = models.CharField(max_length=12, null=True)
    # 邮箱     （对应小程序：个人信息_邮箱）   【属未有新增】
    p_emial = models.CharField(max_length=256, null=True)
    # 身份证    （对应小程序：个人信息_身份证）
    idcard = models.CharField(max_length=32, unique=True, null=True)
    # 籍贯  细化到市 （对应小程序：个人信息_家庭住址）
    nat_pla = models.CharField(max_length=128, null=True, default="")
    # 家庭住址
    fam_adr = models.CharField(max_length=256, null=True)
    # 患者身份证号码
    card = models.CharField(max_length=18, null=True)
    # 导入人员
    imp_per = models.CharField(max_length=12, default=None)
    # 上传机构
    up_mec = models.CharField(max_length=64, default="无", null=True)
    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 所在医院
    hospital_name = models.CharField(max_length=32, default="无", null=True)
    # 标签
    tags = models.CharField(max_length=2560, default="无", null=True)
    # 混淆姓名
    confuse_name = models.CharField(max_length=32, unique=True, null=True)
    # 姓名大写
    upper_case = models.CharField(max_length=32, unique=True, null=True)
    # 年龄
    age = models.CharField(max_length=32, unique=True, null=True)
    # 患者照片
    photo = models.CharField(max_length=255, unique=True, null=True)
    # 网格地址
    address = models.CharField(max_length=32, unique=True, null=True)
    # 疾病描述
    category_describe = models.CharField(max_length=128, unique=True, null=True)
    # bmi值
    bmi = models.CharField(max_length=32, unique=True, null=True)
    # 入组序号
    enrollment_num = models.CharField(max_length=32, unique=True, null=True)
    # 入组时间
    enrollment_time = models.DateTimeField(auto_now_add=True)
    # 胎次
    parity = models.CharField(max_length=32, unique=True, null=True)
    # 产次
    pronum = models.CharField(max_length=32, unique=True, null=True)
    # 孕期感染
    pregnancy_infection = models.CharField(max_length=32, unique=True, null=True)
    # 首次提交时间
    one_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 确诊年龄
    fir_vis_age = models.CharField(max_length=8, null=True)
    # xcxCard（小程序身份识别）
    xcx_card = models.CharField(max_length=225, null=True)
    # 国际疾病分类 (新增)
    ICD = models.CharField(max_length=32, null=True)
    # 是否达终身高
    is_finalhei = models.CharField(max_length=512, default="无", null=True)
    # 修改人员
    modify_per = models.CharField(max_length=12, default=None, null=True)
    # E路童萌id
    eltm_id = models.CharField(max_length=24, default=None, null=True)
    # 民族
    ethnic = models.CharField(max_length=24, default=None, null=True)
    class Meta:
        ordering = ["-c_time"]
        verbose_name = "用户基本信息"
        verbose_name_plural = "用户基本信息"

class Case(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    locaureori = (
        ('0', "正常"),
        ('1', "冠状沟型"),
        ('2', "阴茎型"),
        ('3', "阴茎阴囊型"),
        ('4', "会阴型"),
    )
    tespos = (
        ('1', "在阴唇或阴囊"),
        ('2', "在腹股沟"),
        ('3', "在腹部"),
        ('4', "睾丸缺如"),
    )
    hgg = (
        ('1', "无"),
        ('2', "标准HCG激发"),
        ('3', "延长HCG激发"),
    )
    sry = (
        ('1', "阳性"),
        ('2', "阴性"),
    )
    sourmut = (
        ('0', "新生"),
        ('1', "父"),
        ('2', "母"),
    )
    mutkind = (
        ('10010001', "错义突变"),
        ('10010002', "无义突变"),
        ('10010003', "截断突变"),
        ('10010004', "移码突变"),
        ('10010005', "剪接位点突变"),
        ('10010006', "同义突变"),
        ('10010007', "非编码区突变"),
        ('10010008', "剪接位点附近的突变"),
    )
    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # Htcm  现身高
    Ht = models.CharField(max_length=8, null=True)
    # 诊断
    diagnosis = models.CharField(max_length=512, null=True)
    # HSDS  现身高标准差（单位sds）
    HSDS = models.CharField(max_length=8, null=True)
    # Wtkg  现体重
    Wt = models.CharField(max_length=8, null=True)
    # WSDS  现体重标准差
    WSDS = models.CharField(max_length=8, null=True)
    # 阴茎长cm
    penile_length = models.CharField(max_length=8, null=True)
    # 阴茎直径cm
    penile_dia = models.CharField(max_length=8, null=True)
    # 睾丸容积ml
    tes_volume = models.CharField(max_length=8, null=True)
    # Prader分期
    prader = models.CharField(max_length=64, null=True)
    # 尿道口位置0=正常1=冠状沟型 2=阴茎型3=阴茎阴囊型4=会阴型
    loca_ure_ori = models.CharField(max_length=4, choices=locaureori, default="0")
    # 右睾丸位置1=在阴唇或阴囊2=在腹股沟3=在腹部4=睾丸缺如
    rig_tes_pos = models.CharField(max_length=4, choices=tespos, default="1")
    # 左睾丸位置1=在阴唇或阴囊2=在腹股沟3=在腹部4=睾丸缺如
    lef_tes_pos = models.CharField(max_length=4, choices=tespos, default="1")
    # BA岁骨龄，小数点取一位，单位岁
    bone_age = models.CharField(max_length=8, null=True)
    # 骨龄片
    bone_img = models.CharField(max_length=256, null=True)
    # LH  miu/ml
    LH = models.CharField(max_length=32, null=True)
    # FSH  miu/ml
    FSH = models.CharField(max_length=32, null=True)
    # T ng/dL  睾酮
    T = models.CharField(max_length=32, null=True)
    # E2  pg/ml  雌二醇
    E2 = models.CharField(max_length=32, null=True)
    # DHT ng/ml  双氢睾酮2
    DHT = models.CharField(max_length=32, null=True)
    # 游离睾酮 ng/ml
    FT = models.CharField(max_length=32, null=True)
    # SHBG nmol/L  性激素结合球蛋白
    SHBG = models.CharField(max_length=32, null=True)
    # IGF-1（ng/ml）
    IGF1 = models.CharField(max_length=32, null=True)
    # IGFBP-3（ug/ml）
    IGFBP3 = models.CharField(max_length=32, null=True)
    # AMH 抗缪勒管激素
    AMH = models.CharField(max_length=32, null=True)
    # INHB 抑制素B
    INHB = models.CharField(max_length=32, null=True)
    # 促肾上腺皮质激素
    ACTH = models.CharField(max_length=32, null=True)
    # 皮质醇
    Hyd = models.CharField(max_length=32, null=True)
    # 17-OHP
    OHP = models.CharField(max_length=32, null=True)
    # 硫酸脱氢表雄酮
    DHEAS = models.CharField(max_length=32, null=True)
    # 雄烯二酮
    AD = models.CharField(max_length=32, null=True)
    # HCG激发试验1=无，2=标准HCG激发，3=延长HCG激发
    HCG = models.CharField(max_length=4, choices=hgg, default="1")
    # 标准HCG激发T 单位：ng/dL
    HCGT = models.CharField(max_length=32, null=True)
    # 标准HCG激发激发DHT ng/ml
    HCGDHT = models.CharField(max_length=32, null=True)
    # 标准HCG激发激发AD AD是指雄烯二酮，单位写ng/ml
    HCGAD = models.CharField(max_length=32, null=True)
    # 延长HCG激发T
    HCGT_ext = models.CharField(max_length=32, null=True)
    # 延长HCG激发激发DHT
    HCGDHT_ext = models.CharField(max_length=32, null=True)
    # 延长HCG激发激发AD
    HCGAD_ext = models.CharField(max_length=32, null=True)
    # 染色体核型
    spe_kar = models.CharField(max_length=32, null=True)
    # SRY基因：阳性=1，阴性=2，
    SRY = models.CharField(max_length=4, choices=sry, default="1")
    # 基因突变名称
    gen_mut_name = models.TextField(null=True)
    # 变异类型
    mut_kind = models.CharField(max_length=8, choices=mutkind, default="1")
    # 变异来源 新生=0；父=1；母=2
    sour_mut = models.CharField(max_length=4, choices=sourmut, default="1")
    # 核酸变异
    base_mut =models.CharField(max_length=32, null=True)
    # 氨基酸变异
    ami_aci_mut = models.TextField(null=True)
    # 其他
    other = models.TextField(null=True)
    # 手术情况
    operation = models.TextField(null=True)
    # 病理结果
    pat_res = models.TextField(null=True)
    # 处理意见
    han_opi = models.TextField(null=True)
    # B超图像
    B_ult_image = models.TextField(null=True)
    # 是否有生物样本库
    biolog =models.CharField(max_length=32, null=True)
    # 生物样本库名称
    biolog_bank =models.CharField(max_length=32, null=True)
    # 图像说明
    bscanExplain =models.CharField(max_length=512, null=True)
    # 生殖器评估
    genitals =models.CharField(max_length=32, null=True)
    # 双乳发育分期
    breast_dev =models.CharField(max_length=32, null=True)
    # 外生殖器分期
    ex_genitalia =models.CharField(max_length=32, null=True)
    # 阴毛分期
    pubic_hair =models.CharField(max_length=32, null=True)
    # 其他
    body_other =models.CharField(max_length=256, null=True)
    # 磁共振
    MRI =models.CharField(max_length=1500, null=True)
    # 其他
    sup_other =models.CharField(max_length=1500, null=True)
    # LH  miu/ml
    LHmax = models.CharField(max_length=32, null=True)
    # FSH  miu/ml
    FSHmax = models.CharField(max_length=32, null=True)


    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "性发育异常"
        verbose_name_plural = "性发育异常"

class Short(models.Model):
    sry = (
        ('1', "阳性"),
        ('2', "阴性"),
    )
    mutkind = (
        ('10010001', "错义突变"),
        ('10010002', "无义突变"),
        ('10010003', "截断突变"),
        ('10010004', "移码突变"),
        ('10010005', "剪接位点突变"),
        ('10010006', "同义突变"),
        ('10010007', "非编码区突变"),
        ('10010008', "剪接位点附近的突变"),
    )
    sourmut = (
        ('0', "新生"),
        ('1', "父"),
        ('2', "母"),
    )

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 家族史
    fam_his = models.TextField(null=True)
    # 运动发育落后 1=无；2=有，选择2出现文本框，自行输入
    mot_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 语言发育落后 1=无；2=有，选择2出现文本框，自行输入
    lan_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 智力发育落后 1=无；2=有，选择2出现文本框，自行输入
    int_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 听力异常 1=无；2=有，选择2出现文本框，自行输入。
    abn_hear = models.CharField(max_length=256, default="1", null=True)
    # 反复感染史 1=无；2=有，选择2出现文本框，自行输入。
    rec_inf_his = models.CharField(max_length=256, default="1", null=True)
    # 抽搐史 1=无，2=有
    con_his = models.CharField(max_length=256, default="1", null=True)
    # 其他
    past_other = models.CharField(max_length=256, default="无", null=True)
    # 病史
    med_his = models.CharField(max_length=512, default="无", null=True)
    # 体格检查
    phy_exa = models.CharField(max_length=512, default="无", null=True)
    # 实验室检查
    lab_exa = models.CharField(max_length=512, default="无", null=True)
    # 心电图
    electr = models.CharField(max_length=256, default="无", null=True)
    # 性腺B超
    gon_B_ult = models.CharField(max_length=1024, default="无", null=True)
    # 诊疗方案
    dia_trea_plan = models.CharField(max_length=512, default="无", null=True)
    # 生物样本库
    bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 主要诊断
    main_dia = models.CharField(max_length=256, default="无", null=True)
    # 次要诊断
    sec_dia = models.CharField(max_length=256, default="无", null=True)
    # 随访
    follow_up = models.TextField(null=True)
    # 图像
    B_ult_image = models.TextField(null=True)
    # 染色体核型
    spe_kar = models.CharField(max_length=32, null=True)
    # SRY基因：阳性=1，阴性=2，
    SRY = models.CharField(max_length=4, choices=sry, default="1")
    # 基因突变名称
    gen_mut_name = models.TextField(null=True)
    # 变异类型
    mut_kind = models.CharField(max_length=8, choices=mutkind, default="1")
    # 变异来源 新生=0；父=1；母=2
    sour_mut = models.CharField(max_length=4, choices=sourmut, default="1")
    # 核酸变异
    base_mut =models.CharField(max_length=32, null=True)
    # 氨基酸变异
    ami_aci_mut = models.TextField(null=True)
    # 父亲生物样本库
    f_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 母亲生物样本库
    m_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # ACTH刺激实验
    acth_jf = models.CharField(max_length=512, default="无", null=True)
    # 其他图片名称
    other_ima_name = models.CharField(max_length=512, default="无", null=True)


    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "遗传性骨病"
        verbose_name_plural = "遗传性骨病"

class SGA(models.Model):
    sry = (
        ('1', "阳性"),
        ('2', "阴性"),
    )
    mutkind = (
        ('10010001', "错义突变"),
        ('10010002', "无义突变"),
        ('10010003', "截断突变"),
        ('10010004', "移码突变"),
        ('10010005', "剪接位点突变"),
        ('10010006', "同义突变"),
        ('10010007', "非编码区突变"),
        ('10010008', "剪接位点附近的突变"),
    )
    sourmut = (
        ('0', "新生"),
        ('1', "父"),
        ('2', "母"),
    )

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 母亲孕期疾病
    mot_pre_dis = models.CharField(max_length=128, null=True, default=None)
    # 母亲孕期疾病其他描述
    mot_pre_dis_ms = models.CharField(max_length=128, null=True, default=None)
    # 是否多胎
    is_mul_bir = models.CharField(max_length=32, null=True, default=None)
    # 多胎描述
    mul_bir_ms = models.CharField(max_length=32, null=True, default=None)
    # 胎次
    parity = models.CharField(max_length=32, unique=True, null=True)
    # 产次
    pronum = models.CharField(max_length=32, unique=True, null=True)
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 家族史
    fam_his = models.TextField(null=True)
    # 运动发育落后 1=无；2=有，选择2出现文本框，自行输入
    mot_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 语言发育落后 1=无；2=有，选择2出现文本框，自行输入
    lan_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 智力发育落后 1=无；2=有，选择2出现文本框，自行输入
    int_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 听力异常 1=无；2=有，选择2出现文本框，自行输入。
    abn_hear = models.CharField(max_length=256, default="1", null=True)
    # 反复感染史 1=无；2=有，选择2出现文本框，自行输入。
    rec_inf_his = models.CharField(max_length=256, default="1", null=True)
    # 抽搐史 1=无，2=有
    con_his = models.CharField(max_length=256, default="1", null=True)
    # 其他
    past_other = models.CharField(max_length=256, default="无", null=True)
    # 病史
    med_his = models.CharField(max_length=512, default="无", null=True)
    # 体格检查
    phy_exa = models.CharField(max_length=512, default="无", null=True)
    # 实验室检查
    lab_exa = models.CharField(max_length=512, default="无", null=True)
    # 心电图
    electr = models.CharField(max_length=256, default="无", null=True)
    # 性腺B超
    gon_B_ult = models.CharField(max_length=1024, default="无", null=True)
    # 诊疗方案
    dia_trea_plan = models.CharField(max_length=512, default="无", null=True)
    # 生物样本库
    bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 主要诊断
    main_dia = models.CharField(max_length=256, default="无", null=True)
    # 次要诊断
    sec_dia = models.CharField(max_length=256, default="无", null=True)
    # 随访
    follow_up = models.TextField(null=True)
    # 图像
    B_ult_image = models.TextField(null=True)
    # 染色体核型
    spe_kar = models.CharField(max_length=32, null=True)
    # SRY基因：阳性=1，阴性=2，
    SRY = models.CharField(max_length=4, choices=sry, default="1")
    # 基因突变名称
    gen_mut_name = models.TextField(null=True)
    # 变异类型
    mut_kind = models.CharField(max_length=8, choices=mutkind, default="1")
    # 变异来源 新生=0；父=1；母=2
    sour_mut = models.CharField(max_length=4, choices=sourmut, default="1")
    # 核酸变异
    base_mut =models.CharField(max_length=32, null=True)
    # 氨基酸变异
    ami_aci_mut = models.TextField(null=True)
    # 父亲生物样本库
    f_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 母亲生物样本库
    m_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)


    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "小于胎周龄"
        verbose_name_plural = "小于胎周龄"

class Sexprecocity(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    sry = (
        ('1', "阳性"),
        ('2', "阴性"),
    )
    mutkind = (
        ('10010001', "错义突变"),
        ('10010002', "无义突变"),
        ('10010003', "截断突变"),
        ('10010004', "移码突变"),
        ('10010005', "剪接位点突变"),
        ('10010006', "同义突变"),
        ('10010007', "非编码区突变"),
        ('10010008', "剪接位点附近的突变"),
    )
    sourmut = (
        ('0', "新生"),
        ('1', "父"),
        ('2', "母"),
    )
    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 家族史
    fam_his = models.TextField(null=True)
    # 初次就诊时间
    first_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # # 住院号
    # inp_num = models.CharField(max_length=32, null=True)
    # 发病年龄
    age_ons = models.CharField(max_length=8, null=True)
    # 主诉
    chi_com = models.CharField(max_length=256, default="无", null=True)
    # 生长加速
    acc_growth = models.CharField(max_length=64, null=True)
    # 月经初潮情况
    menarche = models.CharField(max_length=64, null=True)
    # 体格检查
    phy_exa = models.CharField(max_length=512, default="无", null=True)
    # 实验室检查
    lab_exa = models.CharField(max_length=512, default="无", null=True)
    # 心电图
    electr = models.CharField(max_length=512, default="无", null=True)
    # 性腺B超
    gon_B_ult = models.CharField(max_length=1500, default="无", null=True)
    # 诊疗方案
    dia_trea_plan = models.CharField(max_length=512, default="无", null=True)
    # 生物样本库
    bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 主要诊断
    main_dia = models.CharField(max_length=256, default="无", null=True)
    # 次要诊断
    sec_dia = models.CharField(max_length=256, default="无", null=True)
    # 随访
    follow_up = models.TextField(null=True)
    # 图像
    B_ult_image = models.TextField(null=True)

    # 染色体核型
    spe_kar = models.CharField(max_length=32, null=True)
    # SRY基因：阳性=1，阴性=2，
    SRY = models.CharField(max_length=4, choices=sry, default="1")
    # 基因突变名称
    gen_mut_name = models.TextField(null=True)
    # 变异类型
    mut_kind = models.CharField(max_length=8, choices=mutkind, default="1")
    # 变异来源 新生=0；父=1；母=2
    sour_mut = models.CharField(max_length=4, choices=sourmut, default="1")
    # 核酸变异
    base_mut =models.CharField(max_length=32, null=True)
    # 氨基酸变异
    ami_aci_mut = models.TextField(null=True)
    # LH  miu/ml
    LHmax = models.CharField(max_length=32, null=True, default=None)
    # FSH  miu/ml
    FSHmax = models.CharField(max_length=32, null=True, default=None)
    # LH峰值/FSH峰值
    LFmax = models.CharField(max_length=32, null=True, default=None)


    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "中枢性性早熟"
        verbose_name_plural = "中枢性性早熟"

class PatFoll(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 是否行为发育评估
    beh_dev_ass = models.CharField(max_length=128, null=True, default=None)
    # Peabody运动发育评估
    ped_mot_dev_ass = models.CharField(max_length=128, null=True, default=None)
    # 粗大运动
    gro_mot = models.CharField(max_length=64, null=True, default=None)
    # 个人社会
    ind_soc = models.CharField(max_length=64, null=True, default=None)
    # 听力语言
    lis_lan = models.CharField(max_length=64, null=True, default=None)
    # 手眼协调
    han_eye_coo = models.CharField(max_length=64, null=True, default=None)
    # 视觉表现
    vis_rep = models.CharField(max_length=64, null=True, default=None)
    # 实际推理
    pra_rea = models.CharField(max_length=64, null=True, default=None)
    # 韦氏智力量表（分数表示）
    wec_sca = models.CharField(max_length=64, null=True, default=None)
    # 随访日期
    foll_time = models.DateTimeField(default=datetime.datetime.now)
    # 上传日期
    up_time = models.DateTimeField(default=datetime.datetime.now)
    # 年龄
    age = models.CharField(max_length=64, null=True)
    # Htcm  现身高
    Ht = models.CharField(max_length=8, null=True)
    # Wtkg  现体重
    Wt = models.CharField(max_length=8, null=True)
    # 生殖器分期
    gen_stag = models.CharField(max_length=8, null=True)
    # 阴毛分期
    pub_stag = models.CharField(max_length=8, null=True)
    # IGF-1（ng/ml）
    IGF1 = models.CharField(max_length=32, null=True)
    # IGFBP-3（ug/ml）
    IGFBP3 = models.CharField(max_length=32, null=True)
    # 甲功
    Jiagong = models.CharField(max_length=128, null=True)
    # 空腹血糖
    fas_blood_glu = models.CharField(max_length=128, null=True)
    # 空腹胰岛素
    fas_insulin = models.CharField(max_length=32, null=True)
    # 肝肾脂电解质
    liv_kid_lip = models.CharField(max_length=256, null=True)
    # 糖化血红蛋白
    gly_hem = models.CharField(max_length=32, null=True)
    # 性腺B超
    gon_B_ult = models.CharField(max_length=512, default="无", null=True)
    # 诊疗方案
    dia_trea_plan = models.CharField(max_length=512, default="无", null=True)
    # LH
    LH = models.CharField(max_length=32, null=True)
    # FSH
    FSH = models.CharField(max_length=32, null=True)
    # E2
    E2 = models.CharField(max_length=32, null=True)
    # T
    T = models.CharField(max_length=32, null=True)
    # DHT
    DHT = models.CharField(max_length=32, null=True)
    # 游离睾酮
    yltg = models.CharField(max_length=32, null=True)
    # SHBG
    SHBG = models.CharField(max_length=32, null=True)
    # 睾丸大小
    tes_size = models.CharField(max_length=512, default="无", null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 其他
    other = models.CharField(max_length=512, default="无", null=True)
    # 图像
    image = models.TextField(null=True)
    # R系列骨龄（骨龄片）
    rboneAge = models.CharField(max_length=32, default="无", null=True)
    # C系列骨龄（全身骨显像）
    cboneAge = models.CharField(max_length=32, default="无", null=True)
    # 体脂( %)
    bodyFat = models.CharField(max_length=32, default="无", null=True)
    # bmi值
    bmi = models.CharField(max_length=32, default="无", null=True)
    # 腰围(cm)
    waistline = models.CharField(max_length=32, default="无", null=True)
    # 臀围(cm)
    hips = models.CharField(max_length=32, default="无", null=True)
    # 腰臀比
    waistToHipRatio = models.CharField(max_length=32, default="无", null=True)
    # 实验室检查其它字段
    lab_exa_other = models.TextField(null=True)
    # mas实验室检查
    lab_exa_mas = models.TextField(null=True)
    # 疾病
    disease = models.CharField(max_length=512, default="无", null=True)
    # 地舒单抗
    dsdk = models.CharField(max_length=512, default="无", null=True)
    # 唑来膦酸
    clls = models.CharField(max_length=512, default="无", null=True)
    # 其他用量
    qtyl = models.CharField(max_length=512, default="无", null=True)
    # 其他检查
    other_exam = models.CharField(max_length=512, default="无", null=True)
    # EOS
    eos = models.CharField(max_length=512, default="无", null=True)
    # 骨密度
    bon_min_den = models.CharField(max_length=512, default="无", null=True)
    # 是否达终身高
    is_finalhei = models.CharField(max_length=512, default="无", null=True)
    # 诊疗方案其他字段
    otherMedicine = models.CharField(max_length=512, default="无", null=True)
    # 其他图片名称
    other_ima_name = models.CharField(max_length=512, default="无", null=True)


    # def __str__(self):
    #     return self.foll_time

    class Meta:
        ordering = ["-foll_time"]
        verbose_name = "家族性矮小随访"
        verbose_name_plural = "家族性矮小随访"

class Mas(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 家族史
    fam_his = models.TextField(null=True)
    # 检查日期
    check_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 一般情况
    gen_sit = models.CharField(max_length=512, default="无", null=True)
    # 女孩发育分期
    girl_sta_dev = models.CharField(max_length=512, default="无", null=True)
    # 男孩发育分期
    boy_sta_dev = models.CharField(max_length=512, default="无", null=True)
    # 甲状腺肿大
    goiter = models.CharField(max_length=512, default="无", null=True)
    # 皮肤检查（多选）
    skin_exam = models.CharField(max_length=512, default="无", null=True)
    # 骨骼检查（多选）
    ske_sur = models.TextField(null=True)
    # 子宫卵巢B超检查日期
    ult_exam_ova_date = models.DateTimeField(default=datetime.datetime.now)
    # 子宫超声情况
    ute_ult_con = models.CharField(max_length=512, default="无", null=True)
    # 子宫情况具体描述
    spe_des_ute_con = models.CharField(max_length=512, default="无", null=True)
    # 卵巢超声情况
    ova_ult_con = models.CharField(max_length=1024, default="无", null=True)
    # 卵巢、囊肿具体描述
    spe_des_ova_cys = models.CharField(max_length=512, default="无", null=True)
    # 甲状腺B超情况
    thy_ult_con = models.CharField(max_length=512, default="无", null=True)
    # 肾上腺B超情况
    adr_ult_con = models.CharField(max_length=512, default="无", null=True)
    # 肾脏B超情况
    ren_ult_con = models.CharField(max_length=512, default="无", null=True)
    # 所有超声结果上传（图片）
    ult_img_path = models.CharField(max_length=512, default="无", null=True)
    # 病变骨骼X线片检查情况
    X_exa_dis = models.CharField(max_length=512, default="无", null=True)
    # 头颅CT检查情况
    hea_ct_exa = models.CharField(max_length=512, default="无", null=True)
    # 头颅MR检查情况
    hea_mr_exa = models.CharField(max_length=512, default="无", null=True)
    # 全身骨扫描检查情况
    foll_body_scan_exa = models.CharField(max_length=512, default="无", null=True)
    # 常规化验检查日期
    lab_exa = models.DateTimeField(default=datetime.datetime.now)
    # 血常规
    blo_rou = models.CharField(max_length=512, default="无", null=True)
    # 肝功能
    liv_fun = models.CharField(max_length=512, default="无", null=True)
    # 肾功能
    ren_fun = models.CharField(max_length=512, default="无", null=True)
    # 电解质
    electrolyte = models.CharField(max_length=512, default="无", null=True)
    # 血脂
    blood_fat = models.CharField(max_length=512, default="无", null=True)
    # 骨代谢检查
    bone_met_exa = models.CharField(max_length=512, default="无", null=True)
    # 骨代谢检查日期
    bone_met_exa_date = models.DateTimeField(default=datetime.datetime.now)
    # 性激素检查
    sex_hor_exa = models.CharField(max_length=512, default="无", null=True)
    # 性激素检查日期
    sex_hor_exa_date = models.DateTimeField(default=datetime.datetime.now)
    # 甲状腺功能及抗体检查
    thy_fun_ant_exa = models.CharField(max_length=512, default="无", null=True)
    # 甲状腺功能及抗体检查日期
    thy_fun_ant_date = models.DateTimeField(default=datetime.datetime.now)
    # 肾上腺功能检查
    adr_fun_exa = models.CharField(max_length=512, default="无", null=True)
    # 肾上腺功能检查日期
    adr_fun_exa_date = models.DateTimeField(default=datetime.datetime.now)
    # 生长激素分泌功能检查
    phy_exa = models.CharField(max_length=512, default="无", null=True)
    # 生长激素分泌功能检查时间
    gro_hor_exa = models.DateTimeField(default=datetime.datetime.now)
    # 糖代谢情况
    glu_met = models.CharField(max_length=512, default="无", null=True)
    # 糖代谢情况时间
    glu_met_date = models.DateTimeField(default=datetime.datetime.now)
    # 检查结果上传（图片）
    glu_img_path = models.CharField(max_length=512, default="", null=True)
    # 心电图检查
    ecg_exa = models.CharField(max_length=512, default="无", null=True)
    # X线骨龄检查
    x_bone_exa = models.CharField(max_length=512, default="无", null=True)
    # 骨龄测定图片上传
    bone_img_path = models.CharField(max_length=512, default="无", null=True)
    # 垂体MR检查
    pit_exa = models.CharField(max_length=512, default="无", null=True)
    # 检查结果上传（图片）
    pit_img_path = models.CharField(max_length=512, default="无", null=True)
    # GNAS基因测定是否检查
    GNAS = models.CharField(max_length=512, default="无", null=True)
    # 标本采样类型或部位
    GNAS_sam_loc = models.CharField(max_length=512, default="无", null=True)
    # 病理活检是否检查
    pat_exa = models.CharField(max_length=512, default="无", null=True)
    # 标本采样类型或部位
    pat_sam_loc = models.CharField(max_length=512, default="无", null=True)
    # 是否行GnRH激发试验
    GnRH = models.CharField(max_length=512, default="无", null=True)
    # 评估指标
    GnRH_eva = models.CharField(max_length=512, default="无", null=True)
    # 是否行小剂量地塞米松抑制试验
    low_dose = models.CharField(max_length=512, default="无", null=True)
    # 评估指标
    low_dose_eva = models.CharField(max_length=512, default="无", null=True)
    # 是否行生长激素-葡萄糖抑制试验
    gro_glu = models.CharField(max_length=512, default="无", null=True)
    # 评估指标
    gro_glu_eva = models.CharField(max_length=512, default="无", null=True)
    # 是否存在性早熟
    sex_pre = models.CharField(max_length=512, default="无", null=True)
    # 是否存在甲状腺功能亢进
    hyper = models.CharField(max_length=512, default="无", null=True)
    # 是否存在生长激素分泌过多
    is_gro_hor = models.CharField(max_length=512, default="无", null=True)
    # 是否存在皮质醇增多症
    is_inc_cor = models.CharField(max_length=512, default="无", null=True)
    # 填表日期
    fill_date = models.DateTimeField(default=datetime.datetime.now)
    # 填表医生
    fill_doctor = models.CharField(max_length=512, default="无", null=True)
    # 填表医生手机号码
    fill_doctor_phone = models.CharField(max_length=512, default="无", null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 首发表现
    ini_per = models.CharField(max_length=512, default="无", null=True)
    # 首发表现二
    ini_per_ci = models.CharField(max_length=512, default="无", null=True)
    # 首发表现时间
    ini_per_date = models.DateTimeField(default=datetime.datetime.now)
    # 遗传学检测方法
    gen_tes_met = models.CharField(max_length=128, default="无", null=True)
    # 检测结果
    det_res = models.CharField(max_length=64, default="无", null=True)
    # 检测版本
    det_ver = models.CharField(max_length=64, default="无", null=True)
    # 突变位点
    mut_sit = models.CharField(max_length=128, default="无", null=True)





    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "MAS"
        verbose_name_plural = "MAS"

class MasFoll(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # mas表id
    mas = models.ForeignKey(Mas, on_delete=models.CASCADE, default="1")
    # 是否达终身高
    is_finalhei = models.CharField(max_length=512, default="无", null=True)
    # 有无对外周性性早熟进行治疗
    is_per_pre = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    per_pre_sf = models.TextField(null=True)
    # 有无对甲状腺功能亢进进行治疗
    is_hyper = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    hyper_sf = models.TextField(null=True)
    # 监测指标
    hyper_jc = models.CharField(max_length=512, default="无", null=True)
    # 有无对生长激素分泌过多进行治疗
    is_gro_hor = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    gro_hor_sf = models.TextField(null=True)
    # 监测指标
    gro_hor_jc = models.CharField(max_length=512, default="无", null=True)
    # 有无对高泌乳素血症进行治疗
    is_tre_hpy = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    tre_hpy_sf = models.TextField(null=True)
    # 监测指标
    tre_hpy_jc = models.CharField(max_length=512, default="无", null=True)
    # 有无对皮质醇增多症进行治疗
    is_inc_cor = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    inc_cor_sf = models.TextField(null=True)
    # 监测指标
    inc_cor_jc = models.CharField(max_length=512, default="无", null=True)
    # 是否行颅内手术
    is_int_sur = models.CharField(max_length=512, default="无", null=True)
    # 是否行双侧肾上腺切除术
    is_bil_adr = models.CharField(max_length=512, default="无", null=True)
    # 是否对骨痛进行治疗
    is_bon_pai = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    bon_pai_sf = models.TextField(null=True)
    # 监测指标
    bon_pai_jc = models.CharField(max_length=512, default="无", null=True)
    # 是否对低磷酸盐血症进行治疗（补充钙磷、骨化三醇治疗）
    hypop = models.CharField(max_length=512, default="无", null=True)
    # 随访情况
    hypop_sf = models.TextField(null=True)
    # 监测指标
    hypop_jc = models.CharField(max_length=512, default="无", null=True)
    # 是否行骨骼外科手术
    is_ske_sur = models.CharField(max_length=512, default="无", null=True)
    # 是否对牛奶咖啡斑进行激光治疗
    is_cafe_spot = models.CharField(max_length=512, default="无", null=True)
    # 是否进形心理疏导
    is_psy_cou = models.CharField(max_length=512, default="无", null=True)
    # 生存状态
    sur_sta = models.CharField(max_length=512, default="无", null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")




    def __str__(self):
        return self.is_finalhei

    class Meta:
        ordering = ["-is_finalhei"]
        verbose_name = "MAS随访"
        verbose_name_plural = "MAS随访"

class JzxShort(models.Model):
    sry = (
        ('1', "阳性"),
        ('2', "阴性"),
    )
    mutkind = (
        ('10010001', "错义突变"),
        ('10010002', "无义突变"),
        ('10010003', "截断突变"),
        ('10010004', "移码突变"),
        ('10010005', "剪接位点突变"),
        ('10010006', "同义突变"),
        ('10010007', "非编码区突变"),
        ('10010008', "剪接位点附近的突变"),
    )
    sourmut = (
        ('0', "新生"),
        ('1', "父"),
        ('2', "母"),
    )

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # 患者编号
    user_num = models.CharField(max_length=32, null=True)
    # 家族史
    fam_his = models.TextField(null=True)
    # 运动发育落后 1=无；2=有，选择2出现文本框，自行输入
    mot_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 语言发育落后 1=无；2=有，选择2出现文本框，自行输入
    lan_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 智力发育落后 1=无；2=有，选择2出现文本框，自行输入
    int_dev_back = models.CharField(max_length=256, default="1", null=True)
    # 听力异常 1=无；2=有，选择2出现文本框，自行输入。
    abn_hear = models.CharField(max_length=256, default="1", null=True)
    # 反复感染史 1=无；2=有，选择2出现文本框，自行输入。
    rec_inf_his = models.CharField(max_length=256, default="1", null=True)
    # 抽搐史 1=无，2=有
    con_his = models.CharField(max_length=256, default="1", null=True)
    # 其他
    past_other = models.CharField(max_length=256, default="无", null=True)
    # 病史
    med_his = models.CharField(max_length=512, default="无", null=True)
    # 体格检查
    phy_exa = models.CharField(max_length=512, default="无", null=True)
    # 实验室检查
    lab_exa = models.CharField(max_length=512, default="无", null=True)
    # 心电图
    electr = models.CharField(max_length=256, default="无", null=True)
    # 性腺B超
    gon_B_ult = models.CharField(max_length=1024, default="无", null=True)
    # 诊疗方案
    dia_trea_plan = models.CharField(max_length=512, default="无", null=True)
    # 生物样本库
    bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 主要诊断
    main_dia = models.CharField(max_length=256, default="无", null=True)
    # 次要诊断
    sec_dia = models.CharField(max_length=256, default="无", null=True)
    # 随访
    follow_up = models.TextField(null=True)
    # 图像
    B_ult_image = models.TextField(null=True)
    # 染色体核型
    spe_kar = models.CharField(max_length=32, null=True)
    # SRY基因：阳性=1，阴性=2，
    SRY = models.CharField(max_length=4, choices=sry, default="1")
    # 基因突变名称
    gen_mut_name = models.TextField(null=True)
    # 变异类型
    mut_kind = models.CharField(max_length=8, choices=mutkind, default="1")
    # 变异来源 新生=0；父=1；母=2
    sour_mut = models.CharField(max_length=4, choices=sourmut, default="1")
    # 核酸变异
    base_mut =models.CharField(max_length=32, null=True)
    # 氨基酸变异
    ami_aci_mut = models.TextField(null=True)
    # 父亲生物样本库
    f_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # 母亲生物样本库
    m_bio_sam_bank = models.CharField(max_length=512, default="无", null=True)
    # ACTH刺激实验
    acth_jf = models.CharField(max_length=512, default="无", null=True)
    # 其他图片名称
    other_ima_name = models.CharField(max_length=512, default="无", null=True)


    def __str__(self):
        return self.user_num

    class Meta:
        ordering = ["-user_num"]
        verbose_name = "家族性矮小"
        verbose_name_plural = "家族性矮小"

class SzfyEltm(models.Model):

    # 病例主表id
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, default="1")
    # Tanner分期
    tanner = models.CharField(max_length=8, default="", null=True)
    # 发生时间
    star_time = models.DateTimeField(auto_now_add=True)
    # 结束时间
    end_time = models.DateTimeField(auto_now_add=True)
    # 是否为严重不良事件
    is_adv_eve = models.CharField(max_length=8, default="", null=True)
    # 与研究药物的关系(LA-rhGH)
    la_rhGH = models.CharField(max_length=8, default="", null=True)
    # 是否调整剂量
    is_adjust = models.CharField(max_length=8, default="", null=True)
    # 与研究药物的关系(rhGH)
    rhGH = models.CharField(max_length=8, default="", null=True)
    # 不良事件的转归
    outcome = models.CharField(max_length=8, default="", null=True)
    # 药物名称
    med_name = models.CharField(max_length=8, default="", null=True)
    # 单次剂量
    dose = models.CharField(max_length=8, default="", null=True)
    # 用药天数
    days = models.CharField(max_length=8, default="", null=True)
    # 是否停药
    stop_med = models.CharField(max_length=8, default="", null=True)
    # 停药原因
    stop_rea = models.CharField(max_length=128, default="", null=True)
    # 记录日期
    rec_date = models.DateTimeField(auto_now_add=True)
    # 有无既往用药史
    is_has_his = models.CharField(max_length=8, default="", null=True)
    # 用药史
    has_his = models.TextField(null=True)
    # 基因检测方法
    gene_method = models.CharField(max_length=64, default="", null=True)
    # 基因结果
    gene_res = models.CharField(max_length=8, default="", null=True)
    # 基因名称
    gene_name = models.CharField(max_length=64, default="", null=True)
    # 突变位点
    gene_point = models.CharField(max_length=64, default="", null=True)
    # 突变类型
    gene_type = models.CharField(max_length=64, default="", null=True)
    # 遗传模式
    gene_mode = models.CharField(max_length=64, default="", null=True)
    # 染色体核型
    chrom = models.CharField(max_length=128, default="", null=True)
    # 其它异常核型
    chrom_other = models.CharField(max_length=128, default="", null=True)
    # 一般症状
    gen_sym = models.CharField(max_length=128, default="", null=True)
    # 代谢相关症状
    met_sym = models.CharField(max_length=128, default="", null=True)
    # 骨骼和肌肉症状
    bone_sym = models.CharField(max_length=128, default="", null=True)
    # 内分泌症状
    endo_sym = models.CharField(max_length=128, default="", null=True)
    # 其他症状
    other_sym = models.CharField(max_length=128, default="", null=True)
    # 图像
    image = models.TextField(null=True)


    def __str__(self):
        return self.patient

    class Meta:
        ordering = ["-patient"]
        verbose_name = "生长发育(E路童萌)"
        verbose_name_plural = "生长发育(E路童萌)"

# 操作日志表
class OperLog(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    operStep = (
        (10050001, "数据修改"),
    )

    # 操作人id
    oper_per_id = models.CharField(max_length=11)
    # 操作病例id
    oper_case_id = models.CharField(max_length=128)
    # 操作步骤
    oper_step = models.IntegerField(choices=operStep, default="0")
    # 操作时间
    oper_data = models.DateTimeField(auto_now_add=True)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")
    # 是否管理员操作
    is_admin_login = models.CharField(max_length=12, choices=delflg, default="0")
    # 操作表类型
    oper_case_id = models.CharField(max_length=128)


    def __str__(self):
        return self.oper_per_id

    class Meta:
        ordering = ["-oper_data"]
        verbose_name = "操作日志"
        verbose_name_plural = "操作日志"