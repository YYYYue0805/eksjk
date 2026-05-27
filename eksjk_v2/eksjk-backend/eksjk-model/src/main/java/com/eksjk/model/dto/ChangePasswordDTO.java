package com.eksjk.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码 DTO
 *
 * @author eksjk
 */
@Data
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度为 6-30 位")
    private String newPassword;

    /** 确认新密码 */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
