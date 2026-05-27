import datetime
import os

import jwt
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

import datamain.models
import login.models
from common.utils import Code, parse_arguments, FormattedView, require_arguments, decode_id, token_required
from common import extractors
import requests, json

from datamain.views import modifyorAddCase, modifyorAddShort, modifyorAddSexpre, modifyorAddMas, \
    modifyorAddMasFoll
from . import models
from datamain import models as datamodels
from login import models as loginmoddel
import django.utils.timezone as timezone
from django.db.models import Count
from django.db import transaction

# Create your views here.

# 微信登录根据code查询session_key+openId
class CSessionKeyView(FormattedView):
    extractor = extractors.AllExtractor()
    # 不需要登录
    loginRequired = False

    # 查询session_key+openId
    @parse_arguments()
    def get(self, request, *args, **kwargs):
        try:
            context = {}
            appid = 'wx1ae7e6dba9c5e94b'
            secret = 'd6661fd8fc8a5cc161b2ee0730603894'
            code = kwargs['code']
            url = 'https://api.weixin.qq.com/sns/jscode2session?appid='+appid+'&secret='+\
                  secret+'&js_code='+code+'&grant_type=authorization_code'  # 網址
            response = requests.get(url=url)
            context['code'] = response.text
            # 判断数据库中是否有该用户存在，不存在就注册
            try:
                chartuser = models.ChartUser.objects.get(openid=json.loads(response.text)["openid"])
                if chartuser.phone_num!=None:
                    context['isOne'] = 0
                    if chartuser.is_tongb == '0':
                        context['isTongb'] = 0
                    else:
                        context['isTongb'] = 1
                else:
                    context['isOne'] = 1
            except:
                chartuser = models.ChartUser.objects.create(openid=json.loads(response.text)["openid"],
                                                            key=json.loads(response.text)["session_key"],
                                                            del_flg=1,is_tongb=0)
                context['isTongb'] = 0
                context['isOne'] = 1
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

# 绑定医生
class BdDectorView(FormattedView):
    extractor = extractors.AllExtractor()
    # 不需要登录
    loginRequired = False

    @parse_arguments('body')
    def post(self, request, *args, **kwargs):
        try:
            context = {}
            openid = kwargs['openid']
            dector = decode_id(kwargs['Id'])
            try:
                chartuser = models.ChartUser.objects.get(openid=openid)
                chartuser.doctor = dector
                chartuser.save()
            except:
                return self.make_response(None, Code.DATA_PARSE_FAILED)
            context['msg'] = '绑定医生成功'
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

