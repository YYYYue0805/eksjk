package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.FollowUpDTO;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.service.FollowUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/followups")
@RequiredArgsConstructor
@SaCheckLogin
public class FollowUpController {

    private final FollowUpService followUpService;

    /**
     * 获取某患者的随访列表
     */
    @GetMapping("/patient/{patientId}")
    public R<List<FollowUpVO>> listByPatient(@PathVariable String patientId) {
        long id = HashidsUtil.decode(patientId);
        List<FollowUpVO> list = followUpService.listByPatientId(id);
        return R.ok(list);
    }

    /**
     * 获取随访详情
     */
    @GetMapping("/{id}")
    public R<FollowUpVO> detail(@PathVariable String id) {
        long followUpId = HashidsUtil.decode(id);
        FollowUpVO detail = followUpService.getDetail(followUpId);
        return R.ok(detail);
    }

    /**
     * 新增随访记录
     */
    @PostMapping
    public R<Void> create(@RequestBody FollowUpDTO followUpDTO) {
        followUpService.create(followUpDTO);
        return R.ok();
    }

    /**
     * 编辑随访记录
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable String id, @RequestBody FollowUpDTO followUpDTO) {
        long followUpId = HashidsUtil.decode(id);
        followUpService.update(followUpId, followUpDTO);
        return R.ok();
    }

    /**
     * 删除随访记录
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        long followUpId = HashidsUtil.decode(id);
        followUpService.delete(followUpId);
        return R.ok();
    }
}
