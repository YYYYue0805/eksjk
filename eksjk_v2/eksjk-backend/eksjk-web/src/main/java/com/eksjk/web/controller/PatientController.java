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
}
