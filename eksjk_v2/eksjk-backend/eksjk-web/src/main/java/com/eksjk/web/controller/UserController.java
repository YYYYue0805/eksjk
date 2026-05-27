package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.ChangePasswordDTO;
import com.eksjk.model.dto.UserDTO;
import com.eksjk.model.dto.UserQueryDTO;
import com.eksjk.model.vo.UserInfoVO;
import com.eksjk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<PageResult<UserInfoVO>> list(UserQueryDTO queryDTO) {
        PageResult<UserInfoVO> result = userService.queryPage(queryDTO);
        return R.ok(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<UserInfoVO> detail(@PathVariable String id) {
        long userId = HashidsUtil.decode(id);
        UserInfoVO userInfo = userService.getDetail(userId);
        return R.ok(userInfo);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<Map<String, String>> create(@Valid @RequestBody UserDTO userDTO) {
        Map<String, String> result = userService.create(userDTO);
        return R.ok(result);
    }

    /**
     * 编辑用户
     */
    @PutMapping("/{id}")
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<Void> update(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        long userId = HashidsUtil.decode(id);
        userService.update(userId, userDTO);
        return R.ok();
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<Void> updateStatus(@PathVariable String id, @RequestParam Boolean isActive) {
        long userId = HashidsUtil.decode(id);
        userService.updateStatus(userId, isActive);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    @SaCheckRole({RoleConstants.SUPER_ADMIN, RoleConstants.HOSPITAL_ADMIN})
    public R<Map<String, String>> resetPassword(@PathVariable String id) {
        long userId = HashidsUtil.decode(id);
        String newPassword = userService.resetPassword(userId);
        return R.ok(Map.of("password", newPassword));
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @SaCheckRole(RoleConstants.SUPER_ADMIN)
    public R<Void> delete(@PathVariable String id) {
        long userId = HashidsUtil.decode(id);
        userService.delete(userId);
        return R.ok();
    }

    // ==================== 个人中心 ====================

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    @SaCheckLogin
    public R<UserInfoVO> profile() {
        UserInfoVO profile = userService.getProfile();
        return R.ok(profile);
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    @SaCheckLogin
    public R<Void> updateProfile(@Valid @RequestBody UserDTO userDTO) {
        userService.updateProfile(userDTO);
        return R.ok();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @SaCheckLogin
    public R<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return R.ok();
    }
}
