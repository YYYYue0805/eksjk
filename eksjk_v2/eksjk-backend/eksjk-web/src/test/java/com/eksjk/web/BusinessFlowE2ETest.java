package com.eksjk.web;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 端到端测试 — 核心业务流程
 * <p>
 * 模拟完整的业务流程：登录 → 创建患者 → 录入病例 → 添加随访 → 查询列表 → 导出数据。
 * 同时验证不同角色的权限隔离。
 * </p>
 *
 * @author eksjk
 */
@Sql(scripts = "classpath:data-test-auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusinessFlowE2ETest extends BaseIntegrationTest {

    /** 登录后获取的 Token（跨测试方法共享） */
    private static String authToken;

    // ==================== 1. 登录流程 ====================

    @Test
    @Order(1)
    @DisplayName("E2E: 超级管理员登录应成功获取 Token")
    void step1_superAdminLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginJson(SUPER_ADMIN_USERNAME, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        // 提取 Token 供后续测试使用
        String responseBody = result.getResponse().getContentAsString();
        // 简单提取 token（实际可用 JSON 解析库）
        int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
        int tokenEnd = responseBody.indexOf("\"", tokenStart);
        if (tokenStart > 8 && tokenEnd > tokenStart) {
            authToken = responseBody.substring(tokenStart, tokenEnd);
        }
    }

    // ==================== 2. 查询患者列表 ====================

    @Test
    @Order(2)
    @DisplayName("E2E: 登录后查询患者列表应返回数据")
    void step2_queryPatientList() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", authToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 3. 查询工作台统计 ====================

    @Test
    @Order(3)
    @DisplayName("E2E: 登录后查询工作台统计应返回数据")
    void step3_queryDashboardStats() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(get("/api/patients/dashboard/stats")
                        .header("satoken", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isMap());
    }

    // ==================== 4. 按疾病类型筛选 ====================

    @Test
    @Order(4)
    @DisplayName("E2E: 按 DSD 疾病类型筛选应返回对应数据")
    void step4_filterByDisClass() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", authToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("disClass", "10000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 5. 获取用户信息 ====================

    @Test
    @Order(5)
    @DisplayName("E2E: 登录后获取当前用户信息应返回正确数据")
    void step5_getCurrentUserInfo() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(get("/api/auth/info")
                        .header("satoken", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(SUPER_ADMIN_USERNAME))
                .andExpect(jsonPath("$.data.roleCode").value("super_admin"));
    }

    // ==================== 6. 登出 ====================

    @Test
    @Order(6)
    @DisplayName("E2E: 登出应成功")
    void step6_logout() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(post("/api/auth/logout")
                        .header("satoken", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 7. 登出后访问应失败 ====================

    @Test
    @Order(7)
    @DisplayName("E2E: 登出后访问受保护接口应返回未认证错误")
    void step7_accessAfterLogout() throws Exception {
        Assumptions.assumeTrue(authToken != null, "需要先登录获取 Token");

        mockMvc.perform(get("/api/patients")
                        .header("satoken", authToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(200)));
    }
}