# 首次登录根据病历号或者手机号查询是否有病例
class oneCaseByBLPView(FormattedView):
    extractor = extractors.AllExtractor()
    # 不需要登录
    loginRequired = False

    @parse_arguments()
    def get(self, request, *args, **kwargs):
        try:
            context = {}
            openid = kwargs['openid']
            medrec_num = None
            phone_num = None
            try:
                # if 'mobilenumber' in kwargs and kwargs['mobilenumber'] != 'undefined'and len(kwargs['mobilenumber'])>0:
                #     medrec_num = kwargs['mobilenumber']
                #     pation = datamodels.Patient.objects.filter(medrec_num=medrec_num)
                context['msg'] = "200"
                if 'mobilephone' in kwargs and kwargs['mobilephone'] != 'undefined'and len(kwargs['mobilephone'])>0:
                    pation = datamodels.Patient.objects.filter(contacts_num=kwargs['mobilephone'])
                    if len(pation)==0:



                        context['msg'] = "404"
                    phone_num = kwargs['mobilephone']
                chartuser = models.ChartUser.objects.get(openid=openid)
                chartuser.medrec_num = medrec_num
                chartuser.phone_num = phone_num
                chartuser.is_tongb = 1
                chartuser.save()
                return self.make_response(context)
            except:
                context['msg'] = "404"



            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 首次录入数据,无历史数据
    @parse_arguments('body')
    def post(self, request, *args, **kwargs):
        try:
            context = {}
            openid = kwargs['openid']
            chartuser = models.ChartUser.objects.get(openid=openid)
            # 病历号
            medrec_num = kwargs['medrec_num']
            # 手机号
            phone_num = kwargs['mobilephone']

            chartuser.medrec_num = medrec_num
            chartuser.phone_num = phone_num
            disClass = '10000002'
            if 'grow' in kwargs and len(kwargs['grow']):
                if kwargs['grow'] == '性发育异常':
                    disClass = '10000001'
                elif kwargs['grow'] == '家族性矮小':
                    disClass = '10000002'
                elif kwargs['grow'] == '中枢性性早熟':
                    disClass = '10000003'
                elif kwargs['grow'] == 'mas':
                    disClass = '10000004'
            patient = datamodels.Patient()
            # 姓名
            patient_name = kwargs['patient_name']
            patient.dis_class = disClass
            patient.name = patient_name
            patient.contacts_num = phone_num
            patient.fir_vis_time = datetime.datetime.now
            patient.imp_per = chartuser.doctor
            patient.c_time = datetime.datetime.now
            patient.medrec_num = medrec_num
            patient.del_flg = '1'
            # 上传医生所在单位
            dector = loginmoddel.User.objects.get(pk=chartuser.doctor)
            patient.up_mec = dector.unit
            qianzui = 'US-Xcx' + str(
                timezone.now().year * 10000 + timezone.now().month * 100 + timezone.now().day)
            num = datamodels.Patient.objects.filter(case_num__contains=qianzui).values().aggregate(num=Count('case_num'))
            nums = num['num']
            caseNum = qianzui + str(nums + 1)
            case_num = caseNum
            patient.case_num = case_num
            # 出生日期
            if 'date_birth' in kwargs and kwargs['date_birth'] is not None and len(kwargs['date_birth']):
                time = kwargs['date_birth'][0:10]
                patient.birth_time = datetime.datetime.strptime(time + ' 00:00:00', "%Y-%m-%d %H:%M:%S")

            try:
                with transaction.atomic():
                    patient.save()
                    chartuser.save()
                    # 判断检查部位，新增附表
                    if disClass == "10000001":
                        result = modifyorAddCase(patient.pk, kwargs)
                        result.save()
                    elif disClass == "10000002":
                        result = modifyorAddShort(patient.pk, kwargs)
                        result.save()
                    elif disClass == "10000003":
                        result = modifyorAddSexpre(patient.pk, kwargs)
                        result.save()
                    elif disClass == "10000004":
                        result = modifyorAddMas(patient.pk, kwargs)
                        result.save()
                        result2 = modifyorAddMasFoll(result.pk, kwargs)
                        result2.save()

                    patFoll = datamodels.PatFoll()
                    #  身高
                    if 'height' in kwargs:
                        patFoll.Ht = kwargs['height']
                    #  体重
                    if 'weight' in kwargs:
                        patFoll.Wt = kwargs['weight']
                    #  年龄
                    # 出生日期
                    if 'date_birth' in kwargs and kwargs['date_birth'] is not None and len(kwargs['date_birth']):
                        time = kwargs['date_birth'][0:10]
                        BirthDate = datetime.datetime.strptime(time + ' 00:00:00', "%Y-%m-%d %H:%M:%S")
                        Today = datetime.datetime.now()
                        interval = Today - BirthDate
                        interval = interval.days
                        age = str(interval / 365).split('.')
                        year = age[0]
                        month = int(round(float("0." + age[1]) * 12, 0))
                        patFoll.age = str(year) + "岁" + str(month) + "个月"
                    # 病例主表id
                    patFoll.patient_id = patient.pk
                    # 删除标志
                    patFoll.del_flg = '1'
                    patFoll.save()
            except:
                return self.make_response(None, Code.DATA_PARSE_FAILED)

            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)



