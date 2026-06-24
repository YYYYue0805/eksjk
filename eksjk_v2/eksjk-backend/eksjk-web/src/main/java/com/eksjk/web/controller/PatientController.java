package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.PatientDTO;
import com.eksjk.model.dto.PatientQueryDTO;
import com.eksjk.model.vo.PatientVO;
import com.eksjk.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 患者病例管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@SaCheckLogin
public class PatientController {

    private final PatientService patientService;

    /**
     * 分页查询病例列表
     */
    @GetMapping
    public R<PageResult<PatientVO>> list(PatientQueryDTO queryDTO) {
        PageResult<PatientVO> result = patientService.queryPage(queryDTO);
        return R.ok(result);
    }

    /**
     * 获取病例详情
     */
    @GetMapping("/{id}")
    public R<PatientVO> detail(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        PatientVO detail = patientService.getDetail(patientId);
        return R.ok(detail);
    }

    /**
     * 新建病例
     */
    @PostMapping
    public R<Map<String, String>> create(@RequestBody PatientDTO patientDTO) {
        String caseNum = patientService.create(patientDTO);
        return R.ok(Map.of("caseNum", caseNum));
    }

    /**
     * 编辑病例
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable String id, @RequestBody PatientDTO patientDTO) {
        long patientId = HashidsUtil.decode(id);
        patientService.update(patientId, patientDTO);
        return R.ok();
    }

    /**
     * 删除病例（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        patientService.delete(patientId);
        return R.ok();
    }

    /**
     * 导出Excel
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(PatientQueryDTO queryDTO) {
        byte[] data = patientService.exportExcel(queryDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cases.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    /**
     * 获取工作台统计数据
     */
    @GetMapping("/dashboard/stats")
    public R<Map<String, Object>> dashboardStats() {
        Map<String, Object> stats = patientService.getDashboardStats();
        return R.ok(stats);
    }

    // ==================== 家庭关联 ====================

    /**
     * 按病历号精确搜索患者（用于家庭关联）
     */
    @GetMapping("/search-by-medrec")
    public R<PatientVO> searchByMedrecNum(@RequestParam String medrecNum) {
        PatientVO vo = patientService.searchByMedrecNum(medrecNum);
        return R.ok(vo);
    }

    /**
     * 查询同一家庭的所有患者
     */
    @GetMapping("/family/{familyId}")
    public R<Map<String, Object>> getFamilyMembers(@PathVariable String familyId) {
        java.util.List<PatientVO> members = patientService.getFamilyMembers(familyId);
        return R.ok(Map.of("familyId", familyId, "members", members));
    }

    /**
     * 将患者关联到目标患者的家庭
     */
    @PutMapping("/{id}/link-family")
    public R<Void> linkFamily(@PathVariable String id, @RequestBody Map<String, String> body) {
        long patientId = HashidsUtil.decode(id);
        String targetMedrecNum = body.get("targetMedrecNum");
        patientService.linkFamily(patientId, targetMedrecNum);
        return R.ok();
    }

    /**
     * 解除患者与家庭的关联
     */
    @PutMapping("/{id}/unlink-family")
    public R<Void> unlinkFamily(@PathVariable String id) {
        long patientId = HashidsUtil.decode(id);
        patientService.unlinkFamily(patientId);
        return R.ok();
    }

    /**
     * 重新分类患者疾病类型（ELTM → 目标病种）
     */
    @PutMapping("/{id}/reclassify")
    public R<Map<String, String>> reclassify(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            long patientId = HashidsUtil.decode(id);
            String targetDisClass = body.get("targetDisClass");
            if (targetDisClass == null || targetDisClass.isBlank()) {
                return R.fail(400, "目标疾病类型不能为空");
            }
            String newCaseNum = patientService.reclassify(patientId, targetDisClass);
            return R.ok(Map.of("caseNum", newCaseNum));
        } catch (Exception e) {
            return R.fail(500, "重新分类失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
