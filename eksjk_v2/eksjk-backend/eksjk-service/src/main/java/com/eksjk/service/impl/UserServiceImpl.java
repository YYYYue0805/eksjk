package com.eksjk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.constant.CommonConstants;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.ErrorCode;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.ChangePasswordDTO;
import com.eksjk.model.dto.UserDTO;
import com.eksjk.model.dto.UserQueryDTO;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.UserInfoVO;
import com.eksjk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public PageResult<UserInfoVO> queryPage(UserQueryDTO queryDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDeleted, 0).or().isNull(User::getIsDeleted);

        // 数据范围过滤
        String currentRole = SecurityUtil.getCurrentRoleCode();
        if (RoleConstants.HOSPITAL_ADMIN.equals(currentRole)) {
            // 医院管理员只能查看本院用户
            wrapper.eq(User::getUnit, SecurityUtil.getCurrentHospitalId());
        } else if (!RoleConstants.SUPER_ADMIN.equals(currentRole)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问用户管理");
        }

        // 关键词搜索
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                    .like(User::getUsername, queryDTO.getKeyword())
                    .or()
                    .like(User::getName, queryDTO.getKeyword())
            );
        }

        // 角色筛选
        if (queryDTO.getRoleCode() != null && !queryDTO.getRoleCode().isEmpty()) {
            wrapper.eq(User::getRoleCode, queryDTO.getRoleCode());
        }

        // 医院筛选（仅超级管理员可用）
        if (queryDTO.getHospitalId() != null && !queryDTO.getHospitalId().isEmpty()
                && RoleConstants.SUPER_ADMIN.equals(currentRole)) {
            wrapper.eq(User::getUnit, queryDTO.getHospitalId());
        }

        // 状态筛选
        if (queryDTO.getIsActive() != null) {
            wrapper.eq(User::getIsActive, queryDTO.getIsActive());
        }

        wrapper.orderByDesc(User::getId);

        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<User> result = userMapper.selectPage(page, wrapper);

        List<UserInfoVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public UserInfoVO getDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> create(UserDTO userDTO) {
        // 校验用户名唯一性
        checkUsernameUnique(userDTO.getUsername(), null);

        // 医院管理员权限校验
        String currentRole = SecurityUtil.getCurrentRoleCode();
        if (RoleConstants.HOSPITAL_ADMIN.equals(currentRole)) {
            // 医院管理员只能创建普通医生
            userDTO.setRoleCode(RoleConstants.DOCTOR);
            userDTO.setHospitalId(SecurityUtil.getCurrentHospitalId());
        }

        // 生成密码
        String rawPassword = userDTO.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = generateRandomPassword();
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getRealName());
        user.setSex(userDTO.getSex());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setRoleCode(userDTO.getRoleCode());
        user.setDepartment(userDTO.getDepartment());
        user.setProfessional(userDTO.getProfessional());
        user.setJobNumber(userDTO.getJobNumber());
        user.setPassword(PASSWORD_ENCODER.encode(rawPassword));
        user.setIsActive(true);
        user.setIsStaff(true);
        user.setIsSuperuser(RoleConstants.SUPER_ADMIN.equals(userDTO.getRoleCode()));
        user.setDateJoined(LocalDateTime.now());
        user.setDateUpdate(LocalDateTime.now());
        user.setIsDeleted(0);

        // 设置医院
        if (userDTO.getHospitalId() != null && !userDTO.getHospitalId().isEmpty()) {
            user.setUnit(userDTO.getHospitalId());
        }

        // V1 兼容：设置 level 字段
        if (RoleConstants.SUPER_ADMIN.equals(userDTO.getRoleCode())) {
            user.setLevel(1);
        } else {
            user.setLevel(0);
        }

        userMapper.insert(user);
        log.info("新增用户: id={}, username={}, role={}", user.getId(), user.getUsername(), userDTO.getRoleCode());

        Map<String, String> result = new HashMap<>();
        result.put("userId", HashidsUtil.encode(user.getId()));
        result.put("password", rawPassword);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UserDTO userDTO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        // 校验用户名唯一性（排除自身）
        if (!user.getUsername().equals(userDTO.getUsername())) {
            checkUsernameUnique(userDTO.getUsername(), id);
        }

        // 医院管理员权限校验
        String currentRole = SecurityUtil.getCurrentRoleCode();
        if (RoleConstants.HOSPITAL_ADMIN.equals(currentRole)) {
            // 不可修改角色和所属医院
            if (!user.getUnit().equals(SecurityUtil.getCurrentHospitalId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权限编辑非本院用户");
            }
        }

        user.setName(userDTO.getRealName());
        user.setSex(userDTO.getSex());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setProfessional(userDTO.getProfessional());
        user.setJobNumber(userDTO.getJobNumber());
        user.setDateUpdate(LocalDateTime.now());

        // 超级管理员可修改角色和医院
        if (RoleConstants.SUPER_ADMIN.equals(currentRole)) {
            if (userDTO.getRoleCode() != null) {
                user.setRoleCode(userDTO.getRoleCode());
                user.setLevel(RoleConstants.SUPER_ADMIN.equals(userDTO.getRoleCode()) ? 1 : 0);
            }
            if (userDTO.getHospitalId() != null) {
                user.setUnit(userDTO.getHospitalId());
            }
        }

        userMapper.updateById(user);
        log.info("编辑用户: id={}, username={}", id, user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Boolean isActive) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        user.setIsActive(isActive);
        user.setDateUpdate(LocalDateTime.now());
        userMapper.updateById(user);

        // 禁用时立即失效 Token
        if (!isActive) {
            try {
                StpUtil.kickout(id);
            } catch (Exception e) {
                log.warn("踢出用户 Token 失败: {}", e.getMessage());
            }
        }

        log.info("更新用户状态: id={}, isActive={}", id, isActive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        String newPassword = generateRandomPassword();
        user.setPassword(PASSWORD_ENCODER.encode(newPassword));
        user.setPasswordChangedAt(null); // 重置后标记为需要修改
        user.setDateUpdate(LocalDateTime.now());
        userMapper.updateById(user);

        // 使当前 Token 失效
        try {
            StpUtil.kickout(id);
        } catch (Exception e) {
            log.warn("踢出用户 Token 失败: {}", e.getMessage());
        }

        log.info("重置用户密码: id={}, username={}", id, user.getUsername());
        return newPassword;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        user.setIsDeleted(1);
        user.setIsActive(false);
        user.setDateUpdate(LocalDateTime.now());
        userMapper.updateById(user);

        // 使 Token 失效
        try {
            StpUtil.kickout(id);
        } catch (Exception e) {
            log.warn("踢出用户 Token 失败: {}", e.getMessage());
        }

        log.info("删除用户: id={}, username={}", id, user.getUsername());
    }

    @Override
    public UserInfoVO getProfile() {
        long userId = SecurityUtil.getCurrentUserId();
        return getDetail(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UserDTO userDTO) {
        long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        // 个人中心只能修改部分字段
        user.setName(userDTO.getRealName());
        user.setSex(userDTO.getSex());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setProfessional(userDTO.getProfessional());
        user.setJobNumber(userDTO.getJobNumber());
        user.setDateUpdate(LocalDateTime.now());

        userMapper.updateById(user);
        log.info("更新个人信息: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        // 校验新密码和确认密码一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次输入的密码不一致");
        }

        // 校验密码复杂度：必须同时包含数字和字母
        if (!dto.getNewPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,30}$")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "密码必须包含数字和字母，长度 6-30 位");
        }

        long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        // 校验旧密码
        if (!PASSWORD_ENCODER.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "旧密码不正确");
        }

        user.setPassword(PASSWORD_ENCODER.encode(dto.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setDateUpdate(LocalDateTime.now());
        userMapper.updateById(user);

        // 使当前 Token 失效，强制重新登录
        StpUtil.logout();

        log.info("用户修改密码: userId={}", userId);
    }

    // ==================== 私有方法 ====================

    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_EXIST, "用户名已存在");
        }
    }

    private String generateRandomPassword() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        // 确保至少包含一个字母和一个数字
        sb.append(chars.charAt(random.nextInt(26))); // 字母
        sb.append(chars.charAt(26 + random.nextInt(10))); // 数字
        for (int i = 2; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private UserInfoVO convertToVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(HashidsUtil.encode(user.getId()));
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getName());
        vo.setSex(user.getSex());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setDepartment(user.getDepartment());
        vo.setProfessional(user.getProfessional());
        vo.setJobNumber(user.getJobNumber());
        vo.setLastLogin(user.getLastLogin());

        // 角色
        String roleCode = user.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            roleCode = (user.getLevel() != null && user.getLevel() == 1)
                    ? RoleConstants.SUPER_ADMIN : RoleConstants.DOCTOR;
        }
        vo.setRoleCode(roleCode);
        vo.setRoleName(getRoleName(roleCode));

        // 医院
        if (user.getUnit() != null && !user.getUnit().isEmpty()) {
            try {
                long unitId = Long.parseLong(user.getUnit());
                vo.setHospitalId(HashidsUtil.encode(unitId));
            } catch (NumberFormatException e) {
                vo.setHospitalId(user.getUnit());
            }
        }

        return vo;
    }

    private String getRoleName(String roleCode) {
        return switch (roleCode) {
            case RoleConstants.SUPER_ADMIN -> "超级管理员";
            case RoleConstants.HOSPITAL_ADMIN -> "医院管理员";
            case RoleConstants.DOCTOR -> "普通医生";
            case RoleConstants.PARENT -> "家长";
            default -> "未知角色";
        };
    }
}
