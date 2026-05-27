package com.eksjk.model.vo;

import lombok.Data;

import java.util.Map;

/**
 * 医生端小程序登录响应 VO
 *
 * @author eksjk
 */
@Data
public class DoctorAppLoginVO {

    /** Sa-Token */
    private String token;

    /** 是否需要绑定账号（微信登录时，若未绑定则为 true） */
    private Boolean needBind;

    /** 微信 OpenID（needBind=true 时返回，用于后续绑定） */
    private String openid;

    /** 医生基本信息 */
    private Map<String, Object> doctorInfo;
}
