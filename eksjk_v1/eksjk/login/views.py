from . import models
from django.contrib import auth
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import ensure_csrf_cookie
from common.extractors import UserExtractor
from common import extractors
from common.utils import Code, FormattedView, require_arguments, get_public_key, decrypt, parse_arguments, encode_id, decode_id
# from captcha.models import CaptchaStore
import datetime
import json
from django.core.paginator import Paginator
from django.db import transaction, connection

class DologinView(FormattedView):
    extractor = UserExtractor()
    loginRequired = False

    # 登录功能
    @require_arguments(['word'], 'body')
    def post(self, request, *args, **kwargs):
        word = decrypt(kwargs['word'])
        if word is None:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

        items = word.split('"`"')
        if len(items) != 2:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

        username = items[0]
        password = items[1]
        # 调用auth模块的认证方法，判断用户名和密码是否正确，正确返回一个user_obj
        user = auth.authenticate(username=username, password=password)
        if user is not None:
            user.unit = encode_id(int(user.unit))
            # 登录成功,设置Session数据request.user可从Session数据获取登录对象
            # is_active ： 是否允许用户登录, 设置为False，可以在不删除用户的前提下禁止用户登录。
            auth.login(request, user)
            # 添加登录日志
            log = models.log()
            log.login_per_id = user.pk
            log.login_per_name = user.name
            if user.level=='3':
                manaLevel = '10030003'
            elif user.level in ['1', '2']:
                manaLevel = '10030002'
            else:
                manaLevel = '10030001'
            log.mana_level = manaLevel
            '''获取请求者的IP信息'''
            x_forwarded_for = request.META.get('HTTP_X_FORWARDED_FOR')  # 判断是否使用代理
            if x_forwarded_for:
                ip = x_forwarded_for.split(',')[0]  # 使用代理获取真实的ip
            else:
                ip = request.META.get('REMOTE_ADDR')  # 未使用代理获取IP
            log.com_ip = ip
            log.login_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            log.del_flg = '1'
            log.logout_time = datetime.datetime.now()
            log.save()
            request.session['logId'] = encode_id(log.pk)
            return self.make_response(user)
        else:
            return self.make_response(None, Code.USERNAME_PASSWORD_ERROR)

class LogoutView(FormattedView):
    # 登出功能
    @parse_arguments()
    def post(self, request, *args, **kwargs):
        logId = decode_id(request.session.get('logId'))
        log = models.log.objects.get(pk=logId)
        log.logout_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        log.save()
        auth.logout(request)
        return self.make_response(None)

