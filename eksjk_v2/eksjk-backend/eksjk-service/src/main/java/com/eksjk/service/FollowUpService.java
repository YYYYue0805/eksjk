package com.eksjk.service;

import com.eksjk.model.dto.FollowUpDTO;
import com.eksjk.model.vo.FollowUpVO;

import java.util.List;

/**
 * 随访管理服务接口
 *
 * @author eksjk
 */
public interface FollowUpService {

    /**
     * 获取某患者的随访列表（按时间倒序）
     */
    List<FollowUpVO> listByPatientId(Long patientId);

    /**
     * 获取随访详情
     */
    FollowUpVO getDetail(Long id);

    /**
     * 新增随访记录
     */
    void create(FollowUpDTO followUpDTO);

    /**
     * 编辑随访记录
     */
    void update(Long id, FollowUpDTO followUpDTO);

    /**
     * 删除随访记录（逻辑删除）
     */
    void delete(Long id);
}
