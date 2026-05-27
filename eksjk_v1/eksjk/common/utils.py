import jwt
from django.http import HttpRequest, HttpResponse, JsonResponse
from django.views import View
from django.conf import settings

from hashids import Hashids
from Crypto import Random
from Crypto.PublicKey import RSA
from Crypto.Cipher import AES, PKCS1_v1_5

import json
import functools
import base64

hashids = Hashids(salt=settings.SECRET_KEY, min_length=5)
private_key = None
public_key = None


class Code:
    # 正常
    OK = 0
    # 未登录，无权限
    UNAUTHORIZED = 1
    # 用户名或者密码错误
    USERNAME_PASSWORD_ERROR = 2
    # 请求方法不存在
    BAD_METHOD = 3
    # 数据解析错误
    DATA_PARSE_FAILED = 4
    # 缺少必需参数
    MISSING_REQUIRED_ARGUMENTS = 5
    # 资源不存在
    RESOURCE_NOT_EXIST = 6
    # 资源已存在
    RESOURCE_IS_EXIST = 7
    # 权限不足
    PERMISSION_DENIED = 8
    # 保存图片失败
    FAIL_SAVIMG = 9
    # 主键参数为空
    MAIN_NULL = 10
    # 保存压缩包失败
    DEFULT_SAVE_ZIP = 11
    # 先保存，在提交
    NOT_SAVE = 12
    # 已审核无法回退
    IS_CHECKED = 13
    # 读取txt失败
    READ_FAIL = 14
    # 其他
    OTHER = 100

    # 错误字符串列表，需要初始化
    CODE_STRINGS = []

    @staticmethod
    def init():
        for key in Code.__dict__:
            v = Code.__dict__[key]
            if '__' not in key and isinstance(v, int):
                Code.CODE_STRINGS.append(key)


Code.init()


class FormattedResponse(JsonResponse):
    """
    回复统一使用固定的JSON格式
    """

    def __init__(self, data, code=0, **kwargs):
        if code != 0 and data is None:
            data = Code.CODE_STRINGS[code]

        super().__init__({
            'code': code,
            'data': data
        }, **kwargs)


class FormattedView(View):
    """
    支持定义数据提取器并格式化回复
    """

    # 数据提取器
    extractor = None
    # 是否要求登录
    loginRequired = True

    def dispatch(self, request, *args, **kwargs):
        if self.loginRequired and not request.user.is_authenticated:
            return FormattedResponse(None, code=Code.UNAUTHORIZED, status=401)
        return super().dispatch(request, *args, **kwargs)

    def make_response(self, data, code=0, status=200, extractor=None):
        if code == 0:
            _extractor = extractor if extractor else self.extractor
            if _extractor is not None:
                # 数据提取
                data = _extractor.extract(data)

        # 格式化
        return FormattedResponse(data, code, status=status)


def _parse_request_argument(*args, **kwargs):
    """
    限内部调用
    """

    # 搜索request
    request = None
    for arg in args:
        if isinstance(arg, HttpRequest):
            request = arg
            break
    if request is None:
        print('No request object found, please check the funcion args')
        return HttpResponse(status=500)

    position = kwargs['position']

    # 解析request中参数
    no_arg = False
    if position == 'url':
        arg_dict = request.GET.dict()
    elif position == 'body':
        if request.method == 'POST' \
                and 'multipart/form-data' in request.headers['Content-Type']:
            arg_dict = request.POST.dict()
        else:
            if len(request.body) < 1:
                no_arg = True
            else:
                try:
                    arg_dict = json.loads(request.body)
                except Exception:
                    return FormattedResponse('JSON decode error', status=400, code=Code.DATA_PARSE_FAILED)

    # 没有参数
    return_missing = 'check_missing' in kwargs and kwargs['check_missing'] \
                     and (no_arg or len(arg_dict) < 1)
    if return_missing:
        return FormattedResponse(None, status=400, code=Code.MISSING_REQUIRED_ARGUMENTS)

    return arg_dict


