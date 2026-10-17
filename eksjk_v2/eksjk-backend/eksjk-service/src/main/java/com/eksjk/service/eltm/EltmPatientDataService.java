package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.PageResult;
import com.eksjk.mapper.*;
import com.eksjk.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ELTM 外部同步数据只读查询服务
 *
 * <p>仅供详情页展示，不参与内部审核、诊断和随访流程。</p>
 *
 * @author eksjk
 */
@Service
@RequiredArgsConstructor
public class EltmPatientDataService {

    private final PatientMapper patientMapper;
    private final EltmPatientProfileMapper profileMapper;
    private final EltmPhysicalRecordMapper physicalRecordMapper;
    private final EltmPhysicalItemMapper physicalItemMapper;
    private final EltmFormSubmissionMapper formSubmissionMapper;
    private final EltmFormAnswerMapper formAnswerMapper;
    private final EltmPatientPicMapper picMapper;
    private final EltmAssessmentReportMapper reportMapper;
    private final EltmProjectMapper projectMapper;
    private final EltmHospitalBindMapper hospitalBindMapper;
    private final EltmExerciseRecordMapper exerciseRecordMapper;
    private final EltmExerciseSummaryMapper exerciseSummaryMapper;
    private final EltmSleepRecordMapper sleepRecordMapper;
    private final EltmSleepSummaryMapper sleepSummaryMapper;
    private final InformedConsentMapper consentMapper;

