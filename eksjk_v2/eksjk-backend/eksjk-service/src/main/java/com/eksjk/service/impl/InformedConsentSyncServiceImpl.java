package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.InformedConsentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知情同意书外部同步服务实现（占位）
 * <p>
 * 对接 E路童萌 前先以 mock 数据跑通完整链路：
 * mock 记录按 eltm_id 匹配本系统患者，按 external_file_id 幂等 upsert。
 * 真实对接时替换 fetchFromExternal() 为 HTTP 调用即可。
 * </p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformedConsentSyncServiceImpl implements InformedConsentSyncService {

    private final InformedConsentMapper informedConsentMapper;
    private final PatientMapper patientMapper;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> syncFromExternal() {
        List<Map<String, String>> records = fetchFromExternal();
        log.info("开始同步知情同意书: 共 {} 条", records.size());

        int newCount = 0;
        int updatedCount = 0;
        int failedCount = 0;

        for (Map<String, String> rec : records) {
            try {
                String eltmId = rec.get("eltmId");
                Patient patient = patientMapper.selectOne(
                        new LambdaQueryWrapper<Patient>().eq(Patient::getEltmId, eltmId));
                if (patient == null) {
                    log.warn("知情同意书未匹配到患者: eltmId={}", eltmId);
                    failedCount++;
                    continue;
                }

                InformedConsent existing = informedConsentMapper.selectOne(
                        new LambdaQueryWrapper<InformedConsent>()
                                .eq(InformedConsent::getExternalFileId, rec.get("fileId")));
                if (existing == null) {
                    InformedConsent consent = buildConsent(patient, rec);
                    informedConsentMapper.insert(consent);
                    newCount++;
                } else {
                    applyFields(existing, rec);
                    existing.setSyncTime(LocalDateTime.now());
                    informedConsentMapper.updateById(existing);
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("同步知情同意书单条失败", e);
                failedCount++;
            }
        }

        log.info("知情同意书同步完成: total={}, new={}, updated={}, failed={}",
                records.size(), newCount, updatedCount, failedCount);
        Map<String, Object> result = new HashMap<>();
        result.put("total", records.size());
        result.put("newCount", newCount);
        result.put("updatedCount", updatedCount);
        result.put("failedCount", failedCount);
        return result;
    }

    /** 每天凌晨 2:00 定时同步 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledSync() {
        log.info("定时任务触发知情同意书同步");
        syncFromExternal();
    }

    /**
     * 从 E路童萌 拉取签署记录。
     * <p>
     * 占位实现：返回 mock 数据。真实对接时改为调用
     * {@code eksjk.informed-consent.sync-url} 并解析响应（含 OAuth/Token 认证）。
     * </p>
     */
    private List<Map<String, String>> fetchFromExternal() {
        return List.of(
                Map.of(
                        "eltmId", "eltm_mock_001",
                        "fileId", "file_mock_001",
                        "fileName", "知情同意书_张三.pdf",
                        "ossUrl", "https://mock-oss.example.com/consent/file_mock_001.pdf",
                        "signTime", "2026-08-01 10:30:00",
                        "patientName", "张三",
                        "doctorName", "张医生"),
                Map.of(
                        "eltmId", "eltm_mock_002",
                        "fileId", "file_mock_002",
                        "fileName", "知情同意书_李四.pdf",
                        "ossUrl", "https://mock-oss.example.com/consent/file_mock_002.pdf",
                        "signTime", "2026-08-02 14:00:00",
                        "patientName", "李四",
                        "doctorName", "王医生")
        );
    }

    private InformedConsent buildConsent(Patient patient, Map<String, String> rec) {
        InformedConsent consent = new InformedConsent();
        consent.setPatientId(patient.getId());
        consent.setEltmId(patient.getEltmId());
        consent.setSource("ELTM");
        consent.setCreateTime(LocalDateTime.now());
        consent.setSyncTime(LocalDateTime.now());
        applyFields(consent, rec);
        return consent;
    }

    private void applyFields(InformedConsent consent, Map<String, String> rec) {
        consent.setExternalFileId(rec.get("fileId"));
        consent.setFileName(rec.get("fileName"));
        consent.setOssUrl(rec.get("ossUrl"));
        consent.setSignTime(parseTime(rec.get("signTime")));
        consent.setPatientName(rec.get("patientName"));
        consent.setDoctorName(rec.get("doctorName"));
    }

    private LocalDateTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        try {
            if (timeStr.contains("T")) {
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (timeStr.contains(" ")) {
                return LocalDateTime.parse(timeStr, TIME_FORMATTER);
            }
            return LocalDateTime.parse(timeStr + " 00:00:00", TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("解析签署时间失败: {}", timeStr);
            return null;
        }
    }
}
