from django.shortcuts import render
from common.utils import Code, parse_arguments, FormattedView, require_arguments, decode_id
from common import extractors
from django.core.paginator import Paginator
from school import models
from login import models as loginmoddel
# Create your views here.

class StudentListView(FormattedView):
    extractor = extractors.AllExtractor()

    # 查询主列表
    @parse_arguments('url')
    def get(self, request, *args, **kwargs):
        filters = self.get_filters(request.user, kwargs)
        if filters is None:
            return self.make_response(None, Code.PERMISSION_DENIED)
        else:
            student = models.Student.objects.filter(**filters).all()
            if request.user.is_superuser == 1:
                pass
            else:
                if request.user.level == 1:
                    student = student.filter(up_mec=request.user.unit)
                else:
                    student = student.filter(doctor=request.user)
            if 'sortby' in kwargs and kwargs['sortby']:
                sortby_map = {
                    'num': 'num',
                    'sclass': 'sclass',
                    'name': 'name',
                    'sex': 'sex',
                    'birth_time': 'birth_time',
                }
                if 'order' in kwargs and kwargs['order'] == 'desc':
                    student = student.order_by('-' + sortby_map[kwargs['sortby']])
                else:
                    student = student.order_by(sortby_map[kwargs['sortby']])
            else:
                student = student.order_by('-modify_time')
            limit = kwargs['limit']
            paginator = Paginator(student, limit)  # 每页显示10条
            page = kwargs['currPage']
            if page == '0':
                page = '1'
            pagedata = {}  # 获取分页信息
            pagedata['count'] = paginator.count
            pagedata['num_pages'] = paginator.num_pages
            pagedata['per_page'] = limit
            pagedata['current'] = page
            context = {}
            try:
                list = paginator.page(page).object_list
            except:
                list = paginator.page('1').object_list
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

        if 'num' in source and source['num']:
            filters['num__contains'] = source['num']

        if 'sclass' in source and source['sclass']:
            filters['sclass__contains'] = source['sclass']

        if 'name' in source and source['name']:
            filters['name__contains'] = source['name']

        if 'sex' in source and source['sex']:
            filters['sex__contains'] = source['sex']

        # if 'createDateRange' in source and ',' in source['createDateRange']:
        #     items = source['createDateRange'].split(',')
        #     filters['c_time__gte'] = items[0]
        #     filters['c_time__lte'] = items[1]

        filters['del_flg'] = '1'

        return filters
    
class StudentView(FormattedView):
    extractor = extractors.AllExtractor()
    # 根据主表id查询学生详细数据
    @require_arguments(['queryId'], 'url')
    def get(self, request, *args, **kwargs):
        context = {}
        # 主键id
        if len(kwargs['queryId']) > 0:
            student_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            student = models.Student.objects.get(pk=student_id)
            # 根据主表id确定分表
            cchkn = models.Cchkn.objects.filter(student__pk=student_id)[0]
            cbq = models.Cbq.objects.filter(student__pk=student_id)[0]
            mqzyfs = models.Mqzyfs.objects.filter(student__pk=student_id)[0]
            qzhd = models.Qzhd.objects.filter(student__pk=student_id)[0]
            pmbl = models.Pmbl.objects.filter(student__pk=student_id)[0]
            sthd = models.Sthd.objects.filter(student__pk=student_id)[0]
            smxg = models.Smxg.objects.filter(student__pk=student_id)[0]
            context['student'] = self.extractor.extract(student)
            context['cchkn'] = self.extractor.extract(cchkn)
            context['cbq'] = self.extractor.extract(cbq)
            context['mqzyfs'] = self.extractor.extract(mqzyfs)
            context['qzhd'] = self.extractor.extract(qzhd)
            context['pmbl'] = self.extractor.extract(pmbl)
            context['sthd'] = self.extractor.extract(sthd)
            context['smxg'] = self.extractor.extract(smxg)
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)
        
class JiexiView(FormattedView):
    extractor = extractors.AllExtractor()
    # 根据主表id查询学生详细数据
    @require_arguments(['queryId'], 'url')
    def get(self, request, *args, **kwargs):
        context = {}
        # 主键id
        if len(kwargs['queryId']) > 0:
            student_id = decode_id(kwargs['queryId'])
        else:
            return self.make_response(None, Code.MAIN_NULL)
        try:
            student = models.Student.objects.get(pk=student_id)
            # 根据主表id确定分表
            cchkn = models.Cchkn.objects.filter(student__pk=student_id)[0]
            cbq = models.Cbq.objects.filter(student__pk=student_id)[0]
            mqzyfs = models.Mqzyfs.objects.filter(student__pk=student_id)[0]
            qzhd = models.Qzhd.objects.filter(student__pk=student_id)[0]
            pmbl = models.Pmbl.objects.filter(student__pk=student_id)[0]
            sthd = models.Sthd.objects.filter(student__pk=student_id)[0]
            smxg = models.Smxg.objects.filter(student__pk=student_id)[0]
            context['student'] = self.extractor.extract(student)
            context['cchkn'] = self.extractor.extract(cchkn)
            context['cbq'] = self.extractor.extract(cbq)
            context['mqzyfs'] = self.extractor.extract(mqzyfs)
            context['qzhd'] = self.extractor.extract(qzhd)
            context['pmbl'] = self.extractor.extract(pmbl)
            context['sthd'] = self.extractor.extract(sthd)
            context['smxg'] = self.extractor.extract(smxg)
            return self.make_response(context)
        except:
            return self.make_response(None, Code.DATA_PARSE_FAILED)

