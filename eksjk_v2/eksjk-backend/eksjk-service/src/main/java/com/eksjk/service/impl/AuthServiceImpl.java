package com.eksjk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.common.constant.CommonConstants;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.ErrorCode;
import com.eksjk.common.util.DateUtil;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.LoginDTO;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.LoginVO;
import com.eksjk.model.vo.UserInfoVO;
import com.eksjk.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO loginDTO, String ip) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 2. 检查账号状态
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 3. 校验密码
        // V1 使用 Django 的 pbkdf2_sha256 加密，V2 使用 BCrypt
        // 兼容策略：先尝试 BCrypt 校验，如果失败且密码格式为 Django 格式则尝试 Django 校验
        boolean passwordMatch = false;
        try {
            passwordMatch = PASSWORD_ENCODER.matches(loginDTO.getPassword(), user.getPassword());
        } catch (Exception e) {
            // BCrypt 校验异常，可能是 V1 的 Django 密码格式
            log.debug("BCrypt 校验异常，尝试其他方式: {}", e.getMessage());
        }

        // 如果 BCrypt 校验失败，检查是否为 Django pbkdf2_sha256 格式
        if (!passwordMatch && user.getPassword() != null && user.getPassword().startsWith("pbkdf2_sha256$")) {
            passwordMatch = verifyDjangoPassword(loginDTO.getPassword(), user.getPassword());
            // 如果 Django 密码校验通过，将密码升级为 BCrypt 格式
            if (passwordMatch) {
                user.setPassword(PASSWORD_ENCODER.encode(loginDTO.getPassword()));
                userMapper.updateById(user);
                log.info("用户 {} 密码已从 Django 格式升级为 BCrypt 格式", user.getUsername());
            }
        }

        if (!passwordMatch) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 4. 确定角色标识
        String roleCode = resolveRoleCode(user);

        // 5. Sa-Token 登录，携带角色和医院信息
        StpUtil.login(user.getId());
        StpUtil.getSession()
                .set("roleCode", roleCode)
                .set("hospitalId", user.getUnit() != null ? user.getUnit() : "")
                .set("username", user.getUsername())
                .set("realName", user.getName() != null ? user.getName() : "");

        String token = StpUtil.getTokenValue();

        // 6. 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        // 7. 记录登录日志
        log.info("用户登录成功: username={}, role={}, ip={}", user.getUsername(), roleCode, ip);

        // 8. 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(HashidsUtil.encode(user.getId()));
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getName());
        loginVO.setRoleCode(roleCode);

        // 设置医院信息
        if (user.getUnit() != null && !user.getUnit().isEmpty()) {
            try {
                long unitId = Long.parseLong(user.getUnit());
                loginVO.setHospitalId(HashidsUtil.encode(unitId));
            } catch (NumberFormatException e) {
                loginVO.setHospitalId(user.getUnit());
            }
        }

        // 9. 密码策略检查
        loginVO.setPasswordNeedChange(isPasswordNeedChange(user));
        loginVO.setPasswordExpireSoon(isPasswordExpireSoon(user));

        return loginVO;
    }

    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "用户不存在");
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(HashidsUtil.encode(user.getId()));
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getName());
        vo.setSex(user.getSex());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setRoleCode(resolveRoleCode(user));
        vo.setRoleName(getRoleName(resolveRoleCode(user)));
        vo.setDepartment(user.getDepartment());
        vo.setProfessional(user.getProfessional());
        vo.setProfessionalName(getProfessionalName(user.getProfessional()));
        vo.setJobNumber(user.getJobNumber());
        vo.setLastLogin(user.getLastLogin());

        // 医院信息
        if (user.getUnit() != null && !user.getUnit().isEmpty()) {
            try {
                long unitId = Long.parseLong(user.getUnit());
                vo.setHospitalId(HashidsUtil.encode(unitId));
            } catch (NumberFormatException e) {
                vo.setHospitalId(user.getUnit());
            }
        }

        // 密码策略
        vo.setPasswordNeedChange(isPasswordNeedChange(user));
        vo.setPasswordExpireSoon(isPasswordExpireSoon(user));

        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 解析用户角色标识
     * V2 优先使用 roleCode 字段，如果为空则根据 V1 的 level 字段映射
     */
    private String resolveRoleCode(User user) {
        if (user.getRoleCode() != null && !user.getRoleCode().isEmpty()) {
            return user.getRoleCode();
        }
        // V1 兼容：level=1 → super_admin, level=0 → doctor
        if (user.getLevel() != null && user.getLevel() == 1) {
            return RoleConstants.SUPER_ADMIN;
        }
        return RoleConstants.DOCTOR;
    }

    /**
     * 判断是否需要强制修改密码（首次登录/默认密码）
     */
    private boolean isPasswordNeedChange(User user) {
        // 如果密码修改时间为空，说明从未修改过密码
        if (user.getPasswordChangedAt() == null && user.getDateUpdate() == null) {
            return true;
        }
        // 检查是否使用默认密码（通过 BCrypt 匹配）
        try {
            return PASSWORD_ENCODER.matches(CommonConstants.DEFAULT_PASSWORD, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断密码是否即将过期（超过 6 个月）
     */
    private boolean isPasswordExpireSoon(User user) {
        LocalDateTime passwordTime = user.getPasswordChangedAt();
        if (passwordTime == null) {
            passwordTime = user.getDateUpdate();
        }
        if (passwordTime == null) {
            return false;
        }
        return DateUtil.isExpired(passwordTime, CommonConstants.PASSWORD_EXPIRE_DAYS);
    }

    /**
     * 验证 Django pbkdf2_sha256 格式密码
     * 格式：pbkdf2_sha256$iterations$salt$hash
     */
    private boolean verifyDjangoPassword(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            String algorithm = parts[0];
            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String hash = parts[3];

            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            java.security.spec.KeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    rawPassword.toCharArray(), salt.getBytes(), iterations, 256);
            byte[] derived = factory.generateSecret(spec).getEncoded();
            String computedHash = java.util.Base64.getEncoder().encodeToString(derived);

            return hash.equals(computedHash);
        } catch (Exception e) {
            log.warn("Django 密码校验异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取角色中文名称
     */
    private String getRoleName(String roleCode) {
        return switch (roleCode) {
            case RoleConstants.SUPER_ADMIN -> "超级管理员";
            case RoleConstants.HOSPITAL_ADMIN -> "医院管理员";
            case RoleConstants.DOCTOR -> "普通医生";
            case RoleConstants.PARENT -> "家长";
            default -> "未知角色";
        };
    }

    /**
     * 获取职称中文名称
     */
    private String getProfessionalName(String code) {
        if (code == null) return null;
        return switch (code) {
            case "10040001" -> "助理医师";
            case "10040002" -> "医师";
            case "10040003" -> "主治医师";
            case "10040004" -> "副主任医师";
            case "10040005" -> "主任医师";
            default -> null;
        };
    }
}
