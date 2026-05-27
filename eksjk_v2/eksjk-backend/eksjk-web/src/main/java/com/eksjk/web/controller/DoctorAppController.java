package com.eksjk.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.DoctorAppLoginDTO;
import com.eksjk.model.vo.DoctorAppLoginVO;
import com.eksjk.service.DoctorAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序医生端 Controller
 * <p>
 * 所有接口路径以 /api/doctor-app 开头，与 PC 端和家长端 API 区分。
 * 登录相关接口不需要认证，其余接口通过 Sa-Token 鉴权。
 * </p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/doctor-app")
@RequiredArgsConstructor
public class DoctorAppController {

    private final DoctorAppService doctorAppService;

    // ==================== 登录认证 ====================

    /** 微信登录 */
    @PostMapping("/wx-login")
    public R<DoctorAppLoginVO> wxLogin(@RequestBody DoctorAppLoginDTO loginDTO) {
        DoctorAppLoginVO result = doctorAppService.wxLogin(loginDTO);
        return R.ok(result);
    }

    /** 账号密码登录 */
    @PostMapping("/account-login")
    public R<DoctorAppLoginVO> accountLogin(@RequestBody DoctorAppLoginDTO loginDTO) {
        DoctorAppLoginVO result = doctorAppService.accountLogin(loginDTO);
        return R.ok(result);
    }

    /** 绑定微信账号 */
    @PostMapping("/bind-wx")
    public R<DoctorAppLoginVO> bindWxAccount(@RequestBody DoctorAppLoginDTO loginDTO) {
        DoctorAppLoginVO result = doctorAppService.bindWxAccount(loginDTO);
        return R.ok(result);
    }

    // ==================== 个人信息 ====================

    /** 获取医生个人信息 */
    @GetMapping("/profile")
    public R<Map<String, Object>> getProfile() {
        long doctorId = StpUtil.getLoginIdAsLong();
        Map<String, Object> profile = doctorAppService.getProfile(doctorId);
        return R.ok(profile);
    }

    // ==================== 工作台 ====================

    /** 获取工作台统计数据 */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> getDashboard() {
        long doctorId = StpUtil.getLoginIdAsLong();
        Map<String, Object> stats = doctorAppService.getDashboardStats(doctorId);
        return R.ok(stats);
    }

    // ==================== 患者管理 ====================

    /** 获取患者列表（分页） */
    @GetMapping("/patients")
    public R<Map<String, Object>> getPatientList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String disClass,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long doctorId = StpUtil.getLoginIdAsLong();
        Map<String, Object> result = doctorAppService.getPatientList(doctorId, keyword, disClass, pageNum, pageSize);
        return R.ok(result);
    }

    /** 获取患者详情 */
    @GetMapping("/patients/{id}")
    public R<Map<String, Object>> getPatientDetail(@PathVariable String id) {
        long doctorId = StpUtil.getLoginIdAsLong();
        long patientId = HashidsUtil.decode(id);
        Map<String, Object> detail = doctorAppService.getPatientDetail(doctorId, patientId);
        return R.ok(detail);
    }

    // ==================== 快捷随访 ====================

    /** 快捷新增随访记录 */
    @PostMapping("/followup")
    public R<Void> quickCreateFollowUp(@RequestBody Map<String, Object> data) {
        long doctorId = StpUtil.getLoginIdAsLong();
        doctorAppService.quickCreateFollowUp(doctorId, data);
        return R.ok();
    }

    // ==================== 数据审核 ====================

    /** 获取待审核数据列表 */
    @GetMapping("/review/pending")
    public R<List<Map<String, Object>>> getPendingReviewList() {
        long doctorId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> list = doctorAppService.getPendingReviewList(doctorId);
        return R.ok(list);
    }

    /** 审核数据 */
    @PostMapping("/review/{id}")
    public R<Void> reviewData(@PathVariable String id, @RequestBody Map<String, Object> body) {
        long doctorId = StpUtil.getLoginIdAsLong();
        long recordId = HashidsUtil.decode(id);
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String rejectReason = (String) body.get("rejectReason");
        doctorAppService.reviewData(doctorId, recordId, approved, rejectReason);
        return R.ok();
    }

    // ==================== 统计分析 ====================

    /** 获取统计数据 */
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics() {
        long doctorId = StpUtil.getLoginIdAsLong();
        Map<String, Object> stats = doctorAppService.getStatistics(doctorId);
        return R.ok(stats);
    }

    // ==================== 二维码 ====================

    /** 获取医生二维码数据 */
    @GetMapping("/qrcode")
    public R<Map<String, Object>> getQrCode() {
        long doctorId = StpUtil.getLoginIdAsLong();
        Map<String, Object> data = doctorAppService.getQrCodeData(doctorId);
        return R.ok(data);
    }
}
