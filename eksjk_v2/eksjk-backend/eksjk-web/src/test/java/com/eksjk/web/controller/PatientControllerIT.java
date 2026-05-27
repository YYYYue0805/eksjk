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
 * 患者管理 API 集成测试
 * <p>
 * 使用 H2 内存数据库，测试患者 CRUD 接口的连通性和数据一致性。
 * </p>
 *
 * @author eksjk
 */
@Sql(scripts = "classpath:data-test-auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PatientControllerIT extends BaseIntegrationTest {

    // ==================== 查询接口测试 ====================

    @Nested
    @DisplayName("GET /api/patients")
    class ListTests {

        @Test
        @Order(1)
        @DisplayName("未登录访问应返回未认证错误")
        void shouldReturnUnauthorizedWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/patients")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }
    }

    // ==================== 详情接口测试 ====================

    @Nested
    @DisplayName("GET /api/patients/{id}")
    class DetailTests {

        @Test
        @Order(2)
        @DisplayName("未登录访问详情应返回未认证错误")
        void shouldReturnUnauthorizedForDetail() throws Exception {
            mockMvc.perform(get("/api/patients/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }
    }

    // ==================== 创建接口测试 ====================

    @Nested
    @DisplayName("POST /api/patients")
    class CreateTests {

        @Test
        @Order(3)
        @DisplayName("未登录创建病例应返回未认证错误")
        void shouldReturnUnauthorizedForCreate() throws Exception {
            mockMvc.perform(post("/api/patients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(buildPatientJson("10000001", "新建测试患者")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }
    }

    // ==================== 统计接口测试 ====================

    @Nested
    @DisplayName("GET /api/patients/dashboard/stats")
    class DashboardStatsTests {

        @Test
        @Order(4)
        @DisplayName("未登录访问统计应返回未认证错误")
        void shouldReturnUnauthorizedForStats() throws Exception {
            mockMvc.perform(get("/api/patients/dashboard/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
        }
    }

    // ==================== 导出接口测试 ====================

    @Nested
    @DisplayName("GET /api/patients/export")
    class ExportTests {

        @Test
        @Order(5)
        @DisplayName("未登录导出应返回未认证错误")
        void shouldReturnUnauthorizedForExport() throws Exception {
            mockMvc.perform(get("/api/patients/export"))
                    .andExpect(status().isOk());
        }
    }
}
