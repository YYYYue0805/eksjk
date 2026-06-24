package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.EltmCaseMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.EltmCase;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.DiagnosisService;
import com.eksjk.service.PatientService;
import com.eksjk.service.diagnosis.DiagnosisEngine;
import com.eksjk.service.diagnosis.DiagnosisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ELTM 智能诊断服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final PatientMapper patientMapper;
    private final EltmCaseMapper eltmCaseMapper;
    private final PatientService patientService;

    private final DiagnosisEngine engine = new DiagnosisEngine();

    @Override
    public DiagnosisResult diagnoseSingle(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new RuntimeException("患者不存在");
        }
        if (!"10000007".equals(patient.getDisClass())) {
            throw new RuntimeException("仅支持ELTM患者的诊断");
        }

        EltmCase eltmCase = eltmCaseMapper.selectOne(
                new LambdaQueryWrapper<EltmCase>().eq(EltmCase::getPatientId, patientId));

        String rawData = eltmCase != null ? eltmCase.getRawData() : null;
        DiagnosisResult result = engine.diagnose(rawData, patient);

        // 更新 EltmCase 诊断状态
        if (eltmCase != null) {
            updateEltmCaseStatus(eltmCase, result);
        }
        // 同步更新 Patient 诊断状态
        patient.setDiagnosisStatus(result.getConfidence().equals("HIGH") ? "auto_classified"
                : result.hasSuggestion() ? "suggested" : "uncertain");
        patient.setModifyTime(LocalDateTime.now());
        patientMapper.updateById(patient);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> diagnoseBatch() {
        // 查找所有未分类的 ELTM 患者（disClass=10000007, delFlg=1, diagnosis_status 为 null 或 unclassified）
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getDisClass, "10000007")
                .eq(Patient::getDelFlg, "1")
                .and(w -> w.isNull(Patient::getDiagnosisStatus)
                        .or().eq(Patient::getDiagnosisStatus, "unclassified")
                        .or().eq(Patient::getDiagnosisStatus, "uncertain"));

        List<Patient> patients = patientMapper.selectList(wrapper);
        log.info("开始批量诊断ELTM患者: 共 {} 条待诊断", patients.size());

        int autoClassified = 0;
        int suggested = 0;
        int uncertain = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (Patient patient : patients) {
            try {
                EltmCase eltmCase = eltmCaseMapper.selectOne(
                        new LambdaQueryWrapper<EltmCase>().eq(EltmCase::getPatientId, patient.getId()));

                String rawData = eltmCase != null ? eltmCase.getRawData() : null;
                DiagnosisResult result = engine.diagnose(rawData, patient);

                if (result.isAutoClassifiable()) {
                    // 高置信度 → 自动重分类
                    patientService.reclassify(patient.getId(), result.getTargetDisClass());
                    autoClassified++;

                    if (eltmCase != null) {
                        eltmCase.setDiagnosisStatus("auto_classified");
                        eltmCase.setSuggestedDisClass(result.getTargetDisClass());
                        eltmCase.setDiagnosisNote(result.getNote());
                        eltmCaseMapper.updateById(eltmCase);
                    }
                } else if (result.hasSuggestion()) {
                    // 中置信度 → 记录建议
                    patient.setDiagnosisStatus("suggested");
                    patient.setModifyTime(LocalDateTime.now());
                    patientMapper.updateById(patient);

                    if (eltmCase != null) {
                        eltmCase.setDiagnosisStatus("suggested");
                        eltmCase.setSuggestedDisClass(result.getTargetDisClass());
                        eltmCase.setDiagnosisNote(result.getNote());
                        eltmCaseMapper.updateById(eltmCase);
                    }
                    suggested++;
                } else {
                    // 低置信度 → 标记待审核
                    patient.setDiagnosisStatus("uncertain");
                    patient.setModifyTime(LocalDateTime.now());
                    patientMapper.updateById(patient);

                    if (eltmCase != null) {
                        eltmCase.setDiagnosisStatus("uncertain");
                        eltmCase.setDiagnosisNote(result.getNote());
                        eltmCaseMapper.updateById(eltmCase);
                    }
                    uncertain++;
                }

                details.add(Map.of(
                        "patientId", patient.getId(),
                        "name", patient.getName() != null ? patient.getName() : "",
                        "score", result.getScore(),
                        "confidence", result.getConfidence(),
                        "targetDisClass", result.getTargetDisClass() != null ? result.getTargetDisClass() : "",
                        "targetDisName", result.getTargetDisName() != null ? result.getTargetDisName() : ""
                ));
            } catch (Exception e) {
                log.error("诊断患者失败: patientId={}", patient.getId(), e);
                details.add(Map.of(
                        "patientId", patient.getId(),
                        "name", patient.getName() != null ? patient.getName() : "",
                        "error", e.getMessage()
                ));
            }
        }

        log.info("ELTM批量诊断完成: autoClassified={}, suggested={}, uncertain={}", autoClassified, suggested, uncertain);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", patients.size());
        summary.put("autoClassified", autoClassified);
        summary.put("suggested", suggested);
        summary.put("uncertain", uncertain);
        summary.put("details", details);
        return summary;
    }

    private void updateEltmCaseStatus(EltmCase eltmCase, DiagnosisResult result) {
        if (result.isAutoClassifiable()) {
            eltmCase.setDiagnosisStatus("auto_classified");
            eltmCase.setSuggestedDisClass(result.getTargetDisClass());
        } else if (result.hasSuggestion()) {
            eltmCase.setDiagnosisStatus("suggested");
            eltmCase.setSuggestedDisClass(result.getTargetDisClass());
        } else {
            eltmCase.setDiagnosisStatus("uncertain");
        }
        eltmCase.setDiagnosisNote(result.getNote());
        eltmCaseMapper.updateById(eltmCase);
    }
}
