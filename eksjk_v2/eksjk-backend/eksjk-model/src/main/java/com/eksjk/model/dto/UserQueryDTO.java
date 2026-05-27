package com.eksjk.model.dto;

import com.eksjk.common.result.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询 DTO
 *
 * @author eksjk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageRequest {

    /** 关键词（用户名/姓名模糊搜索） */
    private String keyword;

    /** 角色标识 */
    private String roleCode;

    /** 所属医院 ID */
    private String hospitalId;

    /** 账号状态（true=启用，false=禁用） */
    private Boolean isActive;
}
