package com.eksjk.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.eksjk.common.constant.PermissionConstants;
import com.eksjk.common.constant.RoleConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限认证接口实现
 * <p>
 * 为 Sa-Token 提供角色和权限数据，支持 @SaCheckRole、@SaCheckPermission 注解。
 * </p>
 *
 * @author eksjk
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回指定账号 ID 拥有的权限码集合
     * 当前系统使用角色控制，暂不使用细粒度权限码
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();
        String roleCode = (String) StpUtil.getSessionByLoginId(loginId).get("roleCode");
        if (RoleConstants.SUPER_ADMIN.equals(roleCode) || RoleConstants.HOSPITAL_ADMIN.equals(roleCode)) {
            permissions.add(PermissionConstants.CASE_AUDIT);
            permissions.add(PermissionConstants.CASE_RELEASE);
        }
        return permissions;
    }

    /**
     * 返回指定账号 ID 拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        // 从 Session 中获取角色标识
        String roleCode = (String) StpUtil.getSessionByLoginId(loginId).get("roleCode");
        if (roleCode != null && !roleCode.isEmpty()) {
            roles.add(roleCode);
            // 超级管理员同时拥有医院管理员和医生角色
            if (RoleConstants.SUPER_ADMIN.equals(roleCode)) {
                roles.add(RoleConstants.HOSPITAL_ADMIN);
                roles.add(RoleConstants.DOCTOR);
            }
            // 医院管理员同时拥有医生角色
            if (RoleConstants.HOSPITAL_ADMIN.equals(roleCode)) {
                roles.add(RoleConstants.DOCTOR);
            }
        }
        return roles;
    }
}
