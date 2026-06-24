package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.eksjk.common.constant.PermissionConstants;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.AuditActionDTO;
import com.eksjk.model.dto.AuditQueryDTO;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.model.vo.PatientVO;
import com.eksjk.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 审核发放管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@SaCheckLogin
public class AuditController {

    private final AuditService auditService;

    // ==================== 基线审核发放 ====================

    /**
     * 分页查询基线审核列表
     */
    @GetMapping("/patients")
    public R<PageResult<PatientVO>> listPatients(AuditQueryDTO queryDTO) {
        PageResult<PatientVO> result = auditService.listPatients(queryDTO);
        return R.ok(result);
    }

    /**
     * 审核通过基线
     */
    @PostMapping("/patients/{id}/approve")
    @SaCheckPermission(PermissionConstants.CASE_AUDIT)
    public R<Void> approvePatient(@PathVariable String id, @RequestBody(required = false) AuditActionDTO dto) {
        auditService.approvePatient(HashidsUtil.decode(id), dto != null ? dto : new AuditActionDTO());
        return R.ok();
    }

    /**
     * 驳回基线
     */
    @PostMapping("/patients/{id}/reject")
    @SaCheckPermission(PermissionConstants.CASE_AUDIT)
    public R<Void> rejectPatient(@PathVariable String id, @RequestBody AuditActionDTO dto) {
        auditService.rejectPatient(HashidsUtil.decode(id), dto);
        return R.ok();
    }

    /**
     * 发放基线
     */
    @PostMapping("/patients/{id}/release")
    @SaCheckPermission(PermissionConstants.CASE_RELEASE)
    public R<Void> releasePatient(@PathVariable String id) {
        auditService.releasePatient(HashidsUtil.decode(id));
        return R.ok();
    }

    // ==================== 随访审核发放 ====================

    /**
     * 分页查询随访审核列表
     */
    @GetMapping("/followups")
    public R<PageResult<FollowUpVO>> listFollowUps(AuditQueryDTO queryDTO) {
        PageResult<FollowUpVO> result = auditService.listFollowUps(queryDTO);
        return R.ok(result);
    }

    /**
     * 审核通过随访
     */
    @PostMapping("/followups/{id}/approve")
    @SaCheckPermission(PermissionConstants.CASE_AUDIT)
    public R<Void> approveFollowUp(@PathVariable String id, @RequestBody(required = false) AuditActionDTO dto) {
        auditService.approveFollowUp(HashidsUtil.decode(id), dto != null ? dto : new AuditActionDTO());
        return R.ok();
    }

    /**
     * 驳回随访
     */
    @PostMapping("/followups/{id}/reject")
    @SaCheckPermission(PermissionConstants.CASE_AUDIT)
    public R<Void> rejectFollowUp(@PathVariable String id, @RequestBody AuditActionDTO dto) {
        auditService.rejectFollowUp(HashidsUtil.decode(id), dto);
        return R.ok();
    }

    /**
     * 发放随访
     */
    @PostMapping("/followups/{id}/release")
    @SaCheckPermission(PermissionConstants.CASE_RELEASE)
    public R<Void> releaseFollowUp(@PathVariable String id) {
        auditService.releaseFollowUp(HashidsUtil.decode(id));
        return R.ok();
    }

    // ==================== 统计 ====================

    /**
     * 审核发放统计
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        return R.ok(auditService.stats());
    }
}
