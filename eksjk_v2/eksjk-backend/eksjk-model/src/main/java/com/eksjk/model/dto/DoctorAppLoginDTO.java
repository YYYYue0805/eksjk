package com.eksjk.model.dto;

import lombok.Data;

/**
 * 医生端小程序登录 DTO
 *
 * @author eksjk
 */
@Data
public class DoctorAppLoginDTO {

    /** 微信 login 返回的 code（微信登录时传入） */
    private String code;

    /** 登录用户名（账号密码登录时传入） */
    private String username;

    /** 登录密码（账号密码登录时传入） */
    private String password;

    /** 微信 OpenID（绑定账号时传入） */
    private String openid;
}
