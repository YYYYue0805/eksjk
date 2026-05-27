# from eksjk.datamain import models
from datamain import models


def query_sub_table(dis_class, case_id):
    if dis_class == "10000001":
        result = models.Case.objects.filter(patient__pk=case_id)[0]
    elif dis_class == "10000002":
        result = models.Short.objects.get(patient__pk=case_id)
    elif dis_class == "10000003":
        result = models.Sexprecocity.objects.get(patient__pk=case_id)
    elif dis_class == "10000004":
        result = models.Mas.objects.get(patient__pk=case_id)
    elif dis_class == "10000005":
        result = models.SGA.objects.get(patient__pk=case_id)
    elif dis_class == "10000006":
        result = models.JzxShort.objects.get(patient__pk=case_id)
    elif dis_class == "10000007":
        result = models.SzfyEltm.objects.get(patient__pk=case_id)
    return result

class CqSchoolvalue():

    def cqvalyue(listvalue):
        jsonvalue = listvalue.replace("=","':'")

# 生长发育每日同步转换数据
def tb_eltm_szfy(date, plan):
    result = 1
    return result