    public Map<String, Object> overview(Long patientId) {
        Patient patient = requirePatient(patientId);

        Map<String, Object> patientInfo = new LinkedHashMap<>();
        patientInfo.put("id", patient.getId());
        patientInfo.put("externalId", patient.getExternalId());
        patientInfo.put("caseNum", patient.getCaseNum());
        patientInfo.put("name", patient.getName());
        patientInfo.put("sex", patient.getSex());
        patientInfo.put("birthTime", patient.getBirthTime());
        patientInfo.put("age", patient.getAge());
        patientInfo.put("doctorName", patient.getDoctorName());
        patientInfo.put("hospitalName", patient.getHospitalName());
        patientInfo.put("height", patient.getHeight());
        patientInfo.put("weight", patient.getWeight());
        patientInfo.put("bmi", patient.getBmi());

        EltmPatientProfile profile = profileMapper.selectOne(
                new LambdaQueryWrapper<EltmPatientProfile>()
                        .eq(EltmPatientProfile::getPatientId, patientId)
                        .last("LIMIT 1"));

        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("forms", formSubmissionMapper.selectCount(
                new LambdaQueryWrapper<EltmFormSubmission>()
                        .eq(EltmFormSubmission::getPatientId, patientId)));
        counts.put("pics", picMapper.selectCount(
                new LambdaQueryWrapper<EltmPatientPic>()
                        .eq(EltmPatientPic::getPatientId, patientId)));
        counts.put("physicalRecords", physicalRecordMapper.selectCount(
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patientId)));
        counts.put("reports", reportMapper.selectCount(
                new LambdaQueryWrapper<EltmAssessmentReport>()
                        .eq(EltmAssessmentReport::getPatientId, patientId)));
        counts.put("consents", consentMapper.selectCount(
                new LambdaQueryWrapper<InformedConsent>()
                        .eq(InformedConsent::getPatientId, patientId)
                        .isNotNull(InformedConsent::getSignId)));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("patient", patientInfo);
        result.put("profile", profile);
        result.put("counts", counts);
        result.put("latestPhysical", latestPhysical(patientId));
        result.put("projects", listProjects(patientId));
        result.put("hospitalBinds", listHospitalBinds(patientId));
        return result;
    }

    public PageResult<Map<String, Object>> physical(Long patientId, long pageNum, long pageSize) {
        requirePatient(patientId);
        Page<EltmPhysicalRecord> page = new Page<>(pageNum, pageSize);
        Page<EltmPhysicalRecord> result = physicalRecordMapper.selectPage(page,
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patientId)
                        .orderByDesc(EltmPhysicalRecord::getMeasureTime)
                        .orderByDesc(EltmPhysicalRecord::getId));

        List<Long> recordIds = result.getRecords().stream()
                .map(EltmPhysicalRecord::getId)
                .toList();
        Map<Long, List<EltmPhysicalItem>> itemsByRecord = recordIds.isEmpty()
                ? Collections.emptyMap()
                : physicalItemMapper.selectList(new LambdaQueryWrapper<EltmPhysicalItem>()
                        .in(EltmPhysicalItem::getRecordId, recordIds)
                        .orderByAsc(EltmPhysicalItem::getAttributeCode))
                .stream()
                .collect(Collectors.groupingBy(EltmPhysicalItem::getRecordId));

        List<Map<String, Object>> records = result.getRecords().stream().map(record -> {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", record.getId());
            vo.put("measureTime", record.getMeasureTime());
            vo.put("dataSource", record.getDataSource());
            vo.put("itemCount", record.getItemCount());
            Map<String, String> items = new LinkedHashMap<>();
            for (EltmPhysicalItem item : itemsByRecord.getOrDefault(record.getId(), Collections.emptyList())) {
                items.put(item.getAttributeCode(), item.getAttributeValue());
            }
            vo.put("items", items);
            return vo;
        }).toList();

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    public PageResult<EltmFormSubmission> forms(Long patientId, long pageNum, long pageSize) {
        requirePatient(patientId);
        Page<EltmFormSubmission> page = new Page<>(pageNum, pageSize);
        Page<EltmFormSubmission> result = formSubmissionMapper.selectPage(page,
                new LambdaQueryWrapper<EltmFormSubmission>()
                        .select(EltmFormSubmission::getId, EltmFormSubmission::getPatientId,
                                EltmFormSubmission::getFormDataId, EltmFormSubmission::getFormKey,
                                EltmFormSubmission::getFormName, EltmFormSubmission::getFormType,
                                EltmFormSubmission::getProjectName, EltmFormSubmission::getSourceType,
                                EltmFormSubmission::getSerialNumber, EltmFormSubmission::getSubmittedAt,
                                EltmFormSubmission::getScore, EltmFormSubmission::getEvaluationResult,
                                EltmFormSubmission::getResultDescription)
                        .eq(EltmFormSubmission::getPatientId, patientId)
                        .orderByDesc(EltmFormSubmission::getSubmittedAt)
                        .orderByDesc(EltmFormSubmission::getId));
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    public Map<String, Object> formDetail(Long patientId, Long formDataId) {
        requirePatient(patientId);
        EltmFormSubmission submission = formSubmissionMapper.selectOne(
                new LambdaQueryWrapper<EltmFormSubmission>()
                        .eq(EltmFormSubmission::getPatientId, patientId)
                        .eq(EltmFormSubmission::getFormDataId, formDataId)
                        .last("LIMIT 1"));
        if (submission == null) {
            throw new BusinessException("表单记录不存在");
        }
        List<EltmFormAnswer> answers = formAnswerMapper.selectList(
                new LambdaQueryWrapper<EltmFormAnswer>()
                        .eq(EltmFormAnswer::getSubmissionId, submission.getId())
                        .orderByAsc(EltmFormAnswer::getId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submission", submission);
        result.put("answers", answers);
        return result;
    }

    public List<EltmPatientPic> pics(Long patientId) {
        requirePatient(patientId);
        return picMapper.selectList(new LambdaQueryWrapper<EltmPatientPic>()
                .select(EltmPatientPic::getId, EltmPatientPic::getPicId,
                        EltmPatientPic::getRecordDate, EltmPatientPic::getRecordName,
                        EltmPatientPic::getTypeContent, EltmPatientPic::getImageUrl,
                        EltmPatientPic::getImageType, EltmPatientPic::getPicSourceType,
                        EltmPatientPic::getSource, EltmPatientPic::getRemark,
                        EltmPatientPic::getExternalCreateTime, EltmPatientPic::getExternalUpdateTime)
                .eq(EltmPatientPic::getPatientId, patientId)
                .orderByDesc(EltmPatientPic::getRecordDate)
                .orderByDesc(EltmPatientPic::getId));
    }

    public PageResult<EltmAssessmentReport> reports(Long patientId, long pageNum, long pageSize) {
        requirePatient(patientId);
        Page<EltmAssessmentReport> page = new Page<>(pageNum, pageSize);
        Page<EltmAssessmentReport> result = reportMapper.selectPage(page,
                new LambdaQueryWrapper<EltmAssessmentReport>()
                        .select(EltmAssessmentReport::getId, EltmAssessmentReport::getReportId,
                                EltmAssessmentReport::getModelName, EltmAssessmentReport::getFormName,
                                EltmAssessmentReport::getProjectName, EltmAssessmentReport::getScore,
                                EltmAssessmentReport::getTotalScore, EltmAssessmentReport::getEvaluationResult,
                                EltmAssessmentReport::getResultDescription, EltmAssessmentReport::getReportRemarkValue,
                                EltmAssessmentReport::getReportSuggestValue,
                                EltmAssessmentReport::getBaseCheckDateValue,
                                EltmAssessmentReport::getBaseNextCheckDateValue,
                                EltmAssessmentReport::getReportTime)
                        .eq(EltmAssessmentReport::getPatientId, patientId)
                        .orderByDesc(EltmAssessmentReport::getReportTime)
                        .orderByDesc(EltmAssessmentReport::getId));
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    public Map<String, Object> projects(Long patientId) {
        requirePatient(patientId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projects", listProjects(patientId));
        result.put("hospitalBinds", listHospitalBinds(patientId));
        return result;
    }

    public Map<String, Object> exerciseSleep(Long patientId) {
        requirePatient(patientId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("exercises", exerciseRecordMapper.selectList(
                new LambdaQueryWrapper<EltmExerciseRecord>()
                        .eq(EltmExerciseRecord::getPatientId, patientId)
                        .orderByDesc(EltmExerciseRecord::getRecordDate)
                        .orderByDesc(EltmExerciseRecord::getId)));
        result.put("exerciseSummaries", exerciseSummaryMapper.selectList(
                new LambdaQueryWrapper<EltmExerciseSummary>()
                        .eq(EltmExerciseSummary::getPatientId, patientId)
                        .orderByDesc(EltmExerciseSummary::getSummaryDate)
                        .orderByDesc(EltmExerciseSummary::getId)));
        result.put("sleeps", sleepRecordMapper.selectList(
                new LambdaQueryWrapper<EltmSleepRecord>()
                        .eq(EltmSleepRecord::getPatientId, patientId)
                        .orderByDesc(EltmSleepRecord::getRecordDate)
                        .orderByDesc(EltmSleepRecord::getId)));
        result.put("sleepSummaries", sleepSummaryMapper.selectList(
                new LambdaQueryWrapper<EltmSleepSummary>()
                        .eq(EltmSleepSummary::getPatientId, patientId)
                        .orderByDesc(EltmSleepSummary::getSummaryDate)
                        .orderByDesc(EltmSleepSummary::getId)));
        return result;
    }

    private Map<String, Object> latestPhysical(Long patientId) {
        EltmPhysicalRecord latest = physicalRecordMapper.selectOne(
                new LambdaQueryWrapper<EltmPhysicalRecord>()
                        .eq(EltmPhysicalRecord::getPatientId, patientId)
                        .orderByDesc(EltmPhysicalRecord::getMeasureTime)
                        .orderByDesc(EltmPhysicalRecord::getId)
                        .last("LIMIT 1"));
        if (latest == null) {
            return null;
        }
        Map<String, String> items = new LinkedHashMap<>();
        physicalItemMapper.selectList(new LambdaQueryWrapper<EltmPhysicalItem>()
                        .eq(EltmPhysicalItem::getRecordId, latest.getId())
                        .orderByAsc(EltmPhysicalItem::getAttributeCode))
                .forEach(item -> items.put(item.getAttributeCode(), item.getAttributeValue()));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("measureTime", latest.getMeasureTime());
        result.put("dataSource", latest.getDataSource());
        result.put("items", items);
        return result;
    }

    private List<EltmProject> listProjects(Long patientId) {
        return projectMapper.selectList(new LambdaQueryWrapper<EltmProject>()
                .select(EltmProject::getId, EltmProject::getExternalProjectId,
                        EltmProject::getProjectName, EltmProject::getRelationSource,
                        EltmProject::getFirstRelationTime)
                .eq(EltmProject::getPatientId, patientId)
                .orderByDesc(EltmProject::getFirstRelationTime));
    }

    private List<EltmHospitalBind> listHospitalBinds(Long patientId) {
        return hospitalBindMapper.selectList(new LambdaQueryWrapper<EltmHospitalBind>()
                .select(EltmHospitalBind::getId, EltmHospitalBind::getExternalDoctorId,
                        EltmHospitalBind::getDoctorName, EltmHospitalBind::getExternalHospitalId,
                        EltmHospitalBind::getHospitalName, EltmHospitalBind::getProvince,
                        EltmHospitalBind::getCity, EltmHospitalBind::getDistrict,
                        EltmHospitalBind::getBindSource)
                .eq(EltmHospitalBind::getPatientId, patientId));
    }

    private Patient requirePatient(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }
        return patient;
    }
}
