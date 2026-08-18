package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.InformedConsentService;
import com.eksjk.service.InformedConsentSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知情同意书 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/informed-consent")
@RequiredArgsConstructor
@SaCheckLogin
public class InformedConsentController {

    private final InformedConsentService informedConsentService;
    private final InformedConsentSyncService informedConsentSyncService;

    /**
     * 按患者查询知情同意书列表
     */
    @GetMapping("/list-by-patient")
    public R<List<InformedConsentVO>> listByPatient(@RequestParam("patientId") String patientId) {
        long id = HashidsUtil.decode(patientId);
        return R.ok(informedConsentService.listByPatientId(id));
    }

    /**
     * 手动触发从 E路童萌 同步知情同意书（管理端）
     */
    @PostMapping("/sync")
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<Map<String, Object>> sync() {
        return R.ok(informedConsentSyncService.syncFromExternal());
    }

    /**
     * 查询同步状态（最近同步时间与总记录数）
     */
    @GetMapping("/sync-status")
    public R<Map<String, Object>> syncStatus() {
        Map<String, Object> status = informedConsentService.getSyncStatus();
        return R.ok(status);
    }
}
