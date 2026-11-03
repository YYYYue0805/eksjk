package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.eksjk.common.result.R;
import com.eksjk.service.DiagnosisService;
import com.eksjk.service.diagnosis.DiagnosisResult;
import com.eksjk.service.eltm.EltmProjectionService;
import com.eksjk.service.sync.DatamainConsentBackfillService;
import com.eksjk.service.sync.DatamainPicBackfillService;
import com.eksjk.service.sync.DatamainSyncService;
import com.eksjk.service.sync.DatamainUnprocessedRebuildService;
import com.eksjk.common.util.HashidsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    private final DatamainSyncService datamainSyncService;
    private final DatamainConsentBackfillService consentBackfillService;
    private final DiagnosisService diagnosisService;
    private final EltmProjectionService projectionService;
    private final DatamainPicBackfillService picBackfillService;
    private final DatamainUnprocessedRebuildService unprocessedRebuildService;

    /**
     * 手动触发从外部系统同步ELTM数据
     */
    @PostMapping("/sync")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> sync() {
        Map<String, Object> result = datamainSyncService.syncFromExternal("MANUAL");
        return R.ok(result);
    }

    /**
     * 异步触发全量同步，立即返回
     */
    @PostMapping("/sync/async")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> syncAsync(
            @RequestParam(defaultValue = "false") boolean force,
            @RequestParam(defaultValue = "0") int limit) {
        return R.ok(datamainSyncService.startAsync("MANUAL", force, limit));
    }

    /**
     * 对 pic_count>0 的患者批量补拉病历图片（gettpbyid）
     */
    @PostMapping("/sync/pics")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> syncPics(@RequestParam(defaultValue = "0") int limit) {
        return R.ok(picBackfillService.backfillAll(limit));
    }

    /**
     * 从 eltm_patient_raw 重放未处理数据采集
     */
    @PostMapping("/unprocessed/rebuild")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> rebuildUnprocessed() {
        return R.ok(unprocessedRebuildService.rebuildAll());
    }

    /**
     * 查询同步状态
     */
    @GetMapping("/sync-status")
    public R<Map<String, Object>> syncStatus() {
        Map<String, Object> status = datamainSyncService.getSyncStatus();
        return R.ok(status);
    }

    /**
     * 最近同步日志
     */
    @GetMapping("/sync-logs")
    public R<List<?>> syncLogs(@RequestParam(defaultValue = "20") int limit) {
        return R.ok(datamainSyncService.listRecentLogs(limit));
    }

    /**
     * 重跑 ELTM 外部数据投影（只填空，不覆盖本地已填内容）
     */
    @PostMapping("/projection/run")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> runProjection() {
        return R.ok(projectionService.projectAll());
    }

    /**
     * 重跑单个 ELTM 患者的外部数据投影
     */
    @PostMapping("/projection/patient/{id}")
    @SaCheckRole("super_admin")
    public R<Integer> runPatientProjection(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        return R.ok(projectionService.projectByPatientId(patientId));
    }

    /**
     * 按外部患者ID重跑单患者投影
     */
    @PostMapping("/projection/external/{externalId}")
    @SaCheckRole("super_admin")
    public R<Integer> runExternalProjection(@PathVariable long externalId) {
        return R.ok(projectionService.projectByExternalId(externalId));
    }

    /**
     * 迁移外部附件到文件存储（本地/S3 均支持）
     */
    @PostMapping("/attachments/run")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> runAttachments(
            @RequestParam(defaultValue = "0") int limit,
            @RequestParam(defaultValue = "false") boolean async) {
        if (async) {
            return R.ok(projectionService.startAttachmentsAsync(limit));
        }
        return R.ok(projectionService.projectAllAttachments(limit));
    }

    /**
     * 单患者迁移外部附件
     */
    @PostMapping("/attachments/patient/{id}")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> runPatientAttachments(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        return R.ok(projectionService.projectAttachmentsByPatientId(patientId));
    }

    /**
     * 补拉单个患者的知情同意书完整记录（正文/签名/PDF）
     */
    @PostMapping("/sync/consent/{externalId}")
    @SaCheckRole("super_admin")
    public R<Map<String, Object>> backfillConsent(@PathVariable long externalId) {
        return R.ok(consentBackfillService.backfill(externalId));
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
