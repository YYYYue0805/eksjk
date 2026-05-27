package com.eksjk.model.dto;

import lombok.Data;

/**
 * 小程序微信登录 DTO
 *
 * @author eksjk
 */
@Data
public class WxLoginDTO {

    /** 微信 login 返回的 code */
    private String code;

    /** 手机号（首次登录时提供） */
    private String phoneNum;
}