# 查询随访记录
class CaseByBLPView(FormattedView):
    extractor = extractors.AllExtractor()
    # 不需要登录
    loginRequired = False

    @parse_arguments()
    def get(self, request, *args, **kwargs):
        try:
            context = {}
            openid = kwargs['openid']
            try:
                chartuser = models.ChartUser.objects.get(openid=openid)
                patient = datamodels.Patient.objects.filter(contacts_num=chartuser.phone_num)[0]
                patFolllist = datamodels.PatFoll.objects.filter(patient=patient.pk)
                # 性别
                sex = patient.sex
                # 年龄身高
                ageh = []
                # 年龄体重
                agew = []
                for patFoll in patFolllist:
                    if patFoll.Ht and patFoll.Wt and patFoll.age:
                        age = patFoll.age.replace("个月", "").split("岁")
                        age = round(int(age[0]) + int(age[1]) / 12, 2)
                        dgageh = []
                        dgagew = []
                        dgageh.append(age)
                        dgageh.append(float(patFoll.Ht))
                        ageh.append(dgageh)
                        dgagew.append(age)
                        dgagew.append(float(patFoll.Wt))
                        agew.append(dgagew)
                context['sex'] = sex
                context['ageh'] = ageh
                context['agew'] = agew
                return self.make_response(context)
            except:
                return self.make_response(None, Code.DATA_PARSE_FAILED)
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 有历史数据，添加随访
    @parse_arguments('body')
    def post(self, request, *args, **kwargs):
        try:
            context = {}
            openid = kwargs['openid']
            try:
                chartuser = models.ChartUser.objects.get(openid=openid)
                patient = datamodels.Patient.objects.filter(contacts_num=chartuser.phone_num)[0]
                # patFoll = models.PatFoll.objects.get(patient_id=patient.pk)
                patFoll = datamodels.PatFoll()
                #  身高
                if 'height' in kwargs:
                    patFoll.Ht = kwargs['height']
                #  体重
                if 'weight' in kwargs:
                    patFoll.Wt = kwargs['weight']
                #  年龄
                BirthDate = patient.birth_time
                Today = datetime.datetime.now()
                interval = Today - BirthDate
                interval = interval.days
                age = str(interval / 365).split('.')
                year = age[0]
                month = int(round(float("0." + age[1]) * 12, 0))
                patFoll.age = str(year) + "岁" + str(month) + "个月"
                # 病例主表id
                patFoll.patient_id = patient.pk
                # 删除标志
                patFoll.del_flg = '1'
                patFoll.save()
            except:
                return self.make_response(None, Code.DATA_PARSE_FAILED)
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)


# 做登录
@csrf_exempt
def doLoginView(request):
    if request.method == 'POST':
        try:
            print("Jinrr")
            # 从请求体中获取前端传来的数据(json.loads():解析为json格式)
            data = json.loads(request.body.decode('utf-8'))

            # 获取前端生成用于登录的code
            code = data.get('code')

            # 定义小程序所需要的参数 appid 和 secret
            appid = 'wx1ae7e6dba9c5e94b'
            secret = 'd6661fd8fc8a5cc161b2ee0730603894'

            # 构建微信API请求的URL（https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html）
            url = 'https://api.weixin.qq.com/sns/jscode2session?appid=' + appid + '&secret=' + \
                  secret + '&js_code=' + code + '&grant_type=authorization_code'

            # 发送请求，请求微信返回的结果[此步请勿开代理，易报请求错误]
            response = requests.get(url=url)

            # 赋值成功的返回信息,供后续查询
            responseText = response.text
            print(responseText)

            # 【token生成_对生成增强安全】 登录成功后,根据微信请求成功结果中的 【openid】 =》 生成token
            payload = {
                'openid': json.loads(response.text)["openid"],
                'exp': datetime.datetime.utcnow() + datetime.timedelta(days=7)  # token 有效期7天 (现在时间 + 7天后时间)
            }
            # 【token生成_对生成增强安全】 jwt.encode(payload,   '密钥（可以是'123',也可以是123）',   algorithm='HS256') 但是不安全，这里采用随机。增强安全
            token = jwt.encode(payload, 'mk5677123', algorithm='HS256')

            # 尝试从数据库中查找openid对应的用户
            try:
                # 在此步中若找到相应的数据，按顺序执行步骤，若没有会产生{DoesNotExist}ChartUser matching query does not exist. 则进入except步骤进行创建
                # 存在
                chartuser = models.ChartUser.objects.get(openid=json.loads(response.text)["openid"])
                print(chartuser)

                if chartuser.new_user_flag == '0':
                    # 返回老用户数据
                    patientData = {
                        'myself_picture': chartuser.myself_picture if chartuser.myself_picture else "",
                        'contacts_name': chartuser.contacts_name if chartuser.contacts_name else "",
                        'contacts_num': chartuser.contacts_num if chartuser.contacts_num else "",
                        'p_emial': chartuser.p_emial if chartuser.p_emial else "",
                        'idcard': chartuser.idcard if chartuser.idcard else "",
                        'nat_pla': chartuser.nat_pla if chartuser.nat_pla else "",
                    }
                    # 返回登录成功状态 + token
                    response_data = {'code': 200, 'msg': '登录成功', 'token': token, 'new_user_flag': 0, 'patientData': patientData}
                    return JsonResponse(response_data)

                elif chartuser.new_user_flag == '1':
                    return JsonResponse({'code': 200, 'msg': '【新用户】-登录成功', 'token': token, 'new_user_flag': 1})
            except:
                # 创建新用户
                models.ChartUser.objects.create(openid=json.loads(response.text)["openid"],
                                                            key=json.loads(response.text)["session_key"], del_flg=1,
                                                            is_tongb=0, new_user_flag=1)
                response_data = {'code': 200, 'msg': '【新用户】-登录成功', 'token': token, 'new_user_flag': 1}
                return JsonResponse(response_data)
        except:
            return HttpResponse("身份验证过程中出错.", code=500)


