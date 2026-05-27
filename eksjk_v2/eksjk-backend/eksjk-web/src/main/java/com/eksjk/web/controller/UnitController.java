package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.UnitDTO;
import com.eksjk.model.dto.UnitQueryDTO;
import com.eksjk.model.entity.Unit;
import com.eksjk.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医疗机构管理 Controller
 * <p>
 * 除选项列表接口外，所有接口限定超级管理员角色。
 * </p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    /**
     * 分页查询机构列表
     */
    @GetMapping
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<PageResult<Unit>> list(UnitQueryDTO queryDTO) {
        PageResult<Unit> result = unitService.queryPage(queryDTO);
        return R.ok(result);
    }

    /**
     * 获取机构详情
     */
    @GetMapping("/{id}")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Unit> detail(@PathVariable String id) {
        long unitId = HashidsUtil.decode(id);
        Unit unit = unitService.getById(unitId);
        return R.ok(unit);
    }

    /**
     * 新增机构
     */
    @PostMapping
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Void> create(@Valid @RequestBody UnitDTO unitDTO) {
        unitService.create(unitDTO);
        return R.ok();
    }

    /**
     * 编辑机构
     */
    @PutMapping("/{id}")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Void> update(@PathVariable String id, @Valid @RequestBody UnitDTO unitDTO) {
        long unitId = HashidsUtil.decode(id);
        unitService.update(unitId, unitDTO);
        return R.ok();
    }

    /**
     * 启用/禁用机构
     */
    @PutMapping("/{id}/status")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Void> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        long unitId = HashidsUtil.decode(id);
        unitService.updateStatus(unitId, status);
        return R.ok();
    }

    /**
     * 删除机构
     */
    @DeleteMapping("/{id}")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Void> delete(@PathVariable String id) {
        long unitId = HashidsUtil.decode(id);
        unitService.delete(unitId);
        return R.ok();
    }

    /**
     * 获取机构选项列表（轻量级，供下拉选择）
     * 不限定超级管理员，所有登录用户可访问
     */
    @GetMapping("/options")
    @SaCheckLogin
    public R<List<Map<String, Object>>> options(@RequestParam(required = false) String keyword) {
        List<Map<String, Object>> options = unitService.getOptions(keyword);
        return R.ok(options);
    }

    /**
     * 获取机构关联统计
     */
    @GetMapping("/{id}/statistics")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Map<String, Long>> statistics(@PathVariable String id) {
        long unitId = HashidsUtil.decode(id);
        Map<String, Long> stats = unitService.getStatistics(unitId);
        return R.ok(stats);
    }
}
