package com.eksjk.common.util;

import cn.dev33.satoken.stp.StpUtil;
import com.eksjk.common.constant.RoleConstants;

/**
 * 安全上下文工具类
 * <p>
 * 封装从 Sa-Token Session 中获取当前用户信息的方法。
 * </p>
 *
 * @author eksjk
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 获取当前登录用户 ID
     */
    public static long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前用户角色标识
     */
    public static String getCurrentRoleCode() {
        return (String) StpUtil.getSession().get("roleCode");
    }

    /**
     * 获取当前用户所属医院 ID（字符串）
     */
    public static String getCurrentHospitalId() {
        return (String) StpUtil.getSession().get("hospitalId");
    }

    /**
     * 获取当前用户真实姓名
     */
    public static String getCurrentRealName() {
        return (String) StpUtil.getSession().get("realName");
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return (String) StpUtil.getSession().get("username");
    }

    /**
     * 判断当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return RoleConstants.SUPER_ADMIN.equals(getCurrentRoleCode());
    }

    /**
     * 判断当前用户是否为医院管理员
     */
    public static boolean isHospitalAdmin() {
        return RoleConstants.HOSPITAL_ADMIN.equals(getCurrentRoleCode());
    }

    /**
     * 判断当前用户是否为普通医生
     */
    public static boolean isDoctor() {
        return RoleConstants.DOCTOR.equals(getCurrentRoleCode());
    }

    /**
     * 判断当前用户是否为家长
     */
    public static boolean isParent() {
        return RoleConstants.PARENT.equals(getCurrentRoleCode());
    }

    /**
     * 获取当前用户角色标识（getCurrentRoleCode 的别名）
     */
    public static String getCurrentRole() {
        return getCurrentRoleCode();
    }

    /**
     * 获取当前用户所属医院名称
     */
    public static String getCurrentHospitalName() {
        return (String) StpUtil.getSession().get("hospitalName");
    }
}
