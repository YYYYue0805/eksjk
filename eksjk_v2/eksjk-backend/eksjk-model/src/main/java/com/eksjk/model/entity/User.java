package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 兼容 V1 Django AbstractUser 表结构（表名 login_user），
 * 同时扩展 V2 新增字段（均为可空或有默认值）。
 * </p>
 *
 * @author eksjk
 */
@Data
@TableName("login_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    // ==================== V1 Django AbstractUser 字段 ====================

    /** 登录密码（BCrypt 加密） */
    private String password;

    /** 最后登录时间 */
    private LocalDateTime lastLogin;

    /** 是否超级管理员（V1 Django 字段，V2 不使用，保留兼容） */
    private Boolean isSuperuser;

    /** 登录用户名（唯一） */
    private String username;

    /** 名（V1 Django 字段，V2 不使用，保留兼容） */
    private String firstName;

    /** 姓（V1 Django 字段，V2 不使用，保留兼容） */
    private String lastName;

    /** 邮箱 */
    private String email;

    /** 是否为 staff（V1 Django 字段，V2 不使用，保留兼容） */
    private Boolean isStaff;

    /** 账号是否启用（1=启用，0=禁用） */
    private Boolean isActive;

    /** 注册时间 */
    private LocalDateTime dateJoined;

    // ==================== V1 自定义字段 ====================

    /** 真实姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 所属单位 ID（字符串类型，兼容 V1） */
    private String unit;

    /** 用户级别（V1: 0=普通用户, 1=管理员） */
    private Integer level;

    /** 职称编码 */
    private String professional;

    /** 密码修改时间 */
    private LocalDateTime dateUpdate;

    /** 科室 */
    private String department;

    // ==================== V2 新增字段（可空） ====================

    /**
     * V2 角色标识
     * super_admin / hospital_admin / doctor / parent
     */
    private String roleCode;

    /** 密码最后修改时间（V2 新增，用于密码过期判断） */
    private LocalDateTime passwordChangedAt;

    /** 微信 OpenID（小程序登录绑定） */
    private String wxOpenid;

    /** 手机号 */
    private String phone;

    /** 工号 */
    private String jobNumber;

    /** 逻辑删除标志（0=未删除，1=已删除） */
    @TableLogic
    private Integer isDeleted;
}
