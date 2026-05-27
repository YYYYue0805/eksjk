import datetime
import json
import os
from common.utils import Code, parse_arguments, FormattedView, require_arguments, decode_id, encode_id
from common import extractors
from . import models
from django.core.paginator import Paginator
from django.db import transaction
from login import models as loginmoddel
from login import views as loginView
from django.http import HttpResponse
from common.files import opinion_save_img

# 公告
class NoticeView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询公告详细数据
    @require_arguments(['queryId'], 'url')
    def get(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            notice_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            notice = models.Notice.objects.get(pk=notice_id)
            return self.make_response(notice)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 添加,修改公告详细数据
    @require_arguments(['title', 'releTarget'], 'body')
    def put(self, request, *args, **kwargs):
        user = request.user
        if 'queryId' in kwargs and len(kwargs['queryId'])>0:
            notice = models.Notice.objects.get(pk=decode_id(kwargs['queryId']))
        else:
            notice = models.Notice()
        notice.rele_per = user.id
        notice.rele_per_name = user.name
        if 'title' in kwargs and kwargs['title'] is not '':
            notice.title = kwargs['title']
        if 'content' in kwargs:
            notice.content = kwargs['content']
        if 'releTarget' in kwargs:
            releTarget = '{"target":'+json.dumps(kwargs['releTarget'])+'}'
            notice.rele_target = releTarget
        if 'closeNum' in kwargs:
            notice.close_num = kwargs['closeNum']
        notice.rele_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        notice.del_flg = '1'
        try:
            with transaction.atomic():
                notice.save()
                level = ''
                for i in kwargs['releTarget']:
                    if i == '普通用户':
                        level = level + '0,'
                    elif i == '初审人':
                        level = level + '1,'
                    elif i == '复审人':
                        level = level + '2,'
                level = level[0:len(level)-1]
                # 添加人员日志
                userList = loginmoddel.User.objects.extra(where=['level IN (' + level + ')'])
                for useritem in userList:
                    perNoticeLog = models.PerNoticeLog()
                    perNoticeLog.notice_id = notice.id
                    perNoticeLog.close_num = notice.close_num
                    perNoticeLog.per_id = useritem.id
                    perNoticeLog.save()
            return self.make_response(notice)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 删除公告详细数据
    @require_arguments(['queryId'], 'url')
    def delete(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            notice_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            notice = models.Notice.objects.get(pk=notice_id)
            notice.del_flg = '0'
            notice.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)




# 公告列表
class NoticeListView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询主列表
    @parse_arguments('url')
    def get(self, request, *args, **kwargs):
        filters = self.get_filters(kwargs)
        if filters is None:
            return self.make_response(None, Code.PERMISSION_DENIED)
        else:
            notice = models.Notice.objects.filter(**filters).all().order_by('-rele_time')
            limit = kwargs['limit']
            paginator = Paginator(notice, limit)  # 每页显示10条
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

    def get_filters(self, source):
        """
        获取查询条件（将请求参数转换为数据库表字段）
        为安全考虑，请求传过来的参数名称尽量不要和数据库中的字段同名
        """

        filters = {}

        if 'createDateRange' in source and ',' in source['createDateRange']:
            items = source['createDateRange'].split(',')
            filters['rele_time__gte'] = items[0]
            filters['rele_time__lte'] = items[1]
        if 'title' in source:
            filters['title__contains'] = source['title']

        filters['del_flg'] = '1'

        return filters

# 意见反馈
class OpinionView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询意见详细数据
    @require_arguments(['queryId'], 'url')
    def get(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            Opinion_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            opinion = models.Opinion.objects.get(pk=Opinion_id)
            return self.make_response(opinion)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 添加,修改意见详细数据
    @require_arguments(['comCount'], 'body')
    def put(self, request, *args, **kwargs):
        user = request.user
        if 'queryId' in kwargs and len(kwargs['queryId']) > 0:
            opinion = models.Opinion.objects.get(pk=decode_id(kwargs['queryId']))
        else:
            opinion = models.Opinion()
        opinion.sub_per = user.id
        opinion.sub_per_name = user.name
        opinion.sub_unit = user.unit
        if 'telephone' in kwargs and kwargs['telephone'] is not '':
            opinion.telephone = kwargs['telephone']
        if 'comStatus' in kwargs:
            opinion.com_status = kwargs['comStatus']
        if 'comCount' in kwargs:
            opinion.com_count = kwargs['comCount']
        if 'feedback' in kwargs:
            opinion.feedback = kwargs['feedback']
        opinion.sub_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        opinion.del_flg = '1'
        try:
            opinion.save()
            return self.make_response(opinion)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

    # 删除意见详细数据
    @require_arguments(['queryId'], 'url')
    def delete(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            Opinion_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            opinion = models.Opinion.objects.get(pk=Opinion_id)
            opinion.del_flg = '0'
            opinion.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

# 意见反馈列表
class OpinionListView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询主列表
    @parse_arguments('url')
    def get(self, request, *args, **kwargs):
        filters = self.get_filters(kwargs)
        if filters is None:
            return self.make_response(None, Code.PERMISSION_DENIED)
        else:
            opinion = models.Opinion.objects.filter(**filters).all().order_by('-sub_time')
            limit = kwargs['limit']
            paginator = Paginator(opinion, limit)  # 每页显示10条
            page = kwargs['currPage']
            if page == '0':
                page = '1'
            pagedata = {}  # 获取分页信息
            pagedata['count'] = paginator.count
            pagedata['num_pages'] = paginator.num_pages
            pagedata['per_page'] = limit
            pagedata['current'] = page
            context = {}
            objlist = paginator.page(page).object_list
            unitlist = loginView.getAllUnit()
            for opinions in objlist:
                unit = opinions.sub_unit
                if unit in unitlist:
                    opinions.sub_unit = unitlist[unit]
            contacts = self.extractor.extract(objlist)
            context['contacts'] = contacts
            context['pagedata'] = pagedata
            return self.make_response(context)

    def get_filters(self, source):
        """
        获取查询条件（将请求参数转换为数据库表字段）
        为安全考虑，请求传过来的参数名称尽量不要和数据库中的字段同名
        """

        filters = {}

        if 'unit' in source:
            filters['sub_unit__contains'] = source['unit']

        if 'createDateRange' in source and ',' in source['createDateRange']:
            items = source['createDateRange'].split(',')
            filters['sub_time__gte'] = items[0]
            filters['sub_time__lte'] = items[1]

        filters['del_flg'] = '1'

        return filters

# 答复反馈意见
class ReplyOpinionView(FormattedView):
    @require_arguments(['queryId'], 'body')
    def post(self, request, *args, **kwargs):
        filters = self.get_filters(request.user, kwargs)
        if filters is None:
            return self.make_response(None, Code.PERMISSION_DENIED)
        else:
            opinion = models.Opinion.objects.get(**filters)
        try:
            opinion.com_status = '10070002'
            opinion.save()
        except:
            return self.make_response(None, Code.RESOURCE_NOT_EXIST)
        return self.make_response(None)

    def get_filters(self, user, source):
        """
        获取查询条件（将请求参数转换为数据库表字段）
        为安全考虑，请求传过来的参数名称尽量不要和数据库中的字段同名
        """

        filters = {}

        if 'queryId' in source and len(source['queryId']) > 0:
            filters['pk'] = decode_id(source['queryId'])

        filters['del_flg'] = '1'
        return filters


# 公告减一
class CloseNoticeView(FormattedView):
    extractor = extractors.AllExtractor()

    # 人员公告减一详细数据
    @require_arguments(['queryId'], 'body')
    def post(self, request, *args, **kwargs):
        # 主键id
        if len(kwargs['queryId']) > 0:
            noticelog_id = kwargs['queryId']
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            perNoticeLog = models.PerNoticeLog.objects.get(pk=noticelog_id)
            closeNum = int(perNoticeLog.close_num)-1
            perNoticeLog.close_num = str(closeNum)
            perNoticeLog.save()
            return self.make_response(None)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

# 意见反馈图片保存
class ImageView(FormattedView):
    @require_arguments(['queryId','organ', 'path'])
    def get(self, request, *args, **kwargs):
        """
        用于图片下载，判断登录权限并获取到必要参数后交由Nignx处理
        """

        queryId = decode_id(kwargs['queryId'])
        organ = kwargs['organ']
        path = os.path.join(organ, str(queryId), kwargs['path'])
        path = path.replace('-', '/')

        # 转发给Nginx处理
        url = '/protected_files/{}'.format(path)

        # print('image:', url)
        response = HttpResponse('')
        response['X-Accel-Redirect'] = url.encode()

        if 'type' not in kwargs:
            response['Content-Type'] = 'application/octet-stream'
        elif kwargs['type'] == 'mp4':
            response['Content-Type'] = 'video/mp4'

        return response

    @require_arguments(['queryId','organ', 'path'], 'body')
    def post(self, request, *args, **kwargs):
        """
        图片上传
        """

        queryId = decode_id(kwargs['queryId'])
        data = request.FILES.get('package', None)
        if data is None:
            return self.make_response(None, code=Code.MISSING_REQUIRED_ARGUMENTS)
        ret = opinion_save_img(queryId, kwargs['organ'], kwargs['path'], data)
        if ret:
            return self.make_response('')
        else:
            return self.make_response(None, code=Code.FAIL_SAVIMG)

    @require_arguments(['queryId','organ', 'path'])
    def delete(self, request, *args, **kwargs):
        """
        图片删除
        """

        queryId = decode_id(kwargs['queryId'])
        opinion_save_img(queryId,kwargs['organ'], kwargs['path'], None)

        return self.make_response('ok')