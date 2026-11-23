package com.eksjk.service;

import java.util.List;
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
     * 返回：病例总数、本月新增、不良反应上报次数、医生用户数、疾病分布、用户角色分布
     */
    Map<String, Object> getSummary();

    /**
     * 参与医院列表：医院名 + 入组病例数 + 医生人数，按入组病例数倒序
     * 入组病例数为空的医院归入「未分配」并沉底
     */
    List<Map<String, Object>> getHospitalStats();

    /**
     * 单个医院明细：入组病例数、医生人数、随访完成度（已随访患者 / 入组病例）
     *
     * @param unitName 医院名称，或「未分配」
     */
    Map<String, Object> getHospitalDetail(String unitName);

    /**
     * 某病种在各医院的录入例数（含占比），按录入例数倒序
     *
     * @param diseaseName 病种显示名，如「性发育异常(DSD)」
     */
    List<Map<String, Object>> getDiseaseHospitals(String diseaseName);
}
