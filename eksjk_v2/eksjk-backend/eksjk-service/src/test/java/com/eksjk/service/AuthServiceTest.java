package com.eksjk.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.LoginDTO;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.LoginVO;
import com.eksjk.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 *
 * @author eksjk
 */
class AuthServiceTest extends BaseServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserMapper userMapper;

    // ==================== 登录测试 ====================

    @Nested
    @DisplayName("登录功能测试")
    class LoginTests {

        @Test
        @DisplayName("正确的用户名和密码应登录成功")
        void shouldLoginSuccessfully() {
            // Given
            User user = createSuperAdmin();
            LoginDTO loginDTO = createLoginDTO("super_admin", TEST_PASSWORD);

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            // 模拟 Sa-Token 静态方法
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                var mockSession = mock(cn.dev33.satoken.session.SaSession.class);
                when(mockSession.set(anyString(), any())).thenReturn(mockSession);

                stpMock.when(() -> StpUtil.login(any())).thenAnswer(inv -> null);
                stpMock.when(StpUtil::getTokenValue).thenReturn("test-token-123");
                stpMock.when(StpUtil::getSession).thenReturn(mockSession);

                // When
                LoginVO result = authService.login(loginDTO, "127.0.0.1");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getToken()).isEqualTo("test-token-123");
                assertThat(result.getUsername()).isEqualTo("super_admin");
                assertThat(result.getRoleCode()).isEqualTo("super_admin");
            }
        }

        @Test
        @DisplayName("用户名不存在应抛出异常")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            LoginDTO loginDTO = createLoginDTO("nonexistent", TEST_PASSWORD);
            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTO, "127.0.0.1"))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("账号被禁用应抛出异常")
        void shouldThrowExceptionWhenAccountDisabled() {
            // Given
            User user = createSuperAdmin();
            user.setIsActive(false);
            LoginDTO loginDTO = createLoginDTO("super_admin", TEST_PASSWORD);

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTO, "127.0.0.1"))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("密码错误应抛出异常")
        void shouldThrowExceptionWhenPasswordWrong() {
            // Given
            User user = createSuperAdmin();
            LoginDTO loginDTO = createLoginDTO("super_admin", "wrong_password");

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTO, "127.0.0.1"))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("医院管理员登录应返回正确角色")
        void shouldReturnHospitalAdminRole() {
            // Given
            User user = createHospitalAdmin();
            LoginDTO loginDTO = createLoginDTO("hospital_admin_1", TEST_PASSWORD);

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                var mockSession = mock(cn.dev33.satoken.session.SaSession.class);
                when(mockSession.set(anyString(), any())).thenReturn(mockSession);

                stpMock.when(() -> StpUtil.login(any())).thenAnswer(inv -> null);
                stpMock.when(StpUtil::getTokenValue).thenReturn("test-token-456");
                stpMock.when(StpUtil::getSession).thenReturn(mockSession);

                // When
                LoginVO result = authService.login(loginDTO, "127.0.0.1");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getRoleCode()).isEqualTo("hospital_admin");
            }
        }
    }

    // ==================== 登出测试 ====================

    @Nested
    @DisplayName("登出功能测试")
    class LogoutTests {

        @Test
        @DisplayName("已登录用户应成功登出")
        void shouldLogoutSuccessfully() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::isLogin).thenReturn(true);
                stpMock.when(StpUtil::logout).thenAnswer(inv -> null);

                // When
                authService.logout();

                // Then
                stpMock.verify(StpUtil::logout, times(1));
            }
        }

        @Test
        @DisplayName("未登录用户登出不应抛出异常")
        void shouldNotThrowWhenNotLoggedIn() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::isLogin).thenReturn(false);

                // When & Then
                assertThatCode(() -> authService.logout()).doesNotThrowAnyException();
                stpMock.verify(StpUtil::logout, never());
            }
        }
    }

    // ==================== 获取当前用户信息测试 ====================

    @Nested
    @DisplayName("获取当前用户信息测试")
    class GetCurrentUserInfoTests {

        @Test
        @DisplayName("已登录用户应返回正确的用户信息")
        void shouldReturnCurrentUserInfo() {
            // Given
            User user = createDoctor();

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(DOCTOR_ID);
                when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);

                // When
                var result = authService.getCurrentUserInfo();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getUsername()).isEqualTo("doctor_1");
                assertThat(result.getRealName()).isEqualTo("测试医生");
                assertThat(result.getRoleCode()).isEqualTo("doctor");
            }
        }

        @Test
        @DisplayName("用户不存在应抛出异常")
        void shouldThrowExceptionWhenUserNotExist() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(999L);
                when(userMapper.selectById(999L)).thenReturn(null);

                // When & Then
                assertThatThrownBy(() -> authService.getCurrentUserInfo())
                        .isInstanceOf(BusinessException.class);
            }
        }
    }
}
