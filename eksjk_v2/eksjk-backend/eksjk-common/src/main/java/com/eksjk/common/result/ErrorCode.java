package com.eksjk.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author eksjk
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 参数错误 */
    BAD_REQUEST(400, "参数错误"),

    /** 未认证 */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /** 无权限 */
    FORBIDDEN(403, "无权限访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 请求方法不允许 */
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    /** 数据冲突 */
    CONFLICT(409, "数据冲突"),

    /** 系统错误 */
    INTERNAL_ERROR(500, "系统内部错误，请稍后重试"),

    /** 业务错误 — 通用 */
    BIZ_ERROR(600, "业务处理失败"),

    /** 数据已存在 */
    DATA_EXIST(601, "数据已存在"),

    /** 数据不存在 */
    DATA_NOT_EXIST(602, "数据不存在"),

    /** 数据关联，不可删除 */
    DATA_REFERENCED(603, "数据存在关联，无法删除"),

    /** 用户名或密码错误 */
    LOGIN_FAILED(610, "用户名或密码错误"),

    /** 账号已被禁用 */
    ACCOUNT_DISABLED(611, "账号已被禁用"),

    /** 密码已过期 */
    PASSWORD_EXPIRED(612, "密码已过期，请修改密码");

    /** 错误码 */
    private final int code;

    /** 错误消息 */
    private final String message;
}
