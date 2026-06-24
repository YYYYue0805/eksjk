package com.eksjk.service;

import com.eksjk.service.diagnosis.DiagnosisResult;

import java.util.Map;

/**
 * ELTM 智能诊断服务接口
 *
 * @author eksjk
 */
public interface DiagnosisService {

    /**
     * 对单个 ELTM 患者执行诊断
     * @param patientId 患者 ID
     * @return 诊断结果
     */
    DiagnosisResult diagnoseSingle(Long patientId);

    /**
     * 批量诊断所有未分类的 ELTM 患者
     * @return 统计结果 { total, autoClassified, suggested, uncertain, details: [...] }
     */
    Map<String, Object> diagnoseBatch();
}
