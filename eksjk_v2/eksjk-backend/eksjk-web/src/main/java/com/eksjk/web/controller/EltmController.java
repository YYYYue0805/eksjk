package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.service.DiagnosisService;
import com.eksjk.service.EltmSyncService;
import com.eksjk.service.diagnosis.DiagnosisResult;
import com.eksjk.common.util.HashidsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ELTM 数据同步 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/eltm")
@RequiredArgsConstructor
@SaCheckLogin
public class EltmController {

    private final EltmSyncService eltmSyncService;
    private final DiagnosisService diagnosisService;

    /**
     * 手动触发从外部系统同步ELTM数据
     */
    @PostMapping("/sync")
    public R<Map<String, Object>> sync() {
        Map<String, Object> result = eltmSyncService.syncFromExternal();
        return R.ok(result);
    }

    /**
     * 查询同步状态
     */
    @GetMapping("/sync-status")
    public R<Map<String, Object>> syncStatus() {
        Map<String, Object> status = eltmSyncService.getSyncStatus();
        return R.ok(status);
    }

    /**
     * 对单个ELTM患者执行智能诊断
     */
    @PostMapping("/diagnose/{id}")
    public R<DiagnosisResult> diagnose(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        DiagnosisResult result = diagnosisService.diagnoseSingle(patientId);
        return R.ok(result);
    }

    /**
     * 批量诊断所有未分类的ELTM患者，高置信度自动重分类
     */
    @PostMapping("/diagnose-batch")
    public R<Map<String, Object>> diagnoseBatch() {
        Map<String, Object> result = diagnosisService.diagnoseBatch();
        return R.ok(result);
    }

}
