package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 医疗机构（单位）实体类
 * <p>
 * 兼容 V1 表结构（表名 login_unit），
 * 同时扩展 V2 新增字段用于更丰富的机构管理。
 * </p>
 *
 * @author eksjk
 */
@Data
@TableName("login_unit")
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    // ==================== V1 字段 ====================

    /** 单位名称 */
    private String unitName;

    /** 删除标志（V1: '0'=已删除, '1'=有效数据） */
    private String delFlg;

    // ==================== V2 新增字段（可空） ====================

    /** 机构简称 */
    private String unitShortName;

    /** 机构编码（唯一） */
    private String unitCode;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 联系地址 */
    private String contactAddress;

    /** 邮政编码 */
    private String zipCode;

    /** 机构级别（三甲/三乙/二甲等） */
    private String unitLevel;

    /** 机构类型（综合医院/专科医院/社区卫生服务中心等） */
    private String unitType;

    /** 所在省 */
    private String province;

    /** 所在市 */
    private String city;

    /** 所在区 */
    private String district;

    /** 启用状态（1=启用，0=禁用） */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 创建人 ID */
    private Long creatorId;
}
