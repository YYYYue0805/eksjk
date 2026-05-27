package com.eksjk.common.constant;

/**
 * 角色标识常量
 * <p>
 * 定义系统中所有角色的标识符，与数据库中的角色编码保持一致。
 * </p>
 *
 * @author eksjk
 */
public final class RoleConstants {

    private RoleConstants() {
        // 常量类禁止实例化
    }

    /** 超级管理员 */
    public static final String SUPER_ADMIN = "super_admin";

    /** 医院管理员 */
    public static final String HOSPITAL_ADMIN = "hospital_admin";

    /** 普通医生 */
    public static final String DOCTOR = "doctor";

    /** 家长（小程序端） */
    public static final String PARENT = "parent";
}
