package com.eksjk.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息 VO（用于 GET /api/auth/info 和个人中心）
 *
 * @author eksjk
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID（Hashids 编码） */
    private String id;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private String sex;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 角色标识 */
    private String roleCode;

    /** 角色名称（中文） */
    private String roleName;

    /** 所属医院 ID（Hashids 编码） */
    private String hospitalId;

    /** 所属医院名称 */
    private String hospitalName;

    /** 科室 */
    private String department;

    /** 职称编码 */
    private String professional;

    /** 职称名称 */
    private String professionalName;

    /** 工号 */
    private String jobNumber;

    /** 最后登录时间 */
    private LocalDateTime lastLogin;

    /** 是否需要强制修改密码 */
    private Boolean passwordNeedChange;

    /** 是否需要提醒修改密码 */
    private Boolean passwordExpireSoon;
}
