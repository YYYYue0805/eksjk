package com.eksjk.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.ChangePasswordDTO;
import com.eksjk.model.dto.UserDTO;
import com.eksjk.model.dto.UserQueryDTO;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.UserInfoVO;
import com.eksjk.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户管理服务单元测试
 *
 * @author eksjk
 */
class UserServiceTest extends BaseServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    // ==================== 分页查询测试 ====================

    @Nested
    @DisplayName("分页查询用户列表")
    class QueryPageTests {

        @Test
        @DisplayName("超级管理员应能查询所有用户")
        void shouldReturnAllUsersForSuperAdmin() {
            // Given
            UserQueryDTO queryDTO = new UserQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10);

            User u1 = createSuperAdmin();
            User u2 = createDoctor();

            Page<User> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(u1, u2));
            mockPage.setTotal(2);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                        .thenReturn(mockPage);

                // When
                var result = userService.queryPage(queryDTO);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getTotal()).isEqualTo(2);
            }
        }
    }

    // ==================== 获取用户详情测试 ====================

    @Nested
    @DisplayName("获取用户详情")
    class GetDetailTests {

        @Test
        @DisplayName("存在的用户应返回详情")
        void shouldReturnUserDetail() {
            // Given
            User user = createDoctor();
            when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);

            // When
            UserInfoVO result = userService.getDetail(DOCTOR_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("doctor_1");
        }

        @Test
        @DisplayName("不存在的用户应抛出异常")
        void shouldThrowExceptionForNonExistent() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> userService.getDetail(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== 创建用户测试 ====================

    @Nested
    @DisplayName("新增用户")
    class CreateTests {

        @Test
        @DisplayName("有效数据应成功创建用户并返回初始密码")
        void shouldCreateUserSuccessfully() {
            // Given
            UserDTO dto = new UserDTO();
            dto.setUsername("new_doctor");
            dto.setRealName("新医生");
            dto.setRoleCode("doctor");

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                secMock.when(SecurityUtil::getCurrentHospitalId).thenReturn(HOSPITAL_UNIT_ID);

                when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
                when(userMapper.insert(any(User.class))).thenReturn(1);

                // When
                Map<String, String> result = userService.create(dto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).containsKey("password");
                verify(userMapper, times(1)).insert(any(User.class));
            }
        }

        @Test
        @DisplayName("用户名已存在应抛出异常")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            UserDTO dto = new UserDTO();
            dto.setUsername("super_admin");
            dto.setRealName("重复用户");

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");

                when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

                // When & Then
                assertThatThrownBy(() -> userService.create(dto))
                        .isInstanceOf(BusinessException.class);
            }
        }
    }

    // ==================== 修改密码测试 ====================

    @Nested
    @DisplayName("修改密码")
    class ChangePasswordTests {

        @Test
        @DisplayName("旧密码正确应成功修改密码")
        void shouldChangePasswordSuccessfully() {
            // Given
            User user = createDoctor();
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setOldPassword(TEST_PASSWORD);
            dto.setNewPassword("NewPass@5678");

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(DOCTOR_ID);
                when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);
                when(userMapper.updateById(any(User.class))).thenReturn(1);

                // When & Then
                assertThatCode(() -> userService.changePassword(dto)).doesNotThrowAnyException();
                verify(userMapper, times(1)).updateById(any(User.class));
            }
        }

        @Test
        @DisplayName("旧密码错误应抛出异常")
        void shouldThrowExceptionWhenOldPasswordWrong() {
            // Given
            User user = createDoctor();
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setOldPassword("wrong_password");
            dto.setNewPassword("NewPass@5678");

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(DOCTOR_ID);
                when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);

                // When & Then
                assertThatThrownBy(() -> userService.changePassword(dto))
                        .isInstanceOf(BusinessException.class);
            }
        }
    }

    // ==================== 启用/禁用用户测试 ====================

    @Nested
    @DisplayName("启用/禁用用户")
    class UpdateStatusTests {

        @Test
        @DisplayName("应成功切换用户状态")
        void shouldUpdateStatusSuccessfully() {
            // Given
            User user = createDoctor();
            when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            // When & Then
            assertThatCode(() -> userService.updateStatus(DOCTOR_ID, false))
                    .doesNotThrowAnyException();
            verify(userMapper, times(1)).updateById(any(User.class));
        }
    }

    // ==================== 重置密码测试 ====================

    @Nested
    @DisplayName("重置密码")
    class ResetPasswordTests {

        @Test
        @DisplayName("应成功重置密码并返回新密码")
        void shouldResetPasswordSuccessfully() {
            // Given
            User user = createDoctor();
            when(userMapper.selectById(DOCTOR_ID)).thenReturn(user);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            // When
            String newPassword = userService.resetPassword(DOCTOR_ID);

            // Then
            assertThat(newPassword).isNotNull().isNotEmpty();
            verify(userMapper, times(1)).updateById(any(User.class));
        }
    }
}
