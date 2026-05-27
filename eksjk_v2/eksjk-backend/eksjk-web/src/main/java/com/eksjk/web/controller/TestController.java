package com.eksjk.web.controller;

import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.ErrorCode;
import com.eksjk.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 Controller
 * <p>
 * 用于验证统一响应格式和全局异常处理是否正常工作。
 * 仅在开发环境使用。
 * </p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试成功响应
     */
    @GetMapping("/ok")
    public R<String> testOk() {
        return R.ok("Hello EKSJK V2!");
    }

    /**
     * 测试业务异常
     */
    @GetMapping("/biz-error")
    public R<Void> testBizError() {
        throw new BusinessException(ErrorCode.BIZ_ERROR, "这是一个测试业务异常");
    }

    /**
     * 测试系统异常
     */
    @GetMapping("/sys-error")
    public R<Void> testSysError() {
        throw new RuntimeException("这是一个测试系统异常");
    }

    /**
     * 测试参数异常
     */
    @GetMapping("/param-error")
    public R<Void> testParamError() {
        throw new IllegalArgumentException("参数不能为空");
    }
}