# 保存base64图片
# def save_base64_image(base64_string, file_name):
#     # 解码 Base64 字符串
#     image_data = base64.b64decode(base64_string)
# 保存Base64编码的图片到文件系统
            # base64_string = data['headimgurl']
            # file_name = f"{decoded_token}_profile.jpg"


# 个人信息的存储
@csrf_exempt
def selfInfoStoreView(request):
    if request.method == 'POST':
        print('jin')
        # 获取请求头内容
        token = request.headers.get('token')
        try:
            # secret_key = os.urandom(32).hex()
            # 解析是否通过token验证 => 不通过会跳对应过期/无效
            token = jwt.decode(token, 'mk5677123', algorithms=['HS256'])
            decoded_token = token['openid']
            # print('通过token验证', decoded_token)

            # 通过获取请求体里边的内容
            data = json.loads(request.body.decode('utf-8'))
            # print('内容', data)

            # 存在修改数据
            chartUser = models.ChartUser.objects.get(openid=decoded_token)
            # print('找到相应的数据', patient)

            # 如果有相应的数据就在相应的数据上进行值的修改
            chartUser.myself_picture = data['headimgurl']             # 头像
            chartUser.contacts_name = data['name']                    # 联系人姓名
            chartUser.contacts_num = data['phone']                    # 联系人手机号
            chartUser.p_emial = data['email']                         # 联系人邮箱
            chartUser.idcard = data['idCard']                         # 联系人身份证
            chartUser.nat_pla = data['address'].split()               # 联系人籍贯（家庭地址）
            chartUser.new_user_flag = 0
            chartUser.save()
            return JsonResponse({'code': 200, 'msg': '提交成功'})

        # token 已过期
        except jwt.ExpiredSignatureError:
            print('过期')
            return JsonResponse({'error': '请求头中token过期'}, code=400)
        # token 无效
        except jwt.InvalidTokenError:
            print('无效token')
            return JsonResponse({'error': '请求头中token无效'}, code=401)
    else:
        return JsonResponse({'error': '无效的请求方法'}, code=405)



# 个人信息查询
@csrf_exempt
@token_required
def selectSlefInfoView(request):
    if request.method == 'POST':
        token = request.decoded_token
        decoded_token = token['openid']
        try:
            chartuser = models.ChartUser.objects.get(openid=decoded_token)
            patientData = {
                'myself_picture': chartuser.myself_picture,
                'contacts_name': chartuser.contacts_name,
                'contacts_num': chartuser.contacts_num,
                'p_emial': chartuser.p_emial,
                'idcard': chartuser.idcard,
                'nat_pla': chartuser.nat_pla,
                'new_user_flag': chartuser.new_user_flag,
            }
            return JsonResponse({'code': 200, 'msg': '查询成功', 'patientData': patientData})
        except:
            return JsonResponse({'code': 201, 'msg': '查询失败'})

