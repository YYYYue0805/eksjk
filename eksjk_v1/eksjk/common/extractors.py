from datetime import datetime
from django.db.models import Model
from django.db.models.query import QuerySet
from .utils import encode_id

class Extractor:
    """
    数据提取器
    从Django的Model实例中提取需要的字段
    """

    def __init__(self):
        if not hasattr(self, 'fields'):
            self.fields = None
        
        if not hasattr(self, 'exclude'):
            self.exclude = None

        # if not hasattr(self, 'alias'):
        #     self.alias = None

        # if not hasattr(self, 'nested_fields'):
        #     self.nested_fields = None

    def extract(self, instances):
        if isinstance(instances, (list, QuerySet)):
            list_ = []
            for item in instances:
                list_.append(self.extract_instance(item))
            return list_
        else:
            return self.extract_instance(instances)

    def extract_instance(self, instance):
        if not isinstance(instance, Model):
            return instance

        dict_ = {}
        if self.fields is not None and len(self.fields) == 0:
            # fields为空的tuple或者空的list, 则表示不需要自身任何字段
            pass
        else:
            for key in instance.__dict__:
                if '_' == key[0]:
                    continue

                if self.exclude and key in self.exclude:
                    continue

                if self.fields and key not in self.fields:
                    continue

                v = instance.__dict__[key]
                if key == 'id':
                    v = encode_id(v)
                elif isinstance(v, datetime):
                    v = v.strftime('%Y-%m-%d %H:%M:%S')
                dict_[key] = v

        return dict_

class UserExtractor(Extractor):
    fields = [
        'id',
        'username',
        'name',
        'unit',
        'level',
        'email',
        'sex',
        'unit',
        'professional',
        'date_update',
        'department',
        'check_position',
        'unitName',
        'is_superuser'
    ]

# 审核人员列表显示
class CheckUserExtractor(Extractor):
    fields = [
        'id',
        'name',
        'unit'
    ]

class AllExtractor(Extractor):
    fields = None

# 新增病例返回有效数据
class AddCaseExtractor(Extractor):
    fields = [
        'id',
        'case_num',
    ]

# 新增病例返回有效数据
class AddPatExtractor(Extractor):
    fields = [
        'id',
        'case_num',
    ]
