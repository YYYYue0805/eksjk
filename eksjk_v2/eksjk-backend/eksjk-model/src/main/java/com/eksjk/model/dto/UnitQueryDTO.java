package com.eksjk.model.dto;

import com.eksjk.common.result.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机构查询 DTO
 *
 * @author eksjk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnitQueryDTO extends PageRequest {

    /** 关键词（名称/编码模糊搜索） */
    private String keyword;

    /** 机构状态（1=启用，0=禁用） */
    private Integer status;

    /** 机构级别 */
    private String unitLevel;

    /** 机构类型 */
    private String unitType;
}
