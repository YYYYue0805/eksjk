
import ast
from common.area import area
from common.icd_data import ICDDataArray
import os
import zipfile
import xlwt
import xlsxwriter
from django.http import HttpResponse
from wjwsjk import settings
import json
from login import views as loginView
from notiopi import models as notiopimodels
from datamain import models as datamainmodels
import pydicom
import shutil
from datamain import models
from django.core.cache import cache
from common.queSubTable import query_sub_table
from common.utils import decode_id
from common.utils import safe_str


class excelStyle:
    # 为样式创建字体
    font = xlwt.Font()
    # 字体类型
    font.name = '宋体'
    # 字体大小，11为字号，20为衡量单位
    font.height = 20 * 11

    # 设置单元格对齐方式
    alignment = xlwt.Alignment()
    # 0x01(左端对齐)、0x02(水平方向上居中对齐)、0x03(右端对齐)
    alignment.horz = 0x03
    # 0x00(上端对齐)、 0x01(垂直方向上居中对齐)、0x02(底端对齐)
    alignment.vert = 0x01
    # 设置自动换行
    alignment.wrap = 1

    # 设置单元格对齐方式
    alignment2 = xlwt.Alignment()
    # 0x01(左端对齐)、0x02(水平方向上居中对齐)、0x03(右端对齐)
    alignment2.horz = 0x01
    # 0x00(上端对齐)、 0x01(垂直方向上居中对齐)、0x02(底端对齐)
    alignment2.vert = 0x01
    # 设置自动换行
    alignment2.wrap = 1

    # 设置边框
    borders = xlwt.Borders()
    # 细实线:1，小粗实线:2，细虚线:3，中细虚线:4，大粗实线:5，双线:6，细点虚线:7
    # 大粗虚线:8，细点划线:9，粗点划线:10，细双点划线:11，粗双点划线:12，斜点划线:13
    borders.left = 1
    borders.right = 1
    borders.top = 1
    borders.bottom = 1

    # 初始化样式
    style = xlwt.XFStyle()
    style.font = font
    style.alignment = alignment
    style.borders = borders

    # 初始化样式
    stylecount = xlwt.XFStyle()
    stylecount.font = font
    stylecount.borders = borders
    stylecount.alignment = alignment2


class ExcelFile():
    # 导出病例Excel
    def imp_case_excel(patient, rseult, follow, masfollow):
        # 导出病例主表Excel文件
        # 设置HTTPResponse的类型
        response = HttpResponse(content_type='application/vnd.ms-excel')
        response['Content-Disposition'] = 'attachment;filename=labExcel.xls'
        style = excelStyle.style
        stylecount = excelStyle.stylecount
        # 创建工作簿
        ws = xlwt.Workbook(encoding='utf-8')
        """导出excel表"""
        try:
            if patient:
                # 添加第一页数据表
                w = ws.add_sheet('患者信息')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                w.col(0).width = 256 * 20
                w.col(1).width = 512 * 20
                # 行高
                tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'病历号：', u'患者姓名：', u'国际疾病分类', u'性别：', u'性腺性别：', u'初诊时间：', u'出生日期：', u'年龄：', u'主诉：',
                         u'籍贯：', u'父亲身高：', u'母亲身高：', u'家族史：', u'胎龄周：', u'出生体重：', u'出生身长：',
                         u'出生方式：', u'保胎史：', u'既往史：', u'身份证号码：', u'家庭地址：', u'联系人姓名：', u'与患者关系：',
                         u'联系电话：', u'病例编号：']
                # 循环数据24次对应写入表头
                i = 0
                while i < 25:
                    w.write(i, 0, heads[i], style)
                    first_row = w.row(i)
                    first_row.set_style(tall_style)
                    i = i + 1
                # 写入每一行对应的数据
                # w.write(1：第一行，  1：第一列，   写入数据，    表格样式)
                w.write(0, 1, patient.medrec_num, stylecount)  # 病历号
                w.write(1, 1, patient.name, stylecount)  # 患者姓名
                # 国际疾病分类
                if safe_str(patient.ICD) and len(patient.ICD) > 0:
                    # 转换为字典
                    ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                    # 获取 patient.ICD 对应的 label
                    ICD = ICD_dict.get(patient.ICD, "未选择")
                    w.write(2, 1, ICD, stylecount)
                else:
                    w.write(2, 1, "未选择", stylecount)
                # 性别
                if patient.sex == '2':
                    w.write(3, 1, "女", stylecount)
                elif patient.sex == '1':
                    w.write(3, 1, "男", stylecount)
                else:
                    w.write(3, 1, "未选择", stylecount)
                # 性腺性别
                if patient.gonadal_sex == '2':
                    w.write(4, 1, "女", stylecount)
                elif patient.gonadal_sex == '1':
                    w.write(4, 1, "男", stylecount)
                else:
                    w.write(4, 1, "未选择", stylecount)
                # w.write(5, 1, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 初诊时间
                if patient.fir_vis_time is not None:
                    w.write(5, 1, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 初诊时间
                else:
                    w.write(5, 1, "未选择", stylecount)
                # w.write(6, 1, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 出生日期
                if patient.birth_time is not None:
                    w.write(6, 1, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 出生日期
                else:
                    w.write(6, 1, "未选择", stylecount)
                w.write(7, 1, patient.age, stylecount)  # 年龄
                w.write(8, 1, patient.chi_com, stylecount)  # 主诉
                # 籍贯
                natPla = patient.nat_pla
                #第一种方法
                natPList = ast.literal_eval(natPla)
                #第1-3个
                # 选择两个
                if natPList is not None and len(natPList) == 2:
                    one_data = natPList[0]
                    two_data = natPList[1]
                elif natPList is not None and len(natPList) == 3:
                    one_data = natPList[0]
                    two_data = natPList[1]
                    three_data = natPList[2]
                #获取比对
                # 选择两个
                if ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 2:
                    one = area.get(one_data)
                    two = area.get(two_data)
                    w.write(9, 1, one + "/" + two, stylecount)
                # 选择三个
                elif ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 3:
                    one = area.get(one_data)
                    two = area.get(two_data)
                    three = area.get(three_data)
                    w.write(9, 1, one + "/" + two + "/" + three, stylecount)
                # 未选择
                else:
                    w.write(9, 1, "未选择籍贯", stylecount)

                w.write(10, 1, patient.FHt, stylecount)  # 父亲身高
                w.write(11, 1, patient.MHt, stylecount)  # 母亲身高
                # 家族史
                if patient.family_his == '1':
                    w.write(12, 1, "无", stylecount)
                else:
                    w.write(12, 1, patient.family_his, stylecount)
                w.write(13, 1, patient.ges_week, stylecount)  # 胎龄周
                w.write(14, 1, patient.BWt, stylecount)  # 出生体重
                w.write(15, 1, patient.BL, stylecount)  # 出生身长
                # 出生方式
                if patient.dis_class == '10000001':
                    if patient.cesa_sec == '1':
                        w.write(16, 1, "刨宫产", stylecount)
                    elif patient.cesa_sec == '0':
                        w.write(16, 1, "自然分娩", stylecount)
                    else :
                        w.write(16, 1, "未选择", stylecount)
                else:
                    if patient.cesa_sec == '0':
                        w.write(16, 1, "刨宫产", stylecount)
                    elif patient.cesa_sec == '1':
                        w.write(16, 1, "自然分娩", stylecount)
                    else :
                        w.write(16, 1, "未选择", stylecount)
                # 保胎史
                if patient.fet_pro_his == '1':
                    w.write(17, 1, "无", stylecount)
                else:
                    w.write(17, 1, patient.fet_pro_his, stylecount)
                w.write(18, 1, patient.past_his, stylecount)  # 既往史
                w.write(19, 1, patient.card, stylecount)  # 身份证号码
                w.write(20, 1, patient.fam_adr, stylecount)  # 家庭地址
                w.write(21, 1, patient.contacts_name, stylecount)  # 联系人姓名
                w.write(22, 1, patient.relation, stylecount)  # 与患者关系
                w.write(23, 1, patient.contacts_num, stylecount)  # 联系电话
                w.write(24, 1, patient.case_num, stylecount)  # 病例编号
            else:
                return False
            if rseult:
                """
                    bone（性发育异常）
                    fss（家族性矮小）
                    cpp（中枢性早熟）
                    mas（MAS随访）
                """
                if patient.dis_class == '10000001':
                    file_type = 'bone'
                    # 添加第二页数据表
                    sheet2 = ws.add_sheet('临床资料')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet2.col(0).width = 450 * 20
                    sheet2.col(1).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'现身高:', u'现身高标准差:', u'现体重:', u'BMI:', u'外生殖器分期/双乳发育分期：', u'阴毛分期:', u'其他:',
                             u'阴茎长:', u'阴茎直径:', u'睾丸容量:', u'Prader分期:', u'尿道口位置:', u'右睾丸位置:',
                             u'左睾丸位置:', u'生殖器评估:']
                    i = 0
                    while i < 15:
                        sheet2.write(i, 0, heads[i], style)
                        first_row = sheet2.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet2.write(0, 1, safe_str(rseult.Ht) + "(cm)", stylecount)  # 现身高
                    sheet2.write(1, 1, safe_str(rseult.HSDS) + "(SDS)", stylecount)  # 现身高标准差
                    sheet2.write(2, 1, safe_str(rseult.Wt) + "(kg)", stylecount)  # 现体重
                    sheet2.write(3, 1, safe_str(rseult.WSDS) + "(kg/m^2)", stylecount)  # BMI
                    if patient.sex == '1':
                        # 外生殖器分期(男)
                        if (rseult.ex_genitalia is not None) and len(rseult.ex_genitalia) > 0:
                            sheet2.write(4, 1, "外生殖器分期(男): G" + safe_str(rseult.ex_genitalia), stylecount)
                        else:
                            sheet2.write(4, 1, "未选择", stylecount)
                    elif  patient.sex == '2':
                        # 双乳发育分期(女)
                        if (rseult.breast_dev is not None) and len(rseult.breast_dev) > 0:
                            sheet2.write(4, 1, "双乳发育分期(女): B" + safe_str(rseult.breast_dev), stylecount)
                        else:
                            sheet2.write(4, 1, "未选择", stylecount)
                    else:
                        sheet2.write(4, 1, "未选择", stylecount)
                    sheet2.write(5, 1, safe_str(rseult.pubic_hair), stylecount)  # 阴毛分期
                    sheet2.write(6, 1, safe_str(rseult.other), stylecount)  # 其他
                    sheet2.write(7, 1, safe_str(rseult.penile_length) + "(cm)", stylecount)  # 阴茎长
                    sheet2.write(8, 1, safe_str(rseult.penile_dia) + "(cm)", stylecount)  # 阴茎直径
                    sheet2.write(9, 1, safe_str(rseult.tes_volume) + "(ml)", stylecount)  # 睾丸容量
                    sheet2.write(10, 1, rseult.prader, stylecount)  # Prader分期
                    locaUreOriMap = {
                        '0': '正常',
                        '1': '冠状沟型',
                        '2': '阴茎型',
                        '3': '阴茎阴囊型',
                        '4': '会阴型',
                    }
                    loca = rseult.loca_ure_ori
                    finalLoca = locaUreOriMap.get(loca)
                    sheet2.write(11, 1, finalLoca, stylecount)  # 尿道口位置
                    # 右睾丸位置
                    rigTesPosMap = {
                        '1': '在阴唇',
                        '2': '在腹股沟',
                        '3': '在腹部',
                        '4': '睾丸缺如',
                        '5': '在阴囊',
                    }
                    rig = rseult.rig_tes_pos
                    finalRig = rigTesPosMap.get(rig)
                    sheet2.write(12, 1, finalRig, stylecount)
                    # 左睾丸位置
                    lef = rseult.lef_tes_pos
                    finalLef = rigTesPosMap.get(lef)
                    sheet2.write(13, 1, finalLef, stylecount)
                    # 生殖器评估
                    genitalsMap = {
                        '0': '正常男性化',
                        '1': '男性化轻度缺陷的男性表型，如孤立性尿道下裂',
                        '2': '男性化重度缺陷的男性表型，如小阴茎、会阴阴蒂尿道下裂、阴囊裂和/或隐宰',
                        '3': '严重生殖器模糊阴蒂样阴茎、阴唇阴蒂皱褶，单会阴口',
                        '4': '女性表型，后唇融合，阴蒂肥大',
                        '5': '女性表型(成年期有阴毛者为6级，成年期无阴毛者为7级)',
                    }
                    genitals = rseult.genitals
                    finamGenitals = genitalsMap.get(genitals)
                    sheet2.write(14, 1, finamGenitals, stylecount)




                    # 添加第三页数据表
                    sheet3 = ws.add_sheet('检验检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet3.col(0).width = 256 * 20
                    sheet3.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'骨龄:',u'图像说明', u'LH:', u'FSH:', u'睾酮T:', u'雌二醇E2:',
                             u'DHT:', u'游离睾酮:', u'SHBG:', u'IGF-1:', u'IGFBP-3:', u'抗缪勒管激素（AMH）:',
                             u'抑制素B（INHB）:', u'磁共振:', u'其他:', u'促肾上腺皮质激素（ACTH）:', u'皮质醇:', u'17-OHP:',
                             u'硫酸脱氢表雄酮:', u'雄烯二酮:']
                    i = 0
                    while i < 20:
                        sheet3.write(i, 0, heads[i], style)
                        first_row = sheet3.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet3.write(0, 1, rseult.bone_age, stylecount)  # 骨龄
                    # 图像说明
                    #(男)
                    if patient.sex == '1':
                        if rseult.bscanExplain is not None and len(rseult.bscanExplain)>0:
                            captionMan = "右侧睾丸大小" + safe_str(json.loads(rseult.bscanExplain)['testisLeftOne']) + "(cm)x"+ safe_str(json.loads(rseult.bscanExplain)['testisLeftTwo']) + "(cm)x"+ safe_str(json.loads(rseult.bscanExplain)['testisLeftThr']) + "(cm)" + "\n" + \
                                      "左侧睾丸大小" + safe_str(json.loads(rseult.bscanExplain)['testisRightOne']) + "(cm)x" + safe_str(json.loads(rseult.bscanExplain)['testisRightTwo']) + "(cm)x" + safe_str(json.loads(rseult.bscanExplain)['testisRightThr']) + "(cm)"
                            sheet3.write(1, 1, captionMan, stylecount)
                    #(女)
                    elif patient.sex == '2':
                        if rseult.bscanExplain is not None and len(rseult.bscanExplain) > 0:
                            captionWoman = "子宫大小" + safe_str(json.loads(rseult.bscanExplain)['uterusOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['uterusTwo']) + "*"  + safe_str(json.loads(rseult.bscanExplain)['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(rseult.bscanExplain)['intima']) + "(cm)" + "\n" + \
                                            "左侧卵巢大小约：" + safe_str(json.loads(rseult.bscanExplain)['ovaLeftOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaLeftTwo']) + "*"  + safe_str(json.loads(rseult.bscanExplain)['ovaLeftThr']) + "(cm)" + "\n" + \
                                           "左侧卵巢大小约：" + safe_str(json.loads(rseult.bscanExplain)['ovaRightOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaRightTwo']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaRightThr']) + "(cm)" + "\n" + \
                                            "最大滤泡直径大小：" + safe_str(json.loads(rseult.bscanExplain)['follDiameter']) + "(cm)"
                            sheet3.write(1, 1, captionWoman, stylecount)
                    else:
                        sheet3.write(1, 1, "未填写", stylecount)
                    sheet3.write(2, 1, safe_str(rseult.LH) + "(mIU/mL)", stylecount)  # LH
                    sheet3.write(3, 1, safe_str(rseult.FSH) + "(mIU/mL)", stylecount)  # FSH
                    sheet3.write(4, 1, safe_str(rseult.T) + "(ng/dL)", stylecount)  # 睾酮T
                    sheet3.write(5, 1, safe_str(rseult.E2) + "(pg/mL)", stylecount)  # 雌二醇E2
                    sheet3.write(6, 1, safe_str(rseult.DHT) + "(ng/mL)", stylecount)  # DHT
                    sheet3.write(7, 1, safe_str(rseult.FT) + "(ng/mL)", stylecount)  # 游离睾酮
                    sheet3.write(8, 1, safe_str(rseult.SHBG) + "(nmol/L)", stylecount)  # SHBG
                    sheet3.write(9, 1, safe_str(rseult.IGF1) + "(ng/mL)", stylecount)  # IGF-1
                    sheet3.write(10, 1, safe_str(rseult.IGFBP3) + "(μg/mL)", stylecount)  # IGFBP-3
                    sheet3.write(11, 1, safe_str(rseult.AMH), stylecount)  # 抗缪勒管激素
                    sheet3.write(12, 1, safe_str(rseult.INHB), stylecount)  # 抑制素B
                    sheet3.write(13, 1, safe_str(rseult.MRI), stylecount)  # 磁共振
                    sheet3.write(14, 1, rseult.body_other, stylecount)  # 其他
                    sheet3.write(15, 1, safe_str(rseult.ACTH) + "(pg/ml)", stylecount)  # 促肾上腺皮质激素
                    sheet3.write(16, 1, safe_str(rseult.Hyd) + "(ug/dl)", stylecount)  # 皮质醇
                    sheet3.write(17, 1, safe_str(rseult.OHP) + "(nmol/l)", stylecount)  # 17-OHP
                    sheet3.write(18, 1, safe_str(rseult.DHEAS) + "(ug/dl)", stylecount)  # 硫酸脱氢表雄酮
                    sheet3.write(19, 1, safe_str(rseult.AD) + "(ng/ml)", stylecount)  # 雄烯二酮




                    # 添加第四页数据表
                    sheet4 = ws.add_sheet('HCG激发试验')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet4.col(0).width = 256 * 20
                    sheet4.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入数据
                    # HCG激发试验1=无，2=标准HCG激发，3=延长HCG激发
                    if rseult.HCG == "1":
                        # 写入表头
                        heads = [u'HCG激发试验:', u'LH峰值:', u'LH/FSH峰值:']
                        i = 0
                        while i < 3:
                            sheet4.write(i, 0, heads[i], style)
                            first_row = sheet4.row(i)
                            first_row.set_style(tall_style)
                            i = i + 1
                        sheet4.write(0, 1, "无", stylecount)
                        sheet4.write(1, 1, safe_str(rseult.LHmax) + "(mIU/ml)", stylecount)  # LH峰值
                        sheet4.write(2, 1, safe_str(rseult.FSHmax) + "(mIU/ml)", stylecount)  # FSH峰值
                    elif rseult.HCG == "2":
                        # 写入表头
                        heads = [u'标准HCG激发T:', u'标准HCG激发激发DHT:', u'标准HCG激发激发AD:', u'LH峰值:', u'LH/FSH峰值:']
                        i = 0
                        while i < 5:
                            sheet4.write(i, 0, heads[i], style)
                            first_row = sheet4.row(i)
                            first_row.set_style(tall_style)
                            i = i + 1
                        sheet4.write(0, 1, safe_str(rseult.HCGT) + "(ng/dL)", stylecount)  # 标准HCG激发T
                        sheet4.write(1, 1, safe_str(rseult.HCGDHT)  + "(ng/ml)", stylecount)  # 标准HCG激发激发DHT
                        sheet4.write(2, 1, safe_str(rseult.HCGAD)  + "(ng/ml)", stylecount)  # 标准HCG激发激发AD
                        sheet4.write(3, 1, safe_str(rseult.LHmax)  + "(mIU/ml)", stylecount)  # LH峰值
                        sheet4.write(4, 1, safe_str(rseult.FSHmax)  + "(mIU/ml)", stylecount)  # FSH峰值
                    elif rseult.HCG == "3":
                        # 写入表头
                        heads = [u'【延长】HCG激发T:', u'【延长】HCG激发激发DHT:', u'【延长】HCG激发激发AD:',
                                 u'LH峰值:', u'LH/FSH峰值:']
                        i = 0
                        while i < 5:
                            sheet4.write(i, 0, heads[i], style)
                            first_row = sheet4.row(i)
                            first_row.set_style(tall_style)
                            i = i + 1
                        sheet4.write(0, 1, safe_str(rseult.HCGT_ext)  + "(ng/dL)", stylecount)  # 【延长】HCG激发T
                        sheet4.write(1, 1, safe_str(rseult.HCGDHT_ext)  + "(ng/ml)", stylecount)  # 【延长】HCG激发激发DHT
                        sheet4.write(2, 1, safe_str(rseult.HCGAD_ext)  + "(ng/ml)", stylecount)  # 【延长】HCG激发激发AD
                        sheet4.write(3, 1, safe_str(rseult.LHmax)  + "(mIU/ml)", stylecount)  # LH峰值
                        sheet4.write(4, 1, safe_str(rseult.FSHmax)  + "(mIU/ml)", stylecount)  # FSH峰值
                    else:
                        # 写入表头
                        heads = [u'HCG激发试验:', u'LH峰值:', u'LH/FSH峰值:']
                        i = 0
                        while i < 3:
                            sheet4.write(i, 0, heads[i], style)
                            first_row = sheet4.row(i)
                            first_row.set_style(tall_style)
                            i = i + 1
                        sheet4.write(0, 1, "无", stylecount)
                        sheet4.write(1, 1, "无", stylecount)
                        sheet4.write(2, 1, "无", stylecount)




                    # 添加第五页数据表
                    sheet5 = ws.add_sheet('遗传学检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet5.col(0).width = 350 * 20
                    sheet5.col(1).width = 768 * 20
                    sheet5.col(2).width = 768 * 20
                    sheet5.col(3).width = 768 * 20
                    sheet5.col(4).width = 768 * 20
                    sheet5.col(5).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'染色体核型:', u'检测:', u'生物样本库:', u'手术情况:', u'病理结果:', u'处理意见:',
                             u'其他:']
                    i = 0
                    while i < 7:
                        sheet5.write(i, 0, heads[i], style)
                        first_row = sheet5.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet5.write(0, 1, rseult.spe_kar, stylecount)  # 染色体核型
                    # 解析检测
                    if rseult.gen_mut_name:
                        data_list = json.loads(rseult.gen_mut_name)
                        row_count = 1
                        if data_list is not None and len(data_list) > 0:
                            for item in data_list:
                                sheet5.write(1, row_count, "致病基因名称: " + safe_str(item['genName']) + "\n" + "核酸变异：" + safe_str(item['Rna']) + "\n" + "氨基酸变异：" + safe_str(item['amino']) + "\n" + "父亲：" + safe_str(item['father']) + "\n" + "母亲：" + safe_str(item['mother']), stylecount)
                                row_count += 1
                        else:
                            sheet5.write(1, 1, "致病基因名称: 无填写 " + "\n" + "核酸变异：无填写" + "\n" + "氨基酸变异：无填写" + "\n" + "父亲：无填写" + "\n" + "母亲：无填写",stylecount)
                    # 生物样本库
                    if rseult.biolog == '无' or rseult.biolog is None or len(rseult.biolog) == 0:
                        sheet5.write(2, 1, "样本编号：无填写," + "\n" + "样本类型：无填写", stylecount)  # 样本编号
                    else:
                        data_str = rseult.biolog_bank.replace("'", '"')
                        data = json.loads(data_str)
                        if data:
                            data = data[0]
                            # 此处有bug，无法添加第二条。
                            map = {
                                '1':'DNA样本',
                                '2':'血清',
                                '3':'血浆',
                                '4':'尿液',
                            }
                            finalname = map.get(data['name'])
                            sheet5.write(2, 1, "样本编号：" +  safe_str(data['id']) + "\n" + "样本类型：" + safe_str(finalname), stylecount)
                    sheet5.write(3, 1, rseult.operation, stylecount)  # 手术情况
                    sheet5.write(4, 1, rseult.pat_res, stylecount)  # 病理结果
                    sheet5.write(5, 1, rseult.han_opi, stylecount)  # 处理意见
                    sheet5.write(6, 1, rseult.other, stylecount)  # 其他






                    # 添加第六页数据表
                    sheet6 = ws.add_sheet('诊断')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet6.col(0).width = 256 * 20
                    sheet6.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'诊断:']
                    i = 0
                    while i < 1:
                        sheet6.write(i, 0, heads[i], style)
                        first_row = sheet6.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    if rseult.diagnosis is not None and len(rseult.diagnosis) > 0:
                        diaData = rseult.diagnosis.replace("'", '"')
                        Map = {
                            tuple(['A', 'A01']):'性染色体异常 -> 45，X(turner综合征及其变体)',
                            tuple(['A', 'A02']):'性染色体异常 -> 47，XXY(Klinefelter综合征及其变体)',
                            tuple(['A', 'A03']): '性染色体异常 -> 45，X/46，XY[混合性性腺发育不良(MGD),卵睾DSD]',
                            tuple(['A', 'A04']): '性染色体异常 -> 46，XX/46，XY(嵌合体,卵睾DSD)',
                            tuple(['B', 'B01', 'B01A']): '46，XY -> 性腺（睾丸）发育不良 -> 完全性腺发育不良（swyer综合征）',
                            tuple(['B', 'B01', 'B01B']): '46，XY -> 性腺（睾丸）发育不良 -> 部分性腺发育不良睾丸',
                            tuple(['B', 'B01', 'B01C']): '46，XY -> 性腺（睾丸）发育不良 -> 退化综合征',
                            tuple(['B', 'B01', 'B01D']): '46，XY -> 性腺（睾丸）发育不良 -> 卵睾DSD',
                            tuple(['B', 'B02', 'B02A']): '46，XY -> 雄激素合成或作用障碍 -> 雄激素合成障碍（5a-还原酶缺乏，17-羟基类固醇脱氢酶缺乏）',
                            tuple(['B', 'B02', 'B02B']): '46，XY -> 雄激素合成或作用障碍 -> 雄激素作用障碍（完全性雄激素不敏感综合征，部分性雄激素不敏感综合征）',
                            tuple(['B', 'B02', 'B02C']): '46，XY -> 雄激素合成或作用障碍 -> LH受体缺乏（间质细胞萎缩）',
                            tuple(['B', 'B02', 'B02D']): '46，XY -> 雄激素合成或作用障碍 -> AMH的缺乏及AMH受体障碍（持续性副中肾管综合征）',
                            tuple(['B', 'B03', 'B03A']): '46，XY -> 其他 -> 严重的尿道下裂',
                            tuple(['B', 'B03', 'B03B']): '46，XY -> 其他 -> 泄殖腔外翻',
                            tuple(['C', 'C01', 'C01A']): '46，XX -> 性腺（卵巢）发育不良 -> 性腺发育不良',
                            tuple(['C', 'C01', 'C01B']): '46，XX -> 性腺（卵巢）发育不良 -> 卵睾DSD',
                            tuple(['C', 'C01', 'C01C']): '46，XX -> 性腺（卵巢）发育不良 -> 睾丸性DSD',
                            tuple(['C', 'C02', 'C02A']): '46，XX -> 雄激素过多 -> 胎儿源性（21-羟化酶缺乏，11-羟化酶缺乏）',
                            tuple(['C', 'C02', 'C02B']): '46，XX -> 雄激素过多 -> 胎盘源性（芳香化酶缺乏）',
                            tuple(['C', 'C02', 'C02C']): '46，XX -> 雄激素过多 -> 母体源（黄体瘤，孕期服用雄激素）',
                            tuple(['C', 'C03', 'C03A']): '46，XX -> 其他 -> 阴道闭锁',
                            tuple(['C', 'C03', 'C03B']): '46，XX -> 其他 -> 泄殖腔外翻',
                            tuple(['C', 'C03', 'C03C']): '46，XX -> 其他 -> MURCS等',
                        }
                        dia = json.loads(diaData)
                        finalDia = Map.get(tuple(dia))
                        sheet6.write(0, 1, finalDia, stylecount)  # 诊断
                    else:
                        sheet6.write(0, 1, "未选择", stylecount)




                    # 添加第七页数据表
                    sheet7 = ws.add_sheet('随访')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet7.col(0).width = 350 * 20
                    sheet7.col(1).width = 1000 * 20
                    sheet7.col(2).width = 1000 * 20
                    sheet7.col(3).width = 1000 * 20
                    sheet7.col(4).width = 1000 * 20
                    sheet7.col(5).width = 1000 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'随访:',u'诊疗方案']
                    i = 0
                    while i < 2:
                        sheet7.write(i, 0, heads[i], style)
                        first_row = sheet7.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    iFlCount = 1
                    for iFl in follow:
                        #随访
                        # (男)
                        if patient.sex == '1':
                            iFl_listMan = "随访日期:" + iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S') + "\n" + \
                                       "年龄：" + safe_str(iFl.age) + "\n" + \
                                       "身高：" + safe_str(iFl.Ht) + "(cm)" + "\n" + \
                                       "体重：" + safe_str(iFl.Wt) + "(kg)" + "\n" + \
                                       "外生殖器分期：" + "G" + safe_str(iFl.gen_stag) + "\n" + \
                                       "阴毛分期：" + safe_str(iFl.pub_stag) + "\n" + \
                                       "LH：" + safe_str(iFl.LH) + "(mIU/ml)" + "\n" + \
                                       "FSH：" + safe_str(iFl.FSH) + "(mIU/ml)" + "\n" + \
                                       "睾酮T：" + safe_str(iFl.T) + "(ng/dL)" + "\n" + \
                                       "雌二醇E2：" + safe_str(iFl.E2) + "(pg/ml)" + "\n" + \
                                       "DHT：" + safe_str(iFl.DHT) + "(ng/ml)" + "\n" + \
                                       "游离睾酮：" + safe_str(iFl.yltg) + "(ng/ml)" + "\n" + \
                                       "SHBG：" + safe_str(iFl.SHBG) + "(L)" + "\n" + \
                                       "IGF-1：" + safe_str(iFl.IGF1) + "(ng/ml)" + "\n" + \
                                       "IGFBP-3:" + safe_str(iFl.IGFBP3) + "(μg/m)" + "\n" + \
                                       "性腺B超-睾丸大小右侧:" + "右侧" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisRightOne']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisRightTwo']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisRightThr']) + "(cm)x，长颈：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisRightThr']) + "(cm)" + "\n" + \
                                       "性腺B超-睾丸大小左侧:" + "左侧" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisLeftOne']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisLeftTwo']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisLeftThr']) + "(cm)x，长颈：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['testisLeftLon']) + "(cm)" + "\n" + \
                                       "其他 ：" + safe_str(iFl.other) + "\n"
                            sheet7.write(0, iFlCount, iFl_listMan, stylecount)
                        # (女)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" +safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" +safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*"+safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                cyst_info =  "无"
                            else:
                                cyst_info = "未选择"
                            # 代码中引用 cyst_info
                            iFl_listWoman = "随访日期:" + iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S') + "\n" + \
                                       "年龄：" + safe_str(iFl.age) + "\n" + \
                                       "身高：" + safe_str(iFl.Ht) + "(cm)" + "\n" + \
                                       "体重：" + safe_str(iFl.Wt) + "(kg)" + "\n" + \
                                       "双乳发育分期：" + "B" + safe_str(iFl.gen_stag) + "\n" + \
                                       "阴毛分期：" + safe_str(iFl.pub_stag) + "\n" + \
                                       "LH：" + safe_str(iFl.LH) + "(mIU/ml)" + "\n" + \
                                       "FSH：" + safe_str(iFl.FSH) + "(mIU/ml)" + "\n" + \
                                       "睾酮T：" + safe_str(iFl.T) + "(ng/dL)" + "\n" + \
                                       "雌二醇E2：" + safe_str(iFl.E2) + "(pg/ml)" + "\n" + \
                                       "DHT：" + safe_str(iFl.DHT) + "(ng/ml)" + "\n" + \
                                       "游离睾酮：" + safe_str(iFl.yltg) + "(ng/ml)" + "\n" + \
                                       "SHBG：" + safe_str(iFl.SHBG) + "(L)" + "\n" + \
                                       "IGF-1：" + safe_str(iFl.IGF1) + "(ng/ml)" + "\n" + \
                                       "IGFBP-3:" + safe_str(iFl.IGFBP3) + "(μg/m)" + "\n" + \
                                       "子宫三径约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusThr']) + "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['intima']) + "(cm)" + "\n" + \
                                       "左侧卵巢大小约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftThr']) + "(cm)" + "\n" + \
                                        "右侧卵巢大小约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightThr']) + "(cm)，最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['follDiameter']) + "(cm)" + "\n" + \
                                        "有无囊肿：" + cyst_info + "\n" + \
                                        "其他 ：" + safe_str(iFl.other) + "\n"
                            sheet7.write(0, iFlCount, iFl_listWoman, stylecount)
                        else:
                            sheet7.write(0, iFlCount, "未填写", stylecount)
                        # 诊疗方案
                        if iFl.dia_trea_plan == "无":
                            pass
                        else:
                            if 'diaPlan' in  json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                sheet7.write(1, iFlCount, "雄激素替代治疗(药名，剂量，用法)" + safe_str(json.loads(iFl.dia_trea_plan)['rhGH']), stylecount)
                            elif  'diaPlan' in  json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                   sheet7.write(1, iFlCount, "雌激素替代治疗(药名，剂量，用法)" + safe_str(json.loads(iFl.dia_trea_plan)['rhGH']), stylecount)
                            else:
                                sheet7.write(1, iFlCount, "未选择", stylecount)
                        iFlCount += 1
                elif patient.dis_class == '10000002':
                    file_type = 'fss'
                    # 添加第二页数据表
                    sheet2 = ws.add_sheet('临床资料')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet2.col(0).width = 350 * 20
                    sheet2.col(1).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'初次就诊时间:', u'初诊年龄:', u'主诉:', u'生长速率:', u'初次遗精/月经初潮:', u'身高:',
                             u'体重:', u'BMI:', u'外生殖器分期/双乳发育分期', u'阴毛分期:', u'臂长:', u'特殊面容:',
                             u'脊柱侧弯:', u'皮疹:',  u'运动发育落后:', u'语言发育落后:', u'智力发育落后:', u'听力异常:',
                             u'反复感染史:', u'抽搐史:', u'诊疗方案:', u'其他:', u'与患者关系:', u'年龄:', u'身高:', u'体重:',
                             u'初潮/遗精年龄:', u'健康调查:']
                    i = 0
                    while i < 28:
                        sheet2.write(i, 0, heads[i], style)
                        first_row = sheet2.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet2.write(0, 1, safe_str(json.loads(rseult.med_his)['firVisTime']), stylecount)  # 初次就诊时间
                    sheet2.write(1, 1, safe_str(json.loads(rseult.med_his)['morbidAge']), stylecount)  # 初诊年龄
                    sheet2.write(2, 1, safe_str(json.loads(rseult.med_his)['chiefCom']), stylecount)  # 主诉
                    # 生长速率
                    if json.loads(rseult.med_his) == 1 or 'growRate' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['growRate'] == '1':
                        sheet2.write(3, 1, "不详", stylecount)
                    elif json.loads(rseult.med_his) == 2 or 'growRate' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['growRate'] == '2':
                        sheet2.write(3, 1, safe_str(json.loads(rseult.med_his)['growRate'])+"(厘米/年)", stylecount)
                    else:
                        sheet2.write(3, 1, "未选择", stylecount)
                    # 初次遗精
                    if patient.sex == '1':
                        if 'menarchy' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['menarchy'] == '1':
                            sheet2.write(4, 1, "无", stylecount)
                        elif 'menarchy' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['menarchy'] == '2':
                            sheet2.write(4, 1, "初次遗精时间（男）：" + safe_str(json.loads(rseult.med_his)['menarchyTime']),stylecount)
                        else:
                            sheet2.write(4, 1, "未选择", stylecount)
                    # 月经初潮
                    elif patient.sex == '2':
                        if 'menarchy' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['menarchy'] == '1':
                            sheet2.write(4, 1, "无", stylecount)
                        elif  'menarchy' in json.loads(rseult.med_his) and json.loads(rseult.med_his)['menarchy'] == '2':
                            sheet2.write(4, 1, "月经初潮时间（女）：" + safe_str(json.loads(rseult.med_his)['menarchyTime']), stylecount)
                        else:
                            sheet2.write(4, 1, "未选择", stylecount)
                    else:
                        sheet2.write(4, 1, "未选择", stylecount)
                    sheet2.write(5, 1, json.loads(rseult.phy_exa)['height'], stylecount)  # 身高
                    sheet2.write(6, 1, json.loads(rseult.phy_exa)['weight'], stylecount)  # 体重
                    sheet2.write(7, 1, json.loads(rseult.phy_exa)['Bmi'], stylecount)  # BMI
                    # 外生殖器分期
                    if patient.sex == '1':
                        if json.loads(rseult.phy_exa)['exGenitalia'] is not None and  len(json.loads(rseult.phy_exa)['exGenitalia'])>0:
                            sheet2.write(8, 1, "外生殖器分期(男): G"+safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                        else:
                            sheet2.write(8, 1, "未选择", stylecount)
                    elif patient.sex == '2':
                        if json.loads(rseult.phy_exa)['breastDev'] is not None and  len(json.loads(rseult.phy_exa)['breastDev'])>0:
                            sheet2.write(8, 1, "双乳发育分期（女）: B"+safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                        else:
                            sheet2.write(8, 1, "未选择", stylecount)
                    else:
                        sheet2.write(8, 1, "未选择", stylecount)
                    sheet2.write(9, 1, json.loads(rseult.phy_exa)['pubicHair'], stylecount)  # 阴毛分期
                    sheet2.write(10, 1, json.loads(rseult.phy_exa)['armLength'], stylecount)  # 臂长
                    # 特殊面容
                    if 'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '2':
                        sheet2.write(11, 1, json.loads(rseult.phy_exa)['specialFaceDesc'], stylecount)
                    elif  'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '1':
                        sheet2.write(11, 1, "无", stylecount)
                    else:
                        sheet2.write(11, 1, "未选择", stylecount)
                    # 脊柱侧弯
                    if 'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '2':
                        scolioMap = {
                            '1':'轻度',
                            '2': '中度',
                            '3': '重度',
                        }
                        scoliosisDegree = json.loads(rseult.phy_exa)['scoliosisDegree']
                        finalScolio = scolioMap.get(scoliosisDegree)
                        sheet2.write(12, 1, finalScolio, stylecount)
                    elif  'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '1':
                        sheet2.write(12, 1, "无", stylecount)
                    else:
                        sheet2.write(12, 1, "未选择", stylecount)
                    # 皮疹
                    if 'rash' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['rash'] == 2:
                        sheet2.write(13, 1, json.loads(rseult.phy_exa)['rashDescribe'], stylecount)
                    else:
                        sheet2.write(13, 1, "无", stylecount)
                    # 运动发育落后
                    if 'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '2':
                        sheet2.write(14, 1, json.loads(rseult.mot_dev_back)['sport'], stylecount)
                    elif  'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '1':
                        sheet2.write(14, 1, "无", stylecount)
                    else:
                        sheet2.write(14, 1, "未选择", stylecount)
                    # 语言发育落后
                    if 'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '2':
                        sheet2.write(15, 1, json.loads(rseult.lan_dev_back)['language'], stylecount)
                    elif  'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '1':
                        sheet2.write(15, 1, "无", stylecount)
                    else:
                        sheet2.write(15, 1, "未选择", stylecount)
                    # 智力发育落后
                    if 'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '2':
                        sheet2.write(16, 1, json.loads(rseult.int_dev_back)['intelligence'], stylecount)
                    elif  'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '1':
                        sheet2.write(16, 1, "无", stylecount)
                    else:
                        sheet2.write(16, 1, "未选择", stylecount)
                    # 听力异常
                    if 'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '2':
                        sheet2.write(17, 1, json.loads(rseult.abn_hear)['hear'], stylecount)
                    elif  'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '1':
                        sheet2.write(17, 1, "无", stylecount)
                    else:
                        sheet2.write(17, 1, "未选择", stylecount)
                    # 反复感染史
                    if 'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '2':
                        sheet2.write(18, 1, json.loads(rseult.rec_inf_his)['infection'], stylecount)
                    elif  'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '1':
                        sheet2.write(18, 1, "无", stylecount)
                    else:
                        sheet2.write(18, 1, "未选择", stylecount)
                    # 抽搐史
                    if rseult.con_his == '1':
                        sheet2.write(19, 1, "无", stylecount)
                    elif  rseult.con_his == '2':
                        sheet2.write(19, 1, "有", stylecount)
                    else:
                        sheet2.write(19, 1, "未选择", stylecount)
                    # 诊疗方案
                    if rseult.dia_trea_plan is not None and len(rseult.dia_trea_plan) > 0 :
                        # 诊疗方案(多种选择)
                        if rseult.dia_trea_plan:
                            # 治疗1
                            if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                                sheet2.write(20, 1, "未治疗", stylecount)
                            # 治疗2
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet2.write(20, 1, "rhGH治疗，短效rhGH" + safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']) + "（U/kg.d）", stylecount)
                                else:
                                    sheet2.write(20, 1,"rhGH治疗，长效生长激素（PEG-rhGH）" + safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']) + "（mg/kg.w，每周1次）", stylecount)
                            # 治疗3
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet2.write(20, 1, "GnRHa治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                    sheet2.write(20, 1, "GnRHa治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                    sheet2.write(20, 1, "GnRHa治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                    sheet2.write(20, 1, "GnRHa治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                    sheet2.write(20, 1, "GnRHa治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                    sheet2.write(20, 1, "GnRHa治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗4
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                    sheet2.write(20, 1, "GnRHal联合生长激素治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗5
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                                sheet2.write(20, 1, "芳香化酶抑制剂", stylecount)
                            # 治疗6
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                                sheet2.write(20, 1, "停止GnRHa治疗", stylecount)
                            # 治疗7
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                                sheet2.write(20, 1, "停止GnRHal联合生长激素治疗", stylecount)
                            # 治疗8
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                                sheet2.write(20, 1, "停止生长激素治疗", stylecount)
                            # 治疗9
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['diaPlan'] == '9':
                                sheet2.write(20, 1, "中医药治疗", stylecount)
                            else:
                                sheet2.write(20, 1, "未选择", stylecount)

                    sheet2.write(21, 1, rseult.past_other, stylecount)  # 其他
                    if rseult.fam_his:
                        famHisData = json.loads(rseult.fam_his)
                        # 定义一个变量行数（解决循环写入导致覆盖的问题）
                        row_count = 1
                        for item in famHisData:
                            sheet2.write(22, row_count, safe_str(item['relation']), stylecount)  # 与患者关系
                            sheet2.write(23, row_count, safe_str(item['tAge']), stylecount)  # 年龄
                            sheet2.write(24, row_count, safe_str(item['height']), stylecount)  # 身高
                            sheet2.write(25, row_count, safe_str(item['weight']), stylecount)  # 体重
                            sheet2.write(26, row_count, safe_str(item['age']), stylecount)  # 初潮/遗精年龄
                            sheet2.write(27, row_count, safe_str(item['health']), stylecount)  # 健康调查
                            # 每次循环后行号增加6，以保证新的数据在新行中写入
                            row_count += 1

                    # 添加第三页数据表
                    sheet3 = ws.add_sheet('检验检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet3.col(0).width = 256 * 20
                    sheet3.col(1).width = 768 * 20
                    sheet3.col(2).width = 768 * 20
                    sheet3.col(3).width = 768 * 20
                    sheet3.col(4).width = 768 * 20
                    sheet3.col(5).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'LH:', u'FSH:', u'E2:', u'T:', u'PRL:', u' IGF-1:', u' IGFBP-3:',
                             u'甲功:', u'ACTH(8am):', u' 皮质醇（8am）:', u'DHEAs:', u'17-OHP:', u'血常规:',
                             u'尿常规:', u'肝肾脂糖电解质:', u'乙肝三系:', u'Gh药物激发试验-Gh峰值:', u'心电图:',
                              u'性腺B超：', u'垂体MRI:',
                             u'左侧甲状腺b超:', u'右侧甲状腺b超:']
                    i = 0
                    while i < 22:
                        sheet3.write(i, 0, heads[i], style)
                        first_row = sheet3.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet3.write(0, 1, safe_str(json.loads(rseult.lab_exa)['LH']) + "(mIU/mL)", stylecount)  # LH
                    sheet3.write(1, 1, safe_str(json.loads(rseult.lab_exa)['FSH']) + "(mIU/mL)", stylecount)  # FSH
                    sheet3.write(2, 1, safe_str(json.loads(rseult.lab_exa)['E2']) + "(pg/mL)", stylecount)  # E2
                    sheet3.write(3, 1, safe_str(json.loads(rseult.lab_exa)['T']) + "(ng/dL)", stylecount)  # T
                    sheet3.write(4, 1, safe_str(json.loads(rseult.lab_exa)['PRL']) + "(ng/mL)", stylecount)  # PRL
                    sheet3.write(5, 1, safe_str(json.loads(rseult.lab_exa)['IGF']) + "(ng/mL)", stylecount)  #  IGF-1
                    sheet3.write(6, 1, safe_str(json.loads(rseult.lab_exa)['IGFBP3']) + "(ug/mL)", stylecount)  #  IGFBP-3
                    # 甲功
                    if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                        sheet3.write(7, 1, json.loads(rseult.lab_exa)['thyroidDescribe'], stylecount)
                    elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                        sheet3.write(7, 1, "正常", stylecount)
                    else:
                        sheet3.write(7, 1, "未选择", stylecount)
                    sheet3.write(8, 1, safe_str(json.loads(rseult.lab_exa)['ACTH']) + "(pg/mL)", stylecount)  # ACTH
                    sheet3.write(9, 1, safe_str(json.loads(rseult.lab_exa)['cortisol']) + "(ug/dL)", stylecount)  #  皮质醇（8am）
                    sheet3.write(10, 1, safe_str(json.loads(rseult.lab_exa)['DHEAS']) + "(ug/dL)", stylecount)  # DHEAs
                    sheet3.write(11, 1, safe_str(json.loads(rseult.lab_exa)['OHP']) + "(nmol/L)", stylecount)  # 17-OHP
                    # 血常规
                    if 'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '2':
                        sheet3.write(12, 1, json.loads(rseult.lab_exa)['bloodDescribe'], stylecount)
                    elif  'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '1':
                        sheet3.write(12, 1, "正常", stylecount)
                    else:
                        sheet3.write(12, 1, "未选择", stylecount)
                    # 尿常规
                    if 'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '2':
                        sheet3.write(13, 1, json.loads(rseult.lab_exa)['urinalysisDescribe'], stylecount)
                    elif  'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '1':
                        sheet3.write(13, 1, "正常", stylecount)
                    else:
                        sheet3.write(13, 1, "未选择", stylecount)
                    # 肝肾脂糖电解质
                    if 'LAKLGE ' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                        sheet3.write(14, 1, json.loads(rseult.lab_exa)['laklgeDescribe'], stylecount)
                    elif  'LAKLGE ' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                        sheet3.write(14, 1, "正常", stylecount)
                    else:
                        sheet3.write(14, 1, "未选择", stylecount)
                    # 乙肝三系
                    HBsMap = {
                        '1':'阴性',
                        '2': 'HBSAb阳性',
                        '3': '小三阳',
                        '4': '大三阳',
                    }
                    HBs = json.loads(rseult.lab_exa)['HBs']
                    finalHBs = HBsMap.get(HBs)
                    sheet3.write(15, 1, finalHBs, stylecount)
                    sheet3.write(16, 1, safe_str(json.loads(rseult.lab_exa)['gh']) + "(ng/ml)", stylecount)  # Gh药物激发试验-Gh峰值
                    sheet3.write(17, 1, rseult.electr, stylecount)  # 心电图
                    gon_B_ult = rseult.gon_B_ult.replace("\n", "")
                    # 性腺B超
                    # 男
                    if patient.sex == '1':
                        gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightThr']) + "cm，长径：" +  safe_str(json.loads(gon_B_ult)['testisRightLon']) + "(cm)" +  "\n" + \
                                          "睾丸大小-左侧：" + safe_str(json.loads(gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                        sheet3.write(18, 1, gonadUltrasoundMan, stylecount)
                    # 女
                    elif patient.sex == '2':
                        # 判断随访囊肿(是否存在存在)
                        if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                            cyst_info = "有，" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                        elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                            cyst_info = "无"
                        else:
                            cyst_info = "未选择"
                        gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult)['uterusTwo']) + "*"  + safe_str(json.loads(gon_B_ult)['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftTwo']) + "*"  + safe_str(json.loads(gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                               "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                "有无囊肿：" + cyst_info
                        sheet3.write(18, 1, gonadUltrasoundWoman, stylecount)
                    else:
                        sheet3.write(18, 1, "未填写", stylecount)
                    # 垂体MRI
                    if 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '2':
                        sheet3.write(19, 1, json.loads(gon_B_ult)['mriDescribe'], stylecount)
                    elif  'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '1':
                        sheet3.write(19, 1, "正常", stylecount)
                    else:
                        sheet3.write(19, 1, "未选择", stylecount)
                    # 左侧甲状腺b超
                    if 'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '2':
                        sheet3.write(20, 1, "甲状腺结节分级: " + safe_str(json.loads(gon_B_ult)['ThyroidLBGradation']) + "\n" + "甲状腺大小:" + safe_str(json.loads(gon_B_ult)['ThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(json.loads(gon_B_ult)['ThyroidLBLesions']) + "\n" + "其他：" + safe_str(json.loads(gon_B_ult)['ThyroidLBOther']), stylecount)
                    elif  'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '1':
                        sheet3.write(20, 1, "正常", stylecount)
                    else:
                        sheet3.write(20, 1, "未选择", stylecount)
                    # 右侧甲状腺b超
                    if 'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '2':
                        sheet3.write(21, 1, "甲状腺结节分级: " + safe_str(json.loads(gon_B_ult)['ThyroidRBGradation']) + "\n" + "甲状腺大小:" + safe_str(json.loads(gon_B_ult)['ThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(json.loads(gon_B_ult)['ThyroidRBLesions']) + "\n" + "其他：" + safe_str(json.loads(gon_B_ult)['ThyroidRBOther']), stylecount)
                    elif  'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '1':
                        sheet3.write(21, 1, "正常", stylecount)
                    else:
                        sheet3.write(21, 1, "未选择", stylecount)


                    # 添加第四页数据表
                    sheet4 = ws.add_sheet('遗传学检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet4.col(0).width = 256 * 20
                    sheet4.col(1).width = 768 * 20
                    sheet4.col(2).width = 768 * 20
                    sheet4.col(3).width = 768 * 20
                    sheet4.col(4).width = 768 * 20
                    sheet4.col(5).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入数据
                    # 写入表头
                    heads = [u'染色体核型:', u'生物样本库:', u'父亲生物样本库:', u'母亲生物样本库:', u'致病基因名称:', u'核酸异变:',
                             u'氨基酸异变:', u'父亲:', u'母亲:']
                    i = 0
                    while i < 9:
                        sheet4.write(i, 0, heads[i], style)
                        first_row = sheet4.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    sheet4.write(0, 1, rseult.spe_kar, stylecount) # 染色体核型
                    # 生物样本库是否存在
                    if rseult.bio_sam_bank:
                        # 选择 是/否  ——>  1/2
                        if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)['bioBank'] == '2':
                            # json.loads(rseult.bio_sam_bank)['sampleClass'] 结果为 "[{'id': '1', 'name': '1'}]"  不符合 json格式
                            # 添加.replace("'", '"') 变为符合json的格式
                            sample_class_data = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                            # 此时不能直接 sample_class_data[0]['id'] 取，这样取会提示{TypeError}string indices must be integers。
                            # 加入 json.loads() 使得变成 json 格式
                            if len(sample_class_data) > 2:
                                sample_class_list = json.loads(sample_class_data)
                                row_count = 1
                                for sampleClassListItem in sample_class_list:
                                    map = {
                                        '1': 'DNA样本',
                                        '2': '血清',
                                        '3': '血浆',
                                        '4': '尿液',
                                    }
                                    finalname = map.get(sampleClassListItem['name'])
                                    sheet4.write(1, row_count, "样本编号：" + safe_str(sampleClassListItem['id']) + "\n" + "\n样本类型：" + safe_str(finalname), stylecount)
                                    row_count += 1
                            else:
                                sheet4.write(1, 1, "无", stylecount)
                        else:
                            sheet4.write(1, 1, "无", stylecount)  # 生物样本库
                    # 父亲生物样本库
                    if rseult.f_bio_sam_bank:
                        if 'bioBankFa' in json.loads(rseult.f_bio_sam_bank) and json.loads(rseult.f_bio_sam_bank)['bioBankFa'] == '2':
                            row_count = 1
                            sampleClassData = json.loads(rseult.f_bio_sam_bank)['sampleClassFa'].replace("'", '"')
                            if len(sampleClassData) > 2:
                                sampleList = json.loads(sampleClassData)
                                for sampleItemF in sampleList:
                                    sheet4.write(2, row_count, "样本编号：" + safe_str(sampleItemF['id']) + "\n" + "样本类型：" + safe_str(sampleItemF['name']),stylecount)
                                    row_count += 1
                            else:
                                sheet4.write(2, 1, "无", stylecount)
                        else:
                            sheet4.write(2, 1, "无", stylecount)  # 父亲生物样本库
                    # 母亲生物样本库
                    if rseult.m_bio_sam_bank:
                        if 'bioBankMo' in json.loads(rseult.m_bio_sam_bank) and json.loads(rseult.m_bio_sam_bank)['bioBankMo'] == '2':
                            row_count = 1
                            sampleClassData = json.loads(rseult.m_bio_sam_bank)['sampleClassMo'].replace("'", '"')
                            if len(sampleClassData) > 2:
                                sampleList = json.loads(sampleClassData)
                                for sampleItemM in sampleList:
                                    sheet4.write(3, row_count, "样本编号：" + safe_str(sampleItemM['id']) + "\n" + "样本类型：" + safe_str(sampleItemM['name']),stylecount)
                                    row_count += 1
                            else:
                                sheet4.write(3, 1, "无", stylecount)
                        else:
                            sheet4.write(3, 1, "无", stylecount)  # 母亲生物样本库

                    if rseult.gen_mut_name:
                        genMutName = json.loads(rseult.gen_mut_name)
                        row_count = 1
                        for genMutNameItem in genMutName:
                            sheet4.write(4, row_count, safe_str(genMutNameItem['genName']), stylecount)  # 致病基因名称
                            sheet4.write(5, row_count, safe_str(genMutNameItem['Rna']), stylecount)  # 核酸异变
                            sheet4.write(6, row_count, safe_str(genMutNameItem['amino']), stylecount)  # 氨基酸异变
                            sheet4.write(7, row_count, safe_str(genMutNameItem['father']), stylecount)  # 父亲
                            sheet4.write(8, row_count, safe_str(genMutNameItem['mother']), stylecount)  # 母亲
                            row_count += 1



                    # 添加第五页数据表
                    sheet5 = ws.add_sheet('诊断')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet5.row(0).height_mismatch = True
                    sheet5.col(0).width = 256 * 20
                    sheet5.col(1).width = 1000 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'主要诊断:', u'次要诊断:']
                    i = 0
                    while i < 2:
                        sheet5.write(i, 0, heads[i], style)
                        first_row = sheet5.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet5.write(0, 1, rseult.main_dia, stylecount)  # 主要诊断
                    sheet5.write(1, 1, rseult.sec_dia, stylecount)  # 次要诊断



                    # 添加第六页数据表
                    sheet6 = ws.add_sheet('随访')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet6.col(0).width = 256 * 20
                    sheet6.col(1).width = 2186 * 20
                    sheet6.col(2).width = 2186 * 20
                    sheet6.col(3).width = 2186 * 20
                    sheet6.col(4).width = 2186 * 20
                    sheet6.col(5).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'随访:', u'甲攻：', u'肝肾脂电解质：', u'诊疗方案 ：']
                    i = 0
                    while i < 4:
                        sheet6.write(i, 0, heads[i], style)
                        first_row = sheet6.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    iFlCount = 1
                    for iFl in follow:
                        # 随访
                        #(男)
                        if patient.sex == '1':
                            iFl_listMan = "随访日期:" + iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S') + "\n" + \
                                       "年龄：" + safe_str(iFl.age) + "\n" + \
                                       "身高：" + safe_str(iFl.Ht) + "(cm)" + "\n" + \
                                       "体重：" + safe_str(iFl.Wt) + "(kg)" + "\n" + \
                                       "外生殖器分期：" + "G" + safe_str(iFl.gen_stag) + "\n" + \
                                       "阴毛分期：" + safe_str(iFl.pub_stag) + "\n" + \
                                       "IGF-1：" + safe_str(iFl.IGF1) + "(ng/ml)" + "\n" + \
                                       "IGFBP3：" + safe_str(iFl.IGFBP3) + "(μg/ml)" + "\n" + \
                                       "空腹血糖：" + safe_str(iFl.fas_blood_glu) + "(mmol/L)" + "\n" + \
                                       "空腹胰岛素：" + safe_str(iFl.fas_insulin) + "(IU/L)" + "\n" + \
                                       "糖化血红蛋白：" + safe_str(iFl.gly_hem) + "\n" + \
                                       "睾丸大小右侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightOne']) + "cm" + "x" + safe_str(json.loads(iFl.gon_B_ult)['testisRightTwo']) + "cm" + "x" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "cm" + "，长颈：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightLon']) + "\n" + \
                                       "睾丸大小左侧:" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftOne']) + "cm" + "x" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "cm" + "x" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftThr']) + "cm" + "，长颈：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftLon']) + "\n" + \
                                       "其他 ：" + safe_str(iFl.other) + "\n"
                            sheet6.write(0, iFlCount, iFl_listMan, stylecount)
                        # （女）
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(iFl.gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                    json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            iFl_listWoman = "随访日期:" + iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S') + "\n" + \
                                       "年龄：" + safe_str(iFl.age) + "\n" + \
                                       "身高：" + safe_str(iFl.Ht) + "(cm)" + "\n" + \
                                       "体重：" + safe_str(iFl.Wt) + "(kg)" + "\n" + \
                                       "双乳发育分期：" + "G" + safe_str(iFl.gen_stag) + "\n" + \
                                       "阴毛分期：" + safe_str(iFl.pub_stag) + "\n" + \
                                       "IGF-1：" + safe_str(iFl.IGF1) + "(ng/ml)" + "\n" + \
                                       "IGFBP3：" + safe_str(iFl.IGFBP3) + "(μg/ml)" + "\n" + \
                                       "空腹血糖：" + safe_str(iFl.fas_blood_glu) + "(mmol/L)" + "\n" + \
                                       "空腹胰岛素：" + safe_str(iFl.fas_insulin) + "(IU/L)" + "\n" + \
                                       "糖化血红蛋白：" + safe_str(iFl.gly_hem) + "\n" + \
                                        "子宫大小" + safe_str(json.loads(iFl.gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                        "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                        "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                        "最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                        "有无囊肿：" + cyst_info + "\n" + \
                                        "其他 ：" + safe_str(iFl.other) + "\n"
                            sheet6.write(0, iFlCount, iFl_listWoman, stylecount)
                        else:
                            sheet6.write(0, iFlCount, "未填写", stylecount)
                        # 甲攻
                        if iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '2':
                            sheet6.write(1, iFlCount,  "异常," + safe_str(json.loads(iFl.Jiagong)['JiagongDes']) + "(ng/dL)", stylecount)
                        elif  iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '1':
                            sheet6.write(1, iFlCount, "正常",stylecount)
                        else:
                            sheet6.write(1, iFlCount, "未选择", stylecount)
                        # 肝肾脂电解质
                        if iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '2':
                            sheet6.write(2, iFlCount, "异常," + safe_str(json.loads(iFl.liv_kid_lip)['LAKLEdes']), stylecount)
                        elif  iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '1':
                            sheet6.write(2, iFlCount,"正常", stylecount)
                        else:
                            sheet6.write(2, iFlCount, "未选择", stylecount)
                        # 诊疗方案
                        if rseult.dia_trea_plan and iFl.dia_trea_plan!= "无":
                            # 治疗1
                            if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                sheet6.write(3, iFlCount, "未治疗", stylecount)
                            # 治疗2
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    sheet6.write(3, iFlCount, "rhGH治疗，短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（U/kg.d）", stylecount)
                                else:
                                    sheet6.write(3, iFlCount,"rhGH治疗，长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（mg/kg.w，每周1次）", stylecount)
                            # 治疗3
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                    sheet6.write(3, iFlCount, "GnRHa治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗4
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                #达菲林针
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)",stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)",stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次", stylecount)
                                #达必佳针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 短效rhGH，" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次", stylecount)
                                #抑那通针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次", stylecount)
                                #抑那通针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 短效rhGH，" +json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 长效生长激素（PEG-rhGH），" +json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次", stylecount)
                                #伯恩若康针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 短效rhGH，" + json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in  json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                #贝依针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 短效rhGH，" + json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(3, iFlCount,"GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + json.loads(iFl.dia_trea_plan)['rhUnitedDose'] + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(3, iFlCount, "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次", stylecount)
                            # 治疗5
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                sheet6.write(3, iFlCount, "芳香化酶抑制剂", stylecount)
                            # 治疗6
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                promptMap = {
                                    '1':'短效rhGH',
                                    '2': '长效生长激素（PEG-rhGH）',
                                }
                                if 'rhCustomizationDiaPlan' in json.loads(iFl.dia_trea_plan):
                                    rhCustomizationDiaPlan = json.loads(iFl.dia_trea_plan)['rhCustomizationDiaPlan']
                                    finalrhCustomizationDiaPlan = promptMap.get(rhCustomizationDiaPlan)
                                    sheet6.write(3, iFlCount, "停止GnRHa治疗，" +safe_str(finalrhCustomizationDiaPlan)  + "，" + safe_str(json.loads(iFl.dia_trea_plan)['rhCustomizationPrompt']) + "(mg/kg.w，每周1次)", stylecount)
                            # 治疗7
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                sheet6.write(3, iFlCount, "停止GnRHal联合生长激素治疗", stylecount)
                            # 治疗8
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                sheet6.write(3, iFlCount, "停止生长激素治疗", stylecount)
                            # 治疗9
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '9':
                                sheet6.write(3, iFlCount, "中医药治疗", stylecount)
                            else:
                                sheet6.write(3, iFlCount, "未选择", stylecount)




                        # sheet6.write(3, iFlCount, iFl_list, stylecount)
                        iFlCount += 1
                elif patient.dis_class == '10000003':
                    file_type = 'cpp'
                    # 添加第二页数据表
                    sheet2 = ws.add_sheet('一般信息')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet2.col(0).width = 350 * 20
                    sheet2.col(1).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'父亲身高:', u'父亲体重:', u'母亲身高:', u' 母亲体重:', u'初潮年龄:', u'兄弟姐妹:',
                             u'既往史:']
                    i = 0
                    while i < 7:
                        sheet2.write(i, 0, heads[i], style)
                        first_row = sheet2.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet2.write(0, 1, safe_str(json.loads(rseult.fam_his)['fHeight']) + "(cm)", stylecount)  # 父亲身高
                    sheet2.write(1, 1, safe_str(json.loads(rseult.fam_his)['fWeight']) + "(kg)", stylecount)  # 父亲体重
                    sheet2.write(2, 1, safe_str(json.loads(rseult.fam_his)['mHeight']) + "(cm)", stylecount)  # 母亲身高
                    sheet2.write(3, 1, safe_str(json.loads(rseult.fam_his)['mWeight']) + "(kg)", stylecount)  # 母亲体重
                    sheet2.write(4, 1, safe_str(json.loads(rseult.fam_his)['firstAge']) + "(岁)", stylecount)  # 初潮年龄
                    if rseult.fam_his:
                        # 是否有兄弟姐妹
                        if 'bro' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['bro'] == '2':
                            # 有
                            if 'familyData' in json.loads(rseult.fam_his.replace('\n', '')) and len(json.loads(rseult.fam_his.replace('\n', ''))['familyData']):
                                family = json.loads(rseult.fam_his.replace('\n', ''))['familyData']
                                # 兄弟姐妹
                                familyList = "性别：" + safe_str(family[0]['sex']) + "\n" + \
                                             "年龄：" + safe_str(family[0]['age']) + "\n" + \
                                             "身高：" + safe_str(family[0]['height']) + "\n" + \
                                             "体重：" + safe_str(family[0]['weight']) + "\n" + \
                                             "有无性早熟：：" + safe_str(family[0]['health']) + "\n"
                                sheet2.write(5, 1, familyList, stylecount)
                            # 既往史
                            if 'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '2':
                                # 有
                                sheet2.write(6, 1, json.loads(rseult.fam_his.replace('\n', ''))['isHis'], stylecount)
                            elif  'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '1':
                                # 无
                                sheet2.write(6, 1, "健康", stylecount)
                            else:
                                sheet2.write(6, 1, "未选择", stylecount)
                        else:
                            #无
                            sheet2.write(5, 1, "无", stylecount)  # 兄弟姐妹
                            # 既往史
                            if 'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '2':
                                # 有
                                sheet2.write(6, 1, json.loads(rseult.fam_his.replace('\n', ''))['isHis'], stylecount)
                            elif  'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '1':
                                sheet2.write(6, 1, "健康", stylecount)
                            else:
                                # 无
                                sheet2.write(6, 1, "未选择", stylecount)

                    # 添加第三页数据表
                    sheet3 = ws.add_sheet('临床资料')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet3.col(0).width = 350 * 20
                    sheet3.col(1).width = 768 * 20
                    sheet3.col(2).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'初次就诊时间:', u'初诊年龄:', u'主诉:', u'生长速率:', u'月经初潮:', u'身高:', u'体重:', u'外生殖器分期/双乳发育分期',
                             u'阴毛分期:', u'LH:', u'FSH:', u'E2:', u'T:', u'SHBG:',
                             u'PRL:', u' IGF-1:', u'IGFBP-3:', u'甲功:', u'ACTH（8am）:',
                             u' 皮质醇（8am）:', u'DHEAs:', u'17-OHP:', u'肝肾脂糖电解质:', u'心电图:', u'性腺B超', u'垂体MRI:',
                             u'LH峰值:', u'FSH峰值:', u' LH峰值/FSH峰值:', u'诊疗方案:', u'左侧甲状腺b超:', u'右侧甲状腺b超:']
                    i = 0
                    while i < 32:
                        sheet3.write(i, 0, heads[i], style)
                        first_row = sheet3.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet3.write(0, 1, rseult.first_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 初次就诊时间
                    sheet3.write(1, 1, safe_str(rseult.age_ons) + "（岁）", stylecount)  # 初诊年龄
                    sheet3.write(2, 1, rseult.chi_com, stylecount)  # 主诉
                    # 生长速率
                    if json.loads(rseult.acc_growth) == 2 or json.loads(rseult.acc_growth)['growRate'] == '2':
                        sheet3.write(3, 1, safe_str(json.loads(rseult.acc_growth)['rate']) + "（厘米/年）", stylecount)
                    elif  json.loads(rseult.acc_growth) == 1 or json.loads(rseult.acc_growth)['growRate'] == '1':
                        sheet3.write(3, 1, "不详", stylecount)
                    else:
                        sheet3.write(3, 1, "未选择", stylecount)
                    # 月经初潮
                    if 'menarchy' in json.loads(rseult.menarche) and json.loads(rseult.menarche)['menarchy'] == '2':
                        sheet3.write(4, 1, "月经初潮：有" + "\n" + "初潮时间：" + safe_str(json.loads(rseult.menarche)['menarchyTime']), stylecount)
                    elif  'menarchy' in json.loads(rseult.menarche) and json.loads(rseult.menarche)['menarchy'] == '1':
                        sheet3.write(4, 1, "无", stylecount)
                    else:
                        sheet3.write(4, 1, "未选择", stylecount)

                    sheet3.write(5, 1, safe_str(json.loads(rseult.phy_exa)['height'])+"（cm）", stylecount)  # 身高
                    sheet3.write(6, 1, safe_str(json.loads(rseult.phy_exa)['weight'])+"（kg）", stylecount)  # 体重
                    # 外生殖器分期
                    if patient.sex == '1':
                        if json.loads(rseult.phy_exa)['exGenitalia'] is not None and len(json.loads(rseult.phy_exa)['exGenitalia']) > 0:
                            sheet3.write(7, 1, "外生殖器分期(男):G" + safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                        else:
                            sheet3.write(7, 1, "未选择", stylecount)
                    elif patient.sex == '2':
                        if json.loads(rseult.phy_exa)['breastDev'] is not None and len(json.loads(rseult.phy_exa)['breastDev']) > 0:
                            sheet3.write(7, 1, "双乳发育分期（女）B" + safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                        else:
                            sheet3.write(7, 1, "未选择", stylecount)
                    else:
                        sheet3.write(7, 1, "未选择", stylecount)
                    sheet3.write(8, 1, safe_str(json.loads(rseult.phy_exa)['pubicHair']), stylecount)  # 阴毛分期
                    # LH
                    if 'LH' in rseult.lab_exa or json.loads(rseult.lab_exa)['LH']:
                        repLabExa = rseult.lab_exa.replace("'",'"')
                        finaRepLabExa = json.loads(repLabExa)
                        sheet3.write(9, 1, safe_str(finaRepLabExa['LH']) + "（mIU/mL）", stylecount)
                    sheet3.write(10, 1, safe_str(json.loads(rseult.lab_exa)['FSH']) + "（mIU/mL）", stylecount)  # FSH
                    sheet3.write(11, 1, safe_str(json.loads(rseult.lab_exa)['E2']) + "（pg/mL）", stylecount)  # E2
                    sheet3.write(12, 1, safe_str(json.loads(rseult.lab_exa)['T']) + "（ng/dL）", stylecount)  # T
                    sheet3.write(13, 1, safe_str(json.loads(rseult.lab_exa)['HSBG']) + "（nmol/L）", stylecount)  # SHBG
                    sheet3.write(14, 1, safe_str(json.loads(rseult.lab_exa)['PRL']) + "（ng/mL）", stylecount)  # PRL（无）
                    sheet3.write(15, 1, safe_str(json.loads(rseult.lab_exa)['IGF']) + "（ng/mL）", stylecount)  # IGF-1
                    sheet3.write(16, 1, safe_str(json.loads(rseult.lab_exa)['IGFBP3']) + "（ug/mL）",stylecount)  # IGFBP-3
                    # 甲功
                    if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                        sheet3.write(17, 1, "异常信息:" + safe_str(json.loads(rseult.lab_exa)['thyroid']), stylecount)
                    elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                        sheet3.write(17, 1, "正常", stylecount)
                    else:
                        sheet3.write(17, 1, "未选择", stylecount)
                    sheet3.write(18, 1, safe_str(json.loads(rseult.lab_exa)['ACTH'])+"（pg/mL）", stylecount)  # ACTH
                    sheet3.write(19, 1, safe_str(json.loads(rseult.lab_exa)['cortisol'])+"（ug/dL）", stylecount)  #  皮质醇
                    sheet3.write(20, 1, safe_str(json.loads(rseult.lab_exa)['DHEAS'])+"（ug/dL）", stylecount)  #  DHEAs
                    sheet3.write(21, 1, safe_str(json.loads(rseult.lab_exa)['OHP'])+"（nmol/L）", stylecount)  # 17-OHP
                    # 肝肾脂糖电解质
                    if 'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                        sheet3.write(22, 1, "异常信息：" +safe_str(json.loads(rseult.lab_exa)['laklgeDescribe']), stylecount)
                    elif  'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                        sheet3.write(22, 1, "正常", stylecount)
                    else:
                        sheet3.write(22, 1, "未选择", stylecount)
                    sheet3.write(23, 1, rseult.electr, stylecount)  # 心电图
                    gon_B_ult = rseult.gon_B_ult.replace("\n", "")
                    # 性腺B超
                    if patient.sex == '1':
                        gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightThr']) + "cm，长径：" +  safe_str(json.loads(gon_B_ult)['testisRightLon']) + "(cm)" +  "\n" + \
                                          "睾丸大小-左侧：" + safe_str(json.loads(gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"

                        sheet3.write(24, 1, gonadUltrasoundMan, stylecount)
                    elif patient.sex == '2':
                        # 判断随访囊肿(是否存在存在)
                        if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                            cyst_info = "有，" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                        elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                            cyst_info = "无"
                        else:
                            cyst_info = "未选择"
                        gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(gon_B_ult)['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                               "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                               "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                               "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                               "有无囊肿：" + cyst_info
                        sheet3.write(24, 1, gonadUltrasoundWoman, stylecount)
                    else:
                        sheet3.write(24, 1, "未填写", stylecount)
                    # 垂体MRI
                    if 'MRI' in json.loads(rseult.gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '2':
                        sheet3.write(25, 1, "异常信息：" + safe_str(json.loads(gon_B_ult)['mriDescribe']), stylecount)
                    elif  'MRI' in json.loads(rseult.gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '1':
                        sheet3.write(25, 1, "正常", stylecount)
                    else:
                        sheet3.write(25, 1, "未选择", stylecount)
                    sheet3.write(26, 1, safe_str(rseult.LFmax)+"（mIU/ml）", stylecount) #LH峰值
                    sheet3.write(27, 1, safe_str(rseult.FSHmax)+"（mIU/ml） ", stylecount) #FSH峰值
                    sheet3.write(28, 1, rseult.LHmax, stylecount)  #  LH峰值/FSH峰值
                    # 诊疗方案(多种选择)
                    if rseult.dia_trea_plan:
                        #治疗1
                        if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                            sheet3.write(29, 1, "未治疗", stylecount)
                        #治疗2
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                            if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                sheet3.write(29, 1, "rhGH治疗，短效rhGH" + safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])+"（U/kg.d）", stylecount)
                            else:
                                sheet3.write(29, 1, "rhGH治疗，长效生长激素（PEG-rhGH）" + safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])+"（mg/kg.w，每周1次）", stylecount)
                        #治疗3
                        elif  'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                            if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                sheet3.write(29, 1, "GnRHa治疗，达菲林针3.75mg，每28天1次", stylecount)
                            elif  'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                sheet3.write(29, 1, "GnRHa治疗，达必佳针3.75mg，每28天1次", stylecount)
                            elif  'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                sheet3.write(29, 1, "GnRHa治疗，抑那通针3.75mg，每28天1次", stylecount)
                            elif  'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                sheet3.write(29, 1, "GnRHa治疗，抑那通针11.25mg，每3月1次", stylecount)
                            elif  'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                sheet3.write(29, 1, "GnRHa治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                            elif  'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                sheet3.write(29, 1, "GnRHa治疗，贝依针针3.75mg，每28天1次", stylecount)
                        #治疗4
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                            if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次", stylecount)
                            elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次", stylecount)
                            elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次", stylecount)
                            elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次", stylecount)
                            elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                            elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                sheet3.write(29, 1, "GnRHal联合生长激素治疗，贝依针针3.75mg，每28天1次", stylecount)
                        #治疗5
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                            sheet3.write(29, 1, "芳香化酶抑制剂", stylecount)
                        #治疗6
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                            sheet3.write(29, 1, "停止GnRHa治疗", stylecount)
                        #治疗7
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                            sheet3.write(29, 1, "停止GnRHal联合生长激素治疗", stylecount)
                        #治疗8
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                            sheet3.write(29, 1, "停止生长激素治疗", stylecount)
                        #治疗9
                        elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '9':
                            sheet3.write(29, 1, "中医药治疗", stylecount)
                        else:
                            sheet3.write(29, 1, "未选择", stylecount)
                    # 左侧甲状腺b超
                    if 'CThyroidLB' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLB'] == '2':
                        sheet3.write(30, 1, "甲状腺结节分级:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLBGradation']) + "\n" + "大小:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLBLesions']) + "\n" + "其他：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLBOther']), stylecount)
                    elif  'CThyroidLB' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidLB'] == '1':
                        sheet3.write(30, 1, "正常", stylecount)
                    else:
                        sheet3.write(30, 1, "未选择", stylecount)
                    # 右侧甲状腺b超
                    if 'CThyroidRB' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRB'] == '2':
                        sheet3.write(31, 1, "甲状腺结节分级:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRBGradation']) + "\n" + "大小:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRBLesions']) + "\n" + "其他：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRBOther']), stylecount)
                    elif  'CThyroidRB' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['CThyroidRB'] == '1':
                        sheet3.write(31, 1, "正常", stylecount)
                    else:
                        sheet3.write(31, 1, "未选择", stylecount)


                    # 添加第四页数据表
                    sheet4 = ws.add_sheet('遗传学检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet4.col(0).width = 350 * 20
                    sheet4.col(1).width = 500 * 20
                    sheet4.col(2).width = 500 * 20
                    sheet4.col(3).width = 500 * 20
                    sheet4.col(4).width = 500 * 20
                    sheet4.col(5).width = 500 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入数据
                    # 写入表头
                    heads = [u'生物样本库:', u'染色体核型:', u'致病基因名称:', u'核酸异变:', u'氨基酸异变:', u'父亲:', u'母亲:']
                    i = 0
                    while i < 7:
                        sheet4.write(i, 0, heads[i], style)
                        first_row = sheet4.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 生物样本库
                    if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)['bioBank'] == '2':
                        sampleList = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                        if len(sampleList) > 2:
                            Data = json.loads(sampleList)
                            listCount = 1
                            for item in Data:
                                map = {
                                    '1': 'DNA样本',
                                    '2': '血清',
                                    '3': '血浆',
                                    '4': '尿液',
                                }
                                finalname = map.get(item['name'])
                                sheet4.write(0, listCount,"样本编号:" + safe_str(item['id']) + "\n" + "样本类型:" + safe_str(finalname), stylecount)
                                listCount += 1
                        else:
                            sheet4.write(0, 1, "无", stylecount)
                    else:
                        sheet4.write(0, 1, "无", stylecount)
                    # 染色体核型
                    sheet4.write(1, 1, rseult.spe_kar, stylecount)
                    getMutName = rseult.gen_mut_name.replace("'", '"')
                    getMutNameData = json.loads(getMutName)
                    MutNameCount = 1
                    for MutNameitem in  getMutNameData:
                        sheet4.write(2, MutNameCount, MutNameitem['genName'], stylecount)  # 致病基因名称
                        sheet4.write(3, MutNameCount, MutNameitem['Rna'], stylecount)  # 核酸异变
                        sheet4.write(4, MutNameCount, MutNameitem['amino'], stylecount)  # 氨基酸异变
                        sheet4.write(5, MutNameCount, MutNameitem['father'], stylecount)  # 父亲
                        sheet4.write(6, MutNameCount, MutNameitem['mother'], stylecount)  # 母亲
                        MutNameCount += 1


                    # 添加第五页数据表
                    sheet5 = ws.add_sheet('诊断')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet5.col(0).width = 256 * 20
                    sheet5.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'主要诊断:', u'次要诊断:']
                    i = 0
                    while i < 2:
                        sheet5.write(i, 0, heads[i], style)
                        first_row = sheet5.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet5.write(0, 1, rseult.main_dia, stylecount)  # 主要诊断
                    sheet5.write(1, 1, rseult.sec_dia, stylecount)  # 次要诊断



                    # 添加第六页数据表
                    sheet6 = ws.add_sheet('随访')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet6.col(0).width = 350 * 20
                    sheet6.col(1).width = 768 * 20
                    sheet6.col(2).width = 768 * 20
                    sheet6.col(3).width = 768 * 20
                    sheet6.col(4).width = 768 * 20
                    sheet6.col(5).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'随访日期:', u'年龄:', u'身高:', u'体重:', u'外生殖器分期/双乳发育分期:', u'阴毛分期:',
                             u'LH值：',u'FSH值：',u'E2：',u'T：', u'性腺B超:',
                             u'诊疗方案:', u'其他:']
                    i = 0
                    while i < 13:
                        sheet6.write(i, 0, heads[i], style)
                        first_row = sheet6.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    iFlCount = 1
                    for iFl in follow:
                        sheet6.write(0, iFlCount, iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 随访日期
                        sheet6.write(1, iFlCount, safe_str(iFl.age), stylecount)  # 年龄
                        sheet6.write(2, iFlCount, safe_str(iFl.Ht), stylecount)  # 身高
                        sheet6.write(3, iFlCount, safe_str(iFl.Wt), stylecount)  # 体重
                        # 外生殖器分期
                        if patient.sex == '1':
                            sheet6.write(4, iFlCount, "外生殖器分期： G" +safe_str(iFl.gen_stag), stylecount)
                        elif patient.sex == '2':
                            sheet6.write(4, iFlCount, "双乳发育分期： B" +safe_str(iFl.gen_stag), stylecount)
                        else:
                            sheet6.write(4, iFlCount, "未填写", stylecount)
                        sheet6.write(5, iFlCount, safe_str(iFl.pub_stag), stylecount)  # 阴毛分期
                        sheet6.write(6, iFlCount, safe_str(iFl.LH), stylecount)  # LH值
                        sheet6.write(7, iFlCount, safe_str(iFl.FSH), stylecount)  # FSH值
                        sheet6.write(8, iFlCount, safe_str(iFl.E2), stylecount)  # E2
                        sheet6.write(9, iFlCount, safe_str(iFl.T), stylecount)  # T

                        if patient.sex == '1':
                            gonadUltrasoundMan = "睾丸大小-右侧:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisRightOne']) + "cm" + "x" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisRightTwo']) + "cm" + "x" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisRightThr']) + "cm" + ",长径:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisRightLon']) + "（cm）" + "\n" + \
                                                 "睾丸大小-左侧:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisLeftOne']) + "cm" + "x" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisLeftTwo']) + "cm" + "x" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisLeftThr']) + "cm" + ",长径:" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['testisLeftLon']) + "（cm）"
                            sheet6.write(10, iFlCount, gonadUltrasoundMan,stylecount)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            gonadUltrasoundWomans = "子宫大小" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['uterusTwo']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['intima']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaLeftTwo']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaLeftThr']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['ovaRightThr']) + "(cm)" + "\n" + \
                                                   "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult.replace("\n", ""))['follDiameter']) + "(cm)" + "\n" + \
                                                   "有无囊肿：" + cyst_info
                            sheet6.write(10, iFlCount, gonadUltrasoundWomans, stylecount)
                        else:
                            sheet6.write(10, iFlCount,"未填写", stylecount)
                        # 诊疗方案
                        if rseult.dia_trea_plan and iFl.dia_trea_plan!= "无":
                            # 治疗1
                            if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                sheet6.write(11, iFlCount, "未治疗", stylecount)
                            # 治疗2
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    sheet6.write(11, iFlCount, "rhGH治疗，短效rhGH" + safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（U/kg.d）", stylecount)
                                else:
                                    sheet6.write(11, iFlCount,"rhGH治疗，长效生长激素（PEG-rhGH）" + safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（mg/kg.w，每周1次）", stylecount)
                            # 治疗3
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                    sheet6.write(11, iFlCount, "GnRHa治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗4
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                #达菲林针
                                if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)",stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)",stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次", stylecount)
                                #达必佳针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 短效rhGH，" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次", stylecount)
                                #抑那通针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次", stylecount)
                                #抑那通针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 短效rhGH，" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 长效生长激素（PEG-rhGH），" +safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次", stylecount)
                                #伯恩若康针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                #贝依针
                                elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                    if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                        sheet6.write(11, iFlCount,"GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']) + "(mg/kg.w，每周1次)", stylecount)
                                    else:
                                        sheet6.write(11, iFlCount, "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次", stylecount)
                            # 治疗5
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                sheet6.write(11, iFlCount, "芳香化酶抑制剂", stylecount)
                            # 治疗6
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                sheet6.write(11, iFlCount, "停止GnRHa治疗", stylecount)
                            # 治疗7
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                sheet6.write(11, iFlCount, "停止GnRHal联合生长激素治疗", stylecount)
                            # 治疗8
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                sheet6.write(11, iFlCount, "停止生长激素治疗", stylecount)
                            # 治疗9
                            elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['diaPlan'] == '9':
                                sheet6.write(11, iFlCount, "中医药治疗", stylecount)
                            else:
                                sheet6.write(11, iFlCount, "未选择", stylecount)
                        sheet6.write(12, iFlCount, iFl.other, stylecount)#其他
                        iFlCount += 1
                elif patient.dis_class == '10000004':
                    file_type = 'mas'
                    # 添加第二页数据表
                    sheet2 = ws.add_sheet('家族史')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet2.col(0).width = 350 * 20
                    sheet2.col(1).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'父亲身高:', u'父亲体重:', u'母亲身高:', u' 母亲体重:', u'糖尿病家族史:', u'甲状腺疾病家族史:',
                             u'肿瘤家族史:', u'其他疾病家族史:']
                    i = 0
                    while i < 8:
                        sheet2.write(i, 0, heads[i], style)
                        first_row = sheet2.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet2.write(0, 1, safe_str(json.loads(rseult.fam_his)['faHeight']) + "（cm）", stylecount)  # 父亲身高
                    sheet2.write(1, 1, safe_str(json.loads(rseult.fam_his)['faWeight']) + "（kg）", stylecount)  # 父亲体重
                    sheet2.write(2, 1, safe_str(json.loads(rseult.fam_his)['moHeight']) + "（cm）", stylecount)  # 母亲身高
                    sheet2.write(3, 1, safe_str(json.loads(rseult.fam_his)['moWeight']) + "（kg）", stylecount)  # 母亲体重
                    # 糖尿病家族史
                    if 'isDiabetesFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isDiabetesFamily'] == '有':
                        sheet2.write(4, 1, "有，描述：" + safe_str(json.loads(rseult.fam_his)['DiabetesDescription']), stylecount)
                    elif  'isDiabetesFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isDiabetesFamily'] == '无':
                        sheet2.write(4, 1, "无", stylecount)
                    else:
                        sheet2.write(4, 1, "未选择", stylecount)
                    # 甲状腺疾病家族史
                    if 'isThyroidFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isThyroidFamily'] == '有':
                        sheet2.write(5, 1, "有，描述：" + safe_str(json.loads(rseult.fam_his)['ThyroidDescription']), stylecount)
                    elif  'isThyroidFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isThyroidFamily'] == '无':
                        sheet2.write(5, 1, "无", stylecount)
                    else:
                        sheet2.write(5, 1, "未选择", stylecount)
                    # 肿瘤家族史
                    if 'isTumorFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isTumorFamily'] == '有':
                        sheet2.write(6, 1,  "有，描述：" +safe_str(json.loads(rseult.fam_his)['TumorDescription']), stylecount)
                    elif  'isTumorFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isTumorFamily'] == '无':
                        sheet2.write(6, 1, "无", stylecount)
                    else:
                        sheet2.write(6, 1, "未选择", stylecount)
                    # 其他疾病家族史
                    if 'isOtherFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isOtherFamily'] == '有':
                        sheet2.write(7, 1, "有，描述：" + safe_str(json.loads(rseult.fam_his)['OtherDiseaseDescriptions']), stylecount)
                    elif  'isOtherFamily' in json.loads(rseult.fam_his) and json.loads(rseult.fam_his)['isOtherFamily'] == '无':
                        sheet2.write(7, 1, "无", stylecount)
                    else:
                        sheet2.write(7, 1, "未选择", stylecount)

                    # 添加第三页数据表
                    sheet3 = ws.add_sheet('体格检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet3.col(0).width = 350 * 20
                    sheet3.col(1).width = 768 * 20
                    sheet3.col(2).width = 350 * 20
                    sheet3.col(3).width = 350 * 20
                    sheet3.col(4).width = 350 * 20
                    sheet3.col(5).width = 350 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'检查日期:', u'身高:', u'身高增长速度:', u'体重:', u'收缩压:', u'舒张压:', u'心率:',
                             u'睾丸发育分期/乳腺发育分期：', u'甲状腺肿大:', u'皮肤检查:', u'骨骼检查:']
                    i = 0
                    while i < 11:
                        sheet3.write(i, 0, heads[i], style)
                        first_row = sheet3.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet3.write(0, 1, rseult.check_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 检查日期
                    sheet3.write(1, 1, safe_str(json.loads(rseult.gen_sit)['height']) + "(cm)", stylecount)  # 身高
                    sheet3.write(2, 1, safe_str(json.loads(rseult.gen_sit)['heightRate']) + "(cm/year)", stylecount)  # 身高增长速度
                    sheet3.write(3, 1, safe_str(json.loads(rseult.gen_sit)['weight']) + "(kg)", stylecount)  # 体重
                    sheet3.write(4, 1, safe_str(json.loads(rseult.gen_sit)['systolic']) + "(mmHg)", stylecount)  # 收缩压
                    sheet3.write(5, 1, safe_str(json.loads(rseult.gen_sit)['diastolic']) + "(mmHg)", stylecount)  #  舒张压
                    sheet3.write(6, 1, safe_str(json.loads(rseult.gen_sit)['heartRate']) + "(次/分)", stylecount)  # 心率 (无)
                    # 男孩
                    if patient.sex == '1':
                        isHaveMan = {
                            '1':'有',
                            '2':'无'
                        }
                        if isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['penileGrowth']) is not None:
                            appleProtrusion = isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['appleProtrusion'])
                        else:
                            appleProtrusion = "空值"
                        if  isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['breastEnlarg']) is not None:
                            breastEnlarg = isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['breastEnlarg'])
                        else:
                            breastEnlarg = "空值"
                        if  isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['penileGrowth']) is not None:
                            penileGrowth = isHaveMan.get(json.loads(rseult.boy_sta_dev.replace("'",'"'))['penileGrowth'])
                        else:
                            penileGrowth = "空值"
                        testis = "左侧睾丸发育分期：G" + json.loads(rseult.boy_sta_dev.replace("'",'"'))['leftTesticleDev'] + "期   右侧睾丸发育分期： G" + json.loads(rseult.boy_sta_dev.replace("'",'"'))['rightTesticleDev'] + "期" + "\n" + \
                                 "男孩性征发育情况-喉结凸起:  " + appleProtrusion + "\n" +  "男孩性征发育情况-乳房增大:  " + breastEnlarg +"\n" + "男孩性征发育情况-阴茎增长:  " + penileGrowth + "\n"
                        # 睾丸发育分期
                        sheet3.write(7, 1, testis, stylecount)

                    # 女孩
                    elif patient.sex == '2':
                        isHaveWoman = {
                            '1': '有',
                            '2': '无'
                        }
                        if isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['breastTend']) is not None:
                            breastTend = isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['breastTend'])
                        else:
                            breastTend = "空值"
                        if isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['clitoralHypertrophy']) is not None:
                            clitoralHypertrophy = isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['clitoralHypertrophy'])
                        else:
                            clitoralHypertrophy = "空值"
                        if isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['labialColoration']) is not None:
                            labialColoration = isHaveWoman.get(json.loads(rseult.girl_sta_dev.replace("'",'"'))['labialColoration'])
                        else:
                            labialColoration = "空值"
                        breast = "左侧乳腺发育分期: B" + json.loads(rseult.girl_sta_dev.replace("'",'"'))['leftBreastDev'] + "期   右侧乳腺发育分期: B" + json.loads(rseult.girl_sta_dev.replace("'",'"'))['rightBreastDev'] + "期" + "\n" + \
                                 "外阴阴毛分期: P"+ json.loads(rseult.girl_sta_dev.replace("'",'"'))['pubicHair'] + "期" + "\n" + \
                                 "女孩性征发育情况-乳房触痛: " + breastTend + "\n" + "女孩性征发育情况-阴蒂肥大: "+ clitoralHypertrophy + "\n" + "女孩性征发育情况-阴唇着色: " + labialColoration
                        # 乳腺发育分期
                        sheet3.write(7, 1, breast, stylecount)
                    else:
                        sheet3.write(7, 1, "未填写", stylecount)
                    # 甲状腺肿大
                    goiterMap = {
                        '1':'无肿大',
                        '2': '肿大I度',
                        '3': '肿大II度',
                        '4': '肿大III度',
                    }
                    goiter = rseult.goiter
                    finalGoiter =  goiterMap.get(goiter)
                    if rseult.goiter:
                        sheet3.write(8, 1, finalGoiter, stylecount)
                    else:
                        sheet3.write(8, 1, "未选择", stylecount)
                    # 皮肤检查
                    if rseult.skin_exam:
                        sKinExaminationList = json.loads(rseult.skin_exam)['skinExamination']
                        if sKinExaminationList is not None and len(sKinExaminationList) > 0:
                            sKinExaminationStr = ', '.join(sKinExaminationList)
                            # 选择项
                            sheet3.write(9, 1, "选择项有:" + safe_str(sKinExaminationStr) + "\n", stylecount)
                            # 包含项
                            cafeMilkPointList = '牛奶咖啡斑' in json.loads(rseult.skin_exam)['skinExamination']
                            if cafeMilkPointList:
                                sheet3.write(9, 2, "【牛奶咖啡斑】值:" + safe_str(json.loads(rseult.skin_exam)['cafeMilkPoint']), stylecount)
                        else:
                            sheet3.write(9, 1, "未作任何选择", stylecount)

                    # 骨骼检查
                    if rseult.ske_sur:
                        boneExaminationList = json.loads(rseult.ske_sur)['boneExamination']
                        if boneExaminationList is not None and len(boneExaminationList) > 0:
                            boneExaminationList_Str = ', '.join(boneExaminationList)
                            # 选择项
                            sheet3.write(10, 1, "选择项有：" + safe_str(boneExaminationList_Str) + "\n", stylecount)
                            # 包含项
                            boneSwellingList = "骨膨胀或凸起" in json.loads(rseult.ske_sur)['boneExamination']
                            jointDeformityList = "关节畸形" in json.loads(rseult.ske_sur)['boneExamination']
                            jointPainList = "关节疼痛" in json.loads(rseult.ske_sur)['boneExamination']
                            bonePainList = "骨痛" in json.loads(rseult.ske_sur)['boneExamination']
                            # 骨膨胀或凸起
                            if boneSwellingList:
                                sheet3.write(10, 2, "【骨膨胀或凸起】值:" + safe_str(json.loads(rseult.ske_sur)['boneSwelling']), stylecount)
                            # 关节畸形
                            if jointDeformityList:
                                sheet3.write(10, 3, "【关节畸形】值:" + safe_str(json.loads(rseult.ske_sur)['jointDeformity']),stylecount)
                            # 关节疼痛
                            if jointPainList:
                                sheet3.write(10, 4, "【关节疼痛】值:" + safe_str(json.loads(rseult.ske_sur)['jointPain']),stylecount)
                            # 骨痛
                            if bonePainList:
                                sheet3.write(10, 5, "【骨痛】值:" + safe_str(json.loads(rseult.ske_sur)['bonePain']),stylecount)
                        else:
                            sheet3.write(10, 1, "未作任何选择", stylecount)


                    # 添加第四页数据表
                    sheet4 = ws.add_sheet('影像学检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet4.col(0).width = 350 * 20
                    sheet4.col(1).width = 1500 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入数据
                    # 写入表头
                    heads = [u'B超检查日期:', u'子宫/卵巢超声情况', u'甲状腺B超情况:', u'肾上腺B超情况:', u'肾脏B超情况:', u'病变骨骼X线片检查情况:', u'MR:', u'CT:',
                             u'全身骨扫描检查情况:']
                    i = 0
                    while i < 9:
                        sheet4.write(i, 0, heads[i], style)
                        first_row = sheet4.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    sheet4.write(0, 1, rseult.ult_exam_ova_date.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # B超检查日期
                    # 超声情况
                    if patient.sex == '1':
                        # 男
                        sheet4.write(1, 1, "目前处于男生：无", stylecount)
                    elif patient.sex == '2':
                        # 女
                        # 处于有的情况下
                        # 处于无的情况下
                        isHave = {
                            '1':'有',
                            '2':'无'
                        }
                        # 子宫超声情况
                        # 左侧卵巢
                        if isHave.get(json.loads(rseult.ova_ult_con)['zclc']) is not None and 'zclc' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['zclc'] == '1':
                            zclc_Cnt ="长*宽*厚(mm)：" + json.loads(rseult.ova_ult_con)['zclcc'] + "*" + json.loads(rseult.ova_ult_con)['zclck'] + "*" + json.loads(rseult.ova_ult_con)['zclcg']
                        elif  isHave.get(json.loads(rseult.ova_ult_con)['zclc']) is not None and 'zclc' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['zclc'] == '2':
                            zclc = isHave.get(json.loads(rseult.ova_ult_con)['zclc'])
                            zclc_Cnt = zclc
                        else:
                            zclc_Cnt = "未填写"


                        # 左侧卵巢囊肿
                        if  isHave.get(json.loads(rseult.ova_ult_con)['zcnz']) is not None and 'zcnz' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['zcnz'] == '1':
                            zcnz_Cnt ="长*宽*厚(mm)：" + json.loads(rseult.ova_ult_con)['zcnzc'] + "*" + json.loads(rseult.ova_ult_con)['zcnzk'] + "*" + json.loads(rseult.ova_ult_con)['zcnzg']
                        elif  isHave.get(json.loads(rseult.ova_ult_con)['zcnz']) is not None and 'zcnz' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['zcnz'] == '2':
                            zcnz = isHave.get(json.loads(rseult.ova_ult_con)['zcnz'])
                            zcnz_Cnt = zcnz
                        else:
                            zcnz_Cnt = "未填写"


                        # 右侧卵巢
                        if isHave.get(json.loads(rseult.ova_ult_con)['yclc']) is not None and 'yclc' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['yclc'] == '1':
                            yclc_Cnt ="长*宽*厚(mm)：" + json.loads(rseult.ova_ult_con)['yclcc'] + "*" + json.loads(rseult.ova_ult_con)['yclck'] + "*" + json.loads(rseult.ova_ult_con)['yclcg']
                        elif isHave.get(json.loads(rseult.ova_ult_con)['yclc']) is not None and 'yclc' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['yclc'] == '2':
                            yclc = isHave.get(json.loads(rseult.ova_ult_con)['yclc'])
                            yclc_Cnt = yclc
                        else:
                            yclc_Cnt = "未填写"

                        qk = {
                            '1':'有',
                            '2':'无',
                            '3':'未做'
                        }
                        if qk.get(safe_str(rseult.ute_ult_con)):
                            finalQK = qk.get(safe_str(rseult.ute_ult_con))
                        else:
                            finalQK = "未选择"
                        # 右侧卵巢囊肿
                        if isHave.get(json.loads(rseult.ova_ult_con)['ycnz']) is not None and 'ycnz' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['ycnz'] == '1':
                            ycnz_Cnt ="长*宽*厚(mm)：" + json.loads(rseult.ova_ult_con)['ycnzc'] + "*" + json.loads(rseult.ova_ult_con)['ycnzk'] + "*" + json.loads(rseult.ova_ult_con)['ycnzg']
                        elif isHave.get(json.loads(rseult.ova_ult_con)['ycnz']) is not None and 'ycnz' in json.loads(rseult.ova_ult_con) and json.loads(rseult.ova_ult_con)['ycnz'] == '2':
                            ycnz = isHave.get(json.loads(rseult.ova_ult_con)['ycnz'])
                            ycnz_Cnt = ycnz
                        else:
                            ycnz_Cnt = "未填写"
                        ultrasoundSituation =  "子宫超声情况：" + finalQK  + "，子宫情况具体描述: 长*宽*厚(mm):" + safe_str(json.loads(rseult.spe_des_ute_con)['uterusLength']) + "*" + safe_str(json.loads(rseult.spe_des_ute_con)['uterusWidth']) + "*" + safe_str(json.loads(rseult.spe_des_ute_con)['uterineThickness']) + "\n" + \
                                               "卵巢超声情况-左侧卵巢:" + zclc_Cnt + "\n" + \
                                               "卵巢超声情况-左侧卵巢囊肿:"  + zcnz_Cnt + "\n" + \
                                               "卵巢超声情况-右侧卵巢:" + yclc_Cnt + "\n" + \
                                               "卵巢超声情况-右侧卵巢囊肿:" + ycnz_Cnt + "\n"
                        sheet4.write(1, 1, ultrasoundSituation, stylecount)
                    else:
                        sheet4.write(1, 1, "未填写", stylecount)
                    # 甲状腺B超情况
                    if 'thyroidUlt' in json.loads(rseult.thy_ult_con) and json.loads(rseult.thy_ult_con)['thyroidUlt'] == '2':
                        sheet4.write(2, 1, "异常情况描述：" +  safe_str(json.loads(rseult.thy_ult_con)['thyroidUltAbnormal']), stylecount)
                    elif 'thyroidUlt' in json.loads(rseult.thy_ult_con) and json.loads(rseult.thy_ult_con)['thyroidUlt'] == '3':
                        sheet4.write(2, 1, "未做", stylecount)
                    elif  'thyroidUlt' in json.loads(rseult.thy_ult_con) and json.loads(rseult.thy_ult_con)['thyroidUlt'] == '1':
                        sheet4.write(2, 1, "正常", stylecount)
                    else:
                        sheet4.write(2, 1, "未选择", stylecount)

                    # 肾上腺B超情况
                    if 'adrenalUlt' in json.loads(rseult.adr_ult_con) and  json.loads(rseult.adr_ult_con)['adrenalUlt'] == '2':
                        sheet4.write(3, 1, "异常情况描述：" + safe_str(json.loads(rseult.adr_ult_con)['adrenalUltAbnormal']), stylecount)
                    elif 'adrenalUlt' in json.loads(rseult.adr_ult_con) and  json.loads(rseult.adr_ult_con)['adrenalUlt'] == '3':
                        sheet4.write(3, 1, "未做", stylecount)
                    elif  'adrenalUlt' in json.loads(rseult.adr_ult_con) and  json.loads(rseult.adr_ult_con)['adrenalUlt'] == '1':
                        sheet4.write(3, 1, "正常", stylecount)
                    else:
                        sheet4.write(3, 1, "未选择", stylecount)

                    # 肾脏B超情况
                    if 'renalUlt' in json.loads(rseult.ren_ult_con) and json.loads(rseult.ren_ult_con)['renalUlt'] == '2':
                        sheet4.write(4, 1, "异常情况描述：" +  safe_str(json.loads(rseult.ren_ult_con)['renalUltAbnormal']), stylecount)
                    elif 'renalUlt' in  json.loads(rseult.ren_ult_con) and json.loads(rseult.ren_ult_con)['renalUlt'] == '3':
                        sheet4.write(4, 1, "未做", stylecount)
                    elif  'renalUlt' in  json.loads(rseult.ren_ult_con) and json.loads(rseult.ren_ult_con)['renalUlt'] == '1':
                        sheet4.write(4, 1, "正常", stylecount)
                    else:
                        sheet4.write(4, 1, "未选择", stylecount)

                    # 病变骨骼X线片检查情况
                    if 'boneX' in json.loads(rseult.X_exa_dis) and json.loads(rseult.X_exa_dis)['boneX'] == '2':
                        sheet4.write(5, 1, "异常情况描述：" +  safe_str(json.loads(rseult.X_exa_dis)['boneXAbnormal']), stylecount)
                    elif 'boneX' in  json.loads(rseult.X_exa_dis) and json.loads(rseult.X_exa_dis)['boneX'] == '3':
                        sheet4.write(5, 1, "未做", stylecount)
                    elif  'boneX' in  json.loads(rseult.X_exa_dis) and json.loads(rseult.X_exa_dis)['boneX'] == '1':
                        sheet4.write(5, 1, "正常", stylecount)
                    else:
                        sheet4.write(5, 1, "未选择", stylecount)
                    #1.定义部位
                    buweiMapper = {
                        '1': '头颅',
                        '2': '胸部',
                        '3': '腹部',
                        '4': '双上肢',
                        '5': '双下肢',
                    }
                    qingkuangMapper ={
                        '6': '正常',
                        '7':'异常',
                        '8':'未做',
                    }
                    #2.解析【部位】
                    resultMP = json.loads(rseult.hea_mr_exa)['placeMR']
                    resultCT = json.loads(rseult.hea_ct_exa)['placeCT']
                    #3.取值【部位】
                    buweiMR_mapped = buweiMapper.get(resultMP)
                    buweiCT_mapped = buweiMapper.get(resultCT)
                    #4.解析【情况】
                    situationMP = json.loads(rseult.hea_mr_exa)['typeMR']
                    situationCT = json.loads(rseult.hea_ct_exa)['typeCT']
                    #5.取值【情况】
                    qingkuangMR_mapped = qingkuangMapper.get(situationMP)
                    qingkuangCT_mapped = qingkuangMapper.get(situationCT)
                    sheet4.write(6, 1, "部位：" + safe_str(buweiMR_mapped) +"\n" + "情况：" + safe_str(qingkuangMR_mapped) + "\n" + "情况描述：" + safe_str(json.loads(rseult.hea_mr_exa)['MRdescription']), stylecount)  # MR
                    sheet4.write(7, 1, "部位：" + safe_str(buweiCT_mapped)+"\n" + "情况：" + safe_str(qingkuangCT_mapped) + "\n" + "情况描述：" + safe_str(json.loads(rseult.hea_ct_exa)['CTdescription']), stylecount)  # CT

                    # 全身骨扫描检查情况
                    if 'bodyBoneScan' in json.loads(rseult.foll_body_scan_exa) and json.loads(rseult.foll_body_scan_exa)['bodyBoneScan'] == '2':
                        sheet4.write(8, 1, json.loads(rseult.foll_body_scan_exa)['bodyBoneScanAbnormal'], stylecount)
                    elif 'bodyBoneScan' in json.loads(rseult.foll_body_scan_exa) and json.loads(rseult.foll_body_scan_exa)['bodyBoneScan'] == '3':
                        sheet4.write(8, 1, "未做", stylecount)
                    elif  'bodyBoneScan' in json.loads(rseult.foll_body_scan_exa) and json.loads(rseult.foll_body_scan_exa)['bodyBoneScan'] == '1':
                        sheet4.write(8, 1, "正常", stylecount)
                    else:
                        sheet4.write(8, 1, "未选择", stylecount)


                    # 添加第五页数据表
                    sheet5 = ws.add_sheet('化验检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet5.col(0).width = 256 * 20
                    sheet5.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'常规化验检查日期:', u'血常规:', u'肝功能:', u'肾功能:', u'电解质:',
                             u'血脂:', u'骨代谢检查:', u'性激素检查:', u'甲状腺功能及抗体检查:', u'肾上腺功能检查:',
                             u'生长激素分泌功能检查:', u'糖代谢情况:']
                    i = 0
                    while i < 12:
                        sheet5.write(i, 0, heads[i], style)
                        first_row = sheet5.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    sheet5.write(0, 1, rseult.lab_exa.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 常规化验检查日期

                    # 血常规
                    bloRou = "白细胞：" + safe_str(json.loads(rseult.blo_rou)['leukocyte']) + "(10^9/L)" + "\n" + \
                             "血红蛋白：" + safe_str(json.loads(rseult.blo_rou)['hemoglobin']) + "(g/L)" + "\n" + \
                             "血小板：" + safe_str(json.loads(rseult.blo_rou)['platelet']) + "(10^9/L)" + "\n" + \
                             "中性粒细胞比例：" + safe_str(json.loads(rseult.blo_rou)['Neutrophils']) + "(%)" + "\n" + \
                             "红细胞计数：" + safe_str(json.loads(rseult.blo_rou)['erythrocyteNum']) + "(10^12/L)" + "\n"
                    sheet5.write(1, 1, bloRou, stylecount)


                    # 肝功能
                    livFun = "ALT：" + safe_str(json.loads(rseult.liv_fun)['ALT']) + "(U/L)" + "\n" + \
                             "AST：" + safe_str(json.loads(rseult.liv_fun)['AST']) + "(U/L)" + "\n" + \
                             "LDH：" + safe_str(json.loads(rseult.liv_fun)['LDH']) + "(U/L)" + "\n" + \
                             "γ-GT：" + safe_str(json.loads(rseult.liv_fun)['gamaGT']) + "" + "\n" + \
                             "总胆红素：" + safe_str(json.loads(rseult.liv_fun)['totalBilirubin']) + "(umol/L)" + "\n" + \
                             "直接胆红素：" + safe_str(json.loads(rseult.liv_fun)['directBilirubin']) + "(umol/L)" + "\n" + \
                             "间接胆红素：" + safe_str(json.loads(rseult.liv_fun)['indirectBilirubin']) + "(umol/L)" + "\n"
                    sheet5.write(2, 1, livFun, stylecount)


                    # 肾功能
                    renFun = "尿素：" + safe_str(json.loads(rseult.ren_fun)['urea']) + "(mmol/L)" + "\n" + \
                             "肌酐：" + safe_str(json.loads(rseult.ren_fun)['creatinine']) + "(umol/L)" + "\n" + \
                             "尿酸：" + safe_str(json.loads(rseult.ren_fun)['uricAcid']) + "(umol/L)" + "\n"
                    sheet5.write(3, 1, renFun, stylecount)


                    # 电解质
                    electrolyte = "血钾：" + safe_str(json.loads(rseult.electrolyte)['bloodK'])+ "(mmol/L)" + "\n" + \
                                  "血钠：" + safe_str(json.loads(rseult.electrolyte)['bloodNa']) + "(mmol/L)" + "\n" + \
                                  "血氯：" + safe_str(json.loads(rseult.electrolyte)['bloodCl']) + "(mmol/L)" + "\n"
                    sheet5.write(4, 1, electrolyte, stylecount)


                    # 血脂
                    bloodFat = "TC：" + safe_str(json.loads(rseult.blood_fat)['TC']) + "(mmol/L)" + "\n" + \
                               "TG：" + safe_str(json.loads(rseult.blood_fat)['TG']) + "(mmol/L)" + "\n" + \
                               "HDL：" + safe_str(json.loads(rseult.blood_fat)['HDL']) + "(mmol/L)" + "\n" + \
                               "LDL：" + safe_str(json.loads(rseult.blood_fat)['LDL']) + "(mmol/L)" + "\n"
                    sheet5.write(5, 1, bloodFat, stylecount)


                    # 骨代谢检查
                    boneMetExa =  "骨代谢检查日期：" + rseult.bone_met_exa_date.strftime('%Y-%m-%d %H:%M:%S') + "" + "\n" + \
                                "血钙：" + safe_str(json.loads(rseult.bone_met_exa)['bloodCa']) + "(mmol/L)" + "\n" + \
                                "血磷：" + safe_str(json.loads(rseult.bone_met_exa)['bloodP']) + "(mmol/L)" + "\n" + \
                                "β-CTX：" + safe_str(json.loads(rseult.bone_met_exa)['CTX']) + "(pg/mL)" + "\n" + \
                                "骨钙素：" + safe_str(json.loads(rseult.bone_met_exa)['BGP']) + "(ng/mL)，" + "\n" + \
                                "PINP：" + safe_str(json.loads(rseult.bone_met_exa)['PINP']) + "(ug/L)" + "\n" + \
                                "PTH：" + safe_str(json.loads(rseult.bone_met_exa)['PTH']) + "(pg/mL)" + "\n" + \
                                 "25羟维生素D：" + safe_str(json.loads(rseult.bone_met_exa)['OHD25']) + "(nmol/L)" + "\n" + \
                                 "碱性磷酸酶：" + safe_str(json.loads(rseult.bone_met_exa)['ALP']) + "(nmol/L)" + "\n" + \
                                 "24h尿钙：" + safe_str(json.loads(rseult.bone_met_exa)['urineCa']) + "" + "\n" + \
                                "24h尿磷：" + safe_str(json.loads(rseult.bone_met_exa)['urineP']) + "" + "\n"
                    sheet5.write(6, 1, boneMetExa, stylecount)

                    # 性激素检查
                    sexHorExa = "性激素检查日期：" + rseult.sex_hor_exa_date.strftime('%Y-%m-%d %H:%M:%S') + "" + "\n" + \
                                 "LH：" + safe_str(json.loads(rseult.sex_hor_exa)['LH']) + "(mIU/mL)" + "\n" + \
                                 "FSH：" + safe_str(json.loads(rseult.sex_hor_exa)['FSH']) + "(mIU/mL)" + "\n" + \
                                 "E2：" + safe_str(json.loads(rseult.sex_hor_exa)['E2']) + "(pg/mL)" + "\n" + \
                                 "T：" + safe_str(json.loads(rseult.sex_hor_exa)['T']) + "(ng/dL)" + "\n" + \
                                 "PRL：" + safe_str(json.loads(rseult.sex_hor_exa)['PRL']) + "(ng/mL)" + "\n"
                    sheet5.write(7, 1, sexHorExa, stylecount)


                    # 甲状腺功能及抗体检查 thy_fun_ant_exa
                    thyFunAntExa =   "甲状腺功能及抗体检查日期：" + rseult.thy_fun_ant_date.strftime('%Y-%m-%d %H:%M:%S') + "" + "\n" + \
                                    "TT4：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['TT4']) + "(nmol/L)" + "\n" + \
                                    "TT3：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['TT3']) + "(nmol/L)" + "\n" + \
                                    "TSH：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['TSH']) + "(mIU/L)" + "\n" + \
                                    "FT4：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['FT4']) + "(pmol/L)" + "\n" + \
                                    " FT3：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['FT3']) + "(pmol/L)" + "\n" + \
                                    "TPOAb：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['TPOAb']) + "(IU/mL)" + "\n" + \
                                     "TGAb：" + safe_str(json.loads(rseult.thy_fun_ant_exa)['TGAb']) + "(IU/mL)" + "\n"
                    sheet5.write(8, 1, thyFunAntExa, stylecount)


                    # 肾上腺功能检查 adr_fun_exa
                    adrFunExa = "肾上腺功能检查日期：" + rseult.adr_fun_exa_date.strftime('%Y-%m-%d %H:%M:%S') + "" + "\n" + \
                                "ACTH：" + safe_str(json.loads(rseult.adr_fun_exa)['ACTH']) + "(pg/mL)" + "\n" + \
                                "ACTH 8am：" + safe_str(json.loads(rseult.adr_fun_exa)['ACTH8']) + "" + "\n" + \
                                "ACTH 4pm：" + safe_str(json.loads(rseult.adr_fun_exa)['ACTH4']) + "" + "\n" + \
                                "皮质醇8am：" + safe_str(json.loads(rseult.adr_fun_exa)['AM8']) + "(ug/dL)" + "\n" + \
                                "皮质醇4pm：" + safe_str(json.loads(rseult.adr_fun_exa)['PM4']) + "(ug/dL)" + "\n" + \
                                "24h尿游离皮质醇：" + safe_str(json.loads(rseult.adr_fun_exa)['UFC']) + "(ug/24h)" + "\n"
                    sheet5.write(9, 1, adrFunExa, stylecount)


                    # 生长激素分泌功能检查 phy_exa
                    phyExa = "生长激素分泌功能检查日期：" + safe_str(rseult.gro_hor_exa) + "" + "\n" + \
                            "GH：" + safe_str(json.loads(rseult.phy_exa)['GH']) + "(ng/mL)" + "\n" + \
                            "IGF-1：" + safe_str(json.loads(rseult.phy_exa)['IGF1']) + "(ng/mL)" + "\n" + \
                            "IGF-BP3：" + safe_str(json.loads(rseult.phy_exa)['IGFBP3']) + "(ng/mL)" + "\n"
                    sheet5.write(10, 1, phyExa, stylecount)

                    # 糖代谢情况
                    gluMet =  "糖代谢检查日期：" + rseult.glu_met_date.strftime('%Y-%m-%d %H:%M:%S') + "" + "\n" + \
                            "空腹血糖：" + safe_str(json.loads(rseult.glu_met)['FBS']) + "(mmol/L)" + "\n" + \
                            "空腹胰岛素：" + safe_str(json.loads(rseult.glu_met)['FINS']) + "(mIU/L)" + "\n" + \
                            " 空腹C肽：" + safe_str(json.loads(rseult.glu_met)['FCP']) + "(ng/mL)" + "\n" + \
                            "糖化血红蛋白：" + safe_str(json.loads(rseult.glu_met)['HbA1c']) + "(%)" + "\n"
                    sheet5.write(11, 1, gluMet, stylecount)


                    # 添加第六页数据表
                    sheet6 = ws.add_sheet('激发试验情况')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet6.col(0).width = 256 * 20
                    sheet6.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'是否GnRH激发试验:', u'是否行小剂量地塞米松抑制试验:', u'是否行生长激素-葡萄糖抑制试验:']
                    i = 0
                    while i < 3:
                        sheet6.write(i, 0, heads[i], style)
                        first_row = sheet6.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据

                    # 是否GnRH激发试验
                    if rseult.GnRH == '1':
                        GnRH = "药物名称：" + safe_str(json.loads(rseult.GnRH_eva)['GnRHDrugName']) + "" + "\n" + \
                                 "药物剂量：" + safe_str(json.loads(rseult.GnRH_eva)['GnRHDrugDosage']) + "" + "\n" + \
                                 "使用时间：" + safe_str(json.loads(rseult.GnRH_eva)['GnRHUsageTime']) + "" + "\n" + \
                                 " LF峰值：" + safe_str(json.loads(rseult.GnRH_eva)['LFMax']) + "" + "\n" + \
                                 "FSH峰值：" + safe_str(json.loads(rseult.GnRH_eva)['FSHMax']) + "\n" + \
                                 "LH/FSH比值：" + safe_str(json.loads(rseult.GnRH_eva)['LFRatio']) + "\n"
                        sheet6.write(0, 1, GnRH, stylecount)
                    elif rseult.GnRH == '2':
                        sheet6.write(0, 1, "否", stylecount)
                    else:
                        sheet6.write(0, 1, "未选择", stylecount)


                    # 是否行小剂量地塞米松抑制试验
                    if rseult.low_dose == '1':
                        lowDose = "药物名称：" + safe_str(json.loads(rseult.low_dose_eva)['LDDSTDrugName'])+ "" + "\n" + \
                                 "药物剂量：" +safe_str(json.loads(rseult.low_dose_eva)['LDDSTDrugDosage'])+ "" + "\n" + \
                                 "使用时间：" + safe_str(json.loads(rseult.low_dose_eva)['LDDSTUsageTime'])+ "" + "\n" + \
                                 " ACTH（试验前）：" + safe_str(json.loads(rseult.low_dose_eva)['ACTHAfter'])+ "" + "\n" + \
                                 " ACTH（试验后）：" + safe_str(json.loads(rseult.low_dose_eva)['ACTHBefore']) +  "\n" + \
                                " 皮质醇（试验前）：" + safe_str(json.loads(rseult.low_dose_eva)['cortisolAfter']) +  "\n"+\
                                " 皮质醇（试验后）：" +safe_str(json.loads(rseult.low_dose_eva)['cortisolBefore']) + "\n"+\
                                " 24h尿游离皮质醇（试验前）：" + safe_str(json.loads(rseult.low_dose_eva)['UFFAfter'])+ "\n"+\
                                " 24h尿游离皮质醇（试验后）：" + safe_str(json.loads(rseult.low_dose_eva)['UFFBefore'])+ "\n"
                        sheet6.write(1, 1, lowDose, stylecount)
                    elif  rseult.low_dose == '2':
                        sheet6.write(1, 1, "否", stylecount)
                    else:
                        sheet6.write(1, 1, "未选择", stylecount)


                    # 是否行生长激素-葡萄糖抑制试验
                    if rseult.gro_glu == '1':
                        groGlu = "药物名称：" + safe_str(json.loads(rseult.gro_glu_eva)['GHGITDrugName']) + "" + "\n" + \
                                 " 药物剂量：" + safe_str(json.loads(rseult.gro_glu_eva)['GHGITDrugDosage']) + "" + "\n" + \
                                 "使用时间：" + safe_str(json.loads(rseult.gro_glu_eva)['GHGITUsageTime']) + "" + "\n" + \
                                 " GH（0min）：" + safe_str(json.loads(rseult.gro_glu_eva)['GH0']) + "" + "\n" + \
                                 "  GH（60min）：" + json.loads(rseult.gro_glu_eva)['GH3'] + "" + "\n" + \
                                 " GH（90min）：" + json.loads(rseult.gro_glu_eva)['GH6'] + "" + "\n" + \
                                 " GH（120min）：" + json.loads(rseult.gro_glu_eva)['GH9'] + "" + "\n" + \
                                 " 血糖值（0min）：" + safe_str(json.loads(rseult.gro_glu_eva)['XTZ0'])+ "(mmol/l)" + "\n" + \
                                 "血糖值（30min）：" + safe_str(json.loads(rseult.gro_glu_eva)['XTZ3'])+ "(mmol/l)" + "\n" + \
                                 " 血糖值（60min）：" +safe_str(json.loads(rseult.gro_glu_eva)['XTZ6']) + "(mmol/l)" + "\n" + \
                                 " 血糖值（90min）：" +safe_str(json.loads(rseult.gro_glu_eva)['XTZ9']) + "(mmol/l)" + "\n"+ \
                                " 血糖值（120min）：" + safe_str(json.loads(rseult.gro_glu_eva)['XTZ12']) + "(mmol/l)" + "\n"
                        sheet6.write(2, 1, groGlu, stylecount)
                    elif  rseult.gro_glu == '2':
                        sheet6.write(2, 1, "否", stylecount)
                    else:
                        sheet6.write(2, 1, "未选择", stylecount)






                    # 添加第七页数据表
                    sheet7 = ws.add_sheet('其他检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet7.col(0).width = 256 * 20
                    sheet7.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'心电图检查:', u'X线骨龄检查:', u'垂体MR检查:']
                    i = 0
                    while i < 3:
                        sheet7.write(i, 0, heads[i], style)
                        first_row = sheet7.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    # 心电图检查
                    if 'ecgExamination' in json.loads(rseult.ecg_exa) and json.loads(rseult.ecg_exa)['ecgExamination'] == '2':
                        sheet7.write(0, 1, "异常情况描述：" + safe_str(json.loads(rseult.ecg_exa)['ecgExaminationAbnormal']), stylecount)
                    elif  'ecgExamination' in json.loads(rseult.ecg_exa) and json.loads(rseult.ecg_exa)['ecgExamination'] == '3':
                        sheet7.write(0, 1, "未做", stylecount)
                    elif   'ecgExamination' in json.loads(rseult.ecg_exa) and json.loads(rseult.ecg_exa)['ecgExamination'] == '1':
                        sheet7.write(0, 1, "正常", stylecount)
                    else:
                        sheet7.write(0, 1, "未选择", stylecount)
                    # X线骨龄检查
                    if rseult.x_bone_exa == '1':
                        sheet7.write(1, 1, "做", stylecount)
                    elif  rseult.x_bone_exa == '2':
                        sheet7.write(1, 1, "未做", stylecount)
                    else:
                        sheet7.write(1, 1, "未选择", stylecount)

                    # 垂体MR检查
                    if 'pituitaryMR' in json.loads(rseult.pit_exa) and json.loads(rseult.pit_exa)['pituitaryMR'] == '2':
                        sheet7.write(2, 1, "异常情况描述：" + safe_str(json.loads(rseult.pit_exa)['pituitaryMRAbnormal']), stylecount)
                    elif  'pituitaryMR' in json.loads(rseult.pit_exa) and json.loads(rseult.pit_exa)['pituitaryMR'] == '3':
                        sheet7.write(2, 1, "未做", stylecount)
                    elif   'pituitaryMR' in json.loads(rseult.pit_exa) and json.loads(rseult.pit_exa)['pituitaryMR'] == '1':
                        sheet7.write(2, 1, "正常", stylecount)
                    else:
                        sheet7.write(2, 1, "未选择", stylecount)





                        # 添加第八页数据表
                    sheet8 = ws.add_sheet('遗传学检查及病理检查')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet8.col(0).width = 256 * 20
                    sheet8.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'GNAS基因测定检查:', u'病理活检检查:']
                    i = 0
                    while i < 2:
                        sheet8.write(i, 0, heads[i], style)
                        first_row = sheet8.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    # GNAS基因测定检查
                    if rseult.GNAS == '1':
                        GANSDATA = json.loads(rseult.GNAS_sam_loc)
                        # 标本采样类型或部位
                        if 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '1':
                            sheet8.write(0, 1, "标本采样类型或部位: 外周血。" , stylecount)
                        elif  'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '2':
                            sheet8.write(0, 1, "标本采样类型或部位: 病变组织。" + "具体部位：" +safe_str(GANSDATA['gnasSamplingPosition']), stylecount)
                        elif 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '3':
                            sheet8.write(0, 1, "标本采样类型或部位: 囊肿穿刺液。" + "具体部位：" +safe_str(GANSDATA['gnasSamplingPosition']), stylecount)
                        elif 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '4':
                            sheet8.write(0, 1, "标本采样类型或部位: 其他。" + "具体部位：" +safe_str(GANSDATA['gnasSamplingPosition']), stylecount)
                    elif rseult.GNAS == '2':
                        sheet8.write(0, 1, "否", stylecount)
                    elif rseult.GNAS == '3':
                        GANSDATA = json.loads(rseult.GNAS_sam_loc)
                        # 标本采样类型或部位
                        if 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '1':
                            sheet8.write(0, 1, "【不详】，标本采样类型或部位：外周围。" , stylecount)
                        elif  'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '2':
                            sheet8.write(0, 1, "【不详】，标本采样类型或部位: 病变组织。" + "具体部位：" + GANSDATA[
                                'gnasSamplingPosition'], stylecount)
                        elif 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '3':
                            sheet8.write(0, 1, "【不详】，标本采样类型或部位: 囊肿穿刺液。" + "具体部位：" + GANSDATA[
                                'gnasSamplingPosition'], stylecount)
                        elif 'GNASSampling' in json.loads(rseult.GNAS_sam_loc) and GANSDATA['GNASSampling'] == '4':
                            sheet8.write(0, 1, "【不详】，标本采样类型或部位: 其他。" + "具体部位：" + GANSDATA[
                                'gnasSamplingPosition'], stylecount)
                        else:
                            sheet8.write(0, 1, "未选择", stylecount)
                    else:
                        sheet8.write(0, 1, "未选择", stylecount)

                    # 病理活检检查
                    if rseult.pat_exa == '1':
                        # 标本采样类型或部位
                        sheet8.write(1, 1, "标本采样类型或部位:" + safe_str(rseult.pat_sam_loc), stylecount)
                    elif rseult.pat_exa == '2':
                        sheet8.write(1, 1, "否", stylecount)
                    elif  rseult.pat_exa == '3':
                        sheet8.write(1, 1, "【不详】，标本采样类型或部位：" + safe_str(rseult.pat_sam_loc), stylecount)
                    else:
                        sheet8.write(1, 1, "未选择", stylecount)






                    # 添加第九页数据表
                    sheet9 = ws.add_sheet('随访及治疗')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet9.col(0).width = 350 * 20
                    sheet9.col(1).width = 768 * 20
                    sheet9.col(2).width = 768 * 20
                    sheet9.col(3).width = 768 * 20
                    sheet9.col(4).width = 768 * 20
                    sheet9.col(5).width = 768 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'是否达终身高:', u'是否外周性性早熟:', u'是否甲状腺功能亢进:', u'是否生长激素分泌过多:', u'是否高泌乳素血症:',u'是否皮质醇增多症:',
                             u'是否行颅内手术:', u'是否行双侧肾上腺切除术:', u'是否骨痛:', u'是否低磷酸盐血症:', u'是否骨骼外科手术:', u'是否对牛奶咖啡斑进行激光治疗:',
                             u'是否进行心理疏导:', u'生存状态:']
                    i = 0
                    while i < 14:
                        sheet9.write(i, 0, heads[i], style)
                        first_row = sheet9.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    for iFl in masfollow:
                        # 是否达终身高
                        if 'isFinalHeight' in json.loads(iFl.is_finalhei) and json.loads(iFl.is_finalhei)['isFinalHeight'] == '1':
                            sheet9.write(0, 1, "具体身高：" + safe_str(json.loads(iFl.is_finalhei)['finalHeight']) + "（cm）", stylecount)
                        elif  'isFinalHeight' in json.loads(iFl.is_finalhei) and json.loads(iFl.is_finalhei)['isFinalHeight'] == '2':
                            sheet9.write(0, 1, "否", stylecount)
                        else:
                            sheet9.write(0, 1, "未选择", stylecount)
                        # 是否外周性性早熟
                        if 'isPPP' in json.loads(iFl.is_per_pre) and json.loads(iFl.is_per_pre)['isPPP'] == '1':
                            if 'isPrecociousPuberty' in json.loads(iFl.is_per_pre) and json.loads(iFl.is_per_pre)['isPrecociousPuberty'] == '1':
                                iFlCount = 1
                                per = json.loads(iFl.per_pre_sf)
                                for perSF in per:
                                    isPrecociousPubertyCount = "治疗周期：" + safe_str(','.join(json.loads(iFl.is_per_pre)['treatmentCyclePPP'])) + "\n" + \
                                                                "随访日期:" + safe_str(perSF['time']) + "\n" + \
                                                               "药物名称:" + safe_str(perSF['name']) + "\n" + \
                                                                "药物剂量:" + safe_str(perSF['dose']) + "\n" + \
                                                                "身高:" + safe_str(perSF['height']) + "\n" + \
                                                                "体重:" + safe_str(perSF['weight']) + "\n" + \
                                                                "BMI:" + safe_str(perSF['bmi']) + "\n" + \
                                                                "乳腺分期:" + safe_str(perSF['breast']) + "\n" + \
                                                                "睾丸分期:" + safe_str(perSF['testis']) + "\n" + \
                                                                "阴毛分期:" + safe_str(perSF['hair']) + "\n" + \
                                                                "LH:" + safe_str(perSF['LH']) + "\n" + \
                                                                "FSH:" + safe_str(perSF['FSH']) + "\n" + \
                                                                "E2:" + safe_str(perSF['E2']) + "\n" + \
                                                                "T:" + safe_str(perSF['T']) + "\n" + \
                                                                "子宫附件B超 或 睾丸B超:" + safe_str(perSF['ultra']) + "\n" + \
                                                                "骨龄情况:" +safe_str(perSF['boneage']) + "\n"
                                    sheet9.write(1, iFlCount, isPrecociousPubertyCount, stylecount)
                                    iFlCount += 1
                            elif  'isPrecociousPuberty' in json.loads(iFl.is_per_pre) and json.loads(iFl.is_per_pre)['isPrecociousPuberty'] == '2':
                                sheet9.write(1, 1, "否", stylecount)
                            else:
                                sheet9.write(1, 1, "未选择", stylecount)
                        elif  'isPPP' in json.loads(iFl.is_per_pre) and json.loads(iFl.is_per_pre)['isPPP'] == '2':
                            sheet9.write(1, 1, "否", stylecount)
                        else:
                            sheet9.write(1, 1, "未选择", stylecount)
                        # 是否甲状腺功能亢进
                        if 'isHyperthyreosis' in json.loads(iFl.is_hyper) and json.loads(iFl.is_hyper)['isHyperthyreosis'] == '1':
                            if 'isThyroidFunction' in json.loads(iFl.is_hyper) and json.loads(iFl.is_hyper)['isThyroidFunction'] == '1':
                                iFlCount = 1
                                hpy = json.loads(iFl.hyper_sf)
                                for hpySF in hpy:
                                    isThyroidFunctionCount = "治疗起始日期：" + safe_str(','.join(json.loads(iFl.is_hyper)['treatmentCycleHyper'])) + "\n" + \
                                                             "随访日期：" + safe_str(hpySF['time']) + "\n" + \
                                                             "治疗方法：" + safe_str(hpySF['method']) + "\n" + \
                                                             "药物剂量：" + safe_str(hpySF['dose']) + "\n" + \
                                                             "甲状腺功能：" + safe_str(hpySF['TF']) + "\n" + \
                                                             "甲状腺B超" + safe_str(hpySF['thyroidUlt']) + "\n"
                                    sheet9.write(2, iFlCount, isThyroidFunctionCount, stylecount)
                                    iFlCount += 1
                            elif  'isThyroidFunction' in json.loads(iFl.is_hyper) and json.loads(iFl.is_hyper)['isThyroidFunction'] == '2':
                                sheet9.write(2, 1, "否", stylecount)
                            else:
                                sheet9.write(2, 1, "未选择", stylecount)
                        elif  'isHyperthyreosis' in json.loads(iFl.is_hyper) and json.loads(iFl.is_hyper)['isHyperthyreosis'] == '2':
                            sheet9.write(2, 1, "否", stylecount)
                        else:
                            sheet9.write(2, 1, "未选择", stylecount)
                        # 是否生长激素分泌过多
                        if 'isGrowth' in json.loads(iFl.is_gro_hor) and json.loads(iFl.is_gro_hor)['isGrowth'] == '1':
                            if 'isGrowthHormonePlethora' in json.loads(iFl.is_gro_hor) and json.loads(iFl.is_gro_hor)['isGrowthHormonePlethora'] == '1':
                                iFlCount = 1
                                groHor = json.loads(iFl.gro_hor_sf)
                                for groHorSF in groHor:
                                    isGrowthHormonePlethora = "治疗起始日期：" + safe_str(','.join(json.loads(iFl.is_gro_hor)['treatmentCycleGrowth'])) + "\n" + \
                                                              "随访日期:" + safe_str(groHorSF['time']) + "\n" + \
                                                              "药物名称:" + safe_str(groHorSF['name']) + "\n" + \
                                                              "药物剂量:" + safe_str(groHorSF['dose']) + "\n" + \
                                                              "IGF-1:" + safe_str(groHorSF['IGF1']) + "\n" + \
                                                              "IGF-BP3:" + safe_str(groHorSF['IGFBP3']) + "\n" + \
                                                              "GH:" + safe_str(groHorSF['GH']) + "\n" + \
                                                              "垂体MR:" + safe_str(groHorSF['MR']) + "\n"
                                    sheet9.write(3, iFlCount, isGrowthHormonePlethora, stylecount)
                                    iFlCount += 1
                            elif  'isGrowthHormonePlethora' in json.loads(iFl.is_gro_hor) and json.loads(iFl.is_gro_hor)['isGrowthHormonePlethora'] == '2':
                                sheet9.write(3, 1, "否", stylecount)
                            else:
                                sheet9.write(3, 1, "未选择", stylecount)
                        elif  'isGrowth' in json.loads(iFl.is_gro_hor) and json.loads(iFl.is_gro_hor)['isGrowth'] == '2':
                            sheet9.write(3, 1, "否", stylecount)
                        else:
                            sheet9.write(3, 1, "未选择", stylecount)
                    # 是否高泌乳素血症
                    if 'isHPRL' in json.loads(iFl.is_tre_hpy) and json.loads(iFl.is_tre_hpy)['isHPRL'] == '1':
                        if 'isHyperprolactinemia' in json.loads(iFl.is_tre_hpy) and json.loads(iFl.is_tre_hpy)['isHyperprolactinemia'] == '1':
                            iFlCount = 1
                            treHpy = json.loads(iFl.tre_hpy_sf)
                            for treHpySF in treHpy:
                                isHyperprolactinemiaCount = "治疗起始日期：" + safe_str(','.join(json.loads(iFl.is_tre_hpy)['treatmentCycleHPRL'])) + "\n" + \
                                                            "随访日期:" + safe_str(treHpySF['time']) + "\n" + \
                                                            "药物名称:" + safe_str(treHpySF['name']) + "\n" + \
                                                            "药物剂量:" +safe_str(treHpySF['dose'])  + "\n" + \
                                                            "PRL:" + safe_str(treHpySF['PRL']) + "\n"
                                sheet9.write(4, iFlCount, isHyperprolactinemiaCount, stylecount)
                                iFlCount += 1
                        elif  'isHyperprolactinemia' in json.loads(iFl.is_tre_hpy) and json.loads(iFl.is_tre_hpy)['isHyperprolactinemia'] == '2':
                            sheet9.write(4, 1, "否", stylecount)
                        else:
                            sheet9.write(4, 1, "未选择", stylecount)
                    elif  'isHPRL' in json.loads(iFl.is_tre_hpy) and json.loads(iFl.is_tre_hpy)['isHPRL'] == '2':
                        sheet9.write(4, 1, "否", stylecount)
                    else:
                        sheet9.write(4, 1, "未选择", stylecount)
                    # 是否皮质醇增多症
                    if 'isCortisol' in json.loads(iFl.is_inc_cor) and json.loads(iFl.is_inc_cor)['isCortisol'] == '1':
                        if 'isHypercortisolism' in json.loads(iFl.is_inc_cor) and json.loads(iFl.is_inc_cor)['isHypercortisolism'] == '1':
                            iFlCount = 1
                            incCor = json.loads(iFl.inc_cor_sf)
                            for incCorSF in incCor:
                                isHypercortisolismCount = "治疗起始日期：" + safe_str(','.join(json.loads(iFl.is_inc_cor)['treatmentCycleCortisol'])) + "\n" + \
                                                          "随访日期：" + safe_str(incCorSF['time']) + "\n" + \
                                                          "药物名称：" + safe_str(incCorSF['name']) + "\n" + \
                                                          "药物剂量：" + safe_str(incCorSF['dose']) + "\n" + \
                                                          "ACTH：" + safe_str(incCorSF['ACTH']) + "\n" + \
                                                          "皮质醇（8am）：" + safe_str(incCorSF['cortisol8']) + "\n" + \
                                                          "皮质醇（4am）" + safe_str(incCorSF['cortisol4']) + "\n" + \
                                                          "24h尿游离皮质醇" + safe_str(incCorSF['FC']) + "\n"
                                sheet9.write(5, iFlCount, isHypercortisolismCount, stylecount)
                                iFlCount += 1
                        elif  'isHypercortisolism' in json.loads(iFl.is_inc_cor) and json.loads(iFl.is_inc_cor)['isHypercortisolism'] == '2':
                            sheet9.write(5, 1, "无", stylecount)
                        else:
                            sheet9.write(5, 1, "未选择", stylecount)
                    elif  'isCortisol' in json.loads(iFl.is_inc_cor) and json.loads(iFl.is_inc_cor)['isCortisol'] == '2':
                        sheet9.write(5, 1, "无", stylecount)
                    else:
                        sheet9.write(5, 1, "未选择", stylecount)
                    # 是否行颅内手术
                    if iFl.is_int_sur is not None and iFl.is_int_sur == '1':
                        sheet9.write(6, 1, "是", stylecount)
                    elif  iFl.is_int_sur is not None and iFl.is_int_sur == '2':
                        sheet9.write(6, 1, "否", stylecount)
                    else:
                        sheet9.write(6, 1, "未选择", stylecount)
                    # 是否行双侧肾上腺切除术
                    if iFl.is_bil_adr is not None and iFl.is_bil_adr == '1':
                        sheet9.write(7, 1, "是", stylecount)
                    elif iFl.is_bil_adr is not None  and iFl.is_bil_adr == '2':
                        sheet9.write(7, 1, "否", stylecount)
                    else:
                        sheet9.write(7, 1, "未选择", stylecount)
                    # 是否骨痛
                    if 'isOstealgia' in json.loads(iFl.is_bon_pai) and json.loads(iFl.is_bon_pai)['isOstealgia'] == '1':
                        if 'isTreatBonePain' in json.loads(iFl.is_bon_pai) and json.loads(iFl.is_bon_pai)['isTreatBonePain'] == '1':
                            iFlCount = 1
                            bonPai = json.loads(iFl.bon_pai_sf)
                            for bonPaiSF in bonPai:
                                isTreatBonePainCount = "随访日期:" + safe_str(bonPaiSF['time']) + "\n" + \
                                                       "药物名称:" + safe_str(bonPaiSF['name']) + "\n" + \
                                                       "药物剂量:" + safe_str(bonPaiSF['dose']) + "\n" + \
                                                       "骨转化指标:" + safe_str(bonPaiSF['boneTurnover']) + "\n" +\
                                                       "骨密度:" + safe_str(bonPaiSF['BMD']) + "\n"
                                sheet9.write(8, iFlCount, isTreatBonePainCount, stylecount)
                                iFlCount += 1
                        elif  'isTreatBonePain' in json.loads(iFl.is_bon_pai) and json.loads(iFl.is_bon_pai)['isTreatBonePain'] == '2':
                            sheet9.write(8, 1, "否", stylecount)
                        else:
                            sheet9.write(8, 1, "未选择", stylecount)
                    elif  'isOstealgia' in json.loads(iFl.is_bon_pai) and json.loads(iFl.is_bon_pai)['isOstealgia'] == '2':
                        sheet9.write(8, 1, "否", stylecount)
                    else:
                        sheet9.write(8, 1, "未选择", stylecount)
                    # 是否低磷酸盐血症
                    if 'isHaveHypophosphatemia' in json.loads(iFl.hypop) and json.loads(iFl.hypop)['isHaveHypophosphatemia'] == '1':
                        if 'isHypophosphatemia' in json.loads(iFl.hypop) and json.loads(iFl.hypop)['isHypophosphatemia'] == '1':
                            iFlCount = 1
                            hyPop = json.loads(iFl.hypop_sf)
                            for hyPopSF in hyPop:
                                isHypophosphatemia = "随访日期:" + safe_str(hyPopSF['time']) + "\n" + \
                                                     "药物名称:" + safe_str(hyPopSF['name']) + "\n" + \
                                                     "药物剂量:" + safe_str(hyPopSF['dose']) + "\n" + \
                                                     "血钙:" +  safe_str(hyPopSF['bloodCa']) + "\n" + \
                                                     "尿钙:" + safe_str(hyPopSF['urineCa']) + "\n" + \
                                                     "尿磷:" + safe_str(hyPopSF['urineP']) + "\n" + \
                                                     "PTH:" + safe_str(hyPopSF['PTH']) + "\n" + \
                                                     "肾功能:" +safe_str(hyPopSF['renalFunction']) + "\n" + \
                                                     "肾脏B超:" + safe_str(hyPopSF['renalUlt']) + "\n"
                                sheet9.write(9, iFlCount, isHypophosphatemia, stylecount)
                                iFlCount += 1
                        elif  'isHypophosphatemia' in json.loads(iFl.hypop) and json.loads(iFl.hypop)['isHypophosphatemia'] == '2':
                            sheet9.write(9, 1, "否", stylecount)
                        else:
                            sheet9.write(9, 1, "未选择", stylecount)
                    elif  'isHaveHypophosphatemia' in json.loads(iFl.hypop) and json.loads(iFl.hypop)['isHaveHypophosphatemia'] == '2':
                        sheet9.write(9, 1, "否", stylecount)
                    else:
                        sheet9.write(9, 1, "未选择", stylecount)
                    # 是否骨骼外科手术
                    if 'isHaveSkeletalSurgery' in json.loads(iFl.is_ske_sur) and json.loads(iFl.is_ske_sur)['isHaveSkeletalSurgery'] == '1':
                        if  'isSkeletalSurgery' in json.loads(iFl.is_ske_sur) and json.loads(iFl.is_ske_sur)['isSkeletalSurgery'] == '1':
                            map = {
                                '1':'修复骨折',
                                '2':'矫正',
                                '3':'预防骨骼畸形',
                            }
                            surgicalPurpose = json.loads(iFl.is_ske_sur)['surgicalPurpose']
                            surgicalValue = map.get(surgicalPurpose)
                            sheet9.write(10, 1, "手术目的：" + safe_str(surgicalValue), stylecount)
                        elif   'isSkeletalSurgery' in json.loads(iFl.is_ske_sur) and json.loads(iFl.is_ske_sur)['isSkeletalSurgery'] == '2':
                            sheet9.write(10, 1, "否", stylecount)
                        else:
                            sheet9.write(10, 1, "未选择", stylecount)
                    elif  'isHaveSkeletalSurgery' in json.loads(iFl.is_ske_sur) and json.loads(iFl.is_ske_sur)['isHaveSkeletalSurgery'] == '2':
                        sheet9.write(10, 1, "否", stylecount)
                    else:
                        sheet9.write(10, 1, "未选择", stylecount)
                    # 是否对牛奶咖啡斑进行激光治疗
                    if iFl.is_cafe_spot == '1':
                        sheet9.write(11, 1, "是", stylecount)
                    elif  iFl.is_cafe_spot == '2':
                        sheet9.write(11, 1, "否", stylecount)
                    else:
                        sheet9.write(11, 1, "未选择", stylecount)
                    # 是否进行心理疏导
                    if iFl.is_psy_cou == '1':
                        sheet9.write(12, 1, "是", stylecount)
                    elif  iFl.is_psy_cou == '2':
                        sheet9.write(12, 1, "否", stylecount)
                    else:
                        sheet9.write(12, 1, "未选择", stylecount)
                    # 生存状态
                    if json.loads(iFl.sur_sta)['isSurvivalState'] == '1':
                        sheet9.write(13, 1, "生存", stylecount)
                    elif json.loads(iFl.sur_sta)['isSurvivalState'] == '2':
                        sheet9.write(13, 1, "死亡， 死亡原因：" + json.loads(iFl.sur_sta)['CauseOfDeath'], stylecount)
                    else:
                        sheet9.write(13, 1, "未选择", stylecount)






                    # 添加第十页数据表
                    sheet10 = ws.add_sheet('其他')
                    # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                    sheet10.col(0).width = 256 * 20
                    sheet10.col(1).width = 2186 * 20
                    # 行高
                    tall_style = xlwt.easyxf('font:height 250')
                    # 写入表头
                    heads = [u'是否存在性早熟:', u'是否存在甲状腺功能亢进:', u'是否存在生长激素分泌过多:', u'是否存在皮质醇增多症:']
                    i = 0
                    while i < 4:
                        sheet10.write(i, 0, heads[i], style)
                        first_row = sheet10.row(i)
                        first_row.set_style(tall_style)
                        i = i + 1
                    # 写入数据
                    # 是否存在性早熟
                    if rseult.sex_pre == '1':
                        sheet10.write(0, 1, "是", stylecount)
                    elif  rseult.sex_pre == '2':
                        sheet10.write(0, 1, "否", stylecount)
                    else:
                        sheet10.write(0, 1, "未选择", stylecount)
                    # 是否存在甲状腺功能亢进
                    if rseult.hyper == '1':
                        sheet10.write(1, 1, "是", stylecount)
                    elif  rseult.hyper == '2':
                        sheet10.write(1, 1, "否", stylecount)
                    else:
                        sheet10.write(1, 1, "未选择", stylecount)
                    # 是否存在生长激素分泌过多
                    if rseult.is_gro_hor == '1':
                        sheet10.write(2, 1, "是", stylecount)
                    elif  rseult.is_gro_hor == '2':
                        sheet10.write(2, 1, "否", stylecount)
                    else:
                        sheet10.write(2, 1, "未选择", stylecount)
                    # 是否存在皮质醇增多症
                    if rseult.is_inc_cor == '1':
                        sheet10.write(3, 1, "是", stylecount)
                    elif  rseult.is_inc_cor == '2':
                        sheet10.write(3, 1, "否", stylecount)
                    else:
                        sheet10.write(3, 1, "未选择", stylecount)
                else:
                    return False
            fileNum = str(patient.pk % 64)
            newFilePath = settings.IMG_PATH + file_type + "/" + fileNum + "/" + str(patient.pk)
            if not os.path.exists(newFilePath):
                os.makedirs(newFilePath)
            ws.save(settings.IMG_PATH + file_type + "/" + fileNum + "/" + str(patient.pk) + '/labExcel.xls')
            return True
        except Exception as e:
            print(e)
            return False




    # 导出统计每家医院的上传数量Excel
    def UpMecNumExcel(row):
        # 导出病例主表Excel文件
        # 设置HTTPResponse的类型
        response = HttpResponse(content_type='application/vnd.ms-excel')
        response['Content-Disposition'] = 'attachment;filename=labExcel.xls'
        style = excelStyle.style
        stylecount = excelStyle.stylecount
        # 创建工作簿
        ws = xlwt.Workbook(encoding='utf-8')
        """导出excel表"""
        try:
            if row:
                # 添加第一页数据表
                w = ws.add_sheet('统计报表')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                w.col(0).width = 256 * 20
                w.col(1).width = 512 * 20
                # 行高
                tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'序号', u'单位名称', u'总数', u'甲状腺', u'卵巢', u'乳腺', u'浅表']
                i = 0
                while i < 7:
                    w.write(0, i, heads[i], style)
                    first_row = w.row(i)
                    first_row.set_style(tall_style)
                    i = i + 1
                # 写入每一行对应的数据
                y = 1
                unitlist = loginView.getAllUnit()
                for item in row:
                    w.write(y, 0, y, stylecount)
                    if item[0] in unitlist:
                        w.write(y, 1, unitlist[item[0]], stylecount)
                    else:
                        w.write(y, 1, item[0], stylecount)
                    w.write(y, 2, item[1], stylecount)
                    w.write(y, 3, item[2], stylecount)
                    w.write(y, 4, item[3], stylecount)
                    w.write(y, 5, item[4], stylecount)
                    w.write(y, 6, item[5], stylecount)
                    y = y + 1

            else:
                return False
            # 保存到本地
            newFilePath = settings.STA_PATH
            if not os.path.exists(newFilePath):
                os.makedirs(newFilePath)
            ws.save(settings.STA_PATH + '/upMecNumExcel.xls')
            return settings.STA_PATH + '/upMecNumExcel.xls'
        except Exception as e:
            print(e)
            return False

    # 导出统计选择部位的上传数量Excel
    def StatisticPosiExcel(row):
        # 导出病例主表Excel文件
        # 设置HTTPResponse的类型
        response = HttpResponse(content_type='application/vnd.ms-excel')
        response['Content-Disposition'] = 'attachment;filename=labExcel.xls'
        style = excelStyle.style
        stylecount = excelStyle.stylecount
        # 创建工作簿
        ws = xlwt.Workbook(encoding='utf-8')
        """导出excel表"""
        try:
            if row:
                # 添加第一页数据表
                w = ws.add_sheet('统计报表')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                w.col(0).width = 256 * 20
                w.col(1).width = 512 * 20
                # 行高
                tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'序号', u'单位名称', u'总数', u'已入库', u'审核中', u'本周上传', u'本月上传']
                i = 0
                while i < 7:
                    w.write(0, i, heads[i], style)
                    first_row = w.row(i)
                    first_row.set_style(tall_style)
                    i = i + 1
                # 写入每一行对应的数据
                y = 1
                unitlist = loginView.getAllUnit()
                for item in row:
                    w.write(y, 0, y, stylecount)
                    if item[0] in unitlist:
                        w.write(y, 1, unitlist[item[0]], stylecount)
                    else:
                        w.write(y, 1, item[0], stylecount)
                    w.write(y, 2, item[1], stylecount)
                    w.write(y, 3, item[2], stylecount)
                    w.write(y, 4, item[3], stylecount)
                    w.write(y, 5, item[4], stylecount)
                    w.write(y, 6, item[5], stylecount)
                    y = y + 1

            else:
                return False
            # 保存到本地
            newFilePath = settings.STA_PATH
            if not os.path.exists(newFilePath):
                os.makedirs(newFilePath)
            ws.save(settings.STA_PATH + '/statisticPosiExcel.xls')
            return settings.STA_PATH + '/statisticPosiExcel.xls'
        except Exception as e:
            print(e)
            return False

    # 导出病例Excel(一个excel)
    def imp_case_excel_one(patientlist):
        # 导出病例主表Excel文件
        # 设置HTTPResponse的类型
        response = HttpResponse(content_type='application/vnd.ms-excel')
        response['Content-Disposition'] = 'attachment;filename=oneExcel.xls'
        style = excelStyle.style
        stylecount = excelStyle.stylecount
        # 创建工作簿
        # ws = xlwt.Workbook(encoding='utf-8')
        ws = xlsxwriter.Workbook('/RAID5/eksjk/storage/oneExcel.xls')                  #  原*（需线上）
        # ws = xlsxwriter.Workbook('F:/imgtest/oneExcel.xls')                              #  新* (线下测试路径)
        """导出excel表"""
        try:
            if patientlist:
                xfyyc = 1
                jzxax = 1
                zsxxzs = 1
                sga = 1
                ax = 1
                # 添加第一页数据表
                w = ws.add_worksheet('性发育异常')  # 新建sheet（sheet的名称为"患者信息"）
                style = ws.add_format({
                    # 'bold': True,  # 字体加粗
                    'border': 1,  # 单元格边框宽度
                    'align': 'left',  # 水平对齐方式
                    'valign': 'vcenter',  # 垂直对齐方式
                    # 'fg_color': '#F4B084',  # 单元格背景颜色
                    # 'text_wrap': True,  # 是否自动换行
                })
                stylecount = ws.add_format({
                    'border': 1,  # 单元格边框宽度
                    'align': 'left',  # 水平对齐方式
                    'valign': 'vcenter',  # 垂直对齐方式
                })
                # 写入表头
                heads = [u'病历号', u'患者姓名', u'国际疾病分类', u'性别', u'性腺性别', u'初诊时间', u'出生日期', u'年龄', u'主诉',
                         u'籍贯', u'父亲身高', u'母亲身高', u'家族史', u'胎龄周', u'出生体重', u'出生身长',
                         u'出生方式', u'保胎史', u'既往史', u'身份证号码', u'家庭地址', u'联系人姓名', u'与患者关系',
                         u'联系电话', u'病例编号', u'现身高:', u'现身高标准差:', u'现体重:', u'BMI:', u'外生殖器分期/双乳发育分期：',
                         u'阴毛分期:', u'其他:', u'生殖器信息', u'生殖器评估', u'骨龄', u'B超图像说明', u'LH', u'FSH',
                         u'睾酮T', u'雌二醇E2', u' DHT', u' 游离睾酮', u' SHBG', u' IGF-1', u' IGFBP-3',
                         u'抗缪勒管激素（AMH）', u'抑制素B（INHB）', u'磁共振', u'其他', u'促肾上腺皮质激素（ACTH）(pg/ml)',
                         u' 皮质醇(ug/dl)', u' 17-OHP(nmol/l)', u' 硫酸脱氢表雄酮(ug/dl)', u' 雄烯二酮(ng/ml)', u'HCG激发试验',
                         u'LHRH激发试验', u'染色体核型', u'基因变异', u'生物样本库', u'手术情况', u'病理结果',u'处理意见', u'其他',u'诊断',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案',
                         u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',u'阴毛分期', u'LH', u'FSH',
                         u'睾酮T',u'雌二醇E2',u'DHT',u'游离睾酮',u'SHBG',u'IGF-1',u'IGFBP-3',u'性腺B超',u'其他',u'诊疗方案', ]
                i = 0
                while i < len(heads):
                    w.write(0, i, heads[i], style)
                    # first_row = w.row(i)
                    # first_row.set_style(tall_style)
                    i = i + 1

                # 添加第二页数据表
                sheet2 = ws.add_worksheet('矮小症')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                # sheet2.col(0).width = 256 * 20
                # sheet2.col(1).width = 512 * 20
                # # 行高
                # tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'病历号', u'患者姓名', u'国际疾病分类', u'性别', u'出生日期', u'身份证号码', u'家庭住址', u'联系人姓名', u'与患者关系', u'联系电话', 
                         u'出生体重', u'出生身长', u'孕周', u'分娩方式', u'窒息抢救史',u'病例编号', u'初次就诊时间', u'初诊年龄', u'主诉', 
                         u'生长速率', u'初次遗精(男)', u'月经初潮（女）', u'父亲身高', u'母亲身高', u'身高', u'遗传身高', u'体重', u'BMI', u'外生殖器分期（男）', u'左侧乳腺发育分期（女孩）', u'右侧乳腺发育分期（女孩）',
                         u'阴毛分期', u'臂长', u'特殊面容', u'脊柱侧弯', u'皮疹', u'运动发育落后', u'语言发育落后', u'智力发育落后', u'听力异常',
                         u'反复感染史', u'抽搐史', u'首次诊疗方案', u'用药剂型', u'用药剂量', u'单位剂量', u'其他', u'LH（mIU/mL）', u'FSH（mIU/mL）', u'E2（pg/mL）', 
                         u'T（ng/dL）', u'PRL（ng/mL）', u' IGF-1（ng/mL）', u' IGFBP-3（ug/mL）',u'甲功',u'甲功异常', u'ACTH(8am)（pg/mL）', u' 皮质醇（8am）（ug/dL）', u'DHEAs（ug/dL）', 
                         u'17-OHP（nmol/L）', u'血常规', u'血常规异常', u'尿常规',u'尿常规异常', u'肝肾脂糖电解质', u'肝肾脂糖电解质异常', u'乙肝三系', u'Gh药物激发试验-Gh峰值(ng/ml)', u'心电图',u'性腺B超', u'垂体MRI',
                         u'左侧甲状腺b超', u'右侧甲状腺b超', u'染色体核型', u'生物样本库', u'父亲生物样本库', u'母亲生物样本库', u'致病基因',u'主要诊断:', u'次要诊断:'
                         ]
                i = 0
                while i < len(heads):
                    sheet2.write(0, i, heads[i], style)
                    i = i + 1
                # 添加第三页数据表
                sheet3 = ws.add_worksheet('中枢性性早熟')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                # sheet3.col(0).width = 256 * 20
                # sheet3.col(1).width = 512 * 20
                # # 行高
                # tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'病历号', u'患者姓名', u'国际疾病分类', u'性别', u'性腺性别', u'初诊时间', u'出生日期', u'年龄', u'主诉',
                         u'籍贯', u'父亲身高', u'母亲身高', u'家族史', u'胎龄周', u'出生体重', u'出生身长',
                         u'出生方式', u'保胎史', u'既往史', u'身份证号码', u'家庭地址', u'联系人姓名', u'与患者关系',
                         u'联系电话', u'病例编号', u'父亲身高', u'父亲体重', u'母亲身高', u' 母亲体重', u'初潮年龄', u'兄弟姐妹',
                         u'既往史', u'初次就诊时间', u'初诊年龄', u'主诉', u'生长速率', u'初次遗精/月经初潮', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'LH', u'FSH', u'E2', u'T', u'SHBG', u'PRL', u' IGF-1', u'IGFBP-3', u'甲功', u'ACTH（8am）',
                         u' 皮质醇（8am）', u'DHEAs', u'17-OHP', u'肝肾脂糖电解质', u'心电图', u'性腺B超', u'垂体MRI',
                         u'LH峰值', u'FSH峰值', u' LH峰值/FSH峰值', u'诊疗方案', u'左侧甲状腺b超', u'右侧甲状腺b超', u'生物样本库', u'染色体核型',
                         u'致病基因', u'主要诊断', u'次要诊断', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案', u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期/双乳发育分期',
                         u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'糖化血红蛋白',
                         u'性腺B超',  u'其他', u'诊疗方案']
                i = 0
                while i < len(heads):
                    sheet3.write(0, i, heads[i], style)
                    # first_row = sheet3.row(i)
                    # first_row.set_style(tall_style)
                    i = i + 1

                # 添加第四页表数据表
                sheet4 = ws.add_worksheet('sga')
                # 写入表头
                heads = [u'病历号', u'患者姓名', u'国际疾病分类', u'性别', u'出生日期', u'身份证号码', u'家庭住址', u'联系人姓名', u'与患者关系', u'联系电话', 
                         u'出生体重', u'出生身长', u'孕周', u'分娩方式', u'窒息抢救史',u'病例编号', u'母亲孕期疾病',u'是否多胎',u'胎产次',u'初次就诊时间', u'初诊年龄', u'主诉', 
                         u'生长速率', u'初次遗精(男)', u'月经初潮（女）', u'父亲身高', u'母亲身高', u'身高', u'遗传身高', u'体重', u'BMI', u'外生殖器分期（男）', u'左侧乳腺发育分期（女孩）', u'右侧乳腺发育分期（女孩）',
                         u'阴毛分期', u'臂长', u'特殊面容', u'脊柱侧弯', u'皮疹', u'运动发育落后', u'语言发育落后', u'智力发育落后', u'听力异常',
                         u'反复感染史', u'抽搐史', u'首次诊疗方案', u'用药剂型', u'用药剂量', u'单位剂量', u'其他', u'LH（mIU/mL）', u'FSH（mIU/mL）', u'E2（pg/mL）', 
                         u'T（ng/dL）', u'PRL（ng/mL）', u' IGF-1（ng/mL）', u' IGFBP-3（ug/mL）',u'甲功',u'甲功异常', u'ACTH(8am)（pg/mL）', u' 皮质醇（8am）（ug/dL）', u'DHEAs（ug/dL）', 
                         u'17-OHP（nmol/L）', u'血常规', u'血常规异常', u'尿常规',u'尿常规异常', u'肝肾脂糖电解质', u'肝肾脂糖电解质异常', u'乙肝三系', u'Gh药物激发试验-Gh峰值(ng/ml)', u'心电图',u'性腺B超', u'垂体MRI',
                         u'左侧甲状腺b超', u'右侧甲状腺b超', u'染色体核型', u'生物样本库', u'父亲生物样本库', u'母亲生物样本库', u'致病基因',u'主要诊断:', u'次要诊断:'
                         ]
                i = 0
                while i < len(heads):
                    sheet4.write(0, i, heads[i], style)
                    i = i + 1
                suifangmaxshort = 1

                # 添加第五页数据表
                sheet5 = ws.add_worksheet('家族性矮小')  # 新建sheet（sheet的名称为"患者信息"）
                # 设置列宽，一个中文等于两个英文等于两个字符，20为字符数，256为衡量单位
                # sheet2.col(0).width = 256 * 20
                # sheet2.col(1).width = 512 * 20
                # # 行高
                # tall_style = xlwt.easyxf('font:height 250')
                # 写入表头
                heads = [u'病历号', u'患者姓名', u'国际疾病分类', u'性别', u'出生日期', u'身份证号码', u'家庭住址', u'联系人姓名', u'与患者关系', u'联系电话', 
                         u'出生体重', u'出生身长', u'孕周', u'分娩方式', u'窒息抢救史',u'病例编号', u'初次就诊时间', u'初诊年龄', u'主诉', 
                         u'生长速率', u'初次遗精(男)', u'月经初潮（女）', u'父亲身高', u'母亲身高', u'身高', u'遗传身高', u'体重', u'BMI', u'外生殖器分期（男）', u'左侧乳腺发育分期（女孩）', u'右侧乳腺发育分期（女孩）',
                         u'阴毛分期', u'臂长', u'特殊面容', u'脊柱侧弯', u'皮疹', u'运动发育落后', u'语言发育落后', u'智力发育落后', u'听力异常',
                         u'反复感染史', u'抽搐史', u'首次诊疗方案', u'用药剂型', u'用药剂量', u'单位剂量', u'其他', u'LH（mIU/mL）', u'FSH（mIU/mL）', u'E2（pg/mL）', 
                         u'T（ng/dL）', u'PRL（ng/mL）', u' IGF-1（ng/mL）', u' IGFBP-3（ug/mL）',u'甲功',u'甲功异常', u'ACTH(8am)（pg/mL）', u' 皮质醇（8am）（ug/dL）', u'DHEAs（ug/dL）', 
                         u'17-OHP（nmol/L）', u'血常规', u'血常规异常', u'尿常规',u'尿常规异常', u'肝肾脂糖电解质', u'肝肾脂糖电解质异常', u'乙肝三系', u'Gh药物激发试验-Gh峰值(ng/ml)', u'心电图',u'性腺B超', u'垂体MRI',
                         u'左侧甲状腺b超', u'右侧甲状腺b超', u'染色体核型', u'生物样本库', u'父亲生物样本库', u'母亲生物样本库', u'致病基因',u'主要诊断:', u'次要诊断:'
                         ]
                i = 0
                while i < len(heads):
                    sheet5.write(0, i, heads[i], style)
                    i = i + 1

                for patient in patientlist:
                    # 循环全部，出错看所有循环项
                    print(patient.id)
                    if patient.id == 9084:
                        print(patient.id)
                    # 性发育异常
                    if patient.dis_class == '10000001':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        follow = models.PatFoll.objects.filter(patient__pk=patient.id)
                        # 写入每一行对应的数据
                        # 病历号
                        w.write(xfyyc, 0, patient.medrec_num, stylecount)
                        # 患者姓名
                        w.write(xfyyc, 1, patient.name, stylecount)
                        # 国际疾病分类
                        if safe_str(patient.ICD) and len(patient.ICD) > 0:
                            # 转换为字典
                            ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                            # 获取 patient.ICD 对应的 label
                            ICD = ICD_dict.get(patient.ICD, "未选择")
                            w.write(xfyyc, 2, ICD, stylecount)
                        else:
                            w.write(xfyyc, 2, "未选择", stylecount)
                        # 性别
                        if patient.sex == '2':
                            w.write(xfyyc, 3, "女", stylecount)
                        elif patient.sex == '1':
                            w.write(xfyyc, 3, "男", stylecount)
                        else:
                            w.write(xfyyc, 3, "未选择", stylecount)
                        # 性腺性别
                        if patient.gonadal_sex == '2':
                            w.write(xfyyc, 4, "女", stylecount)
                        elif patient.gonadal_sex == '1':
                            w.write(xfyyc, 4, "男", stylecount)
                        else:
                            w.write(xfyyc, 4, "未选择", stylecount)
                        # 初诊时间
                        # w.write(xfyyc, 5, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        if patient.fir_vis_time is not None:
                            w.write(xfyyc, 5, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            w.write(xfyyc, 5, "未选择", stylecount)
                        # 出生日期
                        # w.write(xfyyc, 5, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        if patient.birth_time is not None:
                            w.write(xfyyc, 6, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            w.write(xfyyc, 6, "未选择", stylecount)
                        # 年龄
                        w.write(xfyyc, 7, patient.age, stylecount)
                        # 主诉
                        w.write(xfyyc, 8, patient.chi_com, stylecount)
                        # 籍贯
                        natPla = patient.nat_pla
                        if natPla is not None and len(natPla)>0:
                            # 第一种方法
                            natPList = ast.literal_eval(natPla)
                            # 第1-3个
                            # 选择两个
                            if natPList is not None and len(natPList) == 2:
                                one_data = natPList[0]
                                two_data = natPList[1]
                            elif natPList is not None and len(natPList) == 3:
                                one_data = natPList[0]
                                two_data = natPList[1]
                                three_data = natPList[2]
                            # 获取比对
                            # 选择两个
                            if  ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 2:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                w.write(xfyyc, 9, (one or "未知") + "/" + (two or "未知"), stylecount)
                            # 选择三个
                            elif ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 3:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                three = area.get(three_data)
                                w.write(xfyyc, 9, (one or "未知") + "/" + (two or "未知") + "/" + (three or "未知"), stylecount)
                            # 未选择
                            else:
                                w.write(xfyyc, 9, "未选择籍贯", stylecount)
                        # 父亲身高
                        w.write(xfyyc, 10, patient.FHt, stylecount)
                        # 母亲身高
                        w.write(xfyyc, 11, patient.MHt, stylecount)
                        # 家族史
                        if patient.family_his == '1':
                            w.write(xfyyc, 12, "无", stylecount)
                        else:
                            w.write(xfyyc, 12, patient.family_his, stylecount)
                        w.write(xfyyc, 13, patient.ges_week, stylecount)  # 胎龄周
                        w.write(xfyyc, 14, patient.BWt, stylecount)  # 出生体重
                        w.write(xfyyc, 15, patient.BL, stylecount)  # 出生身长
                        # 出生方式
                        if patient.cesa_sec == '1':
                            w.write(xfyyc, 16, "刨宫产", stylecount)
                        elif patient.cesa_sec == '0':
                            w.write(xfyyc, 16, "自然产", stylecount)
                        else:
                            w.write(xfyyc, 16, "未选择", stylecount)
                        # 保胎史
                        if patient.fet_pro_his == '1':
                            w.write(xfyyc, 17, "无", stylecount)
                        else:
                            w.write(xfyyc, 17, patient.fet_pro_his, stylecount)
                        w.write(xfyyc, 18, patient.past_his, stylecount)  # 既往史
                        w.write(xfyyc, 19, patient.card, stylecount)  # 身份证号码
                        w.write(xfyyc, 20, patient.fam_adr, stylecount)  # 家庭地址
                        w.write(xfyyc, 21, patient.contacts_name, stylecount)  # 联系人姓名
                        w.write(xfyyc, 22, patient.relation, stylecount)  # 与患者关系
                        w.write(xfyyc, 23, patient.contacts_num, stylecount)  # 联系电话
                        w.write(xfyyc, 24, patient.case_num, stylecount)  # 病例编号
                        w.write(xfyyc, 25, safe_str(rseult.Ht) + "(cm)", stylecount)  # 现身高
                        w.write(xfyyc, 26, safe_str(rseult.HSDS) + "(SDS)", stylecount)  # 现身高标准差
                        w.write(xfyyc, 27, safe_str(rseult.Wt) + "(kg)", stylecount)  # 现体重
                        w.write(xfyyc, 28, safe_str(rseult.WSDS) + "(kg/m^2)", stylecount)  # BMI
                        if patient.sex == '1':
                            # 外生殖器分期(男)
                            if (rseult.ex_genitalia is not None) and len(rseult.ex_genitalia) > 0:
                                w.write(xfyyc, 29, "外生殖器分期(男): G" + safe_str(rseult.ex_genitalia), stylecount)
                            else:
                                w.write(xfyyc, 29, "未选择", stylecount)
                        elif patient.sex == '2':
                            # 双乳发育分期(女)
                            if (rseult.breast_dev is not None) and len(rseult.breast_dev) > 0:
                                w.write(xfyyc, 29, "双乳发育分期(女): B" + safe_str(rseult.breast_dev), stylecount)
                            else:
                                w.write(xfyyc, 29, "未选择", stylecount)
                        else:
                            w.write(xfyyc, 29, "未选择", stylecount)
                        w.write(xfyyc, 30, rseult.pubic_hair, stylecount)  # 阴毛分期
                        w.write(xfyyc, 31, rseult.other, stylecount)  # 其他
                        # 生殖器信息
                        stringcount = ""
                        stringcount = stringcount+"阴茎长:"+ safe_str(rseult.penile_length) + "(cm)， 阴茎直径"+safe_str(rseult.penile_dia)+\
                                      "cm， 睾丸容量："+safe_str(rseult.tes_volume) +"ml， Prader分期："+ safe_str(rseult.prader)
                        locaUreOriMap = {
                            '0': '正常',
                            '1': '冠状沟型',
                            '2': '阴茎型',
                            '3': '阴茎阴囊型',
                            '4': '会阴型',
                        }
                        rigTesPosMap = {
                            '1': '在阴唇',
                            '2': '在腹股沟',
                            '3': '在腹部',
                            '4': '睾丸缺如',
                            '5': '在阴囊',
                        }
                        stringcount = stringcount+"尿道口位置："+safe_str(locaUreOriMap.get(rseult.loca_ure_ori))+"右睾丸位置："+safe_str(rigTesPosMap.get(rseult.rig_tes_pos))+"左睾丸位置："+safe_str(rigTesPosMap.get(rseult.lef_tes_pos))
                        w.write(xfyyc, 32, stringcount, stylecount)
                        # 生殖器评估
                        genitalsMap = {
                            '0': '正常男性化',
                            '1': '男性化轻度缺陷的男性表型，如孤立性尿道下裂',
                            '2': '男性化重度缺陷的男性表型，如小阴茎、会阴阴蒂尿道下裂、阴囊裂和/或隐宰',
                            '3': '严重生殖器模糊阴蒂样阴茎、阴唇阴蒂皱褶，单会阴口',
                            '4': '女性表型，后唇融合，阴蒂肥大',
                            '5': '女性表型(成年期有阴毛者为6级，成年期无阴毛者为7级)',
                        }
                        genitals = rseult.genitals
                        finamGenitals = genitalsMap.get(genitals)
                        w.write(xfyyc, 33, finamGenitals, stylecount)
                        # 骨龄
                        w.write(xfyyc, 34,  rseult.bone_age, stylecount)
                        # 图像说明
                        txsm = ""
                        #（男）
                        if patient.sex == '1':
                            if rseult.bscanExplain is not None and len(rseult.bscanExplain)>0:
                                txsm = txsm+"右侧睾丸大小" + safe_str(json.loads(rseult.bscanExplain)['testisLeftOne']) + "(cm)x"+ \
                                       safe_str(json.loads(rseult.bscanExplain)['testisLeftTwo']) + "(cm)x"+ safe_str(json.loads(rseult.bscanExplain)['testisLeftThr']) + \
                                       "(cm).左侧睾丸大小" + safe_str(json.loads(rseult.bscanExplain)['testisRightOne']) + "(cm)x"+ safe_str(json.loads(rseult.bscanExplain)['testisRightTwo']) + \
                                       "(cm)x"+ safe_str(json.loads(rseult.bscanExplain)['testisRightThr']) + "(cm)"
                                w.write(xfyyc, 35, txsm, stylecount)
                        # (女)
                        elif patient.sex == '2':
                            if rseult.bscanExplain is not None and len(rseult.bscanExplain) > 0:
                                captionWoman = "子宫大小" + safe_str(json.loads(rseult.bscanExplain)['uterusOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['uterusTwo']) + "*" + safe_str(json.loads(rseult.bscanExplain)['uterusThr']) + "(cm)，内膜厚度：" + safe_str(json.loads(rseult.bscanExplain)['intima']) + "(cm)" + "\n" + \
                                               "左侧卵巢大小约：" + safe_str(json.loads(rseult.bscanExplain)['ovaLeftOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaLeftTwo']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaLeftThr']) + "(cm)" + "\n" + \
                                               "左侧卵巢大小约：" + safe_str(json.loads(rseult.bscanExplain)['ovaRightOne']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaRightTwo']) + "*" + safe_str(json.loads(rseult.bscanExplain)['ovaRightThr']) + "(cm)" + "\n" + \
                                               "最大滤泡直径大小：" + safe_str(json.loads(rseult.bscanExplain)['follDiameter']) + "(cm)"
                                w.write(xfyyc, 35, captionWoman, stylecount)
                        else:
                            w.write(xfyyc, 35, "未填写", stylecount)

                        w.write(xfyyc, 36, safe_str(rseult.LH) + "(mIU/mL)", stylecount)  # LH
                        w.write(xfyyc, 37, safe_str(rseult.FSH) + "(mIU/mL)", stylecount)  # FSH
                        w.write(xfyyc, 38, safe_str(rseult.T) + "(ng/dL)", stylecount)  # 睾酮T
                        w.write(xfyyc, 39, safe_str(rseult.E2) + "(pg/mL)", stylecount)  # 雌二醇E2
                        w.write(xfyyc, 40, safe_str(rseult.DHT) + "(ng/mL)", stylecount)  # DHT
                        w.write(xfyyc, 41, safe_str(rseult.FT) + "(ng/mL)", stylecount)  # 游离睾酮
                        w.write(xfyyc, 42, safe_str(rseult.SHBG) + "(nmol/L)", stylecount)  # SHBG
                        w.write(xfyyc, 43, safe_str(rseult.IGF1) + "(ng/mL)", stylecount)  # IGF-1
                        w.write(xfyyc, 44, safe_str(rseult.IGFBP3) + "(μg/mL)", stylecount)  # IGFBP-3
                        w.write(xfyyc, 45, rseult.AMH, stylecount)  # 抗缪勒管激素
                        w.write(xfyyc, 46, rseult.INHB, stylecount)  # 抑制素B
                        w.write(xfyyc, 47, rseult.MRI, stylecount)  # 磁共振
                        w.write(xfyyc, 48, rseult.body_other, stylecount)  # 其他
                        w.write(xfyyc, 49, rseult.ACTH, stylecount)  # 促肾上腺皮质激素
                        w.write(xfyyc, 50, rseult.Hyd, stylecount)  # 皮质醇
                        w.write(xfyyc, 51, rseult.OHP, stylecount)  # 17-OHP
                        w.write(xfyyc, 52, rseult.DHEAS, stylecount)  # 硫酸脱氢表雄酮
                        w.write(xfyyc, 53, rseult.AD, stylecount)  # 雄烯二酮
                        # HCG激发试验1=无，2=标准HCG激发，3=延长HCG激发
                        hcgjf = ""
                        if rseult.HCG == "1":
                            hcgjf = "无"
                        elif rseult.HCG == "2":
                            hcgjf = "标准HCG激发T：" + safe_str(rseult.HCGT)+"ng/dL, 标准HCG激发激发DHT:"+safe_str(rseult.HCGDHT)+"ng/ml， 标准HCG激发激发AD："+safe_str(rseult.HCGAD)+"ng/ml"
                        else:
                            hcgjf = "延长HCG激发T：" + safe_str(rseult.HCGT_ext) + "ng/dL,  延长HCG激发激发DHT:" + safe_str(rseult.HCGDHT_ext) + "ng/ml， 延长HCG激发激发AD：" + safe_str(rseult.HCGAD_ext) + "ng/ml"
                        w.write(xfyyc, 54, hcgjf, stylecount)
                        w.write(xfyyc, 55, "LH峰值："+safe_str(rseult.LHmax)+"mIU/ml FSH峰值："+safe_str(rseult.FSHmax)+"mIU/ml", stylecount)
                        w.write(xfyyc, 56, rseult.spe_kar, stylecount)  # 染色体核型
                        # 解析检测
                        if rseult.gen_mut_name:
                            data_list = json.loads(rseult.gen_mut_name)
                            row_count = 1
                            jccount = ""
                            if data_list is not None and len(data_list) > 0:
                                for item in data_list:
                                    jccount=jccount+"致病基因名称: " + safe_str(item['genName']) + "\n" + "核酸变异：" + safe_str(
                                                     item['Rna']) + "\n" + "氨基酸变异：" + safe_str(
                                                     item['amino']) + "\n" + "父亲：" + safe_str(
                                                     item['father']) + "\n" + "母亲：" + safe_str(item['mother'])
                                    row_count += 1
                            else:
                                jccount = jccount+"致病基因名称: 无填写 " + "\n" + "核酸变异：无填写" + "\n" + "氨基酸变异：无填写" + "\n" + "父亲：无填写" + "\n" + "母亲：无填写"
                            w.write(xfyyc, 57, jccount, stylecount)
                        # 生物样本库
                        if rseult.biolog == '无' or rseult.biolog is None or len(rseult.biolog) == 0:
                            w.write(xfyyc, 58, "样本编号：无填写," + "\n" + "样本类型：无填写", stylecount)  # 样本编号
                        else:
                            data_str = rseult.biolog_bank.replace("'", '"')
                            data = json.loads(data_str)
                            if data:
                                data = data[0]
                                # 此处有bug，无法添加第二条。
                                map = {
                                    '1': 'DNA样本',
                                    '2': '血清',
                                    '3': '血浆',
                                    '4': '尿液',
                                }
                                finalname = map.get(data['name'])
                                w.write(xfyyc, 58, "样本编号：" + safe_str(data['id']) + "\n" + "样本类型：" + safe_str(finalname),stylecount)
                        w.write(xfyyc, 59, rseult.operation, stylecount)  # 手术情况
                        w.write(xfyyc, 60, rseult.pat_res, stylecount)  # 病理结果
                        w.write(xfyyc, 61, rseult.han_opi, stylecount)  # 处理意见
                        w.write(xfyyc, 62, rseult.other, stylecount)  # 其他
                        # 诊断
                        if rseult.diagnosis is not None and len(rseult.diagnosis) > 0:
                            diaData = rseult.diagnosis.replace("'", '"')
                            Map = {
                                tuple(['A', 'A01']): '性染色体异常 -> 45，X(turner综合征及其变体)',
                                tuple(['A', 'A02']): '性染色体异常 -> 47，XXY(Klinefelter综合征及其变体)',
                                tuple(['A', 'A03']): '性染色体异常 -> 45，X/46，XY[混合性性腺发育不良(MGD),卵睾DSD]',
                                tuple(['A', 'A04']): '性染色体异常 -> 46，XX/46，XY(嵌合体,卵睾DSD)',
                                tuple(['B', 'B01', 'B01A']): '46，XY -> 性腺（睾丸）发育不良 -> 完全性腺发育不良（swyer综合征）',
                                tuple(['B', 'B01', 'B01B']): '46，XY -> 性腺（睾丸）发育不良 -> 部分性腺发育不良睾丸',
                                tuple(['B', 'B01', 'B01C']): '46，XY -> 性腺（睾丸）发育不良 -> 退化综合征',
                                tuple(['B', 'B01', 'B01D']): '46，XY -> 性腺（睾丸）发育不良 -> 卵睾DSD',
                                tuple(['B', 'B02', 'B02A']): '46，XY -> 雄激素合成或作用障碍 -> 雄激素合成障碍（5a-还原酶缺乏，17-羟基类固醇脱氢酶缺乏）',
                                tuple(
                                    ['B', 'B02', 'B02B']): '46，XY -> 雄激素合成或作用障碍 -> 雄激素作用障碍（完全性雄激素不敏感综合征，部分性雄激素不敏感综合征）',
                                tuple(['B', 'B02', 'B02C']): '46，XY -> 雄激素合成或作用障碍 -> LH受体缺乏（间质细胞萎缩）',
                                tuple(['B', 'B02', 'B02D']): '46，XY -> 雄激素合成或作用障碍 -> AMH的缺乏及AMH受体障碍（持续性副中肾管综合征）',
                                tuple(['B', 'B03', 'B03A']): '46，XY -> 其他 -> 严重的尿道下裂',
                                tuple(['B', 'B03', 'B03B']): '46，XY -> 其他 -> 泄殖腔外翻',
                                tuple(['C', 'C01', 'C01A']): '46，XX -> 性腺（卵巢）发育不良 -> 性腺发育不良',
                                tuple(['C', 'C01', 'C01B']): '46，XX -> 性腺（卵巢）发育不良 -> 卵睾DSD',
                                tuple(['C', 'C01', 'C01C']): '46，XX -> 性腺（卵巢）发育不良 -> 睾丸性DSD',
                                tuple(['C', 'C02', 'C02A']): '46，XX -> 雄激素过多 -> 胎儿源性（21-羟化酶缺乏，11-羟化酶缺乏）',
                                tuple(['C', 'C02', 'C02B']): '46，XX -> 雄激素过多 -> 胎盘源性（芳香化酶缺乏）',
                                tuple(['C', 'C02', 'C02C']): '46，XX -> 雄激素过多 -> 母体源（黄体瘤，孕期服用雄激素）',
                                tuple(['C', 'C03', 'C03A']): '46，XX -> 其他 -> 阴道闭锁',
                                tuple(['C', 'C03', 'C03B']): '46，XX -> 其他 -> 泄殖腔外翻',
                                tuple(['C', 'C03', 'C03C']): '46，XX -> 其他 -> MURCS等',
                            }
                            dia = json.loads(diaData)
                            finalDia = Map.get(tuple(dia))
                            w.write(xfyyc, 63, finalDia, stylecount)  # 诊断
                        else:
                            w.write(xfyyc, 63, "未选择", stylecount)  # 诊断
                        # if patient.name == "蔡威":
                        #     print(patient.name)
                        iFlCount = 64
                        for iFl in follow:
                            # 随访
                            # 随访日期
                            w.write(xfyyc, iFlCount, iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                            iFlCount += 1
                            # 年龄
                            w.write(xfyyc, iFlCount, safe_str(iFl.age), stylecount)
                            iFlCount += 1
                            # 身高
                            w.write(xfyyc, iFlCount, safe_str(iFl.Ht), stylecount)
                            iFlCount += 1
                            # 体重
                            w.write(xfyyc, iFlCount, safe_str(iFl.Wt), stylecount)
                            iFlCount += 1
                            # 外生殖器分期
                            if patient.sex == '1':
                                # 外生殖器分期(男)
                                if (iFl.gen_stag is not None) and len(iFl.gen_stag) > 0:
                                    w.write(xfyyc, iFlCount, "外生殖器分期(男): G" + safe_str(iFl.gen_stag),stylecount)
                                    iFlCount += 1
                                else:
                                    w.write(xfyyc, iFlCount, "未选择", stylecount)
                                    iFlCount += 1
                            elif patient.sex == '2':
                                # 双乳发育分期(女)
                                if (iFl.gen_stag is not None) and len(iFl.gen_stag) > 0:
                                    w.write(xfyyc, iFlCount, "双乳发育分期(女): B" + safe_str(iFl.gen_stag), stylecount)
                                    iFlCount += 1
                                else:
                                    w.write(xfyyc, iFlCount, "未选择", stylecount)
                                    iFlCount += 1
                            else:
                                w.write(xfyyc, iFlCount, "未选择", stylecount)
                                iFlCount += 1

                            # 阴毛分期
                            w.write(xfyyc, iFlCount, safe_str(iFl.pub_stag), stylecount)
                            iFlCount += 1
                            # LH
                            w.write(xfyyc, iFlCount, safe_str(iFl.LH), stylecount)
                            iFlCount += 1
                            # FSH
                            w.write(xfyyc, iFlCount, safe_str(iFl.FSH), stylecount)
                            iFlCount += 1
                            # 睾酮T
                            w.write(xfyyc, iFlCount, safe_str(iFl.T), stylecount)
                            iFlCount += 1
                            # 雌二醇E2
                            w.write(xfyyc, iFlCount, safe_str(iFl.E2), stylecount)
                            iFlCount += 1
                            # DHT
                            w.write(xfyyc, iFlCount, safe_str(iFl.DHT), stylecount)
                            iFlCount += 1
                            # 游离睾酮
                            w.write(xfyyc, iFlCount, safe_str(iFl.yltg), stylecount)
                            iFlCount += 1
                            # SHBG
                            w.write(xfyyc, iFlCount, safe_str(iFl.SHBG), stylecount)
                            iFlCount += 1
                            # IGF-1
                            w.write(xfyyc, iFlCount, safe_str(iFl.IGF1), stylecount)
                            iFlCount += 1
                            # IGFBP-3
                            w.write(xfyyc, iFlCount, safe_str(iFl.IGFBP3), stylecount)
                            iFlCount += 1
                            if patient.sex == '1':
                            # 男
                                # 性腺B超-睾丸大小右侧 和 右侧:
                                w.write(xfyyc, iFlCount, "右侧" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisRightOne']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult)['testisRightTwo']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "(cm)x，长颈：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "(cm)" + "\n" + "左侧" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftOne']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "(cm)x" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftThr']) + "(cm)x，长颈：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftLon']) + "(cm)", stylecount)
                                iFlCount += 1
                            # 男
                            elif patient.sex == '2':
                                # 判断随访囊肿(是否存在存在)
                                if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                    cyst_info = "有，" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                                elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)[
                                    'isCyst'] == '2':
                                    cyst_info = "无"
                                else:
                                    cyst_info = "未选择"
                                # 代码中引用 cyst_info
                                iFl_listWoman = "子宫三径约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['uterusThr']) + "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['intima']) + "(cm)" + "\n" + \
                                                "左侧卵巢大小约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaLeftThr']) + "(cm)" + "\n" + \
                                                "右侧卵巢大小约:" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['ovaRightThr']) + "(cm)，最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['follDiameter']) + "(cm)" + "\n" + \
                                                "有无囊肿：" + cyst_info + "\n"
                                w.write(xfyyc, iFlCount, iFl_listWoman, stylecount) 
                                iFlCount += 1
                            # 其他
                            w.write(xfyyc, iFlCount, safe_str(iFl.other), stylecount)
                            iFlCount += 1
                            # 诊疗方案
                            iFl_list = ""
                            if iFl.dia_trea_plan != "无":
                                if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                    iFl_listM = iFl_list + "雄激素替代治疗(药名，剂量，用法)" + safe_str(json.loads(iFl.dia_trea_plan)['rhGH'])
                                    w.write(xfyyc, iFlCount, iFl_listM, stylecount)
                                    iFlCount += 1
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                    iFl_listW = iFl_list + "雌激素替代治疗(药名，剂量，用法)" + safe_str(json.loads(iFl.dia_trea_plan)['rhGH'])
                                    w.write(xfyyc, iFlCount, iFl_listW, stylecount)
                                    iFlCount += 1
                                else:
                                    w.write(xfyyc, iFlCount, "未选择", stylecount)
                                    iFlCount += 1
                            else:
                                w.write(xfyyc, iFlCount, "未选择", stylecount)
                                iFlCount += 1
                        xfyyc = xfyyc + 1
                    # 家族性矮小
                    elif patient.dis_class == '10000002':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        follow = models.PatFoll.objects.filter(patient__pk=patient.id)

                        # 写入每一行对应的数据
                        # 病历号
                        sheet2.write(jzxax, 0, patient.medrec_num, stylecount)
                        # 患者姓名
                        sheet2.write(jzxax, 1, patient.name, stylecount)
                        # 国际疾病分类
                        if safe_str(patient.ICD) and len(patient.ICD) > 0:
                            # 转换为字典
                            ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                            # 获取 patient.ICD 对应的 label
                            ICD = ICD_dict.get(patient.ICD, "未选择")
                            sheet2.write(jzxax, 2, ICD, stylecount)
                        else:
                            sheet2.write(jzxax, 2, "未选择", stylecount)
                        # 性别
                        if patient.sex == '2':
                            sheet2.write(jzxax, 3, "女", stylecount)
                        elif patient.sex == '1':
                            sheet2.write(jzxax, 3, "男", stylecount)
                        else:
                            sheet2.write(jzxax, 3, "", stylecount)
                        # 出生日期
                        if patient.birth_time is not None:
                            sheet2.write(jzxax, 4, patient.birth_time.strftime('%Y-%m-%d'), stylecount)
                        else:
                            sheet2.write(jzxax, 4, "", stylecount)
                        # 身份证号码
                        sheet2.write(jzxax, 5, patient.card, stylecount)
                        # 家庭住址
                        sheet2.write(jzxax, 6, patient.fam_adr, stylecount)
                        # 联系人姓名
                        sheet2.write(jzxax, 7, patient.contacts_name, stylecount)
                        # 与患者关系
                        sheet2.write(jzxax, 8, patient.relation, stylecount)
                        # 联系电话
                        sheet2.write(jzxax, 9, patient.contacts_num, stylecount)
                        # 出生体重
                        sheet2.write(jzxax, 10, patient.BWt, stylecount)
                        # 出生身长
                        sheet2.write(jzxax, 11, patient.BL, stylecount)
                        # 孕周
                        sheet2.write(jzxax, 12, patient.ges_week, stylecount)
                        # 分娩方式
                        cesasecmap = {
                            '1': '自然分娩',
                            '2': '剖宫产',
                            '': ''
                        }
                        try:
                            sheet2.write(jzxax, 13, cesasecmap[patient.cesa_sec], stylecount)
                        except:
                            pass
                        # 窒息抢救史
                        cesaasphyxiamap = {
                            '1': '无',
                            '2': '轻度窒息',
                            '3': '重度窒息',
                            '': ''
                        }
                        try:
                            sheet2.write(jzxax, 14, cesaasphyxiamap[patient.cesa_asphyxia], stylecount)
                        except:
                            pass
                        # 病例编号
                        sheet2.write(jzxax, 15, patient.case_num, stylecount)
                        med_his = rseult.med_his.replace('\n', '')
                        # 初次就诊时间
                        sheet2.write(jzxax, 16, safe_str(json.loads(med_his)['firVisTime']), stylecount) 
                        # 初诊年龄 
                        sheet2.write(jzxax, 17, safe_str(json.loads(med_his)['morbidAge']), stylecount) 
                        # 主诉 
                        sheet2.write(jzxax, 18, safe_str(json.loads(med_his)['chiefCom']), stylecount)  
                        # 生长速率
                        if json.loads(med_his) == 1 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '1':
                            sheet2.write(jzxax, 19, "不详", stylecount)
                        elif json.loads(med_his) == 2 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '2':
                            sheet2.write(jzxax, 19, safe_str(json.loads(med_his)['growRate']) + "(厘米/年)", stylecount)
                        else:
                            sheet2.write(jzxax, 19, "", stylecount)
                        # 初次遗精
                        if patient.sex == '1':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet2.write(jzxax, 20, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet2.write(jzxax, 20, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet2.write(jzxax, 20, "", stylecount)
                        # 月经初潮
                        elif patient.sex == '2':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet2.write(jzxax, 21, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet2.write(jzxax, 21, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet2.write(jzxax, 21, "", stylecount)
                        try:
                            if rseult.fam_his.replace('\n', '') and len(json.loads(rseult.fam_his.replace('\n', '')))>0:
                                # 父亲身高
                                fhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet2.write(jzxax, 22, fhight, stylecount)
                                # 母亲身高
                                mhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet2.write(jzxax, 23, mhight, stylecount)
                                # 遗传身高（通过父母身高计算得出）
                                if fhight and mhight:
                                    if patient.sex == '1': 
                                        ycsg = (float(fhight)+float(mhight)+13)/2
                                    else:
                                        ycsg = (float(fhight)+float(mhight)-13)/2
                                    sheet2.write(jzxax, 25, ycsg, stylecount)
                        except:
                            pass
                        # 身高
                        sheet2.write(jzxax, 24, json.loads(rseult.phy_exa)['height'], stylecount)
                        # 体重  
                        sheet2.write(jzxax, 26, json.loads(rseult.phy_exa)['weight'], stylecount) 
                        # BMI 
                        sheet2.write(jzxax, 27, json.loads(rseult.phy_exa)['Bmi'], stylecount)  
                        # 外生殖器分期（男）
                        if patient.sex == '1':
                            if json.loads(rseult.phy_exa)['exGenitalia'] is not None and len(json.loads(rseult.phy_exa)['exGenitalia']) > 0:
                                sheet2.write(jzxax, 28, "G" + safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                            else:
                                sheet2.write(jzxax, 28, "", stylecount)
                        elif patient.sex == '2':
                            if json.loads(rseult.phy_exa)['breastDev'] is not None and len(json.loads(rseult.phy_exa)['breastDev']) > 0:
                                sheet2.write(jzxax, 29, "B" + safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                            else:
                                sheet2.write(jzxax, 29, "", stylecount)
                            if 'breastDevRight' in json.loads(rseult.phy_exa)  and json.loads(rseult.phy_exa)['breastDevRight'] is not None and len(json.loads(rseult.phy_exa)['breastDevRight']) > 0 and json.loads(rseult.phy_exa)['breastDevRight'] != "null" :
                                sheet2.write(jzxax, 30, "B" + safe_str(json.loads(rseult.phy_exa)['breastDevRight']), stylecount)
                            else:
                                sheet2.write(jzxax, 30, "", stylecount)
                        # 阴毛分期
                        sheet2.write(jzxax, 31, json.loads(rseult.phy_exa)['pubicHair'], stylecount)
                        # 臂长  
                        sheet2.write(jzxax, 32, json.loads(rseult.phy_exa)['armLength'], stylecount)  
                        # 特殊面容
                        if 'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '2':
                            sheet2.write(jzxax, 33, json.loads(rseult.phy_exa)['specialFaceDesc'], stylecount)
                        elif  'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '1':
                            sheet2.write(jzxax, 33, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 33, "", stylecount)
                        # 脊柱侧弯
                        if 'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '2':
                            scolioMap = {
                                '1': '轻度',
                                '2': '中度',
                                '3': '重度',
                            }
                            scoliosisDegree = json.loads(rseult.phy_exa)['scoliosisDegree']
                            finalScolio = scolioMap.get(scoliosisDegree)
                            sheet2.write(jzxax, 34, finalScolio, stylecount)
                        elif  'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '1':
                            sheet2.write(jzxax, 34, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 34, "", stylecount)
                        # 皮疹
                        if 'rash' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['rash'] == 2:
                            sheet2.write(jzxax, 35, json.loads(rseult.phy_exa)['rashDescribe'], stylecount)
                        else:
                            sheet2.write(jzxax, 35, "无", stylecount)

                        # 运动发育落后
                        if 'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '2':
                            sheet2.write(jzxax, 36, json.loads(rseult.mot_dev_back)['sport'], stylecount)
                        elif  'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '1':
                            sheet2.write(jzxax, 36, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 36, "", stylecount)
                        # 语言发育落后
                        if 'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '2':
                            sheet2.write(jzxax, 37, json.loads(rseult.lan_dev_back)['language'], stylecount)
                        elif  'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '1':
                            sheet2.write(jzxax, 37, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 37, "", stylecount)
                        # 智力发育落后
                        if 'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '2':
                            sheet2.write(jzxax, 38, json.loads(rseult.int_dev_back)['intelligence'], stylecount)
                        elif  'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '1':
                            sheet2.write(jzxax, 38, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 38, "", stylecount)
                        # 听力异常
                        if 'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '2':
                            sheet2.write(jzxax, 39, json.loads(rseult.abn_hear)['hear'], stylecount)
                        elif  'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '1':
                            sheet2.write(jzxax, 39, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 39, "", stylecount)
                        # 反复感染史
                        if 'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '2':
                            sheet2.write(jzxax, 40, json.loads(rseult.rec_inf_his)['infection'], stylecount)
                        elif  'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '1':
                            sheet2.write(jzxax, 40, "无", stylecount)
                        else:
                            sheet2.write(jzxax, 40, "", stylecount)
                        # 抽搐史
                        if rseult.con_his == '1':
                            sheet2.write(jzxax, 41, "无", stylecount)
                        elif  rseult.con_his == '2':
                            sheet2.write(jzxax, 41, "有", stylecount)
                        else:
                            sheet2.write(jzxax, 41, "", stylecount)
                        # 诊疗方案
                        if rseult.dia_trea_plan is not None and len(rseult.dia_trea_plan) > 0:
                            # 诊疗方案(多种选择)
                            if rseult.dia_trea_plan:
                                # 治疗1
                                if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                                    sheet2.write(jzxax, 42, "未治疗", stylecount)
                                # 治疗2
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                                    sheet2.write(jzxax, 42, "rhGH治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet2.write(jzxax, 43, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)                     
                                            except:
                                                pass
                                        sheet2.write(jzxax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet2.write(jzxax, 43, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet2.write(jzxax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗3
                                elif  'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                                    sheet2.write(jzxax, 42, "GnRHa治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet2.write(jzxax, 43, "达必佳针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet2.write(jzxax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet2.write(jzxax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet2.write(jzxax, 45, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet2.write(jzxax, 43, "伯恩若康针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet2.write(jzxax, 43, "贝依针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每35天1次", stylecount)
                                # 治疗4
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                                    sheet2.write(jzxax, 42, "GnRHal联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet2.write(jzxax, 43, "达必佳针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet2.write(jzxax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet2.write(jzxax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet2.write(jzxax, 45, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet2.write(jzxax, 43, "伯恩若康针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet2.write(jzxax, 43, "贝依针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet2.write(jzxax, 43, "达菲林针", stylecount)
                                        sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet2.write(jzxax, 45, "3.75mg，每35天1次", stylecount)
                                # 治疗5
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                                    sheet2.write(jzxax, 42, "芳香化酶抑制剂", stylecount)
                                # 治疗6
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '11':
                                    sheet2.write(jzxax, 42, "停止芳香化酶抑制剂", stylecount)
                                # 治疗7
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '10':
                                    sheet2.write(jzxax, 42, "芳香化酶联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet2.write(jzxax, 43, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass                    
                                        sheet2.write(jzxax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet2.write(jzxax, 43, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet2.write(jzxax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet2.write(jzxax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗8
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '12':
                                    sheet2.write(jzxax, 42, "停止芳香化酶联合生长激素治疗", stylecount)
                                # 治疗9
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                                    sheet2.write(jzxax, 42, "停止GnRHa治疗", stylecount)
                                # 治疗10
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                                    sheet2.write(jzxax, 42, "停止GnRHa联合生长激素治疗", stylecount)
                                # 治疗11
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                                    sheet2.write(jzxax, 42, "停止生长激素治疗", stylecount)
                                else:
                                    sheet2.write(jzxax, 42, "", stylecount)
                        # 其他
                        sheet2.write(jzxax, 46, rseult.past_other, stylecount)  
                        sheet2.write(jzxax, 47, safe_str(json.loads(rseult.lab_exa)['LH']), stylecount)  # LH
                        sheet2.write(jzxax, 48, safe_str(json.loads(rseult.lab_exa)['FSH']), stylecount)  # FSH
                        sheet2.write(jzxax, 49, safe_str(json.loads(rseult.lab_exa)['E2']), stylecount)  # E2
                        sheet2.write(jzxax, 50, safe_str(json.loads(rseult.lab_exa)['T']), stylecount)  # T
                        sheet2.write(jzxax, 51, safe_str(json.loads(rseult.lab_exa)['PRL']), stylecount)  # PRL
                        sheet2.write(jzxax, 52, safe_str(json.loads(rseult.lab_exa)['IGF']), stylecount)  # IGF-1
                        sheet2.write(jzxax, 53, safe_str(json.loads(rseult.lab_exa)['IGFBP3']),stylecount)  # IGFBP-3
                        # 甲功
                        if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                            sheet2.write(jzxax, 54, "异常", stylecount)
                            sheet2.write(jzxax, 55, json.loads(rseult.lab_exa)['thyroidDescribe'], stylecount)
                        elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                            sheet2.write(jzxax, 54, "正常", stylecount)
                        sheet2.write(jzxax, 56, safe_str(json.loads(rseult.lab_exa)['ACTH']), stylecount)  # ACTH
                        sheet2.write(jzxax, 57, safe_str(json.loads(rseult.lab_exa)['cortisol']),stylecount)  # 皮质醇（8am）
                        sheet2.write(jzxax, 58, safe_str(json.loads(rseult.lab_exa)['DHEAS']),stylecount)  # DHEAs
                        sheet2.write(jzxax, 59, safe_str(json.loads(rseult.lab_exa)['OHP']),stylecount)  # 17-OHP
                        # 血常规
                        if 'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '2':
                            sheet2.write(jzxax, 60, "异常", stylecount)
                            sheet2.write(jzxax, 61, json.loads(rseult.lab_exa)['bloodDescribe'], stylecount)
                        elif  'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '1':
                            sheet2.write(jzxax, 60, "正常", stylecount)
                        # 尿常规
                        if 'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '2':
                            sheet2.write(jzxax, 62, "异常", stylecount)
                            sheet2.write(jzxax, 63, json.loads(rseult.lab_exa)['urinalysisDescribe'], stylecount)
                        elif  'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '1':
                            sheet2.write(jzxax, 62, "正常", stylecount)
                        # 肝肾脂糖电解质
                        if 'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                            sheet2.write(jzxax, 64, "异常", stylecount)
                            sheet2.write(jzxax, 65, json.loads(rseult.lab_exa)['laklgeDescribe'], stylecount)
                        elif  'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                            sheet2.write(jzxax, 64, "正常", stylecount)
                        # 乙肝三系
                        HBsMap = {
                            '1': '阴性',
                            '2': 'HBSAb阳性',
                            '3': '小三阳',
                            '4': '大三阳',
                        }
                        HBs = json.loads(rseult.lab_exa)['HBs']
                        finalHBs = HBsMap.get(HBs)
                        sheet2.write(jzxax, 66, finalHBs, stylecount)
                        sheet2.write(jzxax, 67, safe_str(json.loads(rseult.lab_exa)['gh']),stylecount)  # Gh药物激发试验-Gh峰值
                        sheet2.write(jzxax, 68, rseult.electr, stylecount)  # 心电图
                        gon_B_ult = rseult.gon_B_ult.replace("\n","")
                        # 性腺B超
                        if patient.sex == '1':
                            gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                 "睾丸大小-左侧：" + safe_str(json.loads(gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                            sheet2.write(jzxax, 69, gonadUltrasoundMan, stylecount)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(gon_B_ult)['uterusThr'])+ "(cm)，宫颈长约：" + safe_str(json.loads(gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                   "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                   "有无囊肿：" + cyst_info
                            sheet2.write(jzxax, 69, gonadUltrasoundWoman, stylecount)
                        # 垂体MRI
                        if 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '2':
                            sheet2.write(jzxax, 70, json.loads(gon_B_ult)['mriDescribe'], stylecount)
                        elif 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '1':
                            sheet2.write(jzxax, 70, "正常", stylecount)
                            # 左侧甲状腺b超
                        if 'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '2':
                            sheet2.write(jzxax, 71, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBOther']), stylecount)
                        elif  'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '1':
                            sheet2.write(jzxax, 71, "正常", stylecount)
                        # 右侧甲状腺b超
                        if 'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '2':
                            sheet2.write(jzxax, 72, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBOther']), stylecount)
                        elif  'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '1':
                            sheet2.write(jzxax, 72, "正常", stylecount)
                        sheet2.write(jzxax, 73, rseult.spe_kar, stylecount)  # 染色体核型
                        # 生物样本库是否存在
                        if rseult.bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号" + safe_str(
                                            item['id']) + "\n" + "样本类型" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet2.write(jzxax, 74, listCount, stylecount)  # 生物样本库
                        # 父亲生物样本库
                        if rseult.f_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.f_bio_sam_bank) and json.loads(rseult.f_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.f_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet2.write(jzxax, 75, listCount, stylecount)  # 生物样本库
                        # 母亲生物样本库
                        if rseult.m_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.m_bio_sam_bank) and json.loads(rseult.m_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.m_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        map = {
                                            '1': 'DNA样本',
                                            '2': '血清',
                                            '3': '血浆',
                                            '4': '尿液',
                                        }
                                        finalname = map.get(item['name'])
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(finalname)
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"
                            sheet2.write(jzxax, 76, listCount, stylecount)  # 生物样本库
                        if rseult.gen_mut_name:
                            genMutName = json.loads(rseult.gen_mut_name)
                            genMutNamecount = ""
                            for genMutNameItem in genMutName:
                                if 'father' in genMutNameItem:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['father'])+","+genMutNameItem['mother']+"/"
                                else:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['ties1'])+","+genMutNameItem['ties2']+","+genMutNameItem['ties3']+","+genMutNameItem['ties4']+","+genMutNameItem['ties5']+","+genMutNameItem['ties6']+"/"
                            sheet2.write(jzxax, 77, genMutNamecount, stylecount)
                        try:
                            main_dia = json.loads(rseult.main_dia)
                            if main_dia['mainDia'] == "['其他']":
                                sheet2.write(jzxax, 78, "其他："+main_dia['DiaIllustrate'], stylecount)  # 主要诊断
                            elif main_dia['mainDia'] == "['特发性矮小', '其他(手填或不填)']":
                                sheet2.write(jzxax, 78, "特发性矮小:其他："+main_dia['mainDiaIllustrate'], stylecount)  # 主要诊断
                            else:
                                sheet2.write(jzxax, 78, main_dia['mainDia'], stylecount)  # 主要诊断
                        except:
                            pass
                        sheet2.write(jzxax, 79, rseult.sec_dia, stylecount)  # 次要诊断
                        iFlCount = 80
                        iFl_list = ""
                        # 随访
                        i = 1
                        for iFl in follow:
                            if i>suifangmaxshort:
                                suifangmaxshort = i
                            # 序号
                            sheet2.write(jzxax, iFlCount, i, stylecount)
                            iFlCount += 1
                            # 随访日期
                            sheet2.write(jzxax, iFlCount, iFl.foll_time.strftime('%Y-%m-%d'), stylecount)
                            iFlCount += 1
                            # 年龄
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.age), stylecount)
                            iFlCount += 1
                            # 身高
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.Ht), stylecount)
                            iFlCount += 1
                            # 体重
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.Wt), stylecount)
                            iFlCount += 1
                            # 外生殖器分期
                            if patient.sex == '1':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet2.write(jzxax, iFlCount, "G" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet2.write(jzxax, iFlCount, "", stylecount)
                                    iFlCount += 2
                            elif patient.sex == '2':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet2.write(jzxax, iFlCount+1, "B" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet2.write(jzxax, iFlCount+1, "", stylecount)
                                    iFlCount += 2
                            else:
                                iFlCount += 2
                            # 阴毛分期
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.pub_stag), stylecount)
                            iFlCount += 1
                            # IGF-1
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.IGF1), stylecount)
                            iFlCount += 1
                            # IGFBP-3
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.IGFBP3), stylecount)
                            iFlCount += 1
                            # 空腹血糖
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.fas_blood_glu), stylecount)
                            iFlCount += 1
                            # 空腹胰岛素
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.fas_insulin) + "(IU/L)" , stylecount)
                            iFlCount += 1
                            # 糖化血红蛋白
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.gly_hem), stylecount)
                            iFlCount += 1
                            # 性腺B超
                            if patient.sex == '1':
                                gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                     "睾丸大小-左侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                                sheet2.write(jzxax, iFlCount, gonadUltrasoundMan, stylecount)
                                iFlCount += 1
                                # 女
                            elif patient.sex == '2':
                                # 判断随访囊肿(是否存在存在)
                                if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                    cyst_info = "有，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))[
                                            'cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                                elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                    cyst_info = "无"
                                else:
                                    cyst_info = "未选择"
                                gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(iFl.gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusThr']) + "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                       "最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                       "有无囊肿：" + cyst_info
                                sheet2.write(jzxax, iFlCount, gonadUltrasoundWoman, stylecount)
                                iFlCount += 1
                            else:
                                sheet2.write(jzxax, iFlCount, "未填写", stylecount)
                                iFlCount += 1
                            # 其他
                            sheet2.write(jzxax, iFlCount, safe_str(iFl.other), stylecount)
                            iFlCount += 1
                            # 甲攻
                            if iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '2':
                                jg = "甲攻异常," + safe_str(json.loads(iFl.Jiagong)['JiagongDes']) + ""
                            elif iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '1':
                                jg = "甲攻正常"
                            else:
                                jg = ""
                            sheet2.write(jzxax, iFlCount, jg, stylecount)
                            iFlCount += 1
                            # 肝肾脂电解质
                            gsdjz = "未填写"
                            if iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '2':
                                gsdjz = "肝肾脂电解质异常," + safe_str(json.loads(iFl.liv_kid_lip)['LAKLEdes'])
                            elif iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '1':
                                gsdjz = "肝肾脂电解质正常"
                            sheet2.write(jzxax, iFlCount, gsdjz, stylecount)
                            iFlCount += 1
                            # 诊疗方案
                            zlfa = ""
                            yyjx = ""
                            yyjl = ""
                            dwjl = ""
                            lhyyjx = ""
                            lhyyjl = ""
                            lhdwjl = ""
                            if rseult.dia_trea_plan and iFl.dia_trea_plan != "无":
                                try:
                                    # 治疗1
                                    if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                        zlfa = "未治疗"
                                    # 治疗2
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                        zlfa = "rhGH治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗3
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                        zlfa = "GnRHa治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                    # 治疗4
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                        zlfa = "GnRHal联合生长激素治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '短效rhGH'
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '长效生长激素 (PEG-rhGH)'
                                    # 治疗5
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                        zlfa = "芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '11':
                                        zlfa = "停止芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗6
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '10':
                                        zlfa = "芳香化酶联合生长激素治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '12':
                                        zlfa = "停止芳香化酶联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                        zlfa = "停止GnRHa治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                        zlfa = "停止GnRHa联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗8
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                        zlfa = "停止生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    else:
                                        zlfa = ""
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                except:
                                    pass
                            sheet2.write(jzxax, iFlCount, zlfa, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, yyjx, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, yyjl, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, dwjl, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, lhyyjx, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, lhyyjl, stylecount)
                            iFlCount += 1
                            sheet2.write(jzxax, iFlCount, lhdwjl, stylecount)
                            iFlCount += 1
                            i = i+1
                        jzxax = jzxax + 1
                    elif patient.dis_class == '10000003':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        follow = models.PatFoll.objects.filter(patient__pk=patient.id)

                        # 写入每一行对应的数据
                        # 病历号
                        sheet3.write(zsxxzs, 0, patient.medrec_num, stylecount)
                        # 患者姓名
                        sheet3.write(zsxxzs, 1, patient.name, stylecount)
                        # 国际疾病分类
                        if safe_str(patient.ICD) and len(patient.ICD) > 0:
                            # 转换为字典
                            ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                            # 获取 patient.ICD 对应的 label
                            ICD = ICD_dict.get(patient.ICD, "未选择")
                            sheet3.write(zsxxzs, 2, ICD, stylecount)
                        else:
                            sheet3.write(zsxxzs, 2, "未选择", stylecount)
                        # 性别
                        if patient.sex == '2':
                            sheet3.write(zsxxzs, 3, "女", stylecount)
                        elif patient.sex == '1':
                            sheet3.write(zsxxzs, 3, "男", stylecount)
                        else:
                            sheet3.write(zsxxzs, 3, "未选择", stylecount)
                        # 性腺性别
                        if patient.gonadal_sex == '2':
                            sheet3.write(zsxxzs, 4, "女", stylecount)
                        elif  patient.gonadal_sex == '1':
                            sheet3.write(zsxxzs, 4, "男", stylecount)
                        else:
                            sheet3.write(zsxxzs, 4, "未选择", stylecount)
                        # 初诊时间
                        # sheet3.write(zsxxzs, 5, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        if patient.fir_vis_time is not None:
                            sheet3.write(zsxxzs, 5, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            sheet3.write(zsxxzs, 5, "未选择", stylecount)
                        # 出生日期
                        # sheet3.write(zsxxzs, 6, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        if patient.birth_time is not None:
                            sheet3.write(zsxxzs, 6, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            sheet3.write(zsxxzs, 6, "未选择", stylecount)
                        # 年龄
                        sheet3.write(zsxxzs, 7, patient.age, stylecount)
                        # 主诉
                        sheet3.write(zsxxzs, 8, patient.chi_com, stylecount)
                        # 籍贯
                        natPla = patient.nat_pla
                        if natPla is not None and len(natPla)>0:
                            # 第一种方法
                            natPList = ast.literal_eval(natPla)
                            # 第1-3个
                            # 选择两个
                            if natPList is not None and len(natPList) == 2:
                                one_data = natPList[0]
                                two_data = natPList[1]
                            elif natPList is not None and len(natPList) == 3:
                                one_data = natPList[0]
                                two_data = natPList[1]
                                three_data = natPList[2]
                            # 获取比对
                            # 选择两个
                            if ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 2:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                sheet3.write(zsxxzs, 9, one + "/" + two, stylecount)
                            # 选择三个
                            elif ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 3:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                three = area.get(three_data)
                                sheet3.write(zsxxzs, 9, one + "/" + two + "/" + three, stylecount)
                            # 未选择
                            else:
                                sheet3.write(zsxxzs, 9, "未选择籍贯", stylecount)
                        # 父亲身高
                        sheet3.write(zsxxzs, 10, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['fHeight']), stylecount)
                        # 母亲身高
                        sheet3.write(zsxxzs, 11, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['mHeight']), stylecount)
                        # 家族史
                        if patient.family_his == '1':
                            sheet3.write(zsxxzs, 12, "无", stylecount)
                        else:
                            sheet3.write(zsxxzs, 12, patient.family_his, stylecount)
                        sheet3.write(zsxxzs, 13, patient.ges_week, stylecount)  # 胎龄周
                        sheet3.write(zsxxzs, 14, patient.BWt, stylecount)  # 出生体重
                        sheet3.write(zsxxzs, 15, patient.BL, stylecount)  # 出生身长
                        # 出生方式
                        if patient.cesa_sec == '1':
                            sheet3.write(zsxxzs, 16, "刨宫产", stylecount)
                        elif patient.cesa_sec == '0':
                            sheet3.write(zsxxzs, 16, "自然产", stylecount)
                        else:
                            sheet3.write(zsxxzs, 16, "未选择", stylecount)
                        # 保胎史
                        if patient.fet_pro_his == '1':
                            sheet3.write(zsxxzs, 17, "无", stylecount)
                        else:
                            sheet3.write(zsxxzs, 17, patient.fet_pro_his, stylecount)
                        sheet3.write(zsxxzs, 18, patient.past_his, stylecount)  # 既往史
                        sheet3.write(zsxxzs, 19, patient.card, stylecount)  # 身份证号码
                        sheet3.write(zsxxzs, 20, patient.fam_adr, stylecount)  # 家庭地址
                        sheet3.write(zsxxzs, 21, patient.contacts_name, stylecount)  # 联系人姓名
                        sheet3.write(zsxxzs, 22, patient.relation, stylecount)  # 与患者关系
                        sheet3.write(zsxxzs, 23, patient.contacts_num, stylecount)  # 联系电话
                        sheet3.write(zsxxzs, 24, patient.case_num, stylecount)  # 病例编号
                        sheet3.write(zsxxzs, 25, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['fHeight']) + "(cm)", stylecount)  # 父亲身高
                        sheet3.write(zsxxzs, 26, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['fWeight']) + "(kg)", stylecount)  # 父亲体重
                        sheet3.write(zsxxzs, 27, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['mHeight']) + "(cm)", stylecount)  # 母亲身高
                        sheet3.write(zsxxzs, 28, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['mWeight']) + "(kg)", stylecount)  # 母亲体重
                        sheet3.write(zsxxzs, 29, safe_str(json.loads(rseult.fam_his.replace('\n', ''))['firstAge']) + "(岁)", stylecount)  # 初潮年龄
                        if rseult.fam_his:
                            # 是否有兄弟姐妹
                            if 'bro' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['bro'] == '2':
                                # 有
                                if 'familyData' in json.loads(rseult.fam_his.replace('\n', '')) and len(json.loads(rseult.fam_his.replace('\n', ''))['familyData']):
                                    family = json.loads(rseult.fam_his.replace('\n', ''))['familyData']
                                    # 兄弟姐妹
                                    familyList = "性别：" + safe_str(family[0]['sex']) + "\n" + \
                                                 "年龄：" + safe_str(family[0]['age']) + "\n" + \
                                                 "身高：" + safe_str(family[0]['height']) + "\n" + \
                                                 "体重：" + safe_str(family[0]['weight']) + "\n" + \
                                                 "有无性早熟：：" + safe_str(family[0]['health']) + "\n"
                                    sheet3.write(zsxxzs, 30, familyList, stylecount)
                                # 既往史
                                if 'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '2':
                                    # 有
                                    sheet3.write(zsxxzs, 31, json.loads(rseult.fam_his.replace('\n', ''))['isHis'], stylecount)
                                elif  'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '1':
                                    # 无
                                    sheet3.write(zsxxzs, 31, "健康", stylecount)
                                else:
                                    sheet3.write(zsxxzs, 31, "未选择", stylecount)
                            else:
                                # 无
                                sheet3.write(zsxxzs, 30, "无", stylecount)  # 兄弟姐妹
                                # 既往史
                                if 'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '2':
                                    # 有
                                    sheet3.write(zsxxzs, 31, json.loads(rseult.fam_his.replace('\n', ''))['isHis'], stylecount)
                                elif  'isHis' in json.loads(rseult.fam_his.replace('\n', '')) and json.loads(rseult.fam_his.replace('\n', ''))['isHis'] == '1':
                                    # 无
                                    sheet3.write(zsxxzs, 31, "健康", stylecount)
                                else:
                                    sheet3.write(zsxxzs, 31, "未选择", stylecount)
                        sheet3.write(zsxxzs, 32, rseult.first_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)  # 初次就诊时间
                        sheet3.write(zsxxzs, 33, safe_str(rseult.age_ons) + "（岁）", stylecount)  # 初诊年龄
                        sheet3.write(zsxxzs, 34, rseult.chi_com, stylecount)  # 主诉
                        # 生长速率
                        if len(rseult.acc_growth) ==0:
                            sheet3.write(zsxxzs, 35, "不详", stylecount)
                        elif len(rseult.acc_growth) < 3:
                            sheet3.write(zsxxzs, 35, rseult.acc_growth, stylecount)
                        else:
                            if 'rate' in json.loads(rseult.acc_growth) and len(safe_str(json.loads(rseult.acc_growth)['rate']))>0:
                                sheet3.write(zsxxzs, 35, safe_str(json.loads(rseult.acc_growth)['rate']) + "（厘米/年）", stylecount)
                        # 月经初潮
                        if 'menarchy' in json.loads(rseult.menarche) and json.loads(rseult.menarche)['menarchy'] == '2':
                            sheet3.write(zsxxzs, 36, "月经初潮：有" + "\n" + "初潮时间：" + safe_str(
                                json.loads(rseult.menarche)['menarchyTime']), stylecount)
                        elif  'menarchy' in json.loads(rseult.menarche) and json.loads(rseult.menarche)['menarchy'] == '1':
                            sheet3.write(zsxxzs, 36, "无", stylecount)
                        else:
                            sheet3.write(zsxxzs, 36, "未选择", stylecount)

                        sheet3.write(zsxxzs, 37, safe_str(json.loads(rseult.phy_exa)['height']) + "（cm）", stylecount)  # 身高
                        sheet3.write(zsxxzs, 38, safe_str(json.loads(rseult.phy_exa)['weight']) + "（kg）", stylecount)  # 体重
                        # 外生殖器分期
                        if patient.sex == '1':
                            if json.loads(rseult.phy_exa)['exGenitalia'] is not None and len(json.loads(rseult.phy_exa)['exGenitalia']) > 0:
                                sheet3.write(zsxxzs, 39, "外生殖器分期(男): G" + safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                            else:
                                sheet3.write(zsxxzs, 39, "未选择", stylecount)
                        elif patient.sex == '2':
                            if json.loads(rseult.phy_exa)['breastDev'] is not None and len(json.loads(rseult.phy_exa)['breastDev']) > 0:
                                sheet3.write(zsxxzs, 39, "双乳发育分期(女)：B" + safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                            else:
                                sheet3.write(zsxxzs, 39, "未选择", stylecount)
                        else:
                            sheet3.write(zsxxzs, 39, "未选择", stylecount)
                        sheet3.write(zsxxzs, 40, safe_str(json.loads(rseult.phy_exa)['pubicHair']), stylecount)  # 阴毛分期
                        # LH
                        if 'LH' in rseult.lab_exa or json.loads(rseult.lab_exa)['LH']:
                            repLabExa = rseult.lab_exa.replace("'",'"')
                            finaRepLabExa = json.loads(repLabExa)
                            sheet3.write(zsxxzs, 41, safe_str(finaRepLabExa['LH']) + "（mIU/mL）", stylecount)
                        sheet3.write(zsxxzs, 42, safe_str(json.loads(rseult.lab_exa)['FSH']) + "（mIU/mL）", stylecount)  # FSH
                        sheet3.write(zsxxzs, 43, safe_str(json.loads(rseult.lab_exa)['E2']) + "（pg/mL）", stylecount)  # E2
                        sheet3.write(zsxxzs, 44, safe_str(json.loads(rseult.lab_exa)['T']) + "（ng/dL）", stylecount)  # T
                        sheet3.write(zsxxzs, 45, safe_str(json.loads(rseult.lab_exa)['HSBG']) + "（nmol/L）",
                                     stylecount)  # SHBG
                        sheet3.write(zsxxzs, 46, safe_str(json.loads(rseult.lab_exa)['PRL']) + "（ng/mL）",
                                     stylecount)  # PRL（无）
                        sheet3.write(zsxxzs, 47, safe_str(json.loads(rseult.lab_exa)['IGF']) + "（ng/mL）",
                                     stylecount)  # IGF-1
                        sheet3.write(zsxxzs, 48, safe_str(json.loads(rseult.lab_exa)['IGFBP3']) + "（ug/mL）",
                                     stylecount)  # IGFBP-3
                        # 甲功
                        if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                            sheet3.write(zsxxzs, 49, "异常信息:" + safe_str(json.loads(rseult.lab_exa)['thyroid']), stylecount)
                        elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                            sheet3.write(zsxxzs, 49, "正常", stylecount)
                        else:
                            sheet3.write(zsxxzs, 49, "未选择", stylecount)
                        sheet3.write(zsxxzs, 50, safe_str(json.loads(rseult.lab_exa)['ACTH']) + "（pg/mL）",
                                     stylecount)  # ACTH
                        sheet3.write(zsxxzs, 51, safe_str(json.loads(rseult.lab_exa)['cortisol']) + "（ug/dL）",
                                     stylecount)  # 皮质醇
                        sheet3.write(zsxxzs, 52, safe_str(json.loads(rseult.lab_exa)['DHEAS']) + "（ug/dL）",
                                     stylecount)  # DHEAs
                        sheet3.write(zsxxzs, 53, safe_str(json.loads(rseult.lab_exa)['OHP']) + "（nmol/L）",
                                     stylecount)  # 17-OHP
                        # 肝肾脂糖电解质
                        if 'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                            sheet3.write(zsxxzs, 54, "异常信息：" + safe_str(json.loads(rseult.lab_exa)['laklgeDescribe']),
                                         stylecount)
                        elif  'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                            sheet3.write(zsxxzs, 54, "正常", stylecount)
                        else:
                            sheet3.write(zsxxzs, 54, "未选择", stylecount)
                        sheet3.write(zsxxzs, 55, rseult.electr, stylecount)  # 心电图
                        if patient.id == 3634:
                            print(patient.id)
                        # 性腺B超
                        if patient.sex == '1':
                            gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(rseult.gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(rseult.gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(rseult.gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(rseult.gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                 "睾丸大小-左侧：" + safe_str(json.loads(rseult.gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(rseult.gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(rseult.gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(rseult.gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                            sheet3.write(zsxxzs, 56, gonadUltrasoundMan, stylecount)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(rseult.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(rseult.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(rseult.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(rseult.gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                    json.loads(rseult.gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['uterusOne']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['uterusTwo']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['uterusThr'])+ "(cm)，宫颈长约：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['cervixLong'])  + "(cm)，内膜厚度：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['intima']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaLeftOne']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaLeftTwo']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaLeftThr']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaRightOne']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaRightTwo']) + "*" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['ovaRightThr']) + "(cm)" + "\n" + \
                                                   "最大滤泡直径大小：" + safe_str(json.loads(rseult.gon_B_ult.replace("\n", ""))['follDiameter']) + "(cm)" + "\n" + \
                                                   "有无囊肿：" + cyst_info
                            sheet3.write(zsxxzs, 56, gonadUltrasoundWoman, stylecount)
                        else:
                            sheet3.write(zsxxzs, 56, "未填写", stylecount)
                        # 垂体MRI
                        if 'MRI' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['MRI'] == '2':
                            sheet3.write(zsxxzs, 57, "异常信息：" + safe_str(json.loads(gon_B_ult)['mriDescribe']),
                                         stylecount)
                        elif  'MRI' in json.loads(rseult.gon_B_ult.replace("\n", "")) and json.loads(rseult.gon_B_ult.replace("\n", ""))['MRI'] == '1':
                            sheet3.write(zsxxzs, 57, "正常", stylecount)
                        else:
                            sheet3.write(zsxxzs, 57, "未选择", stylecount)
                        sheet3.write(zsxxzs, 58, safe_str(rseult.LFmax) + "（mIU/ml）", stylecount)  # LH峰值
                        sheet3.write(zsxxzs, 59, safe_str(rseult.FSHmax) + "（mIU/ml） ", stylecount)  # FSH峰值
                        sheet3.write(zsxxzs, 60, rseult.LHmax, stylecount)  # LH峰值/FSH峰值
                        # 诊疗方案(多种选择)
                        if rseult.dia_trea_plan:
                            # 治疗1
                            if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                                sheet3.write(zsxxzs, 61, "未治疗", stylecount)
                            # 治疗2
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet3.write(zsxxzs, 61, "rhGH治疗，短效rhGH" + safe_str(
                                        json.loads(rseult.dia_trea_plan)['rhGHdose']) + "（U/kg.d）", stylecount)
                                else:
                                    sheet3.write(zsxxzs, 61, "rhGH治疗，长效生长激素（PEG-rhGH）" + safe_str(
                                        json.loads(rseult.dia_trea_plan)['rhGHdose']) + "（mg/kg.w，每周1次）", stylecount)
                            # 治疗3
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                    sheet3.write(zsxxzs, 61, "GnRHa治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗4
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                                if 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and  json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次", stylecount)
                                elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                    sheet3.write(zsxxzs, 61, "GnRHal联合生长激素治疗，贝依针针3.75mg，每28天1次", stylecount)
                            # 治疗5
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                                sheet3.write(zsxxzs, 61, "芳香化酶抑制剂", stylecount)
                            # 治疗6
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                                sheet3.write(zsxxzs, 61, "停止GnRHa治疗", stylecount)
                            # 治疗7
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                                sheet3.write(zsxxzs, 61, "停止GnRHal联合生长激素治疗", stylecount)
                            # 治疗8
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                                sheet3.write(zsxxzs, 61, "停止生长激素治疗", stylecount)
                            # 治疗9
                            elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '9':
                                sheet3.write(zsxxzs, 61, "中医药治疗", stylecount)
                            else:
                                sheet3.write(zsxxzs, 61, "未选择", stylecount)
                        # 左侧甲状腺b超
                        gon_B_ult = rseult.gon_B_ult.replace("\n", "")
                        if 'CThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['CThyroidLB'] == '2':
                            sheet3.write(zsxxzs, 62, "甲状腺结节分级:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidLBGradation']) + "\n" + "大小:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidLBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['CThyroidLBOther']), stylecount)
                        elif  'CThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['CThyroidLB'] == '1':
                            sheet3.write(zsxxzs, 62, "正常", stylecount)
                        else:
                            sheet3.write(zsxxzs, 62, "未选择", stylecount)
                        # 右侧甲状腺b超
                        if 'CThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['CThyroidRB'] == '2':
                            sheet3.write(zsxxzs, 63, "甲状腺结节分级:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidRBGradation']) + "\n" + "大小:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['CThyroidRBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['CThyroidRBOther']), stylecount)
                        elif  'CThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['CThyroidRB'] == '1':
                            sheet3.write(zsxxzs, 63, "正常", stylecount)
                        else:
                            sheet3.write(zsxxzs, 63, "未选择", stylecount)
                        # 生物样本库
                        if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)['bioBank'] == '2':
                            sampleList = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                            listCount = ""
                            if len(sampleList) > 2:
                                Data = json.loads(sampleList)
                                for item in Data:
                                    map = {
                                        '1': 'DNA样本',
                                        '2': '血清',
                                        '3': '血浆',
                                        '4': '尿液',
                                    }
                                    finalname = map.get(item['name'])
                                    listCount = listCount + "样本编号:" + safe_str(item['id']) + "\n" + "样本类型:" + safe_str(finalname)
                            else:
                                listCount = "无"
                        else:
                            listCount = "无"
                        sheet3.write(zsxxzs, 64, listCount, stylecount)
                        # 染色体核型
                        sheet3.write(zsxxzs, 65, rseult.spe_kar, stylecount)
                        getMutName = rseult.gen_mut_name.replace("'", '"')
                        getMutNameData = json.loads(getMutName)
                        MutNameCount = ""
                        for MutNameitem in getMutNameData:
                            MutNameCount = MutNameCount + MutNameitem['genName'] + ","+ MutNameitem['Rna'] + ","+ MutNameitem['amino'] + ","+ MutNameitem['father'] + ","+ MutNameitem['mother'] + "/"
                        sheet3.write(zsxxzs, 66, MutNameCount, stylecount)
                        sheet3.write(zsxxzs, 67, rseult.main_dia, stylecount)  # 主要诊断
                        sheet3.write(zsxxzs, 68, rseult.sec_dia, stylecount)  # 次要诊断
                        iFlCount = 69
                        iFl_list = ""
                        for iFl in follow:
                            # 随访
                            # 随访日期
                            sheet3.write(zsxxzs, iFlCount, iFl.foll_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                            iFlCount += 1
                            # 年龄
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.age), stylecount)
                            iFlCount += 1
                            # 身高
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.Ht), stylecount)
                            iFlCount += 1
                            # 体重
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.Wt), stylecount)
                            iFlCount += 1
                            # 外生殖器分期
                            if patient.sex == '1':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet3.write(zsxxzs, iFlCount, "外生殖器分期(男):G" + safe_str(iFl.gen_stag), stylecount)
                                    iFlCount += 1
                                else:
                                    sheet3.write(zsxxzs, iFlCount, "未选择", stylecount)
                                    iFlCount += 1
                            elif patient.sex == '2':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet3.write(zsxxzs, iFlCount, "双乳发育分期（女）B" + safe_str(iFl.gen_stag), stylecount)
                                    iFlCount += 1
                                else:
                                    sheet3.write(zsxxzs, iFlCount, "未选择", stylecount)
                                    iFlCount += 1
                            else:
                                sheet3.write(zsxxzs, iFlCount, "未选择", stylecount)
                                iFlCount += 1
                            # 阴毛分期
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.pub_stag), stylecount)
                            iFlCount += 1
                            # IGF-1
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.IGF1), stylecount)
                            iFlCount += 1
                            # IGFBP-3
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.IGFBP3), stylecount)
                            iFlCount += 1
                            # 空腹血糖
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.fas_blood_glu), stylecount)
                            iFlCount += 1
                            # 糖化血红蛋白
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.gly_hem), stylecount)
                            iFlCount += 1
                            # 性腺B超
                            if patient.sex == '1':
                                gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                     "睾丸大小-左侧：" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(
                                    json.loads(iFl.gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                                sheet3.write(zsxxzs, iFlCount, gonadUltrasoundMan, stylecount)
                                iFlCount += 1
                            elif patient.sex == '2':
                                # 判断随访囊肿(是否存在存在)
                                if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                    cyst_info = "有，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))[
                                            'cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                                elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                    cyst_info = "无"
                                else:
                                    cyst_info = "未选择"
                                gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(iFl.gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusThr'])+ "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult.replace("\n", ""))['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                       "最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                       "有无囊肿：" + cyst_info
                                sheet3.write(zsxxzs, iFlCount, gonadUltrasoundWoman, stylecount)
                                iFlCount += 1
                            else:
                                sheet3.write(zsxxzs, iFlCount, "未填写", stylecount)
                                iFlCount += 1
                            # 其他
                            sheet3.write(zsxxzs, iFlCount, safe_str(iFl.other), stylecount)
                            iFlCount += 1
                            # 诊疗方案
                            zlfa = ""
                            if rseult.dia_trea_plan and iFl.dia_trea_plan != "无":
                                # 治疗1
                                if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                    zlfa = "未治疗"
                                # 治疗2
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                    if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                        zlfa = "rhGH治疗，短效rhGH，" + safe_str(
                                            json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（U/kg.d）"
                                    else:
                                        zlfa = "rhGH治疗，长效生长激素（PEG-rhGH），" + safe_str(
                                            json.loads(iFl.dia_trea_plan)['rhGHdose']) + "（mg/kg.w，每周1次）"
                                # 治疗3
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                    if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                        zlfa = "GnRHa治疗，达菲林针3.75mg，每28天1次"
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                        zlfa = "GnRHa治疗，达必佳针3.75mg，每28天1次"
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                        zlfa = "GnRHa治疗，抑那通针3.75mg，每28天1次"
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                        zlfa = "GnRHa治疗，抑那通针11.25mg，每3月1次"
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                        zlfa = "GnRHa治疗，伯恩若康针3.75mg，每28天1次"
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                        zlfa = "GnRHa治疗，贝依针针3.75mg，每28天1次"
                                # 治疗4
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                    # 达菲林针
                                    if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and  json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，达菲林针3.75mg，每28天1次"
                                    # 达必佳针
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，达必佳针3.75mg，每28天1次"
                                    # 抑那通针
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 短效rhGH，" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        elif  'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + safe_str(
                                                json.loads(iFl.dia_trea_plan)[
                                                    'rhUnitedDose']) + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针3.75mg，每28天1次"
                                    # 抑那通针
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 短效rhGH，" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次 <联合> 长效生长激素（PEG-rhGH），" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，抑那通针11.25mg，每3月1次"
                                    # 伯恩若康针
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 短效rhGH，" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，伯恩若康针3.75mg，每28天1次"
                                    # 贝依针
                                    elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            zlfa = "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 短效rhGH，" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            zlfa = "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次 <联合> 长效生长激素（PEG-rhGH），" + \
                                                       json.loads(iFl.dia_trea_plan)[
                                                           'rhUnitedDose'] + "(mg/kg.w，每周1次)"
                                        else:
                                            zlfa = "GnRHal联合生长激素治疗，贝依针3.75mg，每28天1次"
                                # 治疗5
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                    zlfa = "芳香化酶抑制剂"
                                # 治疗6
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                    promptMap = {
                                        '1': '短效rhGH',
                                        '2': '长效生长激素（PEG-rhGH）',
                                    }
                                    if 'rhCustomizationDiaPlan' in json.loads(iFl.dia_trea_plan):
                                        rhCustomizationDiaPlan = json.loads(iFl.dia_trea_plan)['rhCustomizationDiaPlan']
                                        finalrhCustomizationDiaPlan = promptMap.get(rhCustomizationDiaPlan)
                                        zlfa = "停止GnRHa治疗，" + safe_str(
                                            finalrhCustomizationDiaPlan) + "，" + safe_str(
                                            json.loads(iFl.dia_trea_plan)[
                                                'rhCustomizationPrompt']) + "(mg/kg.w，每周1次)"
                                # 治疗7
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                    zlfa = "停止GnRHal联合生长激素治疗"
                                # 治疗8
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                    zlfa = "停止生长激素治疗"
                                # 治疗9
                                elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '9':
                                    zlfa = "中医药治疗"
                                else:
                                    zlfa = "未选择"
                            sheet3.write(zsxxzs, iFlCount, zlfa, stylecount)
                            iFlCount += 1

                        zsxxzs = zsxxzs + 1
                    # sga
                    elif patient.dis_class == '10000005':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        follow = models.PatFoll.objects.filter(patient__pk=patient.id)

                        # 病历号
                        sheet4.write(sga, 0, patient.medrec_num, stylecount)
                        # 患者姓名
                        sheet4.write(sga, 1, patient.name, stylecount)
                        # 国际疾病分类
                        if safe_str(patient.ICD) and len(patient.ICD) > 0:
                            # 转换为字典
                            ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                            # 获取 patient.ICD 对应的 label
                            ICD = ICD_dict.get(patient.ICD, "未选择")
                            sheet4.write(sga, 2, ICD, stylecount)
                        else:
                            sheet4.write(sga, 2, "未选择", stylecount)
                        # 性别
                        if patient.sex == '2':
                            sheet4.write(sga, 3, "女", stylecount)
                        elif patient.sex == '1':
                            sheet4.write(sga, 3, "男", stylecount)
                        else:
                            sheet4.write(sga, 3, "", stylecount)
                        # 出生日期
                        if patient.birth_time is not None:
                            sheet4.write(sga, 4, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            sheet4.write(sga, 4, "", stylecount)
                        # 身份证号码
                        sheet4.write(sga, 5, patient.card, stylecount)
                        # 家庭住址
                        sheet4.write(sga, 6, patient.fam_adr, stylecount)
                        # 联系人姓名
                        sheet4.write(sga, 7, patient.contacts_name, stylecount)
                        # 与患者关系
                        sheet4.write(sga, 8, patient.relation, stylecount)
                        # 联系电话
                        sheet4.write(sga, 9, patient.contacts_num, stylecount)
                        # 出生体重
                        sheet4.write(sga, 10, patient.BWt, stylecount)
                        # 出生身长
                        sheet4.write(sga, 11, patient.BL, stylecount)
                        # 孕周
                        sheet4.write(sga, 12, patient.ges_week, stylecount)
                        # 分娩方式
                        cesasecmap = {
                            '1': '自然分娩',
                            '2': '剖宫产',
                            '': ''
                        }
                        try:
                            sheet4.write(sga, 13, cesasecmap[patient.cesa_sec], stylecount)
                        except:
                            pass
                        # 窒息抢救史
                        cesaasphyxiamap = {
                            '1': '无',
                            '2': '轻度窒息',
                            '3': '重度窒息',
                            '': ''
                        }
                        try:
                            sheet4.write(sga, 14, cesaasphyxiamap[patient.cesa_asphyxia], stylecount)
                        except:
                            pass
                        # 病例编号
                        sheet4.write(sga, 15, patient.case_num, stylecount)
                        # 母亲孕期疾病
                        if rseult.mot_pre_dis == '0':
                            sheet4.write(sga, 16, "GBS", stylecount)
                        elif rseult.mot_pre_dis == '1':
                            sheet4.write(sga, 16, "GDM", stylecount)
                        elif rseult.mot_pre_dis == '2':
                            sheet4.write(sga, 16, "急性感染", stylecount)
                        elif rseult.mot_pre_dis == '3' and rseult.mot_pre_dis_ms != '':
                            sheet4.write(sga, 16, "其他重大疾病，具体描述：" + rseult.mot_pre_dis_ms, stylecount)
                        else:
                            sheet4.write(sga, 16, "", stylecount)
                        # 是否多胎
                        if rseult.is_mul_bir == '0':
                            sheet4.write(sga, 17, "否", stylecount)
                        elif rseult.is_mul_bir == '1' and rseult.mul_bir_ms != '':
                            sheet4.write(sga, 17, "是，几胎：" + rseult.mul_bir_ms, stylecount)
                        else:
                            sheet4.write(sga, 17, "", stylecount)
                        # 胎产次
                        if (rseult.patient and rseult.pronum) is not None and (len(rseult.parity) > 0 or len(rseult.pronum) > 0):
                            parity_map = {
                                '1': 'G1',
                                '2': 'G2',
                                '3': 'G3',
                                '4': 'G4',
                            }
                            pronum_map = {
                                '1': 'P1',
                                '2': 'P2',
                                '3': 'P3',
                                '4': 'P4',
                            }
                            parity = parity_map.get(rseult.parity) or '未选择'
                            pronum = pronum_map.get(rseult.pronum) or '未选择'
                            sheet4.write(sga, 18, '胎次：' + parity + ',产次:' + pronum, stylecount)
                        else:
                            sheet4.write(sga, 18, "未选择", stylecount)

                        med_his = rseult.med_his.replace('\n', '')
                        # 初次就诊时间
                        sheet4.write(sga, 19, safe_str(json.loads(med_his)['firVisTime']), stylecount) 
                        # 初诊年龄 
                        sheet4.write(sga, 20, safe_str(json.loads(med_his)['morbidAge']), stylecount) 
                        # 主诉 
                        sheet4.write(sga, 21, safe_str(json.loads(med_his)['chiefCom']), stylecount)  
                        # 生长速率
                        if json.loads(med_his) == 1 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '1':
                            sheet4.write(sga, 22, "不详", stylecount)
                        elif json.loads(med_his) == 2 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '2':
                            sheet4.write(sga, 22, safe_str(json.loads(med_his)['growRate']) + "(厘米/年)", stylecount)
                        else:
                            sheet4.write(sga, 22, "", stylecount)
                        # 初次遗精
                        if patient.sex == '1':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet4.write(sga, 23, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet4.write(sga, 23, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet4.write(sga, 23, "", stylecount)
                        # 月经初潮
                        elif patient.sex == '2':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet4.write(sga, 24, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet4.write(sga, 24, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet4.write(sga, 24, "", stylecount)
                        try:
                            if rseult.fam_his.replace('\n', '') and len(json.loads(rseult.fam_his.replace('\n', '')))>0:
                                # 父亲身高
                                fhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet4.write(sga, 25, fhight, stylecount)
                                # 母亲身高
                                mhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet4.write(sga, 26, mhight, stylecount)
                                # 遗传身高（通过父母身高计算得出）
                                if fhight and mhight:
                                    if patient.sex == '1': 
                                        ycsg = (float(fhight)+float(mhight)+13)/2
                                    else:
                                        ycsg = (float(fhight)+float(mhight)-13)/2
                                    sheet4.write(sga, 28, ycsg, stylecount)
                        except:
                            pass
                        # 身高
                        sheet4.write(sga, 27, json.loads(rseult.phy_exa)['height'], stylecount)
                        # 体重  
                        sheet4.write(sga, 29, json.loads(rseult.phy_exa)['weight'], stylecount) 
                        # BMI 
                        sheet4.write(sga, 30, json.loads(rseult.phy_exa)['Bmi'], stylecount)  
                        # 外生殖器分期（男）
                        if patient.sex == '1':
                            if json.loads(rseult.phy_exa)['exGenitalia'] is not None and len(json.loads(rseult.phy_exa)['exGenitalia']) > 0:
                                sheet4.write(sga, 31, "G" + safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                            else:
                                sheet4.write(sga, 31, "", stylecount)
                        elif patient.sex == '2':
                            if json.loads(rseult.phy_exa)['breastDev'] is not None and len(json.loads(rseult.phy_exa)['breastDev']) > 0:
                                sheet4.write(sga, 32, "B" + safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                            else:
                                sheet4.write(sga, 32, "", stylecount)
                            if 'breastDevRight' in json.loads(rseult.phy_exa)  and json.loads(rseult.phy_exa)['breastDevRight'] is not None and len(json.loads(rseult.phy_exa)['breastDevRight']) > 0 and json.loads(rseult.phy_exa)['breastDevRight'] != "null" :
                                sheet4.write(sga, 33, "B" + safe_str(json.loads(rseult.phy_exa)['breastDevRight']), stylecount)
                            else:
                                sheet4.write(sga, 33, "", stylecount)
                        # 阴毛分期
                        sheet4.write(sga, 34, json.loads(rseult.phy_exa)['pubicHair'], stylecount)
                        # 臂长  
                        sheet4.write(sga, 35, json.loads(rseult.phy_exa)['armLength'], stylecount)  
                        # 特殊面容
                        if 'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '2':
                            sheet4.write(sga, 36, json.loads(rseult.phy_exa)['specialFaceDesc'], stylecount)
                        elif  'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '1':
                            sheet4.write(sga, 36, "无", stylecount)
                        else:
                            sheet4.write(sga, 36, "", stylecount)
                        # 脊柱侧弯
                        if 'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '2':
                            scolioMap = {
                                '1': '轻度',
                                '2': '中度',
                                '3': '重度',
                            }
                            scoliosisDegree = json.loads(rseult.phy_exa)['scoliosisDegree']
                            finalScolio = scolioMap.get(scoliosisDegree)
                            sheet4.write(sga, 37, finalScolio, stylecount)
                        elif  'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '1':
                            sheet4.write(sga, 37, "无", stylecount)
                        else:
                            sheet4.write(sga, 37, "", stylecount)
                        # 皮疹
                        if 'rash' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['rash'] == 2:
                            sheet4.write(sga, 38, json.loads(rseult.phy_exa)['rashDescribe'], stylecount)
                        else:
                            sheet4.write(sga, 38, "无", stylecount)
                        # 运动发育落后
                        if 'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '2':
                            sheet4.write(sga, 39, json.loads(rseult.mot_dev_back)['sport'], stylecount)
                        elif  'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '1':
                            sheet4.write(sga, 39, "无", stylecount)
                        else:
                            sheet4.write(sga, 39, "", stylecount)
                        # 语言发育落后
                        if 'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '2':
                            sheet4.write(sga, 40, json.loads(rseult.lan_dev_back)['language'], stylecount)
                        elif  'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '1':
                            sheet4.write(sga, 40, "无", stylecount)
                        else:
                            sheet4.write(sga, 40, "", stylecount)
                        # 智力发育落后
                        if 'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '2':
                            sheet4.write(sga, 41, json.loads(rseult.int_dev_back)['intelligence'], stylecount)
                        elif  'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '1':
                            sheet4.write(sga, 41, "无", stylecount)
                        else:
                            sheet4.write(sga, 41, "", stylecount)
                        # 听力异常
                        if 'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '2':
                            sheet4.write(sga, 42, json.loads(rseult.abn_hear)['hear'], stylecount)
                        elif  'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '1':
                            sheet4.write(sga, 42, "无", stylecount)
                        else:
                            sheet4.write(sga, 42, "", stylecount)
                        # 反复感染史
                        if 'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '2':
                            sheet4.write(sga, 43, json.loads(rseult.rec_inf_his)['infection'], stylecount)
                        elif  'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '1':
                            sheet4.write(sga, 43, "无", stylecount)
                        else:
                            sheet4.write(sga, 43, "", stylecount)
                        # 抽搐史
                        if rseult.con_his == '1':
                            sheet4.write(sga, 44, "无", stylecount)
                        elif  rseult.con_his == '2':
                            sheet4.write(sga, 44, "有", stylecount)
                        else:
                            sheet4.write(sga, 44, "", stylecount)
                        # 诊疗方案
                        if rseult.dia_trea_plan is not None and len(rseult.dia_trea_plan) > 0:
                            # 诊疗方案(多种选择)
                            if rseult.dia_trea_plan:
                                # 治疗1
                                if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                                    sheet4.write(sga, 45, "未治疗", stylecount)
                                # 治疗2
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                                    sheet4.write(sga, 45, "rhGH治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet4.write(sga, 46, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)                     
                                            except:
                                                pass
                                        sheet4.write(sga, 48, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet4.write(sga, 46, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet4.write(sga, 48, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗3
                                elif  'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                                    sheet4.write(sga, 45, "GnRHa治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet4.write(sga, 46, "达必佳针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet4.write(sga, 46, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet4.write(sga, 46, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet4.write(sga, 48, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet4.write(sga, 46, "伯恩若康针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:    
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet4.write(sga, 46, "贝依针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:    
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:    
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:    
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:    
                                            sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每35天1次", stylecount)
                                # 治疗4
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                                    sheet4.write(sga, 42, "GnRHal联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet4.write(sga, 46, "达必佳针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet4.write(sga, 46, "抑那通针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet4.write(sga, 46, "抑那通针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet4.write(sga, 48, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet4.write(sga, 46, "伯恩若康针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet4.write(sga, 46, "贝依针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet4.write(sga, 46, "达菲林针", stylecount)
                                        sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet4.write(sga, 48, "3.75mg，每35天1次", stylecount)
                                # 治疗5
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                                    sheet4.write(sga, 45, "芳香化酶抑制剂", stylecount)
                                # 治疗6
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '11':
                                    sheet4.write(sga, 45, "停止芳香化酶抑制剂", stylecount)
                                # 治疗7
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '10':
                                    sheet4.write(sga, 45, "芳香化酶联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet4.write(sga, 46, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass                    
                                        sheet4.write(sga, 48, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet4.write(sga, 46, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet4.write(sga, 47, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet4.write(sga, 48, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗8
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '12':
                                    sheet4.write(sga, 45, "停止芳香化酶联合生长激素治疗", stylecount)
                                # 治疗9
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                                    sheet4.write(sga, 45, "停止GnRHa治疗", stylecount)
                                # 治疗10
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                                    sheet4.write(sga, 45, "停止GnRHa联合生长激素治疗", stylecount)
                                # 治疗11
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                                    sheet4.write(sga, 45, "停止生长激素治疗", stylecount)
                                else:
                                    sheet4.write(sga, 45, "", stylecount)
                        # 其他
                        sheet4.write(sga, 49, rseult.past_other, stylecount)  
                        sheet4.write(sga, 50, safe_str(json.loads(rseult.lab_exa)['LH']), stylecount)  # LH
                        sheet4.write(sga, 51, safe_str(json.loads(rseult.lab_exa)['FSH']), stylecount)  # FSH
                        sheet4.write(sga, 52, safe_str(json.loads(rseult.lab_exa)['E2']), stylecount)  # E2
                        sheet4.write(sga, 53, safe_str(json.loads(rseult.lab_exa)['T']), stylecount)  # T
                        sheet4.write(sga, 54, safe_str(json.loads(rseult.lab_exa)['PRL']), stylecount)  # PRL
                        sheet4.write(sga, 55, safe_str(json.loads(rseult.lab_exa)['IGF']), stylecount)  # IGF-1
                        sheet4.write(sga, 56, safe_str(json.loads(rseult.lab_exa)['IGFBP3']),stylecount)  # IGFBP-3
                        # 甲功
                        if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                            sheet4.write(sga, 57, "异常", stylecount)
                            sheet4.write(sga, 58, json.loads(rseult.lab_exa)['thyroidDescribe'], stylecount)
                        elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                            sheet4.write(sga, 57, "正常", stylecount)
                        sheet4.write(sga, 59, safe_str(json.loads(rseult.lab_exa)['ACTH']), stylecount)  # ACTH
                        sheet4.write(sga, 60, safe_str(json.loads(rseult.lab_exa)['cortisol']),stylecount)  # 皮质醇（8am）
                        sheet4.write(sga, 61, safe_str(json.loads(rseult.lab_exa)['DHEAS']),stylecount)  # DHEAs
                        sheet4.write(sga, 62, safe_str(json.loads(rseult.lab_exa)['OHP']),stylecount)  # 17-OHP
                        # 血常规
                        if 'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '2':
                            sheet4.write(sga, 63, "异常", stylecount)
                            sheet4.write(sga, 64, json.loads(rseult.lab_exa)['bloodDescribe'], stylecount)
                        elif  'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '1':
                            sheet4.write(sga, 63, "正常", stylecount)
                        # 尿常规
                        if 'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '2':
                            sheet4.write(sga, 65, "异常", stylecount)
                            sheet4.write(sga, 66, json.loads(rseult.lab_exa)['urinalysisDescribe'], stylecount)
                        elif  'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '1':
                            sheet4.write(sga, 65, "正常", stylecount)
                        # 肝肾脂糖电解质
                        if 'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                            sheet4.write(sga, 67, "异常", stylecount)
                            sheet4.write(sga, 68, json.loads(rseult.lab_exa)['laklgeDescribe'], stylecount)
                        elif  'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                            sheet4.write(sga, 67, "正常", stylecount)
                        # 乙肝三系
                        HBsMap = {
                            '1': '阴性',
                            '2': 'HBSAb阳性',
                            '3': '小三阳',
                            '4': '大三阳',
                        }
                        HBs = json.loads(rseult.lab_exa)['HBs']
                        finalHBs = HBsMap.get(HBs)
                        sheet4.write(sga, 69, finalHBs, stylecount)
                        sheet4.write(sga, 70, safe_str(json.loads(rseult.lab_exa)['gh']),stylecount)  # Gh药物激发试验-Gh峰值
                        sheet4.write(sga, 71, rseult.electr, stylecount)  # 心电图
                        gon_B_ult = rseult.gon_B_ult.replace("\n","")
                        # 性腺B超
                        if patient.sex == '1':
                            gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                 "睾丸大小-左侧：" + safe_str(json.loads(gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                            sheet4.write(sga, 72, gonadUltrasoundMan, stylecount)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(gon_B_ult)['uterusThr'])+ "(cm)，宫颈长约：" + safe_str(json.loads(gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                   "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                   "有无囊肿：" + cyst_info
                            sheet4.write(sga, 72, gonadUltrasoundWoman, stylecount)
                        # 垂体MRI
                        if 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '2':
                            sheet4.write(sga, 73, json.loads(gon_B_ult)['mriDescribe'], stylecount)
                        elif 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '1':
                            sheet4.write(sga, 73, "正常", stylecount)
                            # 左侧甲状腺b超
                        if 'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '2':
                            sheet4.write(sga, 74, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBOther']), stylecount)
                        elif  'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '1':
                            sheet4.write(sga, 74, "正常", stylecount)
                        # 右侧甲状腺b超
                        if 'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '2':
                            sheet4.write(sga, 75, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBOther']), stylecount)
                        elif  'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '1':
                            sheet4.write(sga, 75, "正常", stylecount)
                        # 染色体核型
                        sheet4.write(sga, 76, rseult.spe_kar, stylecount)  # 染色体核型
                        # 生物样本库是否存在
                        if rseult.bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号" + safe_str(
                                            item['id']) + "\n" + "样本类型" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet4.write(sga, 77, listCount, stylecount)  # 生物样本库
                        # 父亲生物样本库
                        if rseult.f_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.f_bio_sam_bank) and json.loads(rseult.f_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.f_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet4.write(sga, 78, listCount, stylecount)  # 生物样本库
                        # 母亲生物样本库
                        if rseult.m_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.m_bio_sam_bank) and json.loads(rseult.m_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.m_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        map = {
                                            '1': 'DNA样本',
                                            '2': '血清',
                                            '3': '血浆',
                                            '4': '尿液',
                                        }
                                        finalname = map.get(item['name'])
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(finalname)
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"
                            sheet4.write(sga, 79, listCount, stylecount)  # 生物样本库
                        if rseult.gen_mut_name:
                            genMutName = json.loads(rseult.gen_mut_name)
                            genMutNamecount = ""
                            for genMutNameItem in genMutName:
                                if 'father' in genMutNameItem:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['father'])+","+genMutNameItem['mother']+"/"
                                else:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['ties1'])+","+genMutNameItem['ties2']+","+genMutNameItem['ties3']+","+genMutNameItem['ties4']+","+genMutNameItem['ties5']+","+genMutNameItem['ties6']+"/"
                            sheet4.write(sga, 80, genMutNamecount, stylecount)
                        try:
                            main_dia = json.loads(rseult.main_dia)
                            if main_dia['mainDia'] == "['其他']":
                                sheet4.write(sga, 81, "其他："+main_dia['DiaIllustrate'], stylecount)  # 主要诊断
                            elif main_dia['mainDia'] == "['特发性矮小', '其他(手填或不填)']":
                                sheet4.write(sga, 81, "特发性矮小:其他："+main_dia['mainDiaIllustrate'], stylecount)  # 主要诊断
                            else:
                                sheet4.write(sga, 81, main_dia['mainDia'], stylecount)  # 主要诊断
                        except:
                            pass
                        sheet4.write(sga, 82, rseult.sec_dia, stylecount)  # 次要诊断
                        iFlCount = 83
                        iFl_list = ""
                        # 随访
                        i = 1
                        for iFl in follow:
                            if i>suifangmaxshort:
                                suifangmaxshort = i
                            # 序号
                            sheet4.write(sga, iFlCount, i, stylecount)
                            iFlCount += 1
                            # 随访日期
                            sheet4.write(sga, iFlCount, iFl.foll_time.strftime('%Y-%m-%d'), stylecount)
                            iFlCount += 1
                            # 年龄
                            sheet4.write(sga, iFlCount, safe_str(iFl.age), stylecount)
                            iFlCount += 1
                            # 身高
                            sheet4.write(sga, iFlCount, safe_str(iFl.Ht), stylecount)
                            iFlCount += 1
                            # 体重
                            sheet4.write(sga, iFlCount, safe_str(iFl.Wt), stylecount)
                            iFlCount += 1
                            # 外生殖器分期
                            if patient.sex == '1':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet4.write(sga, iFlCount, "G" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet4.write(sga, iFlCount, "", stylecount)
                                    iFlCount += 2
                            elif patient.sex == '2':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet4.write(sga, iFlCount+1, "B" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet4.write(sga, iFlCount+1, "", stylecount)
                                    iFlCount += 2
                            else:
                                iFlCount += 2
                            # 阴毛分期
                            sheet4.write(sga, iFlCount, safe_str(iFl.pub_stag), stylecount)
                            iFlCount += 1
                            # IGF-1
                            sheet4.write(sga, iFlCount, safe_str(iFl.IGF1), stylecount)
                            iFlCount += 1
                            # IGFBP-3
                            sheet4.write(sga, iFlCount, safe_str(iFl.IGFBP3), stylecount)
                            iFlCount += 1
                            # 空腹血糖
                            sheet4.write(sga, iFlCount, safe_str(iFl.fas_blood_glu), stylecount)
                            iFlCount += 1
                            # 空腹胰岛素
                            sheet4.write(sga, iFlCount, safe_str(iFl.fas_insulin) + "(IU/L)" , stylecount)
                            iFlCount += 1
                            # 糖化血红蛋白
                            sheet4.write(sga, iFlCount, safe_str(iFl.gly_hem), stylecount)
                            iFlCount += 1
                            # 性腺B超
                            if patient.sex == '1':
                                gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                     "睾丸大小-左侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                                sheet4.write(sga, iFlCount, gonadUltrasoundMan, stylecount)
                                iFlCount += 1
                                # 女
                            elif patient.sex == '2':
                                # 判断随访囊肿(是否存在存在)
                                if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                    cyst_info = "有，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))[
                                            'cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                                elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                    cyst_info = "无"
                                else:
                                    cyst_info = "未选择"
                                gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(iFl.gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusThr']) + "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                       "最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                       "有无囊肿：" + cyst_info
                                sheet4.write(sga, iFlCount, gonadUltrasoundWoman, stylecount)
                                iFlCount += 1
                            else:
                                sheet4.write(sga, iFlCount, "未填写", stylecount)
                                iFlCount += 1
                            # 其他
                            sheet4.write(sga, iFlCount, safe_str(iFl.other), stylecount)
                            iFlCount += 1
                            # 甲攻
                            if iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '2':
                                jg = "甲攻异常," + safe_str(json.loads(iFl.Jiagong)['JiagongDes']) + ""
                            elif iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '1':
                                jg = "甲攻正常"
                            else:
                                jg = ""
                            sheet4.write(sga, iFlCount, jg, stylecount)
                            iFlCount += 1
                            # 肝肾脂电解质
                            gsdjz = "未填写"
                            if iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '2':
                                gsdjz = "肝肾脂电解质异常," + safe_str(json.loads(iFl.liv_kid_lip)['LAKLEdes'])
                            elif iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '1':
                                gsdjz = "肝肾脂电解质正常"
                            sheet4.write(sga, iFlCount, gsdjz, stylecount)
                            iFlCount += 1
                            # 诊疗方案
                            zlfa = ""
                            yyjx = ""
                            yyjl = ""
                            dwjl = ""
                            lhyyjx = ""
                            lhyyjl = ""
                            lhdwjl = ""
                            if rseult.dia_trea_plan and iFl.dia_trea_plan != "无":
                                try:
                                    # 治疗1
                                    if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                        zlfa = "未治疗"
                                    # 治疗2
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                        zlfa = "rhGH治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗3
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                        zlfa = "GnRHa治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                    # 治疗4
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                        zlfa = "GnRHal联合生长激素治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '短效rhGH'
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '长效生长激素 (PEG-rhGH)'
                                    # 治疗5
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                        zlfa = "芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '11':
                                        zlfa = "停止芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗6
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '10':
                                        zlfa = "芳香化酶联合生长激素治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '12':
                                        zlfa = "停止芳香化酶联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                        zlfa = "停止GnRHa治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                        zlfa = "停止GnRHa联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗8
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                        zlfa = "停止生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    else:
                                        zlfa = ""
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                except:
                                    pass
                            sheet4.write(sga, iFlCount, zlfa, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, yyjx, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, yyjl, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, dwjl, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, lhyyjx, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, lhyyjl, stylecount)
                            iFlCount += 1
                            sheet4.write(sga, iFlCount, lhdwjl, stylecount)
                            iFlCount += 1
                            i = i+1
                        sga = sga + 1
                    # 家族性矮小
                    elif patient.dis_class == '10000006':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        follow = models.PatFoll.objects.filter(patient__pk=patient.id)

                        # 写入每一行对应的数据
                        # 病历号
                        sheet5.write(ax, 0, patient.medrec_num, stylecount)
                        # 患者姓名
                        sheet5.write(ax, 1, patient.name, stylecount)
                        # 国际疾病分类
                        if safe_str(patient.ICD) and len(patient.ICD) > 0:
                            # 转换为字典
                            ICD_dict = {item['value']: item['label'] for item in ICDDataArray}
                            # 获取 patient.ICD 对应的 label
                            ICD = ICD_dict.get(patient.ICD, "未选择")
                            sheet5.write(ax, 2, ICD, stylecount)
                        else:
                            sheet5.write(ax, 2, "未选择", stylecount)
                        # 性别
                        if patient.sex == '2':
                            sheet5.write(ax, 3, "女", stylecount)
                        elif patient.sex == '1':
                            sheet5.write(ax, 3, "男", stylecount)
                        else:
                            sheet5.write(ax, 3, "", stylecount)
                        # 出生日期
                        if patient.birth_time is not None:
                            sheet5.write(ax, 4, patient.birth_time.strftime('%Y-%m-%d'), stylecount)
                        else:
                            sheet5.write(ax, 4, "", stylecount)
                        # 身份证号码
                        sheet5.write(ax, 5, patient.card, stylecount)
                        # 家庭住址
                        sheet5.write(ax, 6, patient.fam_adr, stylecount)
                        # 联系人姓名
                        sheet5.write(ax, 7, patient.contacts_name, stylecount)
                        # 与患者关系
                        sheet5.write(ax, 8, patient.relation, stylecount)
                        # 联系电话
                        sheet5.write(ax, 9, patient.contacts_num, stylecount)
                        # 出生体重
                        sheet5.write(ax, 10, patient.BWt, stylecount)
                        # 出生身长
                        sheet5.write(ax, 11, patient.BL, stylecount)
                        # 孕周
                        sheet5.write(ax, 12, patient.ges_week, stylecount)
                        # 分娩方式
                        cesasecmap = {
                            '1': '自然分娩',
                            '2': '剖宫产',
                            '': ''
                        }
                        try:
                            sheet5.write(ax, 13, cesasecmap[patient.cesa_sec], stylecount)
                        except:
                            pass
                        # 窒息抢救史
                        cesaasphyxiamap = {
                            '1': '无',
                            '2': '轻度窒息',
                            '3': '重度窒息',
                            '': ''
                        }
                        try:
                            sheet5.write(ax, 14, cesaasphyxiamap[patient.cesa_asphyxia], stylecount)
                        except:
                            pass
                        # 病例编号
                        sheet5.write(ax, 15, patient.case_num, stylecount)
                        med_his = rseult.med_his.replace('\n', '')
                        # 初次就诊时间
                        sheet5.write(ax, 16, safe_str(json.loads(med_his)['firVisTime']), stylecount) 
                        # 初诊年龄 
                        sheet5.write(ax, 17, safe_str(json.loads(med_his)['morbidAge']), stylecount) 
                        # 主诉 
                        sheet5.write(ax, 18, safe_str(json.loads(med_his)['chiefCom']), stylecount)  
                        # 生长速率
                        if json.loads(med_his) == 1 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '1':
                            sheet5.write(ax, 19, "不详", stylecount)
                        elif json.loads(med_his) == 2 or 'growRate' in json.loads(med_his) and json.loads(med_his)['growRate'] == '2':
                            sheet5.write(ax, 19, safe_str(json.loads(med_his)['growRate']) + "(厘米/年)", stylecount)
                        else:
                            sheet5.write(ax, 19, "", stylecount)
                        # 初次遗精
                        if patient.sex == '1':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet5.write(ax, 20, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet5.write(ax, 20, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet5.write(ax, 20, "", stylecount)
                        # 月经初潮
                        elif patient.sex == '2':
                            if 'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '1':
                                sheet5.write(ax, 21, "无", stylecount)
                            elif  'menarchy' in json.loads(med_his) and json.loads(med_his)['menarchy'] == '2':
                                sheet5.write(ax, 21, "时间：" + safe_str(json.loads(med_his)['menarchyTime']), stylecount)
                            else:
                                sheet5.write(ax, 21, "", stylecount)
                        try:
                            if rseult.fam_his.replace('\n', '') and len(json.loads(rseult.fam_his.replace('\n', '')))>0:
                                # 父亲身高
                                fhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet5.write(ax, 22, fhight, stylecount)
                                # 母亲身高
                                mhight = json.loads(rseult.fam_his.replace('\n', ''))[0]['height'] or None
                                sheet5.write(ax, 23, mhight, stylecount)
                                # 遗传身高（通过父母身高计算得出）
                                if fhight and mhight:
                                    if patient.sex == '1': 
                                        ycsg = (float(fhight)+float(mhight)+13)/2
                                    else:
                                        ycsg = (float(fhight)+float(mhight)-13)/2
                                    sheet5.write(ax, 25, ycsg, stylecount)
                        except:
                            pass
                        # 身高
                        sheet5.write(ax, 24, json.loads(rseult.phy_exa)['height'], stylecount)
                        # 体重  
                        sheet5.write(ax, 26, json.loads(rseult.phy_exa)['weight'], stylecount) 
                        # BMI 
                        sheet5.write(ax, 27, json.loads(rseult.phy_exa)['Bmi'], stylecount)  
                        # 外生殖器分期（男）
                        if patient.sex == '1':
                            if json.loads(rseult.phy_exa)['exGenitalia'] is not None and len(json.loads(rseult.phy_exa)['exGenitalia']) > 0:
                                sheet5.write(ax, 28, "G" + safe_str(json.loads(rseult.phy_exa)['exGenitalia']), stylecount)
                            else:
                                sheet5.write(ax, 28, "", stylecount)
                        elif patient.sex == '2':
                            if json.loads(rseult.phy_exa)['breastDev'] is not None and len(json.loads(rseult.phy_exa)['breastDev']) > 0:
                                sheet5.write(ax, 29, "B" + safe_str(json.loads(rseult.phy_exa)['breastDev']), stylecount)
                            else:
                                sheet5.write(ax, 29, "", stylecount)
                            if 'breastDevRight' in json.loads(rseult.phy_exa)  and json.loads(rseult.phy_exa)['breastDevRight'] is not None and len(json.loads(rseult.phy_exa)['breastDevRight']) > 0 and json.loads(rseult.phy_exa)['breastDevRight'] != "null" :
                                sheet5.write(ax, 30, "B" + safe_str(json.loads(rseult.phy_exa)['breastDevRight']), stylecount)
                            else:
                                sheet5.write(ax, 30, "", stylecount)
                        # 阴毛分期
                        sheet5.write(ax, 31, json.loads(rseult.phy_exa)['pubicHair'], stylecount)
                        # 臂长  
                        sheet5.write(ax, 32, json.loads(rseult.phy_exa)['armLength'], stylecount)  
                        # 特殊面容
                        if 'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '2':
                            sheet5.write(ax, 33, json.loads(rseult.phy_exa)['specialFaceDesc'], stylecount)
                        elif  'specialFace' in json.loads(rseult.phy_exa) and 'specialFaceDesc' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['specialFace'] == '1':
                            sheet5.write(ax, 33, "无", stylecount)
                        else:
                            sheet5.write(ax, 33, "", stylecount)
                        # 脊柱侧弯
                        if 'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '2':
                            scolioMap = {
                                '1': '轻度',
                                '2': '中度',
                                '3': '重度',
                            }
                            scoliosisDegree = json.loads(rseult.phy_exa)['scoliosisDegree']
                            finalScolio = scolioMap.get(scoliosisDegree)
                            sheet5.write(ax, 34, finalScolio, stylecount)
                        elif  'scoliosis' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['scoliosis'] == '1':
                            sheet5.write(ax, 34, "无", stylecount)
                        else:
                            sheet5.write(ax, 34, "", stylecount)
                        # 皮疹
                        if 'rash' in json.loads(rseult.phy_exa) and json.loads(rseult.phy_exa)['rash'] == 2:
                            sheet5.write(ax, 35, json.loads(rseult.phy_exa)['rashDescribe'], stylecount)
                        else:
                            sheet5.write(ax, 35, "无", stylecount)

                        # 运动发育落后
                        if 'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '2':
                            sheet5.write(ax, 36, json.loads(rseult.mot_dev_back)['sport'], stylecount)
                        elif  'motDevBack' in json.loads(rseult.mot_dev_back) and json.loads(rseult.mot_dev_back)['motDevBack'] == '1':
                            sheet5.write(ax, 36, "无", stylecount)
                        else:
                            sheet5.write(ax, 36, "", stylecount)
                        # 语言发育落后
                        if 'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '2':
                            sheet5.write(ax, 37, json.loads(rseult.lan_dev_back)['language'], stylecount)
                        elif  'lanDevBack' in json.loads(rseult.lan_dev_back) and json.loads(rseult.lan_dev_back)['lanDevBack'] == '1':
                            sheet5.write(ax, 37, "无", stylecount)
                        else:
                            sheet5.write(ax, 37, "", stylecount)
                        # 智力发育落后
                        if 'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '2':
                            sheet5.write(ax, 38, json.loads(rseult.int_dev_back)['intelligence'], stylecount)
                        elif  'intDevBack' in json.loads(rseult.int_dev_back) and json.loads(rseult.int_dev_back)['intDevBack'] == '1':
                            sheet5.write(ax, 38, "无", stylecount)
                        else:
                            sheet5.write(ax, 38, "", stylecount)
                        # 听力异常
                        if 'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '2':
                            sheet5.write(ax, 39, json.loads(rseult.abn_hear)['hear'], stylecount)
                        elif  'abnHear' in json.loads(rseult.abn_hear) and json.loads(rseult.abn_hear)['abnHear'] == '1':
                            sheet5.write(ax, 39, "无", stylecount)
                        else:
                            sheet5.write(ax, 39, "", stylecount)
                        # 反复感染史
                        if 'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '2':
                            sheet5.write(ax, 40, json.loads(rseult.rec_inf_his)['infection'], stylecount)
                        elif  'recInfHis' in json.loads(rseult.rec_inf_his) and json.loads(rseult.rec_inf_his)['recInfHis'] == '1':
                            sheet5.write(ax, 40, "无", stylecount)
                        else:
                            sheet5.write(ax, 40, "", stylecount)
                        # 抽搐史
                        if rseult.con_his == '1':
                            sheet5.write(ax, 41, "无", stylecount)
                        elif  rseult.con_his == '2':
                            sheet5.write(ax, 41, "有", stylecount)
                        else:
                            sheet5.write(ax, 41, "", stylecount)
                        # 诊疗方案
                        if rseult.dia_trea_plan is not None and len(rseult.dia_trea_plan) > 0:
                            # 诊疗方案(多种选择)
                            if rseult.dia_trea_plan:
                                # 治疗1
                                if 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '1':
                                    sheet5.write(ax, 42, "未治疗", stylecount)
                                # 治疗2
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '2':
                                    sheet5.write(ax, 42, "rhGH治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet5.write(ax, 43, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)                     
                                            except:
                                                pass
                                        sheet5.write(ax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet5.write(ax, 43, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet5.write(ax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗3
                                elif  'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '7':
                                    sheet5.write(ax, 42, "GnRHa治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet5.write(ax, 43, "达必佳针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet5.write(ax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet5.write(ax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet5.write(ax, 45, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet5.write(ax, 43, "伯恩若康针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet5.write(ax, 43, "贝依针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每35天1次", stylecount)
                                # 治疗4
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '3':
                                    sheet5.write(ax, 42, "GnRHal联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet5.write(ax, 43, "达必佳针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '3':
                                        sheet5.write(ax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '4':
                                        sheet5.write(ax, 43, "抑那通针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(11.25), stylecount)    
                                        sheet5.write(ax, 45, "11.25mg，每12周1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '5':
                                        sheet5.write(ax, 43, "伯恩若康针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '6':
                                        sheet5.write(ax, 43, "贝依针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每28天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '7':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每14天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '8':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        if json.loads(rseult.phy_exa)['weight']:
                                            sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每21天1次", stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '9':
                                        sheet5.write(ax, 43, "达菲林针", stylecount)
                                        sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(3.75), stylecount)    
                                        sheet5.write(ax, 45, "3.75mg，每35天1次", stylecount)
                                # 治疗5
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '8':
                                    sheet5.write(ax, 42, "芳香化酶抑制剂", stylecount)
                                # 治疗6
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '11':
                                    sheet5.write(ax, 42, "停止芳香化酶抑制剂", stylecount)
                                # 治疗7
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '10':
                                    sheet5.write(ax, 42, "芳香化酶联合生长激素治疗", stylecount)
                                    if 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '1':
                                        sheet5.write(ax, 43, "短效rhGH", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass                    
                                        sheet5.write(ax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                    elif 'rhGH' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['rhGH'] == '2':
                                        sheet5.write(ax, 43, "长效生长激素（PEG-rhGH）", stylecount)
                                        if json.loads(rseult.phy_exa)['weight'] and json.loads(rseult.dia_trea_plan)['rhGHdose']:
                                            try:
                                                sheet5.write(ax, 44, float(json.loads(rseult.phy_exa)['weight'])*float(safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose'])), stylecount)    
                                            except:
                                                pass
                                        sheet5.write(ax, 45, safe_str(json.loads(rseult.dia_trea_plan)['rhGHdose']), stylecount)
                                # 治疗8
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '12':
                                    sheet5.write(ax, 42, "停止芳香化酶联合生长激素治疗", stylecount)
                                # 治疗9
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '4':
                                    sheet5.write(ax, 42, "停止GnRHa治疗", stylecount)
                                # 治疗10
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '5':
                                    sheet5.write(ax, 42, "停止GnRHa联合生长激素治疗", stylecount)
                                # 治疗11
                                elif 'diaPlan' in json.loads(rseult.dia_trea_plan) and json.loads(rseult.dia_trea_plan)['diaPlan'] == '6':
                                    sheet5.write(ax, 42, "停止生长激素治疗", stylecount)
                                else:
                                    sheet5.write(ax, 42, "", stylecount)
                        # 其他
                        sheet5.write(ax, 46, rseult.past_other, stylecount)  
                        sheet5.write(ax, 47, safe_str(json.loads(rseult.lab_exa)['LH']), stylecount)  # LH
                        sheet5.write(ax, 48, safe_str(json.loads(rseult.lab_exa)['FSH']), stylecount)  # FSH
                        sheet5.write(ax, 49, safe_str(json.loads(rseult.lab_exa)['E2']), stylecount)  # E2
                        sheet5.write(ax, 50, safe_str(json.loads(rseult.lab_exa)['T']), stylecount)  # T
                        sheet5.write(ax, 51, safe_str(json.loads(rseult.lab_exa)['PRL']), stylecount)  # PRL
                        sheet5.write(ax, 52, safe_str(json.loads(rseult.lab_exa)['IGF']), stylecount)  # IGF-1
                        sheet5.write(ax, 53, safe_str(json.loads(rseult.lab_exa)['IGFBP3']),stylecount)  # IGFBP-3
                        # 甲功
                        if 'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '2':
                            sheet5.write(ax, 54, "异常", stylecount)
                            sheet5.write(ax, 55, json.loads(rseult.lab_exa)['thyroidDescribe'], stylecount)
                        elif  'thyroid' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['thyroid'] == '1':
                            sheet5.write(ax, 54, "正常", stylecount)
                        sheet5.write(ax, 56, safe_str(json.loads(rseult.lab_exa)['ACTH']), stylecount)  # ACTH
                        sheet5.write(ax, 57, safe_str(json.loads(rseult.lab_exa)['cortisol']),stylecount)  # 皮质醇（8am）
                        sheet5.write(ax, 58, safe_str(json.loads(rseult.lab_exa)['DHEAS']),stylecount)  # DHEAs
                        sheet5.write(ax, 59, safe_str(json.loads(rseult.lab_exa)['OHP']),stylecount)  # 17-OHP
                        # 血常规
                        if 'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '2':
                            sheet5.write(ax, 60, "异常", stylecount)
                            sheet5.write(ax, 61, json.loads(rseult.lab_exa)['bloodDescribe'], stylecount)
                        elif  'blood' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['blood'] == '1':
                            sheet5.write(ax, 60, "正常", stylecount)
                        # 尿常规
                        if 'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '2':
                            sheet5.write(ax, 62, "异常", stylecount)
                            sheet5.write(ax, 63, json.loads(rseult.lab_exa)['urinalysisDescribe'], stylecount)
                        elif  'urinalysis' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['urinalysis'] == '1':
                            sheet5.write(ax, 62, "正常", stylecount)
                        # 肝肾脂糖电解质
                        if 'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '2':
                            sheet5.write(ax, 64, "异常", stylecount)
                            sheet5.write(ax, 65, json.loads(rseult.lab_exa)['laklgeDescribe'], stylecount)
                        elif  'LAKLGE' in json.loads(rseult.lab_exa) and json.loads(rseult.lab_exa)['LAKLGE'] == '1':
                            sheet5.write(ax, 64, "正常", stylecount)
                        # 乙肝三系
                        HBsMap = {
                            '1': '阴性',
                            '2': 'HBSAb阳性',
                            '3': '小三阳',
                            '4': '大三阳',
                        }
                        HBs = json.loads(rseult.lab_exa)['HBs']
                        finalHBs = HBsMap.get(HBs)
                        sheet5.write(ax, 66, finalHBs, stylecount)
                        sheet5.write(ax, 67, safe_str(json.loads(rseult.lab_exa)['gh']),stylecount)  # Gh药物激发试验-Gh峰值
                        sheet5.write(ax, 68, rseult.electr, stylecount)  # 心电图
                        gon_B_ult = rseult.gon_B_ult.replace("\n","")
                        # 性腺B超
                        if patient.sex == '1':
                            gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                 "睾丸大小-左侧：" + safe_str(json.loads(gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                            sheet5.write(ax, 69, gonadUltrasoundMan, stylecount)
                        elif patient.sex == '2':
                            # 判断随访囊肿(是否存在存在)
                            if 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '1':
                                cyst_info = "有，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                    json.loads(gon_B_ult.replace("\n", ""))['cystDescribe'])
                            elif 'isCyst' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['isCyst'] == '2':
                                cyst_info = "无"
                            else:
                                cyst_info = "未选择"
                            gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(gon_B_ult)['uterusThr'])+ "(cm)，宫颈长约：" + safe_str(json.loads(gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                   "左侧卵巢大小约：" + safe_str(json.loads(gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                   "最大滤泡直径大小：" + safe_str(json.loads(gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                   "有无囊肿：" + cyst_info
                            sheet5.write(ax, 69, gonadUltrasoundWoman, stylecount)
                        # 垂体MRI
                        if 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '2':
                            sheet5.write(ax, 70, json.loads(gon_B_ult)['mriDescribe'], stylecount)
                        elif 'MRI' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['MRI'] == '1':
                            sheet5.write(ax, 70, "正常", stylecount)
                            # 左侧甲状腺b超
                        if 'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '2':
                            sheet5.write(ax, 71, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidLBOther']), stylecount)
                        elif  'ThyroidLB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidLB'] == '1':
                            sheet5.write(ax, 71, "正常", stylecount)
                        # 右侧甲状腺b超
                        if 'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '2':
                            sheet5.write(ax, 72, "甲状腺结节分级: " + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBGradation']) + "\n" + "甲状腺大小:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBSize']) + "\n" + "弥漫性病变:" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBLesions']) + "\n" + "其他：" + safe_str(
                                json.loads(gon_B_ult)['ThyroidRBOther']), stylecount)
                        elif  'ThyroidRB' in json.loads(gon_B_ult) and json.loads(gon_B_ult)['ThyroidRB'] == '1':
                            sheet5.write(ax, 72, "正常", stylecount)
                        sheet5.write(ax, 73, rseult.spe_kar, stylecount)  # 染色体核型
                        # 生物样本库是否存在
                        if rseult.bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.bio_sam_bank) and json.loads(rseult.bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号" + safe_str(
                                            item['id']) + "\n" + "样本类型" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet5.write(ax, 74, listCount, stylecount)  # 生物样本库
                        # 父亲生物样本库
                        if rseult.f_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.f_bio_sam_bank) and json.loads(rseult.f_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.f_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(item['name'])
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"

                            sheet5.write(ax, 75, listCount, stylecount)  # 生物样本库
                        # 母亲生物样本库
                        if rseult.m_bio_sam_bank:
                            listCount = ""
                            if 'bioBank' in json.loads(rseult.m_bio_sam_bank) and json.loads(rseult.m_bio_sam_bank)[
                                'bioBank'] == '2':
                                sampleList = json.loads(rseult.m_bio_sam_bank)['sampleClass'].replace("'", '"')
                                listCount = ""
                                if len(sampleList) > 2:
                                    Data = json.loads(sampleList)
                                    for item in Data:
                                        map = {
                                            '1': 'DNA样本',
                                            '2': '血清',
                                            '3': '血浆',
                                            '4': '尿液',
                                        }
                                        finalname = map.get(item['name'])
                                        listCount = listCount + "样本编号:" + safe_str(
                                            item['id']) + "\n" + "样本类型:" + safe_str(finalname)
                                else:
                                    listCount = "无"
                            else:
                                listCount = "无"
                            sheet5.write(ax, 76, listCount, stylecount)  # 生物样本库
                        if rseult.gen_mut_name:
                            genMutName = json.loads(rseult.gen_mut_name)
                            genMutNamecount = ""
                            for genMutNameItem in genMutName:
                                if 'father' in genMutNameItem:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['father'])+","+genMutNameItem['mother']+"/"
                                elif 'ties1' in genMutNameItem:
                                    genMutNamecount = genMutNamecount+safe_str(genMutNameItem['genName'])+","+safe_str(genMutNameItem['Rna'])+","+\
                                                    safe_str(genMutNameItem['amino'])+","+safe_str(genMutNameItem['ties1'])+","+genMutNameItem['ties2']+","+genMutNameItem['ties3']+","+genMutNameItem['ties4']+","+genMutNameItem['ties5']+","+genMutNameItem['ties6']+"/"
                            sheet5.write(ax, 77, genMutNamecount, stylecount)
                        try:
                            main_dia = json.loads(rseult.main_dia)
                            if main_dia['mainDia'] == "['其他']":
                                sheet5.write(ax, 78, "其他："+main_dia['DiaIllustrate'], stylecount)  # 主要诊断
                            elif main_dia['mainDia'] == "['特发性矮小', '其他(手填或不填)']":
                                sheet5.write(ax, 78, "特发性矮小:其他："+main_dia['mainDiaIllustrate'], stylecount)  # 主要诊断
                            else:
                                sheet5.write(ax, 78, main_dia['mainDia'], stylecount)  # 主要诊断
                        except:
                            pass
                        sheet5.write(ax, 79, rseult.sec_dia, stylecount)  # 次要诊断
                        iFlCount = 80
                        iFl_list = ""
                        # 随访
                        i = 1
                        for iFl in follow:
                            if i>suifangmaxshort:
                                suifangmaxshort = i
                            # 序号
                            sheet5.write(ax, iFlCount, i, stylecount)
                            iFlCount += 1
                            # 随访日期
                            sheet5.write(ax, iFlCount, iFl.foll_time.strftime('%Y-%m-%d'), stylecount)
                            iFlCount += 1
                            # 年龄
                            sheet5.write(ax, iFlCount, safe_str(iFl.age), stylecount)
                            iFlCount += 1
                            # 身高
                            sheet5.write(ax, iFlCount, safe_str(iFl.Ht), stylecount)
                            iFlCount += 1
                            # 体重
                            sheet5.write(ax, iFlCount, safe_str(iFl.Wt), stylecount)
                            iFlCount += 1
                            # 外生殖器分期
                            if patient.sex == '1':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet5.write(ax, iFlCount, "G" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet5.write(ax, iFlCount, "", stylecount)
                                    iFlCount += 2
                            elif patient.sex == '2':
                                if iFl.gen_stag is not None and len(iFl.gen_stag) > 0:
                                    sheet5.write(ax, iFlCount+1, "B" + safe_str(json.loads(iFl.gen_stag)), stylecount)
                                    iFlCount += 2
                                else:
                                    sheet5.write(ax, iFlCount+1, "", stylecount)
                                    iFlCount += 2
                            else:
                                iFlCount += 2
                            # 阴毛分期
                            sheet5.write(ax, iFlCount, safe_str(iFl.pub_stag), stylecount)
                            iFlCount += 1
                            # IGF-1
                            sheet5.write(ax, iFlCount, safe_str(iFl.IGF1), stylecount)
                            iFlCount += 1
                            # IGFBP-3
                            sheet5.write(ax, iFlCount, safe_str(iFl.IGFBP3), stylecount)
                            iFlCount += 1
                            # 空腹血糖
                            sheet5.write(ax, iFlCount, safe_str(iFl.fas_blood_glu), stylecount)
                            iFlCount += 1
                            # 空腹胰岛素
                            sheet5.write(ax, iFlCount, safe_str(iFl.fas_insulin) + "(IU/L)" , stylecount)
                            iFlCount += 1
                            # 糖化血红蛋白
                            sheet5.write(ax, iFlCount, safe_str(iFl.gly_hem), stylecount)
                            iFlCount += 1
                            # 性腺B超
                            if patient.sex == '1':
                                gonadUltrasoundMan = "睾丸大小-右侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisRightThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisRightLon']) + "(cm)" + "\n" + \
                                                     "睾丸大小-左侧：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftOne']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftTwo']) + "cm" + "×" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftThr']) + "cm，长径：" + safe_str(json.loads(iFl.gon_B_ult)['testisLeftLon']) + "（cm）" + "\n"
                                sheet5.write(ax, iFlCount, gonadUltrasoundMan, stylecount)
                                iFlCount += 1
                                # 女
                            elif patient.sex == '2':
                                # 判断随访囊肿(是否存在存在)
                                if 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '1':
                                    cyst_info = "有，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cyst']) + "侧囊肿，" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystOne']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystTwo']) + "*" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))[
                                            'cystThr']) + "*(cm)，囊肿描述：" + safe_str(
                                        json.loads(iFl.gon_B_ult.replace("\n", ""))['cystDescribe'])
                                elif 'isCyst' in json.loads(iFl.gon_B_ult) and json.loads(iFl.gon_B_ult)['isCyst'] == '2':
                                    cyst_info = "无"
                                else:
                                    cyst_info = "未选择"
                                gonadUltrasoundWoman = "子宫大小" + safe_str(json.loads(iFl.gon_B_ult)['uterusOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['uterusThr']) + "(cm)，宫颈长约：" + safe_str(json.loads(iFl.gon_B_ult)['cervixLong']) + "(cm)，内膜厚度：" + safe_str(json.loads(iFl.gon_B_ult)['intima']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaLeftThr']) + "(cm)" + "\n" + \
                                                       "左侧卵巢大小约：" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightOne']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightTwo']) + "*" + safe_str(json.loads(iFl.gon_B_ult)['ovaRightThr']) + "(cm)" + "\n" + \
                                                       "最大滤泡直径大小：" + safe_str(json.loads(iFl.gon_B_ult)['follDiameter']) + "(cm)" + "\n" + \
                                                       "有无囊肿：" + cyst_info
                                sheet5.write(ax, iFlCount, gonadUltrasoundWoman, stylecount)
                                iFlCount += 1
                            else:
                                sheet5.write(ax, iFlCount, "未填写", stylecount)
                                iFlCount += 1
                            # 其他
                            sheet5.write(ax, iFlCount, safe_str(iFl.other), stylecount)
                            iFlCount += 1
                            # 甲攻
                            if iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '2':
                                jg = "甲攻异常," + safe_str(json.loads(iFl.Jiagong)['JiagongDes']) + ""
                            elif iFl.Jiagong is not None and 'Jiagong' in json.loads(iFl.Jiagong) and json.loads(iFl.Jiagong)['Jiagong'] == '1':
                                jg = "甲攻正常"
                            else:
                                jg = ""
                            sheet5.write(ax, iFlCount, jg, stylecount)
                            iFlCount += 1
                            # 肝肾脂电解质
                            gsdjz = "未填写"
                            if iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '2':
                                gsdjz = "肝肾脂电解质异常," + safe_str(json.loads(iFl.liv_kid_lip)['LAKLEdes'])
                            elif iFl.liv_kid_lip is not None and 'livKidLip' in json.loads(iFl.liv_kid_lip) and json.loads(iFl.liv_kid_lip)['livKidLip'] == '1':
                                gsdjz = "肝肾脂电解质正常"
                            sheet5.write(ax, iFlCount, gsdjz, stylecount)
                            iFlCount += 1
                            # 诊疗方案
                            zlfa = ""
                            yyjx = ""
                            yyjl = ""
                            dwjl = ""
                            lhyyjx = ""
                            lhyyjl = ""
                            lhdwjl = ""
                            if rseult.dia_trea_plan and iFl.dia_trea_plan != "无":
                                try:
                                    # 治疗1
                                    if 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '1':
                                        zlfa = "未治疗"
                                    # 治疗2
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '2':
                                        zlfa = "rhGH治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗3
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '7':
                                        zlfa = "GnRHa治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                    # 治疗4
                                    elif  'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '3':
                                        zlfa = "GnRHal联合生长激素治疗"
                                        if 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次" 
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = '达必佳针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '3':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '4':
                                            yyjx = '抑那通针'
                                            yyjl = float(iFl.Wt)*float(11.25)
                                            dwjl = "11.25mg，每12周1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '5':
                                            yyjx = '伯恩若康针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '6':
                                            yyjx = '贝依针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每28天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '7':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每14天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '8':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每21天1次"
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '9':
                                            yyjx = '达菲林针'
                                            yyjl = float(iFl.Wt)*float(3.75)
                                            dwjl = "3.75mg，每35天1次"
                                        if 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '1':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '短效rhGH'
                                        elif 'rhUnitedCustomization' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhUnitedCustomization'] == '2':
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhUnitedDose']:
                                                try:
                                                    lhyyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose']))
                                                except:
                                                    pass
                                            lhdwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhUnitedDose'])
                                            lhyyjx = '长效生长激素 (PEG-rhGH)'
                                    # 治疗5
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '8':
                                        zlfa = "芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '11':
                                        zlfa = "停止芳香化酶抑制剂"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗6
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '10':
                                        zlfa = "芳香化酶联合生长激素治疗"
                                        if  'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '1':
                                            yyjx = "短效rhGH"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                        elif 'rhGH' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['rhGH'] == '2':
                                            yyjx = "长效生长激素（PEG-rhGH)"
                                            if iFl.Wt and json.loads(iFl.dia_trea_plan)['rhGHdose']:
                                                try:
                                                    yyjl = float(iFl.Wt)*float(safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose']))
                                                except:
                                                    pass
                                            dwjl = safe_str(json.loads(iFl.dia_trea_plan)['rhGHdose'])
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '12':
                                        zlfa = "停止芳香化酶联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '4':
                                        zlfa = "停止GnRHa治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗7
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '5':
                                        zlfa = "停止GnRHa联合生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    # 治疗8
                                    elif 'diaPlan' in json.loads(iFl.dia_trea_plan) and json.loads(iFl.dia_trea_plan)['diaPlan'] == '6':
                                        zlfa = "停止生长激素治疗"
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                    else:
                                        zlfa = ""
                                        yyjx = ""
                                        yyjl = ""
                                        dwjl = ""
                                except:
                                    pass
                            sheet5.write(ax, iFlCount, zlfa, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, yyjx, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, yyjl, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, dwjl, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, lhyyjx, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, lhyyjl, stylecount)
                            iFlCount += 1
                            sheet5.write(ax, iFlCount, lhdwjl, stylecount)
                            iFlCount += 1
                            i = i+1
                        ax = ax + 1
                sfheads = [u'序号',u'随访日期', u'年龄', u'身高', u'体重', u'外生殖器分期（男）', u'双乳发育分期（女）',
                           u'阴毛分期', u'IGF-1', u'IGFBP-3', u'空腹血糖', u'空腹胰岛素', u'糖化血红蛋白', u'性腺B超',
                           u'其他', u'甲攻', u'肝肾脂电解质', u'诊疗方案', u'用药剂型', u'用药剂量', u'单位剂量',u'联合用药剂型', u'联合用药剂量', u'联合单位剂量']
                
                z=0
                while suifangmaxshort:
                    i = 1
                    while i < len(sfheads)+1:
                        sheet2.write(0, 80+z*len(sfheads)+i-1, sfheads[i-1], style)
                        sheet4.write(0, 83+z*len(sfheads)+i-1, sfheads[i-1], style)
                        i = i + 1
                    suifangmaxshort = suifangmaxshort-1
                    z = z+1
            
            else:
                return False
            # 保存到本地
            newFilePath = settings.STA_PATH
            if not os.path.exists(newFilePath):
                os.makedirs(newFilePath)
            # ws.save(settings.STA_PATH + '/oneExcel.xls')
            ws.close()
            return settings.STA_PATH + '/oneExcel.xls'
        except Exception as e:
            print(e)
            return False
        

# 导出病例Excel(一个excel)
    def imp_case_excel_mas(patientlist):
        # 导出病例主表Excel文件
        # 设置HTTPResponse的类型
        response = HttpResponse(content_type='application/vnd.ms-excel')
        response['Content-Disposition'] = 'attachment;filename=oneExcel.xls'
        style = excelStyle.style
        stylecount = excelStyle.stylecount
        # 创建工作簿
        # ws = xlwt.Workbook(encoding='utf-8')
        # ws = xlsxwriter.Workbook('/RAID5/eksjk/storage/oneExcel.xls')                  #  原*（需线上）
        ws = xlsxwriter.Workbook('F:/imgtest/oneExcel.xls')                              #  新* (线下测试路径)
        """导出excel表"""
        try:
            if patientlist:
                mas = 1
                # 添加第一页数据表
                w = ws.add_worksheet('mas') 
                style = ws.add_format({
                    # 'bold': True,  # 字体加粗
                    'border': 1,  # 单元格边框宽度
                    'align': 'left',  # 水平对齐方式
                    'valign': 'vcenter',  # 垂直对齐方式
                    # 'fg_color': '#F4B084',  # 单元格背景颜色
                    # 'text_wrap': True,  # 是否自动换行
                })
                stylecount = ws.add_format({
                    'border': 1,  # 单元格边框宽度
                    'align': 'left',  # 水平对齐方式
                    'valign': 'vcenter',  # 垂直对齐方式
                })
                # 写入表头
                heads = [u'病历号', u'入组序号', u'入组时间', u'所在中心', u'患者姓名', u'出生日期', u'年龄', u'性别', u'出生地',u'确诊时间', 
                         u'确诊年龄', u'首诊主诉', u'联系方式', u'首次提交时间', u'孕周', u'胎次',u'产次', u'出生体重', u'出生身长', u'孕期感染', 
                         u'分娩方式', u'窒息史', u'既往史',u'病例编号', 
                         u'首发表现', u'首发表现时间', u'家族史:', u'检查日期', u'一般情况', u'发育分期', u'甲状腺肿大', u'皮肤检查（多选）', 
                         u'骨骼检查（多选）', u'B超检查日期', u'子宫超声情况', u'子宫情况具体描述', u'卵巢超声情况', u'甲状腺B超', u'肾上腺B超', u'肾脏B超',
                         u'病变骨骼X线', u'CT', u'MR', u'全身骨扫描', u'心电图', u' X线骨龄', u'垂体MR', u'常规化验检查日期', u'血常规', u'肝功能', u'肾功能',
                         u'电解质', u'血脂', u'骨代谢检查日期', u'骨代谢检查', u'性激素检查日期', u' 性激素检查', u'甲状腺功能及抗体检查日期', u'甲状腺功能及抗体检查',
                         u'肾上腺功能检查日期', u'肾上腺功能检查', u'生长激素分泌功能检查时间', u'生长激素分泌功能检查', u'糖代谢情况时间', u' 糖代谢情况',
                         u'GnRH激发', u'小剂量地塞米松抑制', u'生长激素-葡萄糖抑制', u'基因测定', u'遗传学检测方法', u' 检测结果', u' 检测版本', u' 突变位点',
                         u'病理活检', u'是否存在性早熟', u'是否存在甲状腺功能亢进', u'是否存在生长激素分泌过多', u'是否存在皮质醇增多症'
                         ]
                i = 0
                while i < len(heads):
                    w.write(0, i, heads[i], style)
                    i = i + 1

                for patient in patientlist:
                    # 循环全部，出错看所有循环项
                    # print(patient.id)
                    # if patient.id == 3634:
                    #     print(patient.id)
                    # mas
                    if patient.dis_class == '10000004':
                        rseult = query_sub_table(patient.dis_class, patient.id)
                        # follow = models.PatFoll.objects.filter(patient__pk=patient.id)
                        # 写入每一行对应的数据
                        # 病历号
                        w.write(mas, 0, patient.medrec_num, stylecount)
                        # 入组序号
                        w.write(mas, 1, patient.enrollment_num, stylecount)
                        # 入组时间
                        w.write(mas, 2, patient.enrollment_time, stylecount)
                        # 所在中心
                        w.write(mas, 3, patient.hospital_name, stylecount)
                        # 患者姓名
                        w.write(mas, 4, patient.name, stylecount)
                        # 出生日期
                        if patient.birth_time is not None:
                            w.write(mas, 5, patient.birth_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            w.write(mas, 5, "", stylecount)
                        # 年龄
                        w.write(mas, 6, patient.age, stylecount)
                        # 性别
                        if patient.sex == '2':
                            w.write(mas, 7, "女", stylecount)
                        elif patient.sex == '1':
                            w.write(mas, 7, "男", stylecount)
                        else:
                            w.write(mas, 7, "", stylecount)
                        # 籍贯
                        natPla = patient.nat_pla
                        if natPla is not None and len(natPla)>0:
                            # 第一种方法
                            natPList = ast.literal_eval(natPla)
                            # 第1-3个
                            # 选择两个
                            if natPList is not None and len(natPList) == 2:
                                one_data = natPList[0]
                                two_data = natPList[1]
                            elif natPList is not None and len(natPList) == 3:
                                one_data = natPList[0]
                                two_data = natPList[1]
                                three_data = natPList[2]
                            # 获取比对
                            # 选择两个
                            if  ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 2:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                w.write(mas, 8, (one or "未知") + "/" + (two or "未知"), stylecount)
                            # 选择三个
                            elif ast.literal_eval(natPla) is not None and len(ast.literal_eval(natPla)) == 3:
                                one = area.get(one_data)
                                two = area.get(two_data)
                                three = area.get(three_data)
                                w.write(mas, 8, (one or "未知") + "/" + (two or "未知") + "/" + (three or "未知"), stylecount)
                            # 未选择
                            else:
                                w.write(mas, 8, "未选择籍贯", stylecount)
                        # 确诊时间
                        if patient.fir_vis_time is not None:
                            w.write(mas, 9, patient.fir_vis_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        else:
                            w.write(mas, 9, "", stylecount)
                        # 确诊年龄
                        w.write(mas, 10, patient.fir_vis_age, stylecount)
                        # 主诉
                        w.write(mas, 11, patient.chi_com, stylecount)
                        # 联系方式
                        w.write(mas, 12, patient.contacts_num, stylecount)
                        # 首次提交时间
                        w.write(mas, 13, patient.one_time.strftime('%Y-%m-%d %H:%M:%S'), stylecount)
                        # 孕周
                        w.write(mas, 14, patient.ges_week, stylecount)
                        # 胎次
                        w.write(mas, 15, patient.parity, stylecount)
                        # 产次
                        w.write(mas, 16, patient.pronum, stylecount)
                        # 出生体重
                        w.write(mas, 17, patient.BWt, stylecount)
                        # 出生身长
                        w.write(mas, 18, patient.BL, stylecount)
                        preinf_map = {
                            '1': '有',
                            '2': '无',
                            '3': '不详',
                        }
                        # 孕期感染
                        w.write(mas, 19, preinf_map.get(patient.pregnancy_infection) or "", stylecount)
                        casesec_map = {
                            '1': '顺产',
                            '2': '剖宫产',
                            '3': '臀围产',
                            '4': '足先露',
                            '5': '其他',
                            '6': '不详',
                        }
                        # 分娩方式
                        w.write(mas, 20, casesec_map.get(patient.cesa_sec) or "", stylecount)
                        # 窒息史
                        w.write(mas, 21, preinf_map.get(patient.cesa_asphyxia) or "", stylecount)
                        # 既往史
                        w.write(mas, 22, patient.past_his, stylecount)
                        # 病例编号
                        w.write(mas, 23, patient.case_num, stylecount)
                        # ini_map = {
                        #     '1': ['性早熟',{'1': ['乳房发育',{'1':'左侧','2':'右侧','3':'双侧'}],'2': '阴道出血','3': '阴毛早现','4': '睾丸增大'}],
                        #     '2': ['骨骼病变',{'1': ['骨痛',{'1':'四肢','2':'躯干骨','3':'颅面骨'}],'2': '病理性骨折','3': '骨骼畸形'}],
                        #     '3': ['牛奶咖啡斑',{}],
                        #     '4': ['甲状腺功能亢进',{}],
                        #     '5': ['肢端肥大/生长过速',{}],
                        #     '6': ['视力下降',{'1': ['左眼',{'1':'左侧','2':'右侧','3':'双侧'}],'2': '右眼','3': '双眼'}],
                        #     '7': ['嗅觉减退',{}],
                        #     '8': ['听力下降',{'1': ['左侧',{'1':'左侧','2':'右侧','3':'双侧'}],'2': '右侧','3': '双侧'}],
                        #     '9': ['其他',{}],
                        # }
                        # [{"isMainBehave": "3", "isMinorBehave": "1", "isSkeletalLesions": "1", "isSkeletalParts": "1", "isLimbParts": "1", "isTorsoBones": "", "isCraniofacialBones": "", "isCoffeeSpots": "123", "isCoffeeSpotArea": "2", "isLocationVision": "", "isHearingArea": "", "isOtherPresentations": "", "isMinorPlace": "1", "isTestisPlace": ""}]
                        # [{"isMainBehave": "2", "isMinorBehave": "4", "isSkeletalLesions": "3", "isSkeletalParts": "1", "isLimbParts": "1", "isTorsoBones": "", "isCraniofacialBones": "", "isCoffeeSpots": "123", "isCoffeeSpotArea": "2", "isLocationVision": "", "isHearingArea": "", "isOtherPresentations": "", "isMinorPlace": "1", "isTestisPlace": ""}]
                        # # 首发表现
                        # inidate = ''
                        # if rseult.ini_per_ci:
                        #     ini_per_ci = json.loads(rseult.ini_per_ci)
                        #     for iniperci in ini_per_ci:
                        #         inidate = inidate + ini_map[iniperci['isMainBehave']][0] +' '+ini_map[iniperci['isMainBehave']][1][iniperci['isMainBehave']]
                        # w.write(mas, 24, ini_per_ci, stylecount)
                        # # 首发表现时间
                        # w.write(mas, 25, patient.ini_per_date, stylecount)
                        # # 家族史
                        # w.write(mas, 26, patient.fam_his, stylecount)
                        # # 检查日期
                        # w.write(mas, 26, patient.check_time, stylecount)
                        # # 一般情况
                        # w.write(mas, 27, patient.gen_sit, stylecount)
                        # # 发育分期
                        # if patient.sex == '1':
                        #     # 外生殖器分期(男)
                        #     if (rseult.ex_genitalia is not None) and len(rseult.ex_genitalia) > 0:
                        #         w.write(mas, 29, "外生殖器分期(男): G" + safe_str(rseult.ex_genitalia), stylecount)
                        #     else:
                        #         w.write(mas, 29, "未选择", stylecount)
                        # elif patient.sex == '2':
                        #     # 双乳发育分期(女)
                        #     if (rseult.breast_dev is not None) and len(rseult.breast_dev) > 0:
                        #         w.write(mas, 29, "双乳发育分期(女): B" + safe_str(rseult.breast_dev), stylecount)
                        #     else:
                        #         w.write(mas, 29, "未选择", stylecount)
                        # else:
                        #     w.write(mas, 29, "未选择", stylecount)
                        # 甲状腺肿大
                        # 皮肤检查（多选）
                        # 骨骼检查（多选）
                        # B超检查日期
                        # 子宫超声情况
                        # 子宫情况具体描述
                        # 卵巢超声情况
                        # 甲状腺B超
                        # 肾上腺B超
                        # 肾脏B超
                        # 病变骨骼X线
                        # CT
                        # MR
                        # 全身骨扫描
                        # 心电图
                        # X线骨龄
                        # 垂体MR
                        # 常规化验检查日期
                        # 血常规
                        # 肝功能
                        # 肾功能
                        # 电解质
                        # 血脂
                        # 骨代谢检查日期
                        # 骨代谢检查
                        # 性激素检查日期
                        # 性激素检查
                        # 甲状腺功能及抗体检查日期
                        # 甲状腺功能及抗体检查
                        # 肾上腺功能检查日期
                        # 肾上腺功能检查
                        # 生长激素分泌功能检查时间
                        # 生长激素分泌功能检查
                        # 糖代谢情况时间
                        # 糖代谢情况
                        # GnRH激发
                        # 小剂量地塞米松抑制
                        # 生长激素-葡萄糖抑制
                        # 基因测定
                        # 遗传学检测方法
                        # 检测结果
                        # 检测版本
                        # 突变位点
                        # 病理活检
                        # 是否存在性早熟
                        # 是否存在甲状腺功能亢进
                        # 是否存在生长激素分泌过多
                        # 是否存在皮质醇增多症
                        mas = mas + 1     
            else:
                return False
            # 保存到本地
            newFilePath = settings.STA_PATH
            if not os.path.exists(newFilePath):
                os.makedirs(newFilePath)
            # ws.save(settings.STA_PATH + '/oneExcel.xls')
            ws.close()
            return settings.STA_PATH + '/oneExcel.xls'
        except Exception as e:
            print(e)
            return False


import subprocess


def convert_video_format(file, file_path):
    name = os.path.splitext(file_path)[0]
    keep_filename = False

    if 'mp4' in file.content_type:
        # 将编码格式为mp4v的mp4文件转换为h264编码的Mmp4，这样才能在浏览器中播放
        ffprobe = f'ffprobe {file_path}'
        exitcode, output = subprocess.getstatusoutput(ffprobe)
        if 'mp4v' in output or 'msmpeg4v2' in output:
            keep_filename = True
            new_file_path = f'{name}h264.mp4'
            command = f'ffmpeg -y -i {file_path} -vcodec h264 -f mp4 {new_file_path}'
        else:
            return file_path
    else:
        new_file_path = f'{name}.mp4'
        command = f'ffmpeg -y -i {file_path} {new_file_path}'

    # print('command:', command)

    exitcode, output = subprocess.getstatusoutput(command)
    if exitcode == 0:
        if keep_filename:
            os.rename(new_file_path, file_path)
            new_file_path = file_path
        else:
            os.remove(file_path)
    else:
        print('convert_video_format failed:', output)
        new_file_path = ''

    return new_file_path


def save_file(file_path, file):
    content = file.read()
    if '.dcm' in file_path:
        raw_bytes = pydicom.filebase.DicomBytesIO(content)
        dataset = pydicom.dcmread(raw_bytes)

        # 脱敏
        if 'PatientName' in dataset:
            dataset.PatientName = '******'
        if 'InstitutionName' in dataset:
            dataset.InstitutionName = '******'
        if 'InstitutionAddress' in dataset:
            dataset.InstitutionAddress = '******'

        pydicom.dcmwrite(file_path, dataset, False)
    else:
        with open(file_path, 'wb') as f:
            f.write(content)

        if 'video' in file.content_type:
            file_path = convert_video_format(file, file_path)

    return file_path


# 图片另存为，已支持多图上传
# def save_img(caseid, organ, path, file, kwargs):
def save_img(caseid, organ, path, file):
    filepath = [
        settings.IMG_PATH,
        organ,
        str(caseid % 64),
        str(caseid),
        *path.split('-')
    ]
    filepath = os.path.join(*filepath)
    dirname = os.path.dirname(filepath)

    if file:
        try:
            if not os.path.exists(dirname):
                os.makedirs(dirname)

            filepath = save_file(filepath, file)
            if filepath == '':
                return False
        except Exception as e:
            print(e)
            return False

    if organ == 'follow':
        patFoll = models.PatFoll.objects.get(pk=caseid)
        if patFoll.image is not None and len(patFoll.image) > 0:
            img_path = json.loads(patFoll.image)
        else:
            img_path = {}
    elif organ == 'mas':
        dis_class = models.Patient.objects.get(pk=caseid).dis_class
        # 根据疾病分类确定分表
        subtable = query_sub_table(dis_class, caseid)
        if subtable.glu_img_path is not None and len(subtable.glu_img_path) > 0:
            img_path = json.loads(subtable.glu_img_path)
        else:
            img_path = {}
    else:
        dis_class = models.Patient.objects.get(pk=caseid).dis_class
        # 根据疾病分类确定分表
        subtable = query_sub_table(dis_class, caseid)
        if subtable.B_ult_image is not None and len(subtable.B_ult_image) > 0:
            img_path = json.loads(subtable.B_ult_image)
        else:
            img_path = {}

    items = path.split('-')
    last_item = items[-2]
    filename = os.path.basename(filepath)
    present = img_path
    for i in items[:-2]:
        if i not in present:
            present[i] = {}
        present = present[i]

    # 新增或者替换文件
    if file:
        if last_item in present:
            item = present[last_item]
            # 兼容之前的单文件存储
            if not isinstance(item, list):
                present[last_item] = [item]
            present[last_item].append(filename)
        else:
            present[last_item] = [filename]
    # 删除文件
    elif last_item in present:
        item = present[last_item]
        if os.path.exists(filepath):
            os.remove(filepath)
        if isinstance(item, str):
            del present[last_item]
        else:
            present[last_item].remove(filename)
    if organ == 'follow':
        patFoll.image = json.dumps(img_path, ensure_ascii=False)
        patFoll.save()
    elif organ == 'mas':
        subtable.glu_img_path = json.dumps(img_path, ensure_ascii=False)
        subtable.save()
    else:
        subtable.B_ult_image = json.dumps(img_path, ensure_ascii=False)
        subtable.save()

    return True


def write_zip(caseid, check_position):
    organ_map = {
        '10010001': 'thyroid',
        '10010002': 'breast',
        '10010003': 'ovary',
        '10010004': 'suplymnod'
    }
    file_type = organ_map[check_position] + "/"
    fileNum = str(int(caseid) % 64)
    try:
        zipFilePath = settings.ZIP_PATH + file_type + fileNum + "/"
        if not os.path.exists(zipFilePath):
            os.makedirs(zipFilePath)
        z_name = zipFilePath + str(caseid) + '.zip'
        returnPath = file_type + fileNum + "/" + str(caseid) + '.zip'
        if os.path.exists(z_name):
            os.remove(z_name)
        # 目标文件夹路径
        dirpath = settings.IMG_PATH + file_type + fileNum + "/" + str(caseid)
        zip = zipfile.ZipFile(z_name, "a", zipfile.ZIP_DEFLATED)
        for path, dirnames, filenames in os.walk(dirpath):
            # 去掉目标跟路径，只对目标文件夹下边的文件及文件夹进行压缩
            fpath = path.replace(dirpath, '')
            for filename in filenames:
                zip.write(os.path.join(path, filename), os.path.join(fpath, filename))
        zip.close()
        # return z_name
        return returnPath
    except Exception as e:
        print(e)
        return False

def write_zippl(caseids):
    organ_map = {
        '10000001': 'bone',
        '10000002': 'fss',
        '10000003': 'cpp',
        '10000004': 'mas'
    }
    z_name = settings.ZIP_PATH + '111.zip'
    if os.path.exists(z_name):
        os.remove(z_name)
    zip = zipfile.ZipFile(z_name, "a", zipfile.ZIP_DEFLATED)
    for caseid in caseids:
        caseid = decode_id(caseid)
        fileNum = str(int(caseid) % 64)
        try:
            patient = datamainmodels.Patient.objects.get(pk=caseid)
            casenum = patient.case_num
            file_type = organ_map[patient.dis_class] + "/"
            # 根据检查部位确定分表
            rseult = query_sub_table(patient.dis_class, caseid)
            follow = datamainmodels.PatFoll.objects.filter(patient__pk=caseid)
            mas = datamainmodels.Mas.objects.filter(patient__pk=caseid)
            # 初始化一个空列表来存储所有 masfollow 对象
            masfollow=[]
            for i in mas:
                masfollows = datamainmodels.MasFoll.objects.filter(mas__pk=i.pk)
                # 从内部使用【变为】外部使用
                masfollow.extend(masfollows)
            # 导出Excel
            # 判断 masfollow 是否存在

            is_succeed = ExcelFile.imp_case_excel(patient, rseult, follow, masfollow)
            if is_succeed:
                zipFilePath = settings.ZIP_PATH+file_type+fileNum + "/"
                if not os.path.exists(zipFilePath):
                    os.makedirs(zipFilePath)
                # 目标文件夹路径
                dirpath = settings.IMG_PATH+file_type+fileNum + "/"+str(caseid)
                for path, dirnames, filenames in os.walk(dirpath):
                    # 去掉目标跟路径，只对目标文件夹下边的文件及文件夹进行压缩
                    fpath = path.replace(dirpath, '')
                    # fpath2 = dirpath.split('/')[-1]
                    for filename in filenames:
                        zip.write(os.path.join(path, filename), os.path.join(casenum + '/' + fpath, filename))
        except Exception as e:
            print(e)
            return False
    zip.close()
    return z_name


def opinion_save_img(opinionid, organ, path, file):
    filepath = [
        settings.IMG_PATH,
        organ,
        str(opinionid),
        path
    ]
    filepath = os.path.join(*filepath)
    dirname = os.path.dirname(filepath)

    if file:
        try:
            if not os.path.exists(dirname):
                os.makedirs(dirname)

            filepath = save_file(filepath, file, None, None)
            if filepath == '':
                return False
        except Exception as e:
            print(e)
            return False

    opinion = notiopimodels.Opinion.objects.get(pk=opinionid)
    if opinion.img_path is not None and len(opinion.img_path) > 0:
        img_path = json.loads(opinion.img_path)
    else:
        img_path = {}

    present = img_path
    filename = os.path.basename(filepath)
    # 新增或者替换文件
    if file:
        if 'imgpath' in present:
            # 文件替换
            present['imgpath'].append(filename)
        else:
            present['imgpath'] = [filename]
    # 删除文件
    elif 'imgpath' in present:
        item = present['imgpath']
        if os.path.exists(filepath):
            
            os.remove(filepath)
        if isinstance(item, str):
            del present['imgpath']
        else:
            present['imgpath'].remove(filename)

    opinion.img_path = json.dumps(img_path, ensure_ascii=False)
    opinion.save()

    return True


# 随访图片另存为，已支持多图上传
def foll_save_img(caseid, organ, path, file):
    filepath = [
        settings.IMG_PATH,
        organ,
        str(caseid % 64),
        str(caseid),
        *path.split('-')
    ]
    filepath = os.path.join(*filepath)
    dirname = os.path.dirname(filepath)

    if file:
        try:
            if not os.path.exists(dirname):
                os.makedirs(dirname)

            filepath = save_file(filepath, file)
            if filepath == '':
                return False
        except Exception as e:
            print(e)
            return False
    patFoll = models.PatFoll.objects.get(pk=caseid)
    if patFoll.image is not None and len(patFoll.image) > 0:
        img_path = json.loads(patFoll.image)
    else:
        img_path = {}

    items = path.split('-')
    last_item = items[-2]
    filename = os.path.basename(filepath)
    present = img_path
    for i in items[:-2]:
        if i not in present:
            present[i] = {}
        present = present[i]

    # 新增或者替换文件
    if file:
        if last_item in present:
            item = present[last_item]
            # 兼容之前的单文件存储
            if not isinstance(item, list):
                present[last_item] = [item]
            present[last_item].append(filename)
        else:
            present[last_item] = [filename]
    # 删除文件
    elif last_item in present:
        item = present[last_item]
        if os.path.exists(filepath):
            os.remove(filepath)
        if isinstance(item, str):
            del present[last_item]
        else:
            present[last_item].remove(filename)                                                                                                                                                                                     

    patFoll.image = json.dumps(img_path, ensure_ascii=False)
    patFoll.save()

    return True


# 通过校验MD5 判断B内的文件与A 不同
def get_MD5(file_path):
    files_md5 = os.popen('md5 %s' % file_path).read().strip()
    file_md5 = files_md5.replace('MD5 (%s) = ' % file_path, '')
    return file_md5


def copyFile(path, out):
    if os.path.exists(path):
        for files in os.listdir(path):
            name = os.path.join(path, files)
            back_name = os.path.join(out, files)
            if os.path.isfile(name):
                if os.path.isfile(back_name):
                    if get_MD5(name) != get_MD5(back_name):
                        shutil.copy(name, back_name)
                else:
                    shutil.copy(name, back_name)
            else:
                if not os.path.isdir(back_name):
                    os.makedirs(back_name)
                copyFile(name, back_name)

# mask文件另存为
def save_maskfile(caseid, file):
    file_path = [
        settings.MASK_PATH,
        str(caseid % 64),
        str(caseid),
        '诊断图像',
        'mask.txt'
    ]
    filepath = os.path.join(*file_path)
    dirname = os.path.dirname(filepath)

    if file:
        try:
            if not os.path.exists(dirname):
                os.makedirs(dirname)
            # 打开特定的文件进行二进制的写操作
            with open(filepath, 'wb') as f:
                # 分块写入文件
                for chunk in file.chunks():
                    f.write(chunk)
        except Exception as e:
            print(e)
            return False
        return filepath
    # 未读取到文件
    else:
        return False

# mask文件读取
def read_mask(caseid):
    file_path = [
        settings.MASK_PATH,
        str(caseid % 64),
        str(caseid),
        '诊断图像',
        'mask.txt'
    ]
    filepath = os.path.join(*file_path)
    result = ''
    try:
        # mask.txt文件
        with open(filepath, 'r') as pfp:
            # 循环读取每一行的数据
            for line in pfp.readlines():
                lins = line.replace('\n', '')
                result += lins
            print(result)
            return result
    except Exception as e:
        print(e)
        return False