class UserView(FormattedView):
    extractor = extractors.UserExtractor()
    # 查询用户详细数据
    @require_arguments(['queryUId'], 'url')
    def get(self, request, *args, **kwargs):
        # 主键id
        if 'queryUId' in kwargs and len(kwargs['queryUId'])>0:
            user_id = decode_id(kwargs['queryUId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            user = models.User.objects.get(pk=user_id)
            unitlist = getAllUnit()
            if user.unit in unitlist:
                user.unitName = unitlist[user.unit]
            user.unit = encode_id(int(user.unit))
            return self.make_response(user)
        except:
            return self.make_response(None)

    # 添加用户
    @require_arguments(['userName', 'password'], 'body')
    def put(self, request, *args, **kwargs):
        try:
            username = kwargs['userName']
            pwd = kwargs['password']
            email = kwargs['email']
            name = kwargs['name']
            sex = kwargs['sex']
            unit = ''
            if 'unit' in kwargs and len(kwargs['unit']) > 0:
                unit = decode_id(kwargs['unit'])
            professional = kwargs['professional']
            level = kwargs['level']
            department = ''
            if 'department' in kwargs and len(kwargs['department']) > 0:
                department = kwargs['department']
            # 去数据库创建一条记录
            models.User.objects.create_user(username=username, password=pwd, name=name, email=email, sex=sex,
                                            unit=unit, level=level, professional=professional, department=department)  # create_user创建用户
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 修改用户
    @require_arguments(['queryId'], 'body')
    def post(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            user_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        user = models.User.objects.get(pk=user_id)
        username = kwargs['userName']
        pwd = kwargs['password']
        newpwd = kwargs['newPassword']
        email = kwargs['email']
        name = kwargs['name']
        sex = kwargs['sex']
        unit = kwargs['unit']
        professional = kwargs['professional']
        level = kwargs['level']
        try:
            if username and len(username) > 0:
                user.username = username
            if email and len(email) > 0:
                user.email = email
            if name and len(name) > 0:
                user.name = name
            if sex and len(sex) > 0:
                user.sex = sex
            if unit and len(unit) > 0:
                unit = decode_id(kwargs['unit'])
                user.unit = unit
            if professional and len(professional) > 0:
                user.professional = professional
            if level and len(level) > 0:
                user.level = level
            if pwd and len(pwd) > 0:
                ok = user.check_password(pwd)
                if ok:
                    user.set_password(newpwd)
                    user.date_update = datetime.datetime.now()
                else:
                    return self.make_response(None, Code.USERNAME_PASSWORD_ERROR)
            if 'department' in kwargs and len(kwargs['department']) > 0:
                user.department = kwargs['department']
            user.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 删除人员(数据库删除)
    @require_arguments(['queryId'], 'url')
    def delete(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            user_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            user = models.User.objects.get(pk=user_id)
            user.delete()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)


class UserStatusView(FormattedView):
    extractor = extractors.UserExtractor()

    # 修改用户状态
    @require_arguments(['queryId'], 'body')
    def post(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            user_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        is_active = kwargs['isActive']
        try:
            user = models.User.objects.get(pk=user_id)
            user.is_active = is_active
            user.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

class UserListView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询人员列表
    @require_arguments(['limit'], 'url')
    def get(self, request, *args, **kwargs):
        filters = self.get_filters(request.user, kwargs)
        if filters is None:
            return self.make_response(None, Code.PERMISSION_DENIED)
        else:
            users = models.User.objects.filter(**filters).all().order_by('-date_joined')
            if 'unit' in kwargs and kwargs['unit']:
                unit = models.Unit.objects.filter(unit_name__contains=kwargs['unit']).values_list('id')
                users = users.filter(unit__in=unit)
            limit = kwargs['limit']
            paginator = Paginator(users, limit)  # 每页显示10条
            page = kwargs['currPage']
            if page == '0':
                page = '1'
            pagedata = {}  # 获取分页信息
            pagedata['count'] = paginator.count
            pagedata['num_pages'] = paginator.num_pages
            pagedata['per_page'] = limit
            pagedata['current'] = page
            context = {}
            list = paginator.page(page).object_list
            unitlist = getAllUnit()
            for user in list:
                unit = user.unit
                if unit in unitlist:
                    user.unit = unitlist[unit]
            contacts = self.extractor.extract(list)
            context['contacts'] = contacts
            context['pagedata'] = pagedata
            return self.make_response(context)

    def get_filters(self, user, source):
        """
        获取查询条件（将请求参数转换为数据库表字段）
        为安全考虑，请求传过来的参数名称尽量不要和数据库中的字段同名
        """

        filters = {}

        if 'name' in source:
            filters['name__contains'] = source['name']

        if 'username' in source and source['username']:
            filters['username__contains'] = source['username']

        # if 'unit' in source and source['unit']:
        #     filters['unit__contains'] = source['unit']

        if 'level' in source and source['level']:
            filters['level'] = source['level']

        # filters['is_active'] = '1'

        return filters

# 初始化用户权限, 写入session
def initPermission(request,user_obj):
    """
    初始化用户权限, 写入session
    :param request:
    :param user_obj:
    :return:
    """
    from django.conf import settings  # 通过这种方式导入配置，具有可迁移性
    # 用户权限url列表，--> 用于中间件验证用户权限
    permission_url_list = []
    role_list = []
    try:
        role_list = models.role.objects.filter(u_r__pk=user_obj.pk).values('id')
        permission_item_list = []
        for item in role_list:
            if item['id'] is not None:
                permission_item_list = models.Permission.objects.filter(role__pk=item['id']).values('url', 'title')
                for item in permission_item_list:
                    permission_url_list.append(item['url'])
        # 保存用户权限url列表
        request.session[settings.SESSION_PERMISSION_URL_KEY] = permission_url_list
    except:
        request.session[settings.SESSION_PERMISSION_URL_KEY] = permission_url_list

class CsrfView(FormattedView):
    """
    返回CSRFTOKEN以及RSA加密公钥
    """

    loginRequired = False

    @method_decorator(ensure_csrf_cookie)
    def get(self, request, *args, **kwargs):
        return self.make_response(get_public_key())

# 根据人员id获取人员姓名
def getNameById(userId):
    try:
        user = models.User.objects.get(pk=userId)
        return user.name
    except:
        return '查无此人'

# 查询所有单位
def getAllUnit():
    try:
        units = {}
        unitlist = models.Unit.objects.filter().all()
        for unit in unitlist:
            unitstr = '{"'+str(unit.id)+'":"'+unit.unit_name+'"}'
            units.update(json.loads(unitstr))
        return units
    except:
        return '查无单位'

# 单位
class UnitView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询单位详细数据
    @require_arguments(['queryId'], 'url')
    def get(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            unit_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            unit = models.Unit.objects.get(pk=unit_id)
            return self.make_response(unit)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 添加,修改单位详细数据
    @require_arguments(['unitName'], 'body')
    def put(self, request, *args, **kwargs):
        if 'queryId' in kwargs and len(kwargs['queryId'])>0:
            unit = models.Unit.objects.get(pk=decode_id(kwargs['queryId']))
        else:
            unit = models.Unit()
        if 'unitName' in kwargs and len(kwargs['unitName']) > 0:
            unit.unit_name = kwargs['unitName']
        unit.del_flg = '1'
        try:
            unit.save()
            return self.make_response(unit)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 删除单位详细数据
    @require_arguments(['queryId'], 'url')
    def delete(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            unit_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            unit = models.Unit.objects.get(pk=unit_id)
            unit.del_flg = '0'
            unit.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

# 查询单位列表
class UnitListView(FormattedView):
    extractor = extractors.AllExtractor()
    # 查询单位列表(分页)
    @parse_arguments()
    def get(self, request, *args, **kwargs):
        filters = {}
        filters['del_flg'] = '1'
        if 'unitName' in kwargs:
            filters['unit_name__contains'] = kwargs['unitName']
        unitlist = models.Unit.objects.filter(**filters)
        limit = kwargs['limit']
        paginator = Paginator(unitlist, limit)  # 每页显示10条
        page = kwargs['currPage']
        if page == '0':
            page = '1'
        pagedata = {}  # 获取分页信息
        pagedata['count'] = paginator.count
        pagedata['num_pages'] = paginator.num_pages
        pagedata['per_page'] = limit
        pagedata['current'] = page
        context = {}
        list = paginator.page(page).object_list
        contacts = self.extractor.extract(list)
        context['contacts'] = contacts
        context['pagedata'] = pagedata
        return self.make_response(context)

# 查询单位列表不分页
class UnitAll(FormattedView):
    extractor = extractors.AllExtractor()
    # 查询单位列表
    @parse_arguments()
    def get(self, request, *args, **kwargs):
        unitlist = models.Unit.objects.filter(del_flg='1')
        return self.make_response(unitlist)

# 根据人员查询需要弹出的公告
class NoticePerView(FormattedView):
    extractor = extractors.AllExtractor()
    @parse_arguments()
    def get(self, request, *args, **kwargs):
        # 组合sql语句
        user = request.user
        sql = "select log.id ,n.content,n.title from notiopi_pernoticelog as log ,notiopi_notice as n where log.per_id = '"\
              + str(user.id) + "' and log.close_num > 0 and log.notice_id = n.id"
        cursor = connection.cursor()
        cursor.execute(sql)
        row = cursor.fetchall()
        return self.make_response(row)
    
# 根据人员id获取人员姓名
def getNameById(userId):
    try:
        user = models.User.objects.get(pk=userId)
        return user.name
    except:
        return '查无此人'