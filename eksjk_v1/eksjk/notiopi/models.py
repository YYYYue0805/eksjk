from django.db import models

# Create your models here.

# 公告表
class Notice(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    releTarget = (
        ('10060001', "普通用户"),
        ('10060002', "初审人"),
        ('10060003', "复审人"),
    )

    # 发布人
    rele_per = models.CharField(max_length=11)
    # 发布人姓名
    rele_per_name = models.CharField(max_length=11, default="")
    # 标题
    title = models.CharField(max_length=128)
    # 发布内容
    content = models.CharField(max_length=1024)
    # 发布对象
    rele_target = models.CharField(max_length=128, choices=releTarget, default="0")
    # 关闭次数
    close_num = models.IntegerField(default=1)
    # 发布时间
    rele_time = models.DateTimeField(auto_now_add=True)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.title

    class Meta:
        ordering = ["-rele_time"]
        verbose_name = "公告"
        verbose_name_plural = "公告"

# 人员公告表
class PerNoticeLog(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )

    # 公告Id
    notice = models.ForeignKey(Notice, on_delete=models.CASCADE)
    # 人员Id
    per_id = models.CharField(max_length=11)
    # 关闭次数
    close_num = models.IntegerField(default=1)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")

    def __str__(self):
        return self.title

    class Meta:
        ordering = ["-notice"]
        verbose_name = "人员公告日志"
        verbose_name_plural = "人员公告日志"

# 意见反馈表
class Opinion(models.Model):
    delflg = (
        ('0', "已删除"),
        ('1', "有效数据"),
    )
    comStatus = (
        ('10070001', "未处理"),
        ('10070002', "已处理"),
    )

    # 提交人
    sub_per = models.CharField(max_length=11)
    # 单位
    sub_unit = models.CharField(max_length=64, default="")
    # 提交人姓名
    sub_per_name = models.CharField(max_length=11, default="")
    # 联系方式
    telephone = models.CharField(max_length=32)
    # 意见状态
    com_status = models.CharField(max_length=8, choices=comStatus, default="10070001")
    # 意见内容
    com_count = models.CharField(max_length=1024)
    # 反馈
    feedback = models.CharField(max_length=1024, null=True)
    # 提交时间
    sub_time = models.DateTimeField(auto_now_add=True)
    # 删除标志
    del_flg = models.CharField(max_length=12, choices=delflg, default="1")
    # 图片视频路径
    img_path = models.TextField(default="", null=True)

    def __str__(self):
        return self.title

    class Meta:
        ordering = ["-sub_time"]
        verbose_name = "意见反馈"
        verbose_name_plural = "意见反馈"