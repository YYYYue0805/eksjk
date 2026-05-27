package com.eksjk.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 VO
 *
 * @author eksjk
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 认证 Token */
    private String token;

    /** 用户 ID（Hashids 编码） */
    private String userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色标识 */
    private String roleCode;

    /** 所属医院 ID（Hashids 编码） */
    private String hospitalId;

    /** 所属医院名称 */
    private String hospitalName;

    /** 是否需要强制修改密码（首次登录/默认密码） */
    private Boolean passwordNeedChange;

    /** 是否需要提醒修改密码（超过 6 个月） */
    private Boolean passwordExpireSoon;
}
