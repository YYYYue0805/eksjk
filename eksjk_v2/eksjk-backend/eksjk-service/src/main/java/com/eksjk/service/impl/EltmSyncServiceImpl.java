package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.EltmCaseMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.EltmCase;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.EltmSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ELTM 数据同步服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmSyncServiceImpl implements EltmSyncService {

    private final PatientMapper patientMapper;
    private final EltmCaseMapper eltmCaseMapper;

    @Value("${eksjk.eltm.sync-url:http://36.26.56.94:8026/datamain/getpatient}")
    private String syncUrl;

    @Override
    public Map<String, Object> syncFromExternal() {
        RestTemplate restTemplate = new RestTemplate();
        log.info("开始同步ELTM数据: url={}", syncUrl);

        // 调用外部API
        Map<String, Object> response;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> raw = restTemplate.getForObject(syncUrl, Map.class);
            response = raw;
        } catch (Exception e) {
            log.error("调用外部ELTM API失败", e);
            throw new RuntimeException("外部ELTM系统不可达: " + e.getMessage());
        }

        if (response == null || !response.containsKey("data")) {
            log.warn("外部ELTM API返回数据为空或无data字段");
            return Map.of("totalRecords", 0, "newCount", 0, "updatedCount", 0, "failedCount", 0);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> dataWrapper = (Map<String, Object>) response.get("data");
        @SuppressWarnings("unchecked")
        List<List<Object>> datalist = (List<List<Object>>) dataWrapper.get("data");

        if (datalist == null || datalist.isEmpty()) {
            log.info("外部ELTM API无新数据");
            return Map.of("totalRecords", 0, "newCount", 0, "updatedCount", 0, "failedCount", 0);
        }

        // 按 eltm_id 分组，构建每个患者的字段Map
        Map<String, Map<String, String>> grouped = new LinkedHashMap<>();
        for (List<Object> info : datalist) {
            try {
                if (info.size() < 5) continue;
                String eltmId = String.valueOf(info.get(1));
                String fieldName = String.valueOf(info.get(3));
                String fieldValue = info.get(4) != null ? String.valueOf(info.get(4)) : "";

                grouped.computeIfAbsent(eltmId, k -> new LinkedHashMap<>());
                grouped.get(eltmId).put(fieldName, fieldValue);
                // 保存导入人员和创建时间
                if (info.size() > 6 && info.get(6) != null) {
                    grouped.get(eltmId).put("__imp_per", String.valueOf(info.get(6)));
                }
                if (info.size() > 7 && info.get(7) != null) {
                    grouped.get(eltmId).put("__c_time", String.valueOf(info.get(7)));
                }
            } catch (Exception e) {
                log.warn("解析单条ELTM记录失败: {}", info, e);
            }
        }

        int totalRecords = grouped.size();
        int newCount = 0;
        int updatedCount = 0;
        int failedCount = 0;

        for (Map.Entry<String, Map<String, String>> entry : grouped.entrySet()) {
            String eltmId = entry.getKey();
            Map<String, String> fields = entry.getValue();
            try {
                boolean isNew = upsertPatient(eltmId, fields);
                if (isNew) {
                    newCount++;
                } else {
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("同步ELTM患者失败: eltmId={}", eltmId, e);
                failedCount++;
            }
        }

        log.info("ELTM同步完成: total={}, new={}, updated={}, failed={}", totalRecords, newCount, updatedCount, failedCount);
        return Map.of("totalRecords", totalRecords, "newCount", newCount, "updatedCount", updatedCount, "failedCount", failedCount);
    }

    /**
     * 插入或更新单个ELTM患者，每个患者独立事务
     * @return true=新建, false=更新
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    protected boolean upsertPatient(String eltmId, Map<String, String> fields) {
        Patient patient = patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getEltmId, eltmId));

        boolean isNew = (patient == null);
        if (isNew) {
            patient = new Patient();
            patient.setEltmId(eltmId);
            patient.setDisClass("10000007");
            patient.setDelFlg("1");
            patient.setCTime(parseTime(fields.get("__c_time")));
            patient.setOneTime(LocalDateTime.now());
            // 生成病例编号
            patient.setCaseNum(generateEltmCaseNum());
        }

        // 设置基本信息
        if (fields.containsKey("name")) patient.setName(fields.get("name"));
        if (fields.containsKey("age")) patient.setAge(fields.get("age"));
        if (fields.containsKey("sex")) patient.setSex(fields.get("sex"));
        if (fields.containsKey("phone")) {
            patient.setSelfTel(fields.get("phone"));
            patient.setContactsNum(fields.get("phone"));
        }
        if (fields.containsKey("idCard")) {
            String idCard = fields.get("idCard");
            if (idCard.length() < 19) {
                patient.setCard(idCard);
                patient.setIdcard(idCard);
            }
        }
        if (fields.containsKey("birthday")) {
            patient.setBirthTime(parseTime(fields.get("birthday")));
        }
        if (patient.getBirthTime() == null) {
            patient.setBirthTime(LocalDateTime.now());
        }

        // 导入人员
        String impPer = fields.get("__imp_per");
        if (impPer != null && !impPer.isEmpty()) {
            patient.setImpPer(impPer);
            patient.setModifyPer(impPer);
        } else if (isNew) {
            patient.setImpPer("ELTM-SYNC");
            patient.setModifyPer("ELTM-SYNC");
        }

        // 同步状态
        patient.setSyncStatus("synced");
        patient.setSyncTime(LocalDateTime.now());
        patient.setModifyTime(LocalDateTime.now());

        if (isNew) {
            patient.setAuditStatus("pending_review");
            patientMapper.insert(patient);
            // 创建 EltmCase 子表记录
            EltmCase eltmCase = new EltmCase();
            eltmCase.setPatientId(patient.getId());
            // 将原始数据存为JSON
            eltmCase.setRawData(toJson(fields));
            eltmCase.setCreateTime(LocalDateTime.now());
            eltmCaseMapper.insert(eltmCase);
        } else {
            patientMapper.updateById(patient);
        }

        return isNew;
    }

    @Override
    public Map<String, Object> getSyncStatus() {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getDisClass, "10000007")
                .eq(Patient::getDelFlg, "1");

        Long totalEltmCount = patientMapper.selectCount(wrapper);

        wrapper.eq(Patient::getSyncStatus, "synced");
        Long syncedCount = patientMapper.selectCount(wrapper);

        // 查询最近同步时间
        LambdaQueryWrapper<Patient> timeWrapper = new LambdaQueryWrapper<>();
        timeWrapper.eq(Patient::getDisClass, "10000007")
                .eq(Patient::getSyncStatus, "synced")
                .orderByDesc(Patient::getSyncTime)
                .last("LIMIT 1");
        Patient latest = patientMapper.selectOne(timeWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("syncedCount", syncedCount);
        result.put("totalEltmCount", totalEltmCount);
        result.put("lastSyncTime", latest != null ? latest.getSyncTime() : null);
        return result;
    }

    private synchronized String generateEltmCaseNum() {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String prefix = "ELTM" + yearMonth;
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Patient::getCaseNum, prefix)
                .orderByDesc(Patient::getCaseNum)
                .last("LIMIT 1");
        Patient last = patientMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getCaseNum() != null) {
            String seqStr = last.getCaseNum().substring(prefix.length());
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + String.format("%03d", seq);
    }

    private LocalDateTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        try {
            // 尝试常见日期格式
            if (timeStr.contains("T")) {
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            // yyyy-MM-dd HH:mm:ss
            if (timeStr.contains(" ")) {
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            // yyyy-MM-dd
            return LocalDateTime.parse(timeStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析时间失败: {}", timeStr);
            return null;
        }
    }

    private String toJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey().replace("\"", "\\\"")).append("\":\"")
                    .append(e.getValue().replace("\"", "\\\"").replace("\n", "\\n")).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
