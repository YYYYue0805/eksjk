package com.eksjk.service;

import java.util.Map;

/**
 * 仪表盘服务接口
 * <p>提供工作台统计数据，包括病例概览、疾病分布、用户统计等</p>
 *
 * @author eksjk
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计摘要
     * 返回：病例总数、本月新增、待随访数、注册用户数、疾病分布、用户角色分布
     */
    Map<String, Object> getSummary();
}
