package com.eksjk.common.exception;

import com.eksjk.common.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于在业务逻辑中抛出可预期的异常，携带错误码和错误消息。
 * 全局异常处理器会捕获此异常并返回对应的错误响应。
 * </p>
 *
 * @author eksjk
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final int code;

    /**
     * 使用错误码枚举构造
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用错误码枚举 + 自定义消息构造
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 使用自定义错误码和消息构造
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用默认业务错误码 + 自定义消息构造
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BIZ_ERROR.getCode();
    }

    /**
     * 使用错误码枚举 + 原始异常构造
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}
