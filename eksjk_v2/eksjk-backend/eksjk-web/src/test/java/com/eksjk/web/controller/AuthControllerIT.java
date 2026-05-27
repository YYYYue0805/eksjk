package com.eksjk.web.controller;

import com.eksjk.web.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证 API 集成测试
 * <p>
 * 使用 H2 内存数据库，测试完整的认证流程。
 * </p>
 *
 * @author eksjk
 */
@Sql(scripts = "classpath:data-test-auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class AuthControllerIT extends BaseIntegrationTest {

    // ==================== 登录接口测试 ====================

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @Order(1)
        @DisplayName("正确的用户名和密码应返回 200 和 Token")
        void shouldLoginSuccessfully() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(buildLoginJson(SUPER_ADMIN_USERNAME, TEST_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.username").value(SUPER_ADMIN_USERNAME))
                    .andExpect(jsonPath("$.data.roleCode").value("super_admin"));
        }

        @Test
        @Order(2)
        @DisplayName("用户名不存在应返回错误")
        void shouldReturnErrorForNonExistentUser() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(buildLoginJson("nonexistent_user", TEST_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }

        @Test
        @Order(3)
        @DisplayName("密码错误应返回错误")
        void shouldReturnErrorForWrongPassword() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(buildLoginJson(SUPER_ADMIN_USERNAME, "wrong_password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }

        @Test
        @Order(4)
        @DisplayName("空用户名应返回错误")
        void shouldReturnErrorForEmptyUsername() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"\",\"password\":\"Test@1234\"}"))
                    .andExpect(status().is4xxClientError());
        }
    }

    // ==================== 登出接口测试 ====================

    @Nested
    @DisplayName("POST /api/auth/logout")
    class LogoutTests {

        @Test
        @Order(5)
        @DisplayName("登出接口应返回成功")
        void shouldLogoutSuccessfully() throws Exception {
            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isOk());
        }
    }
}
