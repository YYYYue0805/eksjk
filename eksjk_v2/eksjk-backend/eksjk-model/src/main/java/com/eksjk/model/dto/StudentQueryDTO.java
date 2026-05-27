package com.eksjk.model.dto;

import lombok.Data;

/**
 * 学生查询条件 DTO
 *
 * @author eksjk
 */
@Data
public class StudentQueryDTO {

    /** 编号 */
    private String num;

    /** 班级 */
    private String sclass;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 20;
}
