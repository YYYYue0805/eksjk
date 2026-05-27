package com.eksjk.common.constant;

/**
 * 系统通用常量
 *
 * @author eksjk
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 最大分页大小 */
    public static final int MAX_PAGE_SIZE = 500;

    /** 逻辑删除 — 未删除 */
    public static final int NOT_DELETED = 0;

    /** 逻辑删除 — 已删除 */
    public static final int DELETED = 1;

    /** 状态 — 启用 */
    public static final int STATUS_ENABLED = 1;

    /** 状态 — 禁用 */
    public static final int STATUS_DISABLED = 0;

    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "eksjk123";

    /** 密码过期天数（180 天 = 6 个月） */
    public static final int PASSWORD_EXPIRE_DAYS = 180;

    /** Token 请求头名称 */
    public static final String TOKEN_HEADER = "satoken";
}
