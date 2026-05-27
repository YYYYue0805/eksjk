package com.eksjk.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应类
 * <p>
 * 所有 API 接口统一返回此格式：{ code, message, data }
 * </p>
 *
 * @param <T> 响应数据类型
 * @author eksjk
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应码 */
    private int code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    private R() {
    }

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功（携带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功（自定义消息 + 数据）
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(ErrorCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败（使用错误码枚举）
     */
    public static <T> R<T> fail(ErrorCode errorCode) {
        return new R<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败（使用错误码枚举 + 自定义消息）
     */
    public static <T> R<T> fail(ErrorCode errorCode, String message) {
        return new R<>(errorCode.getCode(), message, null);
    }

    /**
     * 失败（自定义错误码和消息）
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 失败（仅消息，默认使用业务错误码）
     */
    public static <T> R<T> fail(String message) {
        return new R<>(ErrorCode.BIZ_ERROR.getCode(), message, null);
    }

    // ==================== 判断方法 ====================

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }
}
