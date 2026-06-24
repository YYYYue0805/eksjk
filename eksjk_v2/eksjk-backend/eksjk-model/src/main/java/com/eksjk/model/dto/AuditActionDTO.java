package com.eksjk.model.dto;

import lombok.Data;

/**
 * 审核操作 DTO
 *
 * @author eksjk
 */
@Data
public class AuditActionDTO {

    /** 操作: approved / rejected */
    private String action;

    /** 审核意见（驳回时必填） */
    private String comment;
}
