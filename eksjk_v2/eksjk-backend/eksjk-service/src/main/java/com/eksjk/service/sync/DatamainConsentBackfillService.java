package com.eksjk.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.entity.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 知情同意书完整记录补拉（getzqbyid）
 *
 * <p>consent_json 只包含签署基础字段，正文/签名/PDF 需要通过 getzqbyid 补拉。
 * 患者签名是 URL；监护人/医生签名是 base64，超过 500 字符时暂不落库（等待对象存储接入）。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatamainConsentBackfillService {

    private static final int MAX_INLINE_IMAGE_LENGTH = 500;

    private final DatamainApiClient apiClient;
    private final PatientMapper patientMapper;
    private final InformedConsentMapper informedConsentMapper;

    public Map<String, Object> backfill(long externalId) {
        Patient patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getExternalId, externalId).last("LIMIT 1"));
        if (patient == null) {
            throw new IllegalArgumentException("未找到外部患者: " + externalId);
        }

        JsonNode rows = apiClient.fetchConsentRecords(externalId);
        int created = 0;
        int updated = 0;
        int skipped = 0;
        int skippedImages = 0;

        for (JsonNode row : rows) {
            String signId = DatamainParseUtil.text(row.get(0));
            if (signId == null) {
                skipped++;
                continue;
            }
            InformedConsent consent = informedConsentMapper.selectOne(
                    new LambdaQueryWrapper<InformedConsent>()
                            .eq(InformedConsent::getSignId, signId).last("LIMIT 1"));
            boolean isNew = consent == null;
            if (isNew) {
                consent = new InformedConsent();
                consent.setSignId(signId);
                consent.setPatientId(patient.getId());
                consent.setPatientName(patient.getName());
                consent.setExternalPatientId(externalId);
                consent.setEltmId(patient.getEltmId() != null
                        ? patient.getEltmId() : String.valueOf(externalId));
                consent.setSource("ELTM");
                consent.setCreateTime(LocalDateTime.now());
                created++;
            } else {
                updated++;
            }

            consent.setExternalPatientId(externalId);
            consent.setPatientId(patient.getId());
            consent.setProjectId(DatamainParseUtil.text(row.get(1)));
            consent.setGroupId(DatamainParseUtil.text(row.get(2)));
            consent.setExternalConsentId(DatamainParseUtil.text(row.get(3)));
            consent.setExternalDoctorId(DatamainParseUtil.longValue(row.get(7)));
            consent.setContent(DatamainParseUtil.text(row.get(9)));
            consent.setSignDoctorId(DatamainParseUtil.text(row.get(10)));
            String patientImage = DatamainParseUtil.text(row.get(11));
            if (patientImage != null && patientImage.length() > MAX_INLINE_IMAGE_LENGTH) {
                skippedImages++;
                patientImage = null;
            }
            consent.setPatientSignImage(patientImage);
            consent.setPatientSignTime(DatamainParseUtil.parseDateTime(row.get(12)));

            String guardianImage = DatamainParseUtil.text(row.get(13));
            if (guardianImage != null) {
                if (guardianImage.length() > MAX_INLINE_IMAGE_LENGTH) {
                    skippedImages++;
                    guardianImage = null;
                }
            }
            consent.setGuardianSignImage(guardianImage);
            consent.setGuardianSignTime(DatamainParseUtil.parseDateTime(row.get(14)));

            String doctorImage = DatamainParseUtil.text(row.get(15));
            if (doctorImage != null) {
                if (doctorImage.length() > MAX_INLINE_IMAGE_LENGTH) {
                    skippedImages++;
                    doctorImage = null;
                }
            }
            consent.setDoctorSignImage(doctorImage);
            consent.setDoctorSignTime(DatamainParseUtil.parseDateTime(row.get(16)));

            consent.setSignStatus(DatamainParseUtil.text(row.get(17)));
            String source = DatamainParseUtil.text(row.get(18));
            if (source != null) {
                consent.setSource(source);
            }
            consent.setSignTime(consent.getPatientSignTime() != null
                    ? consent.getPatientSignTime() : consent.getDoctorSignTime());

            consent.setPdfPath(DatamainParseUtil.text(row.get(19)));
            consent.setPdfFileName(DatamainParseUtil.text(row.get(20)));
            consent.setPdfErrorMsg(DatamainParseUtil.text(row.get(21)));
            consent.setPdfStatus(DatamainParseUtil.text(row.get(22)));
            consent.setPdfRetryCount(DatamainParseUtil.intValue(row.get(23)));
            consent.setPdfGenerateTime(DatamainParseUtil.parseDateTime(row.get(24)));
            consent.setExternalCreateTime(DatamainParseUtil.parseDateTime(row.get(5)));
            consent.setExternalUpdateTime(DatamainParseUtil.parseDateTime(row.get(6)));
            consent.setRawJson(row.toString());
            consent.setSyncTime(LocalDateTime.now());

            if (isNew) {
                informedConsentMapper.insert(consent);
            } else {
                informedConsentMapper.updateById(consent);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("externalId", externalId);
        result.put("total", rows.size());
        result.put("created", created);
        result.put("updated", updated);
        result.put("skipped", skipped);
        result.put("skippedLargeImages", skippedImages);
        return result;
    }
}
