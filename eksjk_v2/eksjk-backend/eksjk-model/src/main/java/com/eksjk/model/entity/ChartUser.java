package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 微信小程序用户实体类
 * <p>兼容 V1 表结构（表名 xcx_chartuser）</p>
 *
 * @author eksjk
 */
@Data
@TableName("xcx_chartuser")
public class ChartUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信 OpenID */
    private String openid;

    /** Session Key */
    @TableField("`key`")
    private String key;

    /** 病历号 */
    private String medrecNum;

    /** 手机号 */
    private String phoneNum;

    /** 绑定医生ID */
    private String doctor;

    /** 是否同步过数据（'0'=未同步, '1'=已同步） */
    private String isTongb;

    /** 删除标志（'0'=已删除, '1'=有效数据） */
    private String delFlg;

    /** 是否新用户标志 */
    private String newUserFlag;

    /** 个人头像（Base64 或 URL） */
    private String myselfPicture;

    /** 联系人姓名 */
    private String contactsName;

    /** 联系电话 */
    private String contactsNum;

    /** 邮箱 */
    private String pEmial;

    /** 身份证号 */
    private String idcard;

    /** 籍贯/家庭住址 */
    private String natPla;
}
