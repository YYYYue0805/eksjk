package com.eksjk.model.dto;

import lombok.Data;

/**
 * 小程序个人信息 DTO
 *
 * @author eksjk
 */
@Data
public class MiniAppProfileDTO {

    /** 联系人姓名 */
    private String contactsName;

    /** 联系电话 */
    private String contactsNum;

    /** 邮箱 */
    private String email;

    /** 身份证号 */
    private String idcard;

    /** 籍贯/家庭住址 */
    private String natPla;

    /** 个人头像 */
    private String myselfPicture;
}
