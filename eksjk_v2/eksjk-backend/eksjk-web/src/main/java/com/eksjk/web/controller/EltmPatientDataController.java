package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.entity.EltmAssessmentReport;
import com.eksjk.model.entity.EltmFormSubmission;
import com.eksjk.model.entity.EltmPatientPic;
import com.eksjk.model.entity.EltmPatientRaw;
import com.eksjk.model.entity.EltmUnprocessedData;
import com.eksjk.service.eltm.EltmPatientDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ELTM 外部同步数据只读 Controller
 *
 * <p>数据来源为 datamain 同步落库的 eltm_* 表，仅供详情页展示。</p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/eltm/patients")
@RequiredArgsConstructor
@SaCheckLogin
public class EltmPatientDataController {

    private final EltmPatientDataService eltmPatientDataService;

    /**
     * 患者外部数据概览
     */
    @GetMapping("/{id}/overview")
    public R<Map<String, Object>> overview(@PathVariable String id) {
        return R.ok(eltmPatientDataService.overview(HashidsUtil.decode(id)));
    }

    /**
     * 体征测量分页
     */
    @GetMapping("/{id}/physical")
    public R<PageResult<Map<String, Object>>> physical(@PathVariable String id,
                                                      @RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(eltmPatientDataService.physical(HashidsUtil.decode(id), pageNum, pageSize));
    }

    /**
     * 表单提交分页
     */
    @GetMapping("/{id}/forms")
    public R<PageResult<EltmFormSubmission>> forms(@PathVariable String id,
                                                   @RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(eltmPatientDataService.forms(HashidsUtil.decode(id), pageNum, pageSize));
    }

    /**
     * 表单详情（含原始答题数据）
     */
    @GetMapping("/{id}/forms/{formDataId}")
    public R<Map<String, Object>> formDetail(@PathVariable String id,
                                             @PathVariable Long formDataId) {
        return R.ok(eltmPatientDataService.formDetail(HashidsUtil.decode(id), formDataId));
    }

    /**
     * 病历图片
     */
    @GetMapping("/{id}/pics")
    public R<List<EltmPatientPic>> pics(@PathVariable String id) {
        return R.ok(eltmPatientDataService.pics(HashidsUtil.decode(id)));
    }

    /**
     * 评估报告分页
     */
    @GetMapping("/{id}/reports")
    public R<PageResult<EltmAssessmentReport>> reports(@PathVariable String id,
                                                       @RequestParam(defaultValue = "1") long pageNum,
                                                       @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(eltmPatientDataService.reports(HashidsUtil.decode(id), pageNum, pageSize));
    }

    /**
     * 所属项目与医院绑定
     */
    @GetMapping("/{id}/projects")
    public R<Map<String, Object>> projects(@PathVariable String id) {
        return R.ok(eltmPatientDataService.projects(HashidsUtil.decode(id)));
    }

    /**
     * 运动与睡眠记录
     */
    @GetMapping("/{id}/exercise-sleep")
    public R<Map<String, Object>> exerciseSleep(@PathVariable String id) {
        return R.ok(eltmPatientDataService.exerciseSleep(HashidsUtil.decode(id)));
    }

    /**
     * 未识别数据分页
     */
    @GetMapping("/{id}/unprocessed")
    public R<PageResult<EltmUnprocessedData>> unprocessed(
            @PathVariable String id,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String module,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(eltmPatientDataService.unprocessed(
                HashidsUtil.decode(id), category, module, pageNum, pageSize));
    }

    /**
     * 未处理数据按目标模块统计
     */
    @GetMapping("/{id}/unprocessed-modules")
    public R<List<Map<String, Object>>> unprocessedModules(@PathVariable String id) {
        return R.ok(eltmPatientDataService.unprocessedModules(HashidsUtil.decode(id)));
    }

    /**
     * 未识别数据详情（含完整 raw_json）
     */
    @GetMapping("/{id}/unprocessed/{recordId}")
    public R<EltmUnprocessedData> unprocessedDetail(@PathVariable String id,
                                                    @PathVariable Long recordId) {
        return R.ok(eltmPatientDataService.unprocessedDetail(HashidsUtil.decode(id), recordId));
    }

    /**
     * cxList 原始行
     */
    @GetMapping("/{id}/raw")
    public R<EltmPatientRaw> raw(@PathVariable String id) {
        return R.ok(eltmPatientDataService.rawRow(HashidsUtil.decode(id)));
    }

    /**
     * 单患者补拉病历图片（gettpbyid）
     */
    @PostMapping("/{id}/pics/sync")
    public R<Map<String, Object>> syncPics(@PathVariable String id) {
        return R.ok(eltmPatientDataService.syncPics(HashidsUtil.decode(id)));
    }
}
