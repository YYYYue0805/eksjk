package com.eksjk.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eksjk.mapper.*;
import com.eksjk.model.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 单患者同步事务写入
 *
 * <p>每个患者一个独立事务；明细数据为外部数据，按患者整体重建；
 * 知情同意按 sign_id upsert，保留已补拉的正文/签名/PDF 字段。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatamainSyncTxService {

    private final PatientMapper patientMapper;
    private final EltmPatientProfileMapper profileMapper;
    private final EltmPatientRawMapper rawMapper;
    private final EltmHospitalBindMapper hospitalBindMapper;
    private final EltmProjectMapper projectMapper;
    private final EltmFormSubmissionMapper formSubmissionMapper;
    private final EltmFormAnswerMapper formAnswerMapper;
    private final EltmPatientPicMapper patientPicMapper;
    private final EltmPhysicalRecordMapper physicalRecordMapper;
    private final EltmPhysicalItemMapper physicalItemMapper;
    private final EltmAssessmentReportMapper assessmentReportMapper;
    private final EltmExerciseRecordMapper exerciseRecordMapper;
    private final EltmExerciseSummaryMapper exerciseSummaryMapper;
    private final EltmSleepRecordMapper sleepRecordMapper;
    private final EltmSleepSummaryMapper sleepSummaryMapper;
    private final InformedConsentMapper informedConsentMapper;
    private final DatamainDetailParser detailParser;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean syncPatient(CxListRow row, String rowHash) {
        long externalId = row.id();
        Patient patient = matchExisting(row);
        boolean isNew = patient == null;
        if (isNew) {
            patient = new Patient();
            applyNewPatientDefaults(patient);
        }
        applyRowFields(patient, row);
        if (isNew) {
            patientMapper.insert(patient);
        } else {
            patientMapper.updateById(patient);
        }
        Long patientId = patient.getId();

        upsertProfile(patientId, row);
        upsertRaw(patientId, row, rowHash);
        replaceHospitalBinds(patientId, detailParser.parseHospitalBinds(row, patientId));
        replaceProjects(patientId, detailParser.parseProjects(row, patientId));
        replaceForms(patientId, row);
        replacePics(patientId, detailParser.parsePics(row, patientId));
        replacePhysical(patientId, row);
        updateLatestPhysicalSummary(patientId);
        replaceReports(patientId, detailParser.parseReports(row, patientId));
        replaceExercises(patientId, detailParser.parseExercises(row, patientId));
        replaceExerciseSummaries(patientId, detailParser.parseExerciseSummaries(row, patientId));
        replaceSleeps(patientId, detailParser.parseSleeps(row, patientId));
        replaceSleepSummaries(patientId, detailParser.parseSleepSummaries(row, patientId));
        upsertConsents(patientId, row, patient);
        return isNew;
    }

    // ==================== 患者主体 ====================

    private Patient matchExisting(CxListRow row) {
        Patient patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getExternalId, row.id()).last("LIMIT 1"));
        if (patient != null) {
            return patient;
        }
        // 以下兜底匹配只针对尚未关联 external_id 的历史患者，
        // 避免两条外部记录因身份证/手机号相同而被合并到同一个本地患者。
        patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getEltmId, String.valueOf(row.id())).last("LIMIT 1"));
        if (patient != null && patient.getExternalId() == null) {
            return patient;
        }
        String idCard = row.str(3);
        if (idCard != null && idCard.length() < 19) {
            patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                    .eq(Patient::getIdcard, idCard).last("LIMIT 1"));
            if (patient != null && patient.getExternalId() == null) {
                return patient;
            }
        }
        String name = row.str(1);
        String phone = row.str(2);
        if (name != null && phone != null) {
            LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>()
                    .eq(Patient::getName, name)
                    .eq(Patient::getSelfTel, phone)
                    .isNull(Patient::getExternalId);
            LocalDateTime birthday = row.dateTime(6);
            if (birthday != null) {
                wrapper.eq(Patient::getBirthTime, birthday);
            }
            return patientMapper.selectOne(wrapper.last("LIMIT 1"));
        }
        return null;
    }

    private void applyNewPatientDefaults(Patient patient) {
        patient.setDisClass("10000007");
        patient.setDelFlg("1");
        patient.setCaseNum(generateEltmCaseNum());
        patient.setOneTime(LocalDateTime.now());
        patient.setAuditStatus("pending_review");
        patient.setImpPer("ELTM-SYNC");
        patient.setModifyPer("ELTM-SYNC");
    }

    private void applyRowFields(Patient patient, CxListRow row) {
        patient.setExternalId(row.id());
        if (patient.getEltmId() == null) {
            patient.setEltmId(String.valueOf(row.id()));
        }
        String name = row.str(1);
        if (notBlank(name)) {
            patient.setName(name);
        }
        String sex = DatamainParseUtil.normalizeSex(row.str(4), row.str(5));
        if (sex != null) {
            patient.setSex(sex);
        }
        LocalDateTime birthday = row.dateTime(6);
        if (birthday != null) {
            patient.setBirthTime(birthday);
            patient.setAge(DatamainParseUtil.calcAge(birthday.toLocalDate()));
        }
        String phone = row.str(2);
        if (notBlank(phone)) {
            patient.setSelfTel(phone);
            patient.setContactsNum(phone);
        }
        String idCard = row.str(3);
        if (notBlank(idCard) && idCard.length() < 19) {
            patient.setIdcard(idCard);
            patient.setCard(idCard);
        }
        String doctorName = row.str(11);
        if (notBlank(doctorName)) {
            patient.setDoctorName(doctorName);
        }
        String hospitalName = row.str(13);
        if (notBlank(hospitalName)) {
            patient.setHospitalName(hospitalName);
        }
        LocalDateTime createdAt = row.dateTime(10);
        if (createdAt != null) {
            patient.setCTime(createdAt);
        }
        patient.setSyncStatus("synced");
        patient.setSyncTime(LocalDateTime.now());
        patient.setModifyTime(LocalDateTime.now());
        patient.setModifyPer("ELTM-SYNC");
    }

    private synchronized String generateEltmCaseNum() {
        String prefix = "ELTM" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        Patient last = patientMapper.selectOne(new LambdaQueryWrapper<Patient>()
                .likeRight(Patient::getCaseNum, prefix)
                .orderByDesc(Patient::getCaseNum)
                .last("LIMIT 1"));
        int seq = 1;
        if (last != null && last.getCaseNum() != null) {
            try {
                seq = Integer.parseInt(last.getCaseNum().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
                // 保持 1
            }
        }
        return prefix + String.format("%03d", seq);
    }

    private void upsertProfile(Long patientId, CxListRow row) {
        EltmPatientProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<EltmPatientProfile>()
                .eq(EltmPatientProfile::getPatientId, patientId).last("LIMIT 1"));
        if (profile == null) {
            profile = profileMapper.selectOne(new LambdaQueryWrapper<EltmPatientProfile>()
                    .eq(EltmPatientProfile::getExternalId, row.id()).last("LIMIT 1"));
        }
        if (profile == null) {
            profile = new EltmPatientProfile();
            profile.setPatientId(patientId);
        }
        profile.setExternalId(row.id());
        profile.setRegisterSource(row.str(8));
        profile.setIdType(row.str(9));
        profile.setExternalCreatedAt(row.dateTime(10));
        profile.setExternalDoctorId(row.str(12));
        profile.setExternalHospitalId(row.str(14));
        profile.setFirstDoctorNames(row.str(15));
        profile.setFirstDoctorIds(row.str(16));
        profile.setFirstHospitalNames(row.str(17));
        profile.setFirstHospitalIds(row.str(18));
        profile.setAssignType(row.str(19));
        profile.setProjectCount(row.intValue(20));
        profile.setProjectName(row.str(21));
        profile.setPatientType(row.str(24));
        profile.setDataScope(row.str(25));
        profile.setFormCount(row.intValue(27));
        profile.setLatestFormTime(row.dateTime(29));
        profile.setLatestFormUpdate(row.dateTime(30));
        profile.setLatestFormName(row.str(31));
        profile.setPicCount(row.intValue(32));
        profile.setLatestPicTime(row.dateTime(33));
        profile.setLatestPicUpdate(row.dateTime(34));
        profile.setPhysicalSignCount(row.intValue(35));
        profile.setLatestSignTime(row.dateTime(37));
        profile.setLatestSignUpdate(row.dateTime(38));
        profile.setReportCount(row.intValue(39));
        profile.setLatestReportTime(row.dateTime(41));
        profile.setLatestReportUpdate(row.dateTime(42));
        profile.setExerciseFlag(row.str(43));
        profile.setLatestExerciseTime(row.dateTime(45));
        profile.setLatestExerciseUpdate(row.dateTime(46));
        profile.setSleepFlag(row.str(47));
        profile.setLatestSleepTime(row.dateTime(49));
        profile.setLatestSleepUpdate(row.dateTime(50));
        profile.setConsentCount(row.intValue(51));
        profile.setLatestConsentTime(row.dateTime(52));
        profile.setLatestConsentUpdate(row.dateTime(53));
        profile.setSyncTime(LocalDateTime.now());
        if (profile.getId() == null) {
            profileMapper.insert(profile);
        } else {
            profileMapper.updateById(profile);
        }
    }

    private void upsertRaw(Long patientId, CxListRow row, String rowHash) {
        EltmPatientRaw raw = rawMapper.selectOne(new LambdaQueryWrapper<EltmPatientRaw>()
                .eq(EltmPatientRaw::getPatientId, patientId).last("LIMIT 1"));
        if (raw == null) {
            raw = rawMapper.selectOne(new LambdaQueryWrapper<EltmPatientRaw>()
                    .eq(EltmPatientRaw::getExternalId, row.id()).last("LIMIT 1"));
        }
        if (raw == null) {
            raw = new EltmPatientRaw();
            raw.setPatientId(patientId);
        }
        raw.setExternalId(row.id());
        raw.setRowHash(rowHash);
        raw.setRawJson(row.rawJson());
        raw.setExternalUpdatedAt(latestUpdate(row));
        raw.setSyncTime(LocalDateTime.now());
        if (raw.getId() == null) {
            rawMapper.insert(raw);
        } else {
            rawMapper.updateById(raw);
        }
    }

    private LocalDateTime latestUpdate(CxListRow row) {
        LocalDateTime latest = null;
        for (int column : new int[]{30, 34, 38, 42, 46, 50, 53}) {
            LocalDateTime value = row.dateTime(column);
            if (value != null && (latest == null || value.isAfter(latest))) {
                latest = value;
            }
        }
        return latest;
    }

    // ==================== 明细重建 ====================

    private void replaceHospitalBinds(Long patientId, List<EltmHospitalBind> list) {
        hospitalBindMapper.delete(new LambdaQueryWrapper<EltmHospitalBind>()
                .eq(EltmHospitalBind::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            hospitalBindMapper.insert(item);
        });
    }

    private void replaceProjects(Long patientId, List<EltmProject> list) {
        projectMapper.delete(new LambdaQueryWrapper<EltmProject>().eq(EltmProject::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            projectMapper.insert(item);
        });
    }

    private void replaceForms(Long patientId, CxListRow row) {
        formAnswerMapper.delete(new LambdaQueryWrapper<EltmFormAnswer>().inSql(
                EltmFormAnswer::getSubmissionId,
                "SELECT id FROM eltm_form_submission WHERE patient_id = " + patientId));
        formSubmissionMapper.delete(new LambdaQueryWrapper<EltmFormSubmission>()
                .eq(EltmFormSubmission::getPatientId, patientId));
        DatamainDetailParser.FormParseResult result = detailParser.parseForms(row, patientId);
        for (EltmFormSubmission submission : result.getSubmissions()) {
            formSubmissionMapper.insert(submission);
            List<EltmFormAnswer> answers = result.getAnswers().get(submission.getFormDataId());
            if (answers != null) {
                for (EltmFormAnswer answer : answers) {
                    answer.setSubmissionId(submission.getId());
                    formAnswerMapper.insert(answer);
                }
            }
        }
    }

    private void replacePics(Long patientId, List<EltmPatientPic> list) {
        patientPicMapper.delete(new LambdaQueryWrapper<EltmPatientPic>()
                .eq(EltmPatientPic::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            patientPicMapper.insert(item);
        });
    }

    private void replacePhysical(Long patientId, CxListRow row) {
        physicalItemMapper.delete(new LambdaQueryWrapper<EltmPhysicalItem>().inSql(
                EltmPhysicalItem::getRecordId,
                "SELECT id FROM eltm_physical_record WHERE patient_id = " + patientId));
        physicalRecordMapper.delete(new LambdaQueryWrapper<EltmPhysicalRecord>()
                .eq(EltmPhysicalRecord::getPatientId, patientId));
        DatamainDetailParser.PhysicalParseResult result = detailParser.parsePhysical(row, patientId);
        for (DatamainDetailParser.PhysicalSession session : result.getSessions()) {
            EltmPhysicalRecord record = new EltmPhysicalRecord();
            record.setPatientId(patientId);
            record.setExternalPatientId(row.id());
            record.setMeasureTime(session.getMeasureTime() != null
                    ? session.getMeasureTime() : LocalDateTime.now());
            record.setDataSource(session.getDataSource());
            record.setArchivesType(session.getArchivesType());
            record.setItemCount(session.getItems().size());
            record.setSyncTime(LocalDateTime.now());
            physicalRecordMapper.insert(record);
            for (EltmPhysicalItem item : session.getItems()) {
                item.setRecordId(record.getId());
                physicalItemMapper.insert(item);
            }
        }
    }

    private void replaceReports(Long patientId, List<EltmAssessmentReport> list) {
        assessmentReportMapper.delete(new LambdaQueryWrapper<EltmAssessmentReport>()
                .eq(EltmAssessmentReport::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            assessmentReportMapper.insert(item);
        });
    }

    private void replaceExercises(Long patientId, List<EltmExerciseRecord> list) {
        exerciseRecordMapper.delete(new LambdaQueryWrapper<EltmExerciseRecord>()
                .eq(EltmExerciseRecord::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            exerciseRecordMapper.insert(item);
        });
    }

    private void replaceExerciseSummaries(Long patientId, List<EltmExerciseSummary> list) {
        exerciseSummaryMapper.delete(new LambdaQueryWrapper<EltmExerciseSummary>()
                .eq(EltmExerciseSummary::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            exerciseSummaryMapper.insert(item);
        });
    }

    private void replaceSleeps(Long patientId, List<EltmSleepRecord> list) {
        sleepRecordMapper.delete(new LambdaQueryWrapper<EltmSleepRecord>()
                .eq(EltmSleepRecord::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            sleepRecordMapper.insert(item);
        });
    }

    /**
     * 将最近一次体征测量回填到患者主表，供列表和基本信息展示。
     */
    private void updateLatestPhysicalSummary(Long patientId) {
        EltmPhysicalRecord latest = physicalRecordMapper.selectOne(
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patientId)
                        .orderByDesc(EltmPhysicalRecord::getMeasureTime)
                        .last("LIMIT 1"));
        if (latest == null) {
            return;
        }
        List<EltmPhysicalItem> items = physicalItemMapper.selectList(
                new LambdaQueryWrapper<EltmPhysicalItem>()
                        .eq(EltmPhysicalItem::getRecordId, latest.getId())
                        .in(EltmPhysicalItem::getAttributeCode, "height:Tab", "weight:Tab", "bmi"));
        String height = null;
        String weight = null;
        String bmi = null;
        for (EltmPhysicalItem item : items) {
            switch (item.getAttributeCode()) {
                case "height:Tab" -> height = item.getAttributeValue();
                case "weight:Tab" -> weight = item.getAttributeValue();
                case "bmi" -> bmi = item.getAttributeValue();
                default -> {
                    // 忽略其他属性
                }
            }
        }
        if (height != null || weight != null || bmi != null) {
            patientMapper.update(null, new LambdaUpdateWrapper<Patient>()
                    .eq(Patient::getId, patientId)
                    .set(height != null, Patient::getHeight, height)
                    .set(weight != null, Patient::getWeight, weight)
                    .set(bmi != null, Patient::getBmi, bmi));
        }
    }

    private void replaceSleepSummaries(Long patientId, List<EltmSleepSummary> list) {        sleepSummaryMapper.delete(new LambdaQueryWrapper<EltmSleepSummary>()
                .eq(EltmSleepSummary::getPatientId, patientId));
        list.forEach(item -> {
            item.setPatientId(patientId);
            sleepSummaryMapper.insert(item);
        });
    }

    private void upsertConsents(Long patientId, CxListRow row, Patient patient) {
        List<InformedConsent> list = detailParser.parseConsents(
                row, patientId, patient.getName(), patient.getEltmId());
        for (InformedConsent parsed : list) {
            InformedConsent existing = informedConsentMapper.selectOne(
                    new LambdaQueryWrapper<InformedConsent>()
                            .eq(InformedConsent::getSignId, parsed.getSignId()).last("LIMIT 1"));
            if (existing == null) {
                parsed.setDoctorName(patient.getDoctorName());
                informedConsentMapper.insert(parsed);
            } else {
                existing.setPatientId(parsed.getPatientId());
                existing.setEltmId(parsed.getEltmId());
                existing.setPatientName(parsed.getPatientName());
                existing.setDoctorName(patient.getDoctorName());
                existing.setExternalPatientId(parsed.getExternalPatientId());
                existing.setExternalDoctorId(parsed.getExternalDoctorId());
                existing.setProjectId(parsed.getProjectId());
                existing.setGroupId(parsed.getGroupId());
                existing.setExternalConsentId(parsed.getExternalConsentId());
                existing.setSignStatus(parsed.getSignStatus());
                existing.setPatientSignTime(parsed.getPatientSignTime());
                existing.setGuardianSignTime(parsed.getGuardianSignTime());
                existing.setDoctorSignTime(parsed.getDoctorSignTime());
                existing.setSignTime(parsed.getSignTime());
                existing.setSource("ELTM");
                existing.setRawJson(parsed.getRawJson());
                existing.setExternalCreateTime(parsed.getExternalCreateTime());
                existing.setExternalUpdateTime(parsed.getExternalUpdateTime());
                existing.setSyncTime(LocalDateTime.now());
                informedConsentMapper.updateById(existing);
            }
        }
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