def parse_arguments(position='url'):
    """
    装饰器：解析请求参数，并将参数放置到目标函数的kwarg中

    Args:
        position： 参数在request中的位置，可选为'url'、'header'、'body'
    """

    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            ret = _parse_request_argument(*args, position=position)
            if isinstance(ret, HttpResponse):
                return ret
            kwargs = {**kwargs, **ret}
            return func(*args, **kwargs)

        return wrapper

    return decorator


def require_arguments(arg_names, position='url'):
    """
    装饰器：调用目标函数之前，检测请求中是否包含某些必须参数
    * 如果包含必需参数，则将参数解析并放到kwargs中并调用目标函数
    * 如果未包含，则直接返回错误，不再调用目标函数

    Args:
        arg_names: 参数名列表
        position： 参数在request中的位置，可选为'url'、'header'、'body'
    """

    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            ret = _parse_request_argument(*args, position=position, check_missing=True)
            if isinstance(ret, HttpResponse):
                return ret

            arg_dict = ret
            for arg_name in arg_names:
                if arg_name not in arg_dict:
                    return FormattedResponse(arg_name, status=400, code=Code.MISSING_REQUIRED_ARGUMENTS)
            kwargs = {**kwargs, **arg_dict}

            return func(*args, **kwargs)

        return wrapper

    return decorator


# 加密数据库id，隐藏业务量并提高爬虫门槛
def encode_id(id):
    return hashids.encode(id)


def decode_id(hashid):
    return hashids.decode(hashid)[0]


def get_public_key():
    global public_key
    if public_key is None:
        with open(settings.PUBLIC_KEY_PATH) as f:
            public_key = f.read()

    return public_key


def decrypt(string):
    global private_key
    if private_key is None:
        with open(settings.PRIVATE_KEY_PATH) as f:
            private_key = f.read()
            private_key = RSA.import_key(private_key)

    try:
        random_generator = Random.new().read
        RSA.generate(1024, random_generator)
        cipher_rsa = PKCS1_v1_5.new(private_key)
        decrypt_bytes = cipher_rsa.decrypt(base64.b64decode(string),
                                           random_generator)
        return decrypt_bytes.decode()
    except:
        return None

# 对批量下载判断值是否为空（适用于 + 如（cm）,(kg)）这些。
def safe_str(value):
    # "" 如果值是（空：None）就返回 空字符串（“”） ----  如果值是  （存在） 就返回强转  【方法包括内容】
    return "" if value is None else str(value)




"""
    一个可复用的token验证
"""
def verify_token(request):
    token = request.headers.get('token')
    try:
        # 通过验证
        decoded_token = jwt.decode(token, 'mk5677123', algorithms=['HS256'])
        return decoded_token
    # token 已过期
    except jwt.ExpiredSignatureError:
        return JsonResponse({'error': '请求头中token过期'}, status=400)
    # token 无效
    except jwt.InvalidTokenError:
        return JsonResponse({'error': '请求头中token无效'}, status=401)
def token_required(view_func):
    #  wrapper 的内部函数
    def wrapper(request, *args, **kwargs):
        # 调用 verify_token 函数验证请求中的 token
        result = verify_token(request)

        # 如果 result 是 JsonResponse 的实例,说明 token 验证失败,直接返回错误响应
        if isinstance(result, JsonResponse):
            if result.status_code == 400:
                print('请求头中Token过期')
            elif result.status_code == 401:
                print('请求头中Token无效')
            return result

        # 如果 token 验证通过,则执行原始的视图函数,并返回其结果
        # 将 decoded_token 添加到 request 对象中。一并返回,在其他方法使用@token_required时候能够获取到里边的值
        request.decoded_token = result
        return view_func(request, *args, **kwargs)

    # 返回包装后的函数
    return wrapper