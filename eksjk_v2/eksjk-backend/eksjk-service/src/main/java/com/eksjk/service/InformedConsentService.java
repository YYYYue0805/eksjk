package com.eksjk.service;

import com.eksjk.model.vo.InformedConsentVO;

import java.util.List;
import java.util.Map;

/**
 * 知情同意书服务
 *
 * @author eksjk
 */
public interface InformedConsentService {

    /**
     * 按患者ID查询知情同意书列表
     *
     * @param patientId 患者ID（内部自增ID）
     */
    List<InformedConsentVO> listByPatientId(Long patientId);

    /**
     * 查询同步状态（最近同步时间与总记录数）
     */
    Map<String, Object> getSyncStatus();
}
