from django.db import models

# Create your models here.

class ChartUser(models.Model):

    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    isTongb = (
        ('0', "没有同步过"),
        ('1', "同步过"),
    )


    # openid
    openid = models.CharField(max_length=32)
    # key
    key = models.CharField(max_length=32, unique=True, null=True)
    # 病历号
    medrec_num = models.CharField(max_length=32, null=True)
    # 手机号
    phone_num = models.CharField(max_length=32, null=True)
    # 绑定医生
    doctor = models.CharField(max_length=32, null=True)
    # 判断是否同步过数据
    is_tongb = models.CharField(max_length=4, choices=isTongb, default="0")
    # 删除标志
    del_flg = models.CharField(max_length=4, choices=delflg, default="1")
    # 判断是否是新用户
    new_user_flag = models.CharField(max_length=32, null=True)
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


    class Meta:
        ordering = ["-openid"]
        verbose_name = "微信用户基本信息"
        verbose_name_plural = "微信用户基本信息"
