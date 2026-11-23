package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计分析 Controller
 * <p>医院×月份新增矩阵、病种新增统计、自定义条件检索</p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@SaCheckLogin
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 医院 × 月份 新增病例矩阵
     */
    @GetMapping("/hospital-monthly")
    public R<Map<String, Object>> hospitalMonthly(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok(statisticsService.hospitalMonthly(startDate, endDate));
    }

    /**
     * 按病种统计新增病例数
     */
    @GetMapping("/disease-newly")
    public R<List<Map<String, Object>>> diseaseNewly(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok(statisticsService.diseaseNewly(startDate, endDate));
    }

    /**
     * 自定义条件检索
     */
    @PostMapping("/custom-search")
    public R<Map<String, Object>> customSearch(@RequestBody Map<String, Object> criteria) {
        return R.ok(statisticsService.customSearch(criteria));
    }
}
