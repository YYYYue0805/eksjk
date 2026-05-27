package com.eksjk.service;

import com.eksjk.common.result.PageResult;
import com.eksjk.model.dto.UnitDTO;
import com.eksjk.model.dto.UnitQueryDTO;
import com.eksjk.model.entity.Unit;

import java.util.List;
import java.util.Map;

/**
 * 医疗机构服务接口
 *
 * @author eksjk
 */
public interface UnitService {

    /**
     * 分页查询机构列表
     */
    PageResult<Unit> queryPage(UnitQueryDTO queryDTO);

    /**
     * 获取机构详情
     */
    Unit getById(Long id);

    /**
     * 新增机构
     */
    void create(UnitDTO unitDTO);

    /**
     * 编辑机构
     */
    void update(Long id, UnitDTO unitDTO);

    /**
     * 启用/禁用机构
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除机构
     */
    void delete(Long id);

    /**
     * 获取机构选项列表（轻量级，供下拉选择）
     */
    List<Map<String, Object>> getOptions(String keyword);

    /**
     * 获取机构关联统计（用户数、病例数）
     */
    Map<String, Long> getStatistics(Long id);
}
