package com.eksjk.service;

import java.util.List;
import java.util.Map;

/**
 * 统计分析服务接口
 * <p>提供医院×月份新增矩阵、病种新增统计、自定义条件检索</p>
 *
 * @author eksjk
 */
public interface StatisticsService {

    /**
     * 医院 × 月份 新增病例矩阵
     *
     * @param startDate 起始日期 yyyy-MM-dd（可空）
     * @param endDate   结束日期 yyyy-MM-dd（可空）
     * @return { months:[], rows:[{hospitalName, counts:{月:数}, total}], monthTotals:{}, grandTotal }
     */
    Map<String, Object> hospitalMonthly(String startDate, String endDate);

    /**
     * 按病种统计新增病例数
     *
     * @param startDate 起始日期 yyyy-MM-dd（可空）
     * @param endDate   结束日期 yyyy-MM-dd（可空）
     * @return [{ diseaseName, count }]，按数量倒序
     */
    List<Map<String, Object>> diseaseNewly(String startDate, String endDate);

    /**
     * 自定义条件检索
     *
     * @param criteria 条件：disClass/sex/startDate/endDate/minAge/maxAge/minHeight/maxHeight/minBoneAge/maxBoneAge/finalHeight
     * @return { total, list }
     */
    Map<String, Object> customSearch(Map<String, Object> criteria);
}
