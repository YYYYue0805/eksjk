package com.eksjk.service;

import com.eksjk.common.result.PageResult;
import com.eksjk.model.dto.PatientDTO;
import com.eksjk.model.dto.PatientQueryDTO;
import com.eksjk.model.vo.PatientVO;

/**
 * 患者病例管理服务接口
 *
 * @author eksjk
 */
public interface PatientService {

    /**
     * 分页查询病例列表（根据角色自动过滤数据范围）
     */
    PageResult<PatientVO> queryPage(PatientQueryDTO queryDTO);

    /**
     * 获取病例详情（含疾病专项数据）
     */
    PatientVO getDetail(Long id);

    /**
     * 新建病例（自动生成病例编号，同时创建疾病子表记录）
     */
    String create(PatientDTO patientDTO);

    /**
     * 编辑病例（更新主表和疾病子表）
     */
    void update(Long id, PatientDTO patientDTO);

    /**
     * 删除病例（逻辑删除）
     */
    void delete(Long id);

    /**
     * 导出病例 Excel
     */
    byte[] exportExcel(PatientQueryDTO queryDTO);

    /**
     * 获取工作台统计数据
     */
    java.util.Map<String, Object> getDashboardStats();
}
