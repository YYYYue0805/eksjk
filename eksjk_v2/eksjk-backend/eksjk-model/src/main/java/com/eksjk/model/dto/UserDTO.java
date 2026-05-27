package com.eksjk.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户新增/编辑 DTO
 *
 * @author eksjk
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /** 性别 */
    private String sex;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色标识 */
    private String roleCode;

    /** 所属医院 ID（Hashids 编码） */
    private String hospitalId;

    /** 科室 */
    private String department;

    /** 职称编码 */
    private String professional;

    /** 工号 */
    private String jobNumber;

    /** 初始密码（新增时可选，为空则自动生成） */
    private String password;
}
