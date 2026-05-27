package com.eksjk.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 小程序登录响应 VO
 *
 * @author eksjk
 */
@Data
public class WxLoginVO {

    /** Sa-Token */
    private String token;

    /** 是否新用户（需要填写手机号） */
    @JsonProperty("isNewUser")
    private boolean isNewUser;

    /** 是否已同步数据 */
    @JsonProperty("isSynced")
    private boolean isSynced;

    /** OpenID */
    private String openid;
}
