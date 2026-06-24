package com.eksjk.common.constant;

/**
 * 权限码常量
 * <p>
 * 用于 Sa-Token {@code @SaCheckPermission} 注解的细粒度权限控制。
 * </p>
 *
 * @author eksjk
 */
public final class PermissionConstants {

    private PermissionConstants() {
    }

    /** 病例审核权限 */
    public static final String CASE_AUDIT = "case:audit";

    /** 病例发放权限 */
    public static final String CASE_RELEASE = "case:release";
}
