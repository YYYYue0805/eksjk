package com.eksjk.service;

import com.eksjk.common.result.PageResult;
import com.eksjk.model.dto.StudentDTO;
import com.eksjk.model.dto.StudentQueryDTO;
import com.eksjk.model.vo.StudentVO;

import java.util.Map;

/**
 * 学校健康筛查服务接口
 *
 * @author eksjk
 */
public interface SchoolService {

    /**
     * 分页查询学生列表
     */
    PageResult<StudentVO> queryPage(StudentQueryDTO queryDTO);

    /**
     * 获取学生详情（含全部问卷数据）
     */
    StudentVO getDetail(Long id);

    /**
     * 新增学生
     */
    void create(StudentDTO studentDTO);

    /**
     * 编辑学生基本信息
     */
    void update(Long id, StudentDTO studentDTO);

    /**
     * 删除学生（逻辑删除）
     */
    void delete(Long id);

    /**
     * 保存单张问卷数据
     *
     * @param studentId 学生ID
     * @param type      问卷类型（cchkn/cbq/mqzyfs/qzhd/pmbl/sthd/smxg）
     * @param data      问卷数据
     */
    void saveQuestionnaire(Long studentId, String type, Map<String, Object> data);
}
