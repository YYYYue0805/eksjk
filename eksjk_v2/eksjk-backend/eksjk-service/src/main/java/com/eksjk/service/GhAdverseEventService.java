package com.eksjk.service;

import com.eksjk.model.dto.GhAdverseEventDTO;
import com.eksjk.model.vo.GhAdverseEventVO;

import java.util.List;

/**
 * GH不良事件服务接口
 *
 * @author eksjk
 */
public interface GhAdverseEventService {

    /**
     * 按患者ID查询不良事件列表
     */
    List<GhAdverseEventVO> listByPatientId(Long patientId);

    /**
     * 查询不良事件详情
     */
    GhAdverseEventVO getDetail(Long id);

    /**
     * 新增不良事件
     */
    void create(GhAdverseEventDTO dto);

    /**
     * 编辑不良事件
     */
    void update(Long id, GhAdverseEventDTO dto);

    /**
     * 删除不良事件（逻辑删除）
     */
    void delete(Long id);
}
