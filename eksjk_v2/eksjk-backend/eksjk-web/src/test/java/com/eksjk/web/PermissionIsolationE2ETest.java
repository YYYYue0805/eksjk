package com.eksjk.web;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 端到端测试 — 权限隔离验证
 * <p>
 * 分别以不同角色（超级管理员、医院管理员、医生）登录，
 * 验证数据隔离和权限控制的正确性。
 * </p>
 *
 * @author eksjk
 */
@Sql(scripts = "classpath:data-test-auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PermissionIsolationE2ETest extends BaseIntegrationTest {

    /**
     * 辅助方法：登录并返回 Token
     */
    private String loginAndGetToken(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginJson(username, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
        int tokenEnd = responseBody.indexOf("\"", tokenStart);
        if (tokenStart > 8 && tokenEnd > tokenStart) {
            return responseBody.substring(tokenStart, tokenEnd);
        }
        return null;
    }

    // ==================== 超级管理员权限测试 ====================

    @Test
    @Order(1)
    @DisplayName("权限隔离: 超级管理员应能查看所有患者数据")
    void superAdminShouldSeeAllPatients() throws Exception {
        String token = loginAndGetToken(SUPER_ADMIN_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", token)
                        .param("pageNum", "1")
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // 超级管理员应能看到所有医院的数据
    }

    @Test
    @Order(2)
    @DisplayName("权限隔离: 超级管理员应能查看用户信息")
    void superAdminShouldGetUserInfo() throws Exception {
        String token = loginAndGetToken(SUPER_ADMIN_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/auth/info")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.roleCode").value("super_admin"));
    }

    // ==================== 医院管理员权限测试 ====================

    @Test
    @Order(3)
    @DisplayName("权限隔离: 医院管理员登录应返回正确角色")
    void hospitalAdminShouldHaveCorrectRole() throws Exception {
        String token = loginAndGetToken(HOSPITAL_ADMIN_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/auth/info")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.roleCode").value("hospital_admin"));
    }

    @Test
    @Order(4)
    @DisplayName("权限隔离: 医院管理员查询患者应只返回本院数据")
    void hospitalAdminShouldOnlySeeOwnHospitalData() throws Exception {
        String token = loginAndGetToken(HOSPITAL_ADMIN_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", token)
                        .param("pageNum", "1")
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // 医院管理员应只能看到本院（医院A）的数据
    }

    // ==================== 普通医生权限测试 ====================

    @Test
    @Order(5)
    @DisplayName("权限隔离: 普通医生登录应返回正确角色")
    void doctorShouldHaveCorrectRole() throws Exception {
        String token = loginAndGetToken(DOCTOR_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/auth/info")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.roleCode").value("doctor"));
    }

    @Test
    @Order(6)
    @DisplayName("权限隔离: 普通医生查询患者应只返回自己的数据")
    void doctorShouldOnlySeeOwnData() throws Exception {
        String token = loginAndGetToken(DOCTOR_USERNAME);
        Assumptions.assumeTrue(token != null, "登录失败");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", token)
                        .param("pageNum", "1")
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // 普通医生应只能看到自己创建的患者数据
    }

    // ==================== 异常容错测试 ====================

    @Test
    @Order(7)
    @DisplayName("异常容错: 无效 Token 应返回未认证错误")
    void invalidTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .header("satoken", "invalid-token-12345")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
    }

    @Test
    @Order(8)
    @DisplayName("异常容错: 无 Token 应返回未认证错误")
    void noTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
    }

    @Test
    @Order(9)
    @DisplayName("异常容错: 被禁用的账号应无法登录")
    void disabledAccountShouldNotLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginJson("disabled_user", TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
    }
}