@csrf_exempt
@token_required
def addBabyView(request):
    """ 加入定义的@token_required 避免下面重复内容 """
    # if request.method == 'POST':
    #     print('111')
    #     # 获取请求头内容
    #     token = request.headers.get('token')
    #     try:
    #         # 解析是否通过token验证 => 不通过会跳对应过期/无效
    #         jwt.decode(token, 'mk5677123', algorithms=['HS256'])
    #         print('通过')
    #     # token 已过期
    #     except jwt.ExpiredSignatureError:
    #         print('过期')
    #         return JsonResponse({'error': '请求头中token过期'}, code=400)
    #     # token 无效
    #     except jwt.InvalidTokenError:
    #         print('无效token')
    #         return JsonResponse({'error': '请求头中token无效'}, code=401)
    # else:
    #     return JsonResponse({'error': '无效的请求方法'}, code=405)

    """ 直接正常写,避免重复判断token验证 """
    if request.method == 'POST':
        # 取的@token_required的解密令牌
        token = request.decoded_token
        decoded_token = token['openid']
        print('jin')

        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))

        # 处理日期字段
        survey_date_str = data.get('survey')
        if survey_date_str:
            survey_date = datetime.datetime.strptime(survey_date_str + ' 00:00:00', "%Y-%m-%d %H:%M:%S")
        else:
            survey_date = datetime.datetime.now()

        # 用于新增宝宝数据
        new_patient = datamain.models.Patient.objects.create(
            xcx_card=decoded_token,
            name=data['name'],
            sex=data['sex'],
            birth_time=datetime.datetime.strptime(data['birthData'] + ' 00:00:00', "%Y-%m-%d %H:%M:%S"),
            relation=data['relation'],
            self_tel=data['self_tel'],
            doctor_name=data['doctor_id'],
            FHt=data['FHt'],
            MHt=data['MHt'],
            dis_class=data['dis_class'],
            expected_height=data['expected_height'],
            current_city=data['current_city'],
            height=data['height'],
            weight=data['weight'],
            rbone_age=data['rbone_age_year'] + '-' + data['rbone_age_month'],
            cbone_age=data['cbone_age_year'] + '-' + data['cbone_age_month'],
            past_time=survey_date,
            past_height=data['past_height'],
            past_weight=data['past_weight'],
            baby_flag=1,
            imp_per=2,
        )

        # 用于新增历史评测相关体重身高数据
        if data['past_height'] != "" and data['past_weight'] != '':
            datamain.models.PatFoll.objects.create(
                patient=new_patient,
                foll_time=survey_date,
                Ht=data['past_height'],
                Wt=data['past_weight'],
            )
            datamain.models.PatFoll.objects.create(
                patient=new_patient,
                foll_time=datetime.datetime.now(),
                Ht=data['height'],
                Wt=data['weight'],
            )
        else:
            datamain.models.PatFoll.objects.create(
                patient=new_patient,
                foll_time=datetime.datetime.now(),
                Ht=data['height'],
                Wt=data['weight'],
            )

        return JsonResponse({'code': 200, 'msg': '保存成功'})
    else:
        return JsonResponse({'error': '无效的请求方法'}, code=405)


@csrf_exempt
@token_required
def editBabyView(request):
    if request.method == 'POST':
        # 取的@token_required的解密令牌
        token = request.decoded_token
        decoded_token = token['openid']
        print('jin')

        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))

        # 处理日期字段
        survey_date_str = data.get('survey')
        if survey_date_str:
            survey_date = datetime.datetime.strptime(survey_date_str + ' 00:00:00', "%Y-%m-%d %H:%M:%S")
        else:
            survey_date = datetime.datetime.now()

        patient = datamain.models.Patient.objects.filter(xcx_card=decoded_token, id=data['id'], baby_flag=1)
        # 进行数据修改
        if patient.exists():
            isSuccess = patient.update(
                xcx_card=decoded_token,
                name=data['name'],
                sex=data['sex'],
                birth_time=datetime.datetime.strptime(data['birthData'] + ' 00:00:00', "%Y-%m-%d %H:%M:%S"),
                relation=data['relation'],
                self_tel=data['self_tel'],
                doctor_name=data['doctor_id'],
                FHt=data['FHt'],
                MHt=data['MHt'],
                dis_class=data['dis_class'],
                expected_height=data['expected_height'],
                current_city=data['current_city'],
                height=data['height'],
                weight=data['weight'],
                rbone_age=data['rbone_age_year'] + '-' + data['rbone_age_month'],
                cbone_age=data['cbone_age_year'] + '-' + data['cbone_age_month'],
                past_time=survey_date,
                past_height=data['past_height'],
                past_weight=data['past_weight'],
            )

            # 修改历史评测相关体重身高数据

            if isSuccess > 0:
                return JsonResponse({'code': 200, 'msg': '操作成功'})
            else:
                return JsonResponse({'code': 201, 'msg': '操作失败'})


