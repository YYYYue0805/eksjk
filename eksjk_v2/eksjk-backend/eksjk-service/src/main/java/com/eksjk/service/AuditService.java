package com.eksjk.service;

import com.eksjk.common.result.PageResult;
import com.eksjk.model.dto.AuditActionDTO;
import com.eksjk.model.dto.AuditQueryDTO;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.model.vo.PatientVO;

import java.util.Map;

/**
 * 审核发放服务接口
 *
 * @author eksjk
 */
public interface AuditService {

    /**
     * 分页查询待审核/待发放/已发放的患者列表
     */
    PageResult<PatientVO> listPatients(AuditQueryDTO queryDTO);

    /**
     * 审核通过患者基线信息
     */
    void approvePatient(Long patientId, AuditActionDTO dto);

    /**
     * 驳回患者基线信息
     */
    void rejectPatient(Long patientId, AuditActionDTO dto);

    /**
     * 发放患者基线信息
     */
    void releasePatient(Long patientId);

    /**
     * 分页查询待审核/待发放/已发放的随访列表
     */
    PageResult<FollowUpVO> listFollowUps(AuditQueryDTO queryDTO);

    /**
     * 审核通过随访信息
     */
    void approveFollowUp(Long followUpId, AuditActionDTO dto);

    /**
     * 驳回随访信息
     */
    void rejectFollowUp(Long followUpId, AuditActionDTO dto);

    /**
     * 发放随访信息
     */
    void releaseFollowUp(Long followUpId);

    /**
     * 审核发放统计
     */
    Map<String, Object> stats();
}
