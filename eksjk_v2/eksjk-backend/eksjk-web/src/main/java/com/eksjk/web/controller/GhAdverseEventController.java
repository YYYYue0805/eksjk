package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.GhAdverseEventDTO;
import com.eksjk.model.vo.GhAdverseEventVO;
import com.eksjk.service.GhAdverseEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GH不良事件管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/gh-adverse-events")
@RequiredArgsConstructor
@SaCheckLogin
public class GhAdverseEventController {

    private final GhAdverseEventService ghAdverseEventService;

    /**
     * 获取某患者的不良事件列表
     */
    @GetMapping("/patient/{patientId}")
    public R<List<GhAdverseEventVO>> listByPatient(@PathVariable String patientId) {
        long id = HashidsUtil.decode(patientId);
        List<GhAdverseEventVO> list = ghAdverseEventService.listByPatientId(id);
        return R.ok(list);
    }

    /**
     * 获取不良事件详情
     */
    @GetMapping("/{id}")
    public R<GhAdverseEventVO> detail(@PathVariable String id) {
        long eventId = HashidsUtil.decode(id);
        GhAdverseEventVO detail = ghAdverseEventService.getDetail(eventId);
        return R.ok(detail);
    }

    /**
     * 新增不良事件
     */
    @PostMapping
    public R<Void> create(@RequestBody GhAdverseEventDTO dto) {
        ghAdverseEventService.create(dto);
        return R.ok();
    }

    /**
     * 编辑不良事件
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable String id, @RequestBody GhAdverseEventDTO dto) {
        long eventId = HashidsUtil.decode(id);
        ghAdverseEventService.update(eventId, dto);
        return R.ok();
    }

    /**
     * 删除不良事件
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        long eventId = HashidsUtil.decode(id);
        ghAdverseEventService.delete(eventId);
        return R.ok();
    }
}
