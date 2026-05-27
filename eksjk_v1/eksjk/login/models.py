from django.db import models
from django.contrib.auth.models import AbstractUser

# Create your models here.
# 用户表
class User(AbstractUser):
    level_choices = (
        (0, '普通用户'),
        (1, '管理员'),
    )

    professional = (
        ('10040001', "助理医师"),
        ('10040002', "医师"),
        ('10040003', "主治医师"),
        ('10040004', "副主任医师"),
        ('10040005', "主任医师"),
    )

    # 姓名
    name = models.CharField(max_length=128)
    # 性别
    sex = models.CharField(max_length=32, default="男")
    # 单位
    unit = models.CharField(max_length=128, default="1")
    # 级别
    level = models.IntegerField(choices=level_choices, default=0)
    # 职称
    professional = models.CharField(max_length=32, choices=professional, default="10040001")
    # 密码修改时间
    date_update = models.DateTimeField(auto_now=True)
    # 科室
    department = models.CharField(max_length=64, default="")

    def __str__(self):
        return self.name

    class Meta:
        verbose_name = "用户"
        verbose_name_plural = "用户"

# 权限表
class Permission(models.Model):
    """
    权限,一个权限对应一个url
    """
    title = models.CharField(max_length=32, unique=True,verbose_name = u"权限")
    url = models.CharField(max_length=128, unique=True)

    def __str__(self):
        return self.title

    class Meta:
        verbose_name = u"权限"
        verbose_name_plural = verbose_name

# 角色表
class role(models.Model):

    # 角色名声
    role_name = models.CharField(max_length=128, unique=True, default="默认角色名称")
    # 角色
    role = models.CharField(max_length=128)

    u_r = models.ManyToManyField('User')

    # 定义角色和权限的多对多关系
    permissions = models.ManyToManyField("Permission")

    def __str__(self):
        return self.role_name

    class Meta:
        ordering = ["-role"]
        verbose_name = "角色"
        verbose_name_plural = "角色"

# 登录日志表
class log(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    manaLevel = (
        ('10030001', "普通用户"),
        ('10030002', "管理员用户"),
        ('10030003', "超级管理员用户"),
    )

    # 登录人id
    login_per_id = models.CharField(max_length=11)
    # 登录人姓名
    login_per_name = models.CharField(max_length=32)
    # 管理级别
    mana_level = models.CharField(max_length=32, choices=manaLevel, default="10030001")
    # 电脑ip
    com_ip = models.CharField(max_length=64)
    # 登录时间
    login_time = models.DateTimeField()
    # 注销时间
    logout_time = models.DateTimeField()
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.login_per_name

    class Meta:
        ordering = ["-login_time"]
        verbose_name = "登录日志"
        verbose_name_plural = "登录日志"

# 单位表
class Unit(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 单位名称
    unit_name = models.CharField(max_length=64)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.unit_name

    class Meta:
        ordering = ["-unit_name"]
        verbose_name = "单位"
        verbose_name_plural = "单位"

# 标准药物单位表
class StandaUnit(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 药物名称
    drug_name = models.CharField(max_length=64)
    # 标准单位
    stand_unit = models.CharField(max_length=64)
    # 所属医院
    unit = models.CharField(max_length=64)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.drug_name

    class Meta:
        ordering = ["-drug_name"]
        verbose_name = "药物名称"
        verbose_name_plural = "药物名称"


# 个性药物单位表
class PersUnit(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 药物名称
    drug_name = models.CharField(max_length=64)
    # 个性单位
    stand_unit = models.CharField(max_length=64)
    # 和标准单位差值
    stand_unit = models.CharField(max_length=64)
    # 所属医院
    unit = models.CharField(max_length=64)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.drug_name

    class Meta:
        ordering = ["-drug_name"]
        verbose_name = "药物名称"
        verbose_name_plural = "药物名称"