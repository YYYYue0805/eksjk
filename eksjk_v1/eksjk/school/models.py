from django.db import models
import datetime

# Create your models here.

class Student(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 主责医生
    doctor = models.CharField(max_length=8, null=True)
    # 手机号
    phone = models.CharField(max_length=16, null=True)
    # 填表人和孩子的关系
    hhzgx = models.CharField(max_length=32, null=True)
    # 编号
    num = models.CharField(max_length=32, null=True)
    # 班级
    sclass = models.CharField(max_length=18, null=True)
    # 姓名
    name = models.CharField(max_length=12, null=True, default="")
    # 性别
    sex = models.CharField(max_length=4, default="1", null=True)
    # 出生日期
    birth_time = models.DateTimeField(blank=True, null=True)
    # 当前身高
    height = models.CharField(max_length=32, null=True)
    # 当前体重
    weight = models.CharField(max_length=32, null=True)
    # 母亲受教育程度
    mqjycd = models.CharField(max_length=32, null=True)
    # 父亲受教育程度
    fqjycd = models.CharField(max_length=32, null=True)
    # 家庭年收入
    jtnsr = models.CharField(max_length=32, null=True)
    # 孩子的主要照护人
    zyjhr = models.CharField(max_length=32, null=True)
    # 主要照护人受教育程度
    zhrjycd = models.CharField(max_length=32, null=True)
    # 是否有兄弟姐妹
    isxdjm = models.CharField(max_length=32, null=True)
    # 医生诊断为妊娠期糖尿病
    yszdrstlb = models.CharField(max_length=32, null=True)
    # 医生诊断为妊娠期高血压
    yszdrsgxy = models.CharField(max_length=32, null=True)
    # 精神压力大或者情绪问题且需要专业人员帮助
    yldisbz = models.CharField(max_length=32, null=True)
    # 医生诊断为营养不良
    yszdyybl = models.CharField(max_length=32, null=True)
    # 分娩方式
    fmfs = models.CharField(max_length=32, null=True)
    # 出生体重
    bweight = models.CharField(max_length=32, null=True)
    # 出生孕周
    bweek = models.CharField(max_length=32, null=True)
    # 出生时是否发生窒息或抢救
    csiszxqj = models.CharField(max_length=32, null=True)
    # 出生后喂养方式
    cswyfs = models.CharField(max_length=32, null=True)
    # 断母乳时间
    dmrsj = models.CharField(max_length=32, null=True)
    # 添加辅食时间
    jfssj = models.CharField(max_length=32, null=True)
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
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-c_time"]
        verbose_name = "用户基本信息"
        verbose_name_plural = "用户基本信息"


class Cchkn(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 能体谅到别人的感受
    ntlbrgs = models.CharField(max_length=32, null=True)
    # 不安定、过分活跃、不能长久安静
    bad = models.CharField(max_length=32, null=True)
    # 经常抱怨头痛、肚子痛或身体不舒服
    jcdzt = models.CharField(max_length=32, null=True)
    # 很乐意与别的小孩分享东西，比如糖果、玩具、铅笔等等
    lyfx = models.CharField(max_length=32, null=True)
    # 经常发脾气或大吵大闹
    jcfpq = models.CharField(max_length=32, null=True)
    # 比较孤独，喜欢自己一个人玩
    bjgd = models.CharField(max_length=32, null=True)
    # 一般来说，比较顺从，通常是大人要求做的都肯做
    bjsc = models.CharField(max_length=32, null=True)
    # 有很多担忧，经常表现出忧虑
    hdyy = models.CharField(max_length=32, null=True)
    # 如果有人受伤，不舒服或是生病，都很乐意提供帮助
    lybz = models.CharField(max_length=32, null=True)
    # 经常的坐立不安或躁动
    jczlba = models.CharField(max_length=32, null=True)
    # 有一个或一个以上的好朋友
    yhpy = models.CharField(max_length=32, null=True)
    # 经常与别的小孩吵架或欺负其他小孩子
    cjqfbr = models.CharField(max_length=32, null=True)
    # 经常不高兴、情绪低落或哭泣
    jcbgx = models.CharField(max_length=32, null=True)
    # 一般来说，受别的小孩所喜欢
    sxpyxh = models.CharField(max_length=32, null=True)
    # 容易分心，注意力不集中
    ryfx = models.CharField(max_length=32, null=True)
    # 在新环境下，会紧张或粘住大人，容易失去信心
    xhjjz = models.CharField(max_length=32, null=True)
    # 爱对年纪小的儿童和善
    dljxyh = models.CharField(max_length=32, null=True)
    # 经常撒谎或欺骗
    jcshqp = models.CharField(max_length=32, null=True)
    # 受别的小孩捉弄或欺负
    sbrzn = models.CharField(max_length=32, null=True)
    # 经常自愿的帮助别人
    zybzbr = models.CharField(max_length=32, null=True)
    # 做事前会想清楚
    zsqxqc = models.CharField(max_length=32, null=True)
    # 会从事家里、学校或其他地方偷东西
    htdx = models.CharField(max_length=32, null=True)
    # 跟大人相处比跟小孩子相处融洽
    hdrrq = models.CharField(max_length=32, null=True)
    # 对很多事情容易感到害怕，容易受惊吓
    ryjx = models.CharField(max_length=32, null=True)
    # 做事情能做到底，注意力持久
    zylcj = models.CharField(max_length=32, null=True)   
    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "长处和困难问卷"
        verbose_name_plural = "长处和困难问卷"

class Cbq(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 似乎总是匆匆忙忙地从一个地方到另一个地方
    zscm = models.CharField(max_length=32, null=True)
    # 当被阻止做想做的事时，会变得非常失落
    bzzsl = models.CharField(max_length=32, null=True)
    # 在书上画图或涂色时表现得非常专注
    htzz = models.CharField(max_length=32, null=True)
    # 喜欢滑高的滑梯或其它冒险性活动
    xhmxhd = models.CharField(max_length=32, null=True)
    # 会因为很小的切伤或擦伤而非常不安
    xcsba = models.CharField(max_length=32, null=True)
    # 能为旅行或外出准备他
    nwczb = models.CharField(max_length=32, null=True)
    # 经常贸然进入新情境
    mrjrxdf = models.CharField(max_length=32, null=True)
    # 如果家庭计划没有兑现（比如，外出旅行没有按计划实施
    jtjhmdx = models.CharField(max_length=32, null=True)
    # 喜欢别人对他唱歌
    xhdtrcg = models.CharField(max_length=32, null=True)
    # 好像对任何人都不拘束
    drhrbjs = models.CharField(max_length=32, null=True)
    # 害怕夜贼或者“大灰狼”
    hpyz = models.CharField(max_length=32, null=True)
    # 当父母穿了新衣服时，能注意到
    nzyfmxyf = models.CharField(max_length=32, null=True)
    # 相对于活跃性游戏，更喜欢安静的活动
    gxhaj = models.CharField(max_length=32, null=True)
    # 对某事生气时，往往要持续十分钟或更长时间
    sqgcsj = models.CharField(max_length=32, null=True)
    # 当搭建或者拼凑某些东西时，能够非常投入且坚持很长时间
    trjccsj = models.CharField(max_length=32, null=True)
    # 在荡秋千时喜欢又高又快
    dqqgk = models.CharField(max_length=32, null=True)
    # 在不能完成某些任务时似乎很沮丧
    bwcjs = models.CharField(max_length=32, null=True)
    # 善于按照要求行动
    syayqxd = models.CharField(max_length=32, null=True)
    # 需要花很长的时间适应新的环境
    hcsjsyxhj = models.CharField(max_length=32, null=True)
    # 在感冒时很少抱怨
    gmssby = models.CharField(max_length=32, null=True)
    # 喜欢歌曲，比如童谣
    xhgq = models.CharField(max_length=32, null=True)
    # 即使在认识了很长时间的人面前，有时也会害羞
    hhx = models.CharField(max_length=32, null=True)
    # 在烦躁时，很容易被安抚下来
    ryaf = models.CharField(max_length=32, null=True)
    # 能很快注意到客厅里的新东西
    zyktxsw = models.CharField(max_length=32, null=True)
    # 即使在晚上，也精力充沛
    wsjlcp = models.CharField(max_length=32, null=True)
    # 不害怕黑夜
    bhphy = models.CharField(max_length=32, null=True)
    # 有时会专注于图画书很长时间
    zztscsj = models.CharField(max_length=32, null=True)
    # 不喜欢粗野的游戏
    bxhcyyx = models.CharField(max_length=32, null=True)
    # 对轻微切伤或擦伤并不十分心烦
    qwcsbxf = models.CharField(max_length=32, null=True)
    # 到听说有危险的地方时会小心翼翼
    zywx = models.CharField(max_length=32, null=True)
    # 会缓慢而不匆忙地决定接下来要做的事
    hmzs = models.CharField(max_length=32, null=True)
    # 当不能找到他/她想玩的东西时会生气
    bnzdhsc = models.CharField(max_length=32, null=True)
    # 喜欢柔和有节拍的活动，比如摇摆
    xhrhhd = models.CharField(max_length=32, null=True)
    # 有时会对新认识的人害羞地转过脸去
    xrshx = models.CharField(max_length=32, null=True)
    # 当喜欢的亲戚或朋友在来访后准备离开时，变得烦躁
    pyzhfz = models.CharField(max_length=32, null=True)
    # 会对父母外表的变化做评价
    dfmwbpj = models.CharField(max_length=32, null=True)
    
    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "儿童气质问卷CBQ"
        verbose_name_plural = "儿童气质问卷CBQ"

class Mqzyfs(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 只要孩子不高兴，犯了错误，也不批评
    hzbgxbpp = models.CharField(max_length=32, null=True)
    # 孩子要什么就给什么
    hzysmgsm = models.CharField(max_length=32, null=True)
    # 孩子是否服从自己无所谓
    hzsffcwsw = models.CharField(max_length=32, null=True)
    # 只要孩子高兴，可以不惜一切
    hzgxbxyq = models.CharField(max_length=32, null=True)
    # 孩子不服从家长时打骂孩子
    hzbfcdm = models.CharField(max_length=32, null=True)
    # 对孩子犯了错误并不在乎
    hzfcbzh = models.CharField(max_length=32, null=True)
    # 对孩子一点点异常过分着急
    hzyddycgfzj = models.CharField(max_length=32, null=True)
    # 鼓励孩子做他会做的事
    glhzzs = models.CharField(max_length=32, null=True)
    # 孩子做不好的事情替他做
    thzzs = models.CharField(max_length=32, null=True)
    # 根据孩子本人的兴趣培养他的特长
    gjhzxqpytc = models.CharField(max_length=32, null=True)
    # 对孩子的哭闹，有时查问清楚，有时拒绝
    dhzknysdc = models.CharField(max_length=32, null=True)
    # 在和孩子谈话时允许孩子插话提问
    yxchtw = models.CharField(max_length=32, null=True)
    # 要求孩子做什么事都必须报告家长
    bxbgjz = models.CharField(max_length=32, null=True)
    # 当孩子做错时问明原因再批评
    wmyyzpp = models.CharField(max_length=32, null=True)
    # 孩子缠着问这问那不耐烦
    bnfwzwn = models.CharField(max_length=32, null=True)
    # 不向孩子做任何承诺
    bxhzzcn = models.CharField(max_length=32, null=True)
    # 吩咐孩子做事时让孩子明白为什么或怎么做
    rhzmbwsm = models.CharField(max_length=32, null=True)
    # 对孩子的学习、生活有时关心，有时不关心
    dhzshysgx = models.CharField(max_length=32, null=True)
    # 不注意孩子在做什么或怎么做
    bzyhzzsm = models.CharField(max_length=32, null=True)
    # 自己忙的时候不理睬孩子的提问
    zjmblchz = models.CharField(max_length=32, null=True)
    # 孩子想怎么样就怎么样
    hzxzmyjzy = models.CharField(max_length=32, null=True)
    # 不切实际地表扬孩子
    bqsjbyhz = models.CharField(max_length=32, null=True)
    # 不了解孩子不和父母在一起时具体做什么
    bljhzhfuzsm = models.CharField(max_length=32, null=True)
    # 以适当的方式表扬或奖励孩子
    ysdfsbyhz = models.CharField(max_length=32, null=True)
    # 对孩子无理要求，有时满足，有时拒绝
    wlyqysmz = models.CharField(max_length=32, null=True)
    # 对孩子没有惩罚或奖励
    dhzmyjc = models.CharField(max_length=32, null=True)
    # 在孩子学习或做其他事遇到困难时帮助他解决
    bhzjjkn = models.CharField(max_length=32, null=True)
    # 看着孩子做事情并随时指点
    khzzs = models.CharField(max_length=32, null=True)
    # 对孩子提出的问题予以认真解答
    dhzrzhd = models.CharField(max_length=32, null=True)
    # 同样一件事情，有时允许，有时拒绝
    tyjsysyx = models.CharField(max_length=32, null=True)
    # 对孩子不讲是非
    dhzbjsf = models.CharField(max_length=32, null=True)
    # 孩子和谁在一起经过家长同意
    hsyqyty = models.CharField(max_length=32, null=True)
    # 同孩子一起消遣、游戏
    thzyqyx = models.CharField(max_length=32, null=True)
    # 要求孩子做什么事必讲明原因或怎么做
    yqhzjyy = models.CharField(max_length=32, null=True)
    # 孩子做了错事，有时批评，有时无所谓
    hzzcyspp = models.CharField(max_length=32, null=True)
    # 不关心孩子的生活小事
    bgxxs = models.CharField(max_length=32, null=True)
    # 通过说理使孩子服从
    slshzsc = models.CharField(max_length=32, null=True)
    # 有时说服孩子，有时强制孩子
    ysqz = models.CharField(max_length=32, null=True)
    # 孩子在家里随便做自己的事情，家长没有具体要求
    zjmjtyq = models.CharField(max_length=32, null=True)
    # 培养孩子哪方面特长由家长决定
    pyhzjzjd = models.CharField(max_length=32, null=True)
    
    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "母亲照养方式问卷"
        verbose_name_plural = "母亲照养方式问卷"


class Qzhd(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 与孩子一起阅读、看图画书
    yhzyqks = models.CharField(max_length=32, null=True)
    # 在生活中教孩子数的概念
    jhzsdgl = models.CharField(max_length=32, null=True)
    # 涂涂画画
    tthh = models.CharField(max_length=32, null=True)
    # 跟孩子一起玩开发智力的游戏
    yhzyqyx = models.CharField(max_length=32, null=True)
    # 结合日常生活与孩子一起识字
    yqsz = models.CharField(max_length=32, null=True)
    # 一起听唱歌曲、诗歌、童谣
    yqcg = models.CharField(max_length=32, null=True)
    # 讲故事
    jgs = models.CharField(max_length=32, null=True)
    # 做手工
    zsg = models.CharField(max_length=32, null=True)
    # 做运动
    zyd = models.CharField(max_length=32, null=True)
    # 教孩子生活自理技能，如吃饭、穿衣等
    jhzzl = models.CharField(max_length=32, null=True)
    # 与孩子谈论周围发生的一些事
    yhztlzw = models.CharField(max_length=32, null=True)
    # 与孩子一起认识大自然的动植物
    yhzrsdzr = models.CharField(max_length=32, null=True)
    
    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "亲子活动"
        verbose_name_plural = "亲子活动"

class Pmbl(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 第一次接触电子屏幕的月龄
    dycyn = models.CharField(max_length=32, null=True)
    # 平均每天接触电子屏幕时间
    mtjcsj = models.CharField(max_length=32, null=True)
    # 矩阵填写
    jztx = models.CharField(max_length=512, null=True)
    # 您的孩子观看电视时，您或者其他照样人陪同观看的时间
    ptgksj = models.CharField(max_length=32, null=True)
    # 您的孩子观看电视时，您或者其他照样人与其交流电视内容的时间
    ptgkjlsj = models.CharField(max_length=32, null=True)

    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "屏幕暴露"
        verbose_name_plural = "屏幕暴露"

class Sthd(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 参加的“中强度身体活动”的频率（每周几天）
    cjzqdpl = models.CharField(max_length=32, null=True)
    # 在参加中高强度身体活动的那几天，您孩子通常每天花多少时间来做中高强度身体活动？每次至少持续10分钟以上
    dssjzqd = models.CharField(max_length=32, null=True)
    # 参加的“低强度身体活动”的频率（每周几天）
    cjdqdpl = models.CharField(max_length=32, null=True)
    # 在参加低强度身体活动的那几天，您孩子通常每天花多少时间来做低强度身体活动？每次至少持续10分钟以上
    dssjdqd = models.CharField(max_length=32, null=True)
    # 孩子静坐的频率（每周几天）
    jzpl = models.CharField(max_length=32, null=True)
    # 每天静坐的时间
    jzsj = models.CharField(max_length=32, null=True)
    # 孩子非看屏幕的静坐频率（每周几天）
    fkpmjspv = models.CharField(max_length=32, null=True)
    # 每天非看屏幕的静坐时间如躺在垫子上，坐在高脚椅上、婴儿车或手推车中而几乎不动，坐着看书或坐着玩游戏
    fkpmjzsj = models.CharField(max_length=32, null=True)
    # 孩子屏幕前静坐的频率（每周几天）
    pmjzpl = models.CharField(max_length=32, null=True)
    # 每天屏幕前静坐的时间如被动地观看屏幕娱乐节目（电视、计算机、移动设备）的时间
    pmjzsj = models.CharField(max_length=32, null=True)

    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "身体活动"
        verbose_name_plural = "身体活动"

class Smxg(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 病例主表id
    student = models.ForeignKey(Student, on_delete=models.CASCADE, default="1")
    # 孩子晚上就寝/上床时间：平时（周一至周五）
    psscsj = models.DateTimeField(blank=True, null=True)
    # 孩子晚上就寝/上床时间：周末
    zmscsj = models.DateTimeField(blank=True, null=True)
    # 孩子晚上睡着时间（通常晚于就寝时间）：平时（周一至周五）
    psszsj = models.DateTimeField(blank=True, null=True)
    # 孩子晚上睡着时间（通常晚于就寝时间）：周末
    zmszsj = models.DateTimeField(blank=True, null=True)
    # 孩子晚上在固定时间上床睡觉
    gdsjscsj = models.CharField(max_length=32, null=True)
    # 孩子上床后在20分钟内入睡
    esfzrs = models.CharField(max_length=32, null=True)
    # 孩子在自己床上独自入睡
    dzrs = models.CharField(max_length=32, null=True)
    # 孩子在他人（父母或兄弟姐妹）床上入睡
    ztrcsrs = models.CharField(max_length=32, null=True)
    # 孩子入睡时出现摇摆或节律性动作
    rsyb = models.CharField(max_length=32, null=True)
    # 孩子需要特定物品入睡（如玩偶、特定的毛毯等）
    tdwrs = models.CharField(max_length=32, null=True)
    # 孩子需要家长在房间陪伴才能入睡
    xypbrs = models.CharField(max_length=32, null=True)
    # 到了就寝时间，孩子会准备好去睡觉
    hzbrs = models.CharField(max_length=32, null=True)
    # 到了就寝时间，孩子会抗拒去睡觉
    hkjrs = models.CharField(max_length=32, null=True)
    # 到了就寝时间，孩子会挣扎（如哭闹、拒绝待在床上等）
    hzhzz = models.CharField(max_length=32, null=True)
    # 孩子害怕在黑暗中睡觉
    hzhpha = models.CharField(max_length=32, null=True)
    # 孩子害怕独自一个人睡觉
    hzhpyr = models.CharField(max_length=32, null=True)
    # 通常孩子每天的睡眠：（包括夜间睡眠和日间小睡时间） 
    hzmtsm = models.CharField(max_length=32, null=True)
    # 孩子睡得太少
    sdts = models.CharField(max_length=32, null=True)
    # 孩子睡得太多
    sdtd = models.CharField(max_length=32, null=True)
    # 孩子的睡眠适量
    smsl = models.CharField(max_length=32, null=True)
    # 孩子每天的睡眠量都一样
    mtsmlyy = models.CharField(max_length=32, null=True)
    # 孩子晚上会尿床
    wsnc = models.CharField(max_length=32, null=True)
    # 孩子睡眠中会说梦话
    smsmh = models.CharField(max_length=32, null=True)
    # 孩子睡眠中不安稳，常动来动去
    smbaw = models.CharField(max_length=32, null=True)
    # 孩子夜间会梦游（睡眠过程中行走）
    hmy = models.CharField(max_length=32, null=True)
    # 孩子夜间会移动到他人（如父母、兄弟姐妹等）的床上
    hdtrcs = models.CharField(max_length=32, null=True)
    # 孩子反映睡眠中身体疼痛。如果有，说明哪里痛
    fytt = models.CharField(max_length=32, null=True)
    # 如果有，在何部位
    ttbw = models.CharField(max_length=32, null=True)
    # 孩子夜间醒来后没有帮助，能自主重新入睡
    mybzcxrs = models.CharField(max_length=32, null=True)
    # 孩子睡眠中有磨牙现象（牙医可能告诉过您）
    myxx = models.CharField(max_length=32, null=True)
    # 孩子睡眠中打鼾
    dhl = models.CharField(max_length=32, null=True)
    # 孩子睡眠中出现呼吸暂停
    hxzt = models.CharField(max_length=32, null=True)
    # 孩子睡眠中鼻息重或气急
    bxz = models.CharField(max_length=32, null=True)
    # 孩子不在家（如到亲戚家或去旅行）睡觉时有问题
    jzbzjywt = models.CharField(max_length=32, null=True)
    # 孩子抱怨睡眠问题
    bysmwt = models.CharField(max_length=32, null=True)
    # 孩子夜间醒来尖叫、出汗且无法安抚
    yjxljj = models.CharField(max_length=32, null=True)
    # 孩子被噩梦惊醒
    emjx = models.CharField(max_length=32, null=True)
    # 夜间醒来一般总共持续：平时（周一至周五）
    psxlsj = models.CharField(max_length=32, null=True)
    # 夜间醒来一般总共持续：周末
    zmxlsj = models.CharField(max_length=32, null=True)
    # 孩子夜间会醒来一次
    xlyc = models.CharField(max_length=32, null=True)
    # 孩子夜间会醒来一次以上
    xlycys = models.CharField(max_length=32, null=True)
    # 孩子早晨醒来的时间：平时
    pszcxlsj = models.DateTimeField(blank=True, null=True)
    # 孩子早晨醒来的时间：周末
    zmzcxlsj = models.DateTimeField(blank=True, null=True)
    # 孩子早晨起床的时间（一般晚于醒来的时间）：平时
    psqcsj = models.DateTimeField(blank=True, null=True)
    # 孩子早晨起床的时间（一般晚于醒来的时间）：周末
    zmqcsj = models.DateTimeField(blank=True, null=True)
    # 孩子早晨自己醒来
    zjxl = models.CharField(max_length=32, null=True)
    # 孩子早晨由闹钟叫醒
    nzjx = models.CharField(max_length=32, null=True)
    # 孩子醒来后情绪不佳
    xlqxbj = models.CharField(max_length=32, null=True)
    # 孩子早晨由他人（如家长或兄弟姐妹）叫醒
    yjrjx = models.CharField(max_length=32, null=True)
    # 孩子早晨起床困难
    qckn = models.CharField(max_length=32, null=True)
    # 孩子早晨需要很长时间才能清醒
    csjcnqx = models.CharField(max_length=32, null=True)
    # 孩子早晨醒来很早
    xlhz = models.CharField(max_length=32, null=True)
    # 孩子早晨胃口很好
    wkhh = models.CharField(max_length=32, null=True)
    # 孩子日间会小睡
    rjhxs = models.CharField(max_length=32, null=True)
    # 孩子在兴奋活动中突然睡着了
    xfhdzsz = models.CharField(max_length=32, null=True)
    # 孩子看起来很疲倦
    kqlhpj = models.CharField(max_length=32, null=True)
    # 独自玩耍
    dzws = models.CharField(max_length=32, null=True)
    # 看电视
    kds = models.CharField(max_length=32, null=True)
    # 坐车
    zc = models.CharField(max_length=32, null=True)
    # 吃饭
    cf = models.CharField(max_length=32, null=True)

    # 导入时间
    c_time = models.DateTimeField(auto_now_add=True)
    # 修改时间
    modify_time = models.DateTimeField(default=datetime.datetime.now, null=True)
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 内容
    count = models.TextField(null=True, default="")
    
    class Meta:
        ordering = ["-id"]
        verbose_name = "儿童睡眠习惯问卷"
        verbose_name_plural = "儿童睡眠习惯问卷"