import datetime

from datamain import models


def saveLog(request, caseid, operStep):
    user = request.user
    operLog = models.OperLog()
    try:
        operLog.oper_case_id = caseid
        operLog.oper_per_id = user.id
        operLog.oper_step = operStep
        operLog.oper_data = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        operLog.del_flg = '1'
        # 去数据库创建一条记录
        operLog.save()
        return True
    except:
        return False