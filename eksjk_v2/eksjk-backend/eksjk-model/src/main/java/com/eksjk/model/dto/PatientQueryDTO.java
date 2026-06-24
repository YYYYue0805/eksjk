package com.eksjk.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 患者查询 DTO
 *
 * @author eksjk
 */
@Data
public class PatientQueryDTO {

    /** 疾病分类编码 */
    private String disClass;

    /** 病例编号 */
    private String caseNum;

    /** 病历号 */
    private String medrecNum;

    /** 患者姓名（模糊搜索） */
    private String name;

    /** 性别 */
    private String sex;

    /** 上传时间范围 - 开始 */
    private LocalDateTime startTime;

    /** 上传时间范围 - 结束 */
    private LocalDateTime endTime;

    /** 审核状态过滤 */
    private String auditStatus;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 20;
}
