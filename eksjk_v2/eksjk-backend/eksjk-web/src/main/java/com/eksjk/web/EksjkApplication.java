package com.eksjk.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EKSJK V2 后端启动类
 * 
 * <p>儿科生长发育数据管理系统 V2 版本后端应用入口</p>
 * 
 * <h3>技术栈配置：</h3>
 * <ul>
 *   <li><strong>Spring Boot 3.x</strong> - 主框架，提供 RESTful API 和依赖注入</li>
 *   <li><strong>MyBatis-Plus 3.x</strong> - ORM 框架，集成通用 CRUD 和分页插件</li>
 *   <li><strong>Sa-Token</strong> - 轻量级权限认证框架，支持 JWT 模式</li>
 *   <li><strong>MySQL 8.0+</strong> - 关系型数据库，存储患者数据和系统配置</li>
 *   <li><strong>EasyExcel</strong> - Excel 导入导出工具，支持大数据量处理</li>
 * </ul>
 * 
 * <h3>模块结构：</h3>
 * <ul>
 *   <li><strong>eksjk-common</strong> - 公共工具类和基础配置</li>
 *   <li><strong>eksjk-model</strong> - 数据实体和 DTO/VO 对象</li>
 *   <li><strong>eksjk-mapper</strong> - MyBatis 数据访问层</li>
 *   <li><strong>eksjk-service</strong> - 业务逻辑层</li>
 *   <li><strong>eksjk-web</strong> - Web 控制器层（当前模块）</li>
 * </ul>
 * 
 * <h3>启动参数：</h3>
 * <ul>
 *   <li>默认端口：8080</li>
 *   <li>上下文路径：/</li>
 *   <li>开发环境：application-dev.yml</li>
 *   <li>生产环境：application-prod.yml</li>
 * </ul>
 * 
 * @author eksjk
 * @version 2.0.0
 * @since 2024
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.eksjk")
@MapperScan("com.eksjk.mapper")
@EnableScheduling
public class EksjkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EksjkApplication.class, args);
    }
}
