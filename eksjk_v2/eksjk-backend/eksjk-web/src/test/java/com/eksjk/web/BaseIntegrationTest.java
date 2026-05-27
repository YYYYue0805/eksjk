package com.eksjk.web;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 集成测试基类
 * <p>
 * 启动完整的 Spring Boot 上下文，使用 H2 内存数据库，
 * 适用于 Controller 层集成测试和端到端测试。
 * </p>
 *
 * <h3>使用方式：</h3>
 * <pre>
 * class PatientControllerIT extends BaseIntegrationTest {
 *     &#64;Test
 *     void shouldCreatePatient() {
 *         // 使用 mockMvc 发送请求
 *     }
 * }
 * </pre>
 *
 * @author eksjk
 */
@SpringBootTest(
        classes = EksjkApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    // ==================== 常量定义 ====================

    /** JSON Content-Type */
    protected static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    /** 测试用超级管理员用户名 */
    protected static final String SUPER_ADMIN_USERNAME = "super_admin";

    /** 测试用医院管理员用户名 */
    protected static final String HOSPITAL_ADMIN_USERNAME = "hospital_admin_1";

    /** 测试用医生用户名 */
    protected static final String DOCTOR_USERNAME = "doctor_1";

    /** 测试用密码 */
    protected static final String TEST_PASSWORD = "Test@1234";

    // ==================== 工具方法 ====================

    /**
     * 构建登录请求 JSON
     */
    protected String buildLoginJson(String username, String password) {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
    }

    /**
     * 构建患者创建请求 JSON
     */
    protected String buildPatientJson(String disClass, String name) {
        return String.format(
                "{\"disClass\":\"%s\",\"name\":\"%s\",\"sex\":\"男\",\"height\":\"120.5\",\"weight\":\"25.0\"}",
                disClass, name
        );
    }

    /**
     * 构建随访记录创建请求 JSON
     */
    protected String buildFollowUpJson(Long patientId) {
        return String.format(
                "{\"patientId\":%d,\"height\":\"125.0\",\"weight\":\"27.0\",\"bmi\":\"17.3\"}",
                patientId
        );
    }
}
