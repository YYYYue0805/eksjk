package com.eksjk.common.result;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @author eksjk
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码（从 1 开始） */
    @Min(value = 1, message = "页码最小为 1")
    private int pageNum = 1;

    /** 每页条数 */
    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 500, message = "每页条数最大为 500")
    private int pageSize = 10;

    /** 排序字段 */
    private String orderBy;

    /** 排序方向（asc/desc） */
    private String orderDirection = "desc";

    /**
     * 获取 MyBatis-Plus 分页偏移量
     */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}
