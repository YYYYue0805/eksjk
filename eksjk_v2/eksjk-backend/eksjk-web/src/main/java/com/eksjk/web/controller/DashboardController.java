package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 仪表盘 Controller
 * <p>提供工作台统计数据和系统公告</p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SaCheckLogin
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计摘要
     * 返回：病例总数、本月新增、待随访数、注册用户数、疾病分布、用户角色分布
     */
    @GetMapping("/summary")
    public R<Map<String, Object>> summary() {
        return R.ok(dashboardService.getSummary());
    }

    /**
     * 获取系统公告列表
     */
    @GetMapping("/notices")
    public R<List<Map<String, String>>> notices() {
        return R.ok(Collections.emptyList());
    }

    /**
     * 获取参与医院列表（医院名 + 入组病例数 + 医生人数）
     */
    @GetMapping("/hospitals")
    public R<List<Map<String, Object>>> hospitals() {
        return R.ok(dashboardService.getHospitalStats());
    }

    /**
     * 获取单个医院明细（入组人数、医生人数、随访完成度）
     */
    @GetMapping("/hospitals/detail")
    public R<Map<String, Object>> hospitalDetail(@RequestParam String unitName) {
        return R.ok(dashboardService.getHospitalDetail(unitName));
    }

    /**
     * 某病种在各医院的录入例数
     */
    @GetMapping("/disease/hospitals")
    public R<List<Map<String, Object>>> diseaseHospitals(@RequestParam String diseaseName) {
        return R.ok(dashboardService.getDiseaseHospitals(diseaseName));
    }
}