@csrf_exempt
@token_required
def deletBabyView(request):
    if request.method == 'POST':
        # 取的@token_required的解密令牌
        token = request.decoded_token
        decoded_token = token['openid']
        print('jin')

        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))

        patient = datamain.models.Patient.objects.filter(xcx_card=decoded_token, id=data['id'], baby_flag=1)
        # 进行数据修改
        if patient.exists():
            isSuccess = patient.update(
                xcx_card=decoded_token,
                name=data['name'],
                baby_flag=0,
            )
            if isSuccess > 0:
                return JsonResponse({'code': 200, 'msg': '操作成功'})
            else:
                return JsonResponse({'code': 201, 'msg': '操作失败'})


@csrf_exempt
@token_required
def selectBabyView(request):
    if request.method == 'POST':
        # 取的@token_required的解密令牌
        token = request.decoded_token
        decoded_token = token['openid']
        print('jin')

        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))
        try:
            # 使用 get 方法获取单个对象
            patient = datamain.models.Patient.objects.get(xcx_card=decoded_token, id=data['id'], baby_flag=1)
            # print('patient:', patient)  # 添加调试信息

            # 根据患者的性别确定照片URL
            if patient.sex == '男':
                photo_url = 'https://pic.616pic.com/ys_img/00/75/02/QHD87QwvbV.jpg'
            elif patient.sex == '女':
                photo_url = 'https://bpic.588ku.com/element_pic/20/06/30/978e71e51f5f0025c8d8c1bf07345e3b.jpg%21/fw/253/quality/100/unsharp/true/compress/true#'
            else:
                photo_url = ''  # 如果未指定性别，使用默认或占位符URL
            # strftime: 日期格式转字符串， strptime：字符串转日期格式
            patient_data = {
                'id': patient.id,
                'name': patient.name,
                'sex': patient.sex,
                'birth_time': patient.birth_time.strftime('%Y-%m-%d'),
                'relation': patient.relation,
                'self_tel': patient.self_tel,
                'FHt': patient.FHt,
                'MHt': patient.MHt,
                'dis_class': patient.dis_class,
                'expected_height': patient.expected_height,
                'current_city': patient.current_city,
                'height': patient.height,
                'weight': patient.weight,
                'rbone_age': patient.rbone_age,
                'cbone_age': patient.cbone_age,
                'past_time': patient.past_time.strftime('%Y-%m-%d'),
                'past_height': patient.past_height,
                'past_weight': patient.past_weight,
                'baby_flag': patient.baby_flag,
                'photo': photo_url,
                'doctor_name': patient.doctor_name,
            }
            # print('patient_data:', patient_data)  # 调试信息
            return JsonResponse({'code': 200, 'msg': '查询成功', 'patientData': patient_data})

        except datamain.models.Patient.DoesNotExist:
            # print('patient不存在')  # 添加调试信息
            return JsonResponse({'code': 202, 'msg': '没有相关数据请添加'})
        except Exception as e:
            print('Exception:', e)  # 添加调试信息
            return JsonResponse({'code': 202, 'msg': '没有相关数据请添加'})



@csrf_exempt
@token_required
def selectBabyAllView(request):
    if request.method == 'POST':
        # 取的@token_required的解密令牌
        token = request.decoded_token
        decoded_token = token['openid']
        print('jin')

        try:
            patientList = datamain.models.Patient.objects.filter(xcx_card=decoded_token, baby_flag=1)
            if patientList.exists():
                # 构建返回数据列表
                patient_data_list = []
                for patient in patientList:
                    # 根据患者的性别确定照片URL
                    if patient.sex == '男':
                        photo_url = 'https://pic.616pic.com/ys_img/00/75/02/QHD87QwvbV.jpg'
                        # photo_url = os.path.join(settings.BABY_IMAGES_DIR, '男_default.jpg')
                    elif patient.sex == '女':
                        photo_url = 'https://bpic.588ku.com/element_pic/20/06/30/978e71e51f5f0025c8d8c1bf07345e3b.jpg%21/fw/253/quality/100/unsharp/true/compress/true#'
                        # photo_url = os.path.join(settings.BABY_IMAGES_DIR, '女_default.jpg')
                    else:
                        photo_url = ''  # 如果未指定性别，使用默认或占位符URL
                    patient_data = {
                        'id': patient.id,
                        'name': patient.name,
                        'sex': patient.sex,
                        'birth_time': patient.birth_time.strftime('%Y-%m-%d'),
                        'relation': patient.relation,
                        'self_tel': patient.self_tel,
                        'FHt': patient.FHt,
                        'MHt': patient.MHt,
                        'dis_class': patient.dis_class,
                        'expected_height': patient.expected_height,
                        'current_city': patient.current_city,
                        'height': patient.height,
                        'weight': patient.weight,
                        'rbone_age': patient.rbone_age,
                        'cbone_age': patient.cbone_age,
                        'past_time': patient.past_time.strftime('%Y-%m-%d'),
                        'past_height': patient.past_height,
                        'past_weight': patient.past_weight,
                        'baby_flag': patient.baby_flag,
                        'photo': photo_url,
                        'doctor_name': patient.doctor_name,
                    }
                    patient_data_list.append(patient_data)
                return JsonResponse({'code': 200, 'msg': '查询成功', 'patientData': patient_data_list})
            else:
                return JsonResponse({'code': 202, 'msg': '没有相关数据请添加'})
        except:
            return JsonResponse({'code': 202, 'msg': '没有相关数据请添加'})


