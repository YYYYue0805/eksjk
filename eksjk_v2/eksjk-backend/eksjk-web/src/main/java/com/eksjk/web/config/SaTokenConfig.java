package com.eksjk.web.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 * <p>
 * 配置 Sa-Token 拦截器，实现路由拦截鉴权
 * </p>
 *
 * @author eksjk
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 公开接口 — 不需要认证
                        "/api/auth/login",
                        "/api/wx/login",
                        "/api/miniapp/login",
                        "/api/doctor-app/wx-login",
                        "/api/doctor-app/account-login",
                        "/api/doctor-app/bind-wx",
                        // 健康检查
                        "/actuator/**",
                        // 静态资源
                        "/favicon.ico",
                        "/error",
                        // 测试接口（开发环境）
                        "/api/test/**"
                );
    }
}
