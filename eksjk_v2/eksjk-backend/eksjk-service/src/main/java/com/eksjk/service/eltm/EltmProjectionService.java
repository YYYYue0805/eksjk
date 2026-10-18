package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ELTM 外部数据投影编排
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmProjectionService {

    private final PatientMapper patientMapper;
    private final EltmProjectionTxService txService;
    private final EltmAttachmentService attachmentService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 全量重投影（可重复执行，只填空不覆盖）
     */
    public Map<String, Object> projectAll() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("投影任务正在执行中");
        }
        long start = System.currentTimeMillis();
        int success = 0;
        int skipped = 0;
        int failed = 0;
        int total = 0;
        try {
            List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                    .select(Patient::getId)
                    .isNotNull(Patient::getExternalId)
                    .eq(Patient::getDelFlg, "1"));
            total = patients.size();
            for (Patient patient : patients) {
                try {
                    int changed = txService.projectPatient(patient.getId());
                    if (changed > 0) {
                        success++;
                    } else {
                        skipped++;
                    }
                } catch (Exception e) {
                    failed++;
                    log.error("ELTM 投影失败 patientId={}", patient.getId(), e);
                }
            }
        } finally {
            running.set(false);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("changed", success);
        result.put("unchanged", skipped);
        result.put("failed", failed);
        result.put("costMs", System.currentTimeMillis() - start);
        return result;
    }

    /**
     * 按外部患者ID投影（同步流程调用）
     */
    public int projectByExternalId(Long externalId) {
        if (externalId == null) {
            return 0;
        }
        Patient patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .select(Patient::getId)
                .eq(Patient::getExternalId, externalId)
                .last("LIMIT 1"));
        if (patient == null) {
            return 0;
        }
        return txService.projectPatient(patient.getId());
    }

    /**
     * 按本地患者ID投影（单患者重跑）
     */
    public int projectByPatientId(Long patientId) {
        return txService.projectPatient(patientId);
    }

    /**
     * 全量迁移外部附件到本地文件存储
     */
    public Map<String, Object> projectAllAttachments() {
        return attachmentService.projectAll();
    }

    /**
     * 单患者迁移外部附件
     */
    public Map<String, Object> projectAttachmentsByPatientId(Long patientId) {
        return attachmentService.projectByPatientId(patientId);
    }
}