@csrf_exempt
@token_required
def selectDoctorView(request):
    if request.method == 'POST':
        users = login.models.User.objects.all().values('id', 'name')
        doctor_data = list(users)
        return JsonResponse({'code': 200, 'doctor_data': doctor_data})


# @csrf_exempt
# def upload_image(request):
#     if request.method == 'POST':
#         image = request.FILES.get('image')
#         if image:
#             # 确定保存路径
#             image_folder = os.path.join(settings.MEDIA_ROOT, 'img')
#             os.makedirs(image_folder, exist_ok=True)  # 确保文件夹存在
#
#             image_path = os.path.join(image_folder, image.name)
#             with open(image_path, 'wb+') as destination:
#                 for chunk in image.chunks():
#                     destination.write(chunk)
#
#             # 返回图片的URL
#             image_url = f'/media/img/{image.name}'
#             return JsonResponse({'code': 200, 'imageUrl': image_url})
#         else:
#             return JsonResponse({'code': 400, 'msg': '没有上传图片'})
#     return JsonResponse({'code': 405, 'msg': '仅支持POST请求'})




# @csrf_exempt
# def uploadImageView(request):
#     if request.method == 'POST':
#         try:
#             # 获取上传的文件
#             image = request.FILES['image']
#             sex = request.POST['sex']  # 获取性别信息
#
#             # 确定保存路径
#             file_extension = os.path.splitext(image.name)[1]
#             save_path = os.path.join(settings.BABY_IMAGES_DIR, f"{sex}_default{file_extension}")
#
#             # 保存文件
#             with open(save_path, 'wb+') as destination:
#                 for chunk in image.chunks():
#                     destination.write(chunk)
#
#             # 返回图片的相对路径
#             relative_path = os.path.relpath(save_path, settings.MEDIA_ROOT)
#             relative_path = relative_path.replace('\\', '/')  # 替换反斜杠为正斜杠
#
#             return JsonResponse({'code': 200, 'msg': '图片上传成功', 'path': f"{settings.MEDIA_URL}{relative_path}"})
#         except Exception as e:
#             print('Exception:', e)
#             return JsonResponse({'code': 400, 'msg': '图片上传失败'})


# 查询历史评测(体重、身高、日期)
@csrf_exempt
@token_required
def selectHistroyView(request):
    if request.method == 'POST':
        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))
        # 获取请求体内容
        patFolList = datamain.models.PatFoll.objects.filter(patient_id=data['id'])
        # 创建一个列表来存储 Ht 和 Wt 数据
        history_data = []
        # 迭代查询集并提取 Ht 和 Wt 字段
        for patFoll in patFolList:
            history_data.append({
                'patient_id': patFoll.patient_id,
                'patfoll_id': patFoll.id,
                'foll_time': patFoll.foll_time.strftime('%Y-%m-%d'),
                'Ht': patFoll.Ht,
                'Wt': patFoll.Wt
            })
        # 返回包含 Ht 和 Wt 数据的 JSON 响应
        return JsonResponse({'code': 200, 'history_data': history_data})
    else:
        return JsonResponse({'error': '无效的请求方法'}, code=405)


# 再次评测身高
@csrf_exempt
@token_required
def againReviewView(request):
    if request.method == 'POST':
        # 获取请求体内容
        data = json.loads(request.body.decode('utf-8'))
        print(data)