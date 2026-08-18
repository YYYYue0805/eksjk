package com.eksjk.service;

import com.eksjk.model.vo.InformedConsentVO;

import java.util.List;

/**
 * 知情同意书服务
 *
 * @author eksjk
 */
public interface InformedConsentService {

    /**
     * 按患者查询知情同意书列表
     *
     * @param patientId 患者ID（内部自增ID）
     */
    List<InformedConsentVO> listByPatient(Long patientId);
}
