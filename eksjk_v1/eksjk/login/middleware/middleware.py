from django.conf import settings
from django.shortcuts import HttpResponse, redirect
import re


class MiddlewareMixin(object):
    def __init__(self, get_response=None):
        self.get_response = get_response
        super(MiddlewareMixin, self).__init__()

    def __call__(self, request):
        response = None
        if hasattr(self, 'process_request'):
            response = self.process_request(request)
        if not response:
            response = self.get_response(request)
        if hasattr(self, 'process_response'):
            response = self.process_response(request, response)
        return response


class UseuMiddleware(MiddlewareMixin):
    """
    检查用户的url请求是否是其权限范围内
    """
    def process_request(self, request):
        request_url = request.path_info
        permission_url = request.session.get(settings.SESSION_PERMISSION_URL_KEY)

        # 如果请求url在白名单，放行
        for url in settings.SAFE_URL:
            if re.match(url, request_url):
                return None

        # 如果未登录并且url不是登录url, 重定向至登录；为了可移植性，将登录url写入配置
        if not request.session.get('is_login', None):
            if not request_url== settings.LOGIN_URL:
                return redirect(settings.LOGIN_URL)

        i=0
        # 如果请求url不在黑名单中，则放行
        for url in settings.BACK_URL:
            if url == request_url:
                i=i+1
        if i==0:
            return None



        # 循环permission_url，作为正则，匹配用户request_url
        # 正则应该进行一些限定，以处理：/user/ -- /user/add/匹配成功的情况
        flag = False
        for url in permission_url:
            # url_pattern = settings.REGEX_URL.format(url=url)
            if url == request_url:
                flag = True
                break
        if flag:
            return None
        else:
            # 如果是调试模式，显示可访问url
            if settings.DEBUG:
                if len(permission_url)>0:
                    info ='<br/>' + ( '<br/>'.join(permission_url))
                    return HttpResponse('无权限，请尝试访问以下地址：%s' %info)
                else:
                    return HttpResponse('无权限访问')
            else:
                return HttpResponse('无权限访问')
