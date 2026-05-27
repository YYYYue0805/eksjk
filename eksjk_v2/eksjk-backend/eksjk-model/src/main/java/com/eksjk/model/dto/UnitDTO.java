package com.eksjk.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 机构新增/编辑 DTO
 *
 * @author eksjk
 */
@Data
public class UnitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 机构名称 */
    @NotBlank(message = "机构名称不能为空")
    private String unitName;

    /** 机构简称 */
    private String unitShortName;

    /** 机构编码 */
    private String unitCode;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 联系地址 */
    private String contactAddress;

    /** 邮政编码 */
    private String zipCode;

    /** 机构级别 */
    private String unitLevel;

    /** 机构类型 */
    private String unitType;

    /** 所在省 */
    private String province;

    /** 所在市 */
    private String city;

    /** 所在区 */
    private String district;

    /** 备注 */
    private String remark;
}
