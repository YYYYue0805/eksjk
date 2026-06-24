package com.eksjk.model.dto;

import lombok.Data;

/**
 * 审核查询 DTO
 *
 * @author eksjk
 */
@Data
public class AuditQueryDTO {

    /** 病例编号（模糊搜索） */
    private String caseNum;

    /** 患者姓名（模糊搜索） */
    private String name;

    /** 审核状态过滤 */
    private String auditStatus;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 20;
}
