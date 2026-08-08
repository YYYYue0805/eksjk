package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.constant.AuditStatusConstants;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.dto.AuditActionDTO;
import com.eksjk.model.dto.AuditQueryDTO;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.model.vo.PatientVO;
import com.eksjk.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审核发放服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final PatientMapper patientMapper;
    private final PatientFollowUpMapper followUpMapper;

    // ==================== 患者基线审核发放 ====================

    @Override
    public PageResult<PatientVO> listPatients(AuditQueryDTO queryDTO) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        // 列表查询仅选取必要列，避免拉取 TEXT/JSON 大字段
        wrapper.select(Patient::getId, Patient::getCaseNum, Patient::getName, Patient::getSex,
                Patient::getAge, Patient::getMedrecNum, Patient::getChiCom,
                Patient::getIcd, Patient::getDisClass, Patient::getImpPer, Patient::getCTime,
                Patient::getHospitalName, Patient::getAuditStatus, Patient::getAuditBy,
                Patient::getAuditTime, Patient::getAuditRemark, Patient::getDelFlg);
        wrapper.eq(Patient::getDelFlg, "1");

        // 审核状态过滤
        if (StringUtils.isNotBlank(queryDTO.getAuditStatus())) {
            wrapper.eq(Patient::getAuditStatus, queryDTO.getAuditStatus());
        }

        // 病例编号模糊搜索
        if (StringUtils.isNotBlank(queryDTO.getCaseNum())) {
            wrapper.like(Patient::getCaseNum, queryDTO.getCaseNum());
        }

        // 姓名模糊搜索
        if (StringUtils.isNotBlank(queryDTO.getName())) {
            wrapper.like(Patient::getName, queryDTO.getName());
        }

        // 数据隔离
        applyDataScope(wrapper);

        wrapper.orderByDesc(Patient::getCTime);

        Page<Patient> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Patient> result = patientMapper.selectPage(page, wrapper);

        List<PatientVO> voList = result.getRecords().stream()
                .map(this::convertToPatientVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePatient(Long patientId, AuditActionDTO dto) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }
        if (!AuditStatusConstants.PENDING_REVIEW.equals(patient.getAuditStatus())) {
            throw new BusinessException("当前状态不可审核，仅待审核状态可操作");
        }

        LambdaUpdateWrapper<Patient> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Patient::getId, patientId)
                .eq(Patient::getAuditStatus, AuditStatusConstants.PENDING_REVIEW)
                .set(Patient::getAuditStatus, AuditStatusConstants.PENDING_RELEASE)
                .set(Patient::getAuditBy, SecurityUtil.getCurrentUsername())
                .set(Patient::getAuditTime, LocalDateTime.now())
                .set(Patient::getAuditRemark, dto.getComment());

        int rows = patientMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("审核通过病例基线: patientId={}, operator={}", patientId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectPatient(Long patientId, AuditActionDTO dto) {
        if (StringUtils.isBlank(dto.getComment())) {
            throw new BusinessException("驳回时必须填写驳回原因");
        }

        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }
        if (!AuditStatusConstants.PENDING_REVIEW.equals(patient.getAuditStatus())) {
            throw new BusinessException("当前状态不可审核，仅待审核状态可操作");
        }

        LambdaUpdateWrapper<Patient> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Patient::getId, patientId)
                .eq(Patient::getAuditStatus, AuditStatusConstants.PENDING_REVIEW)
                .set(Patient::getAuditStatus, AuditStatusConstants.REJECTED)
                .set(Patient::getAuditBy, SecurityUtil.getCurrentUsername())
                .set(Patient::getAuditTime, LocalDateTime.now())
                .set(Patient::getAuditRemark, dto.getComment());

        int rows = patientMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("驳回病例基线: patientId={}, operator={}", patientId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePatient(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }
        if (!AuditStatusConstants.PENDING_RELEASE.equals(patient.getAuditStatus())) {
            throw new BusinessException("当前状态不可发放，请先审核通过");
        }

        LambdaUpdateWrapper<Patient> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Patient::getId, patientId)
                .eq(Patient::getAuditStatus, AuditStatusConstants.PENDING_RELEASE)
                .set(Patient::getAuditStatus, AuditStatusConstants.RELEASED)
                .set(Patient::getReleaseBy, SecurityUtil.getCurrentUsername())
                .set(Patient::getReleaseTime, LocalDateTime.now());

        int rows = patientMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("发放病例基线: patientId={}, operator={}", patientId, SecurityUtil.getCurrentUsername());
    }

    // ==================== 随访审核发放 ====================

    @Override
    public PageResult<FollowUpVO> listFollowUps(AuditQueryDTO queryDTO) {
        LambdaQueryWrapper<PatientFollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientFollowUp::getDelFlg, "1");

        if (StringUtils.isNotBlank(queryDTO.getAuditStatus())) {
            wrapper.eq(PatientFollowUp::getAuditStatus, queryDTO.getAuditStatus());
        }

        wrapper.orderByDesc(PatientFollowUp::getUpTime);

        Page<PatientFollowUp> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<PatientFollowUp> result = followUpMapper.selectPage(page, wrapper);

        // 批量查询关联患者信息
        List<Long> patientIds = result.getRecords().stream()
                .map(PatientFollowUp::getPatientId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Patient> patientMap = Collections.emptyMap();
        if (!patientIds.isEmpty()) {
            LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
            patientWrapper.select(Patient::getId, Patient::getCaseNum, Patient::getName, Patient::getSex,
                            Patient::getAuditStatus, Patient::getHospitalName, Patient::getImpPer, Patient::getDisClass)
                    .in(Patient::getId, patientIds);
            List<Patient> patients = patientMapper.selectList(patientWrapper);
            patientMap = patients.stream().collect(Collectors.toMap(Patient::getId, p -> p));
        }

        Map<Long, Patient> finalPatientMap = patientMap;
        List<FollowUpVO> voList = result.getRecords().stream()
                .map(fu -> convertToFollowUpVO(fu, finalPatientMap))
                .collect(Collectors.toList());

        // 应用姓名搜索过滤（后置过滤）
        if (StringUtils.isNotBlank(queryDTO.getName())) {
            voList = voList.stream()
                    .filter(vo -> vo.getPatientName() != null && vo.getPatientName().contains(queryDTO.getName()))
                    .collect(Collectors.toList());
        }

        // 数据隔离：根据关联患者的数据范围过滤
        voList = applyFollowUpDataScope(voList);

        return PageResult.of(voList, voList.size(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveFollowUp(Long followUpId, AuditActionDTO dto) {
        PatientFollowUp followUp = getFollowUpOrThrow(followUpId);
        checkPatientAuditStatus(followUp.getPatientId());

        if (!AuditStatusConstants.PENDING_REVIEW.equals(followUp.getAuditStatus())) {
            throw new BusinessException("当前状态不可审核，仅待审核状态可操作");
        }

        LambdaUpdateWrapper<PatientFollowUp> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PatientFollowUp::getId, followUpId)
                .eq(PatientFollowUp::getAuditStatus, AuditStatusConstants.PENDING_REVIEW)
                .set(PatientFollowUp::getAuditStatus, AuditStatusConstants.PENDING_RELEASE)
                .set(PatientFollowUp::getAuditBy, SecurityUtil.getCurrentUsername())
                .set(PatientFollowUp::getAuditTime, LocalDateTime.now())
                .set(PatientFollowUp::getAuditRemark, dto.getComment());

        int rows = followUpMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("审核通过随访: followUpId={}, operator={}", followUpId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectFollowUp(Long followUpId, AuditActionDTO dto) {
        if (StringUtils.isBlank(dto.getComment())) {
            throw new BusinessException("驳回时必须填写驳回原因");
        }

        PatientFollowUp followUp = getFollowUpOrThrow(followUpId);
        checkPatientAuditStatus(followUp.getPatientId());

        if (!AuditStatusConstants.PENDING_REVIEW.equals(followUp.getAuditStatus())) {
            throw new BusinessException("当前状态不可审核，仅待审核状态可操作");
        }

        LambdaUpdateWrapper<PatientFollowUp> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PatientFollowUp::getId, followUpId)
                .eq(PatientFollowUp::getAuditStatus, AuditStatusConstants.PENDING_REVIEW)
                .set(PatientFollowUp::getAuditStatus, AuditStatusConstants.REJECTED)
                .set(PatientFollowUp::getAuditBy, SecurityUtil.getCurrentUsername())
                .set(PatientFollowUp::getAuditTime, LocalDateTime.now())
                .set(PatientFollowUp::getAuditRemark, dto.getComment());

        int rows = followUpMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("驳回随访: followUpId={}, operator={}", followUpId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseFollowUp(Long followUpId) {
        PatientFollowUp followUp = getFollowUpOrThrow(followUpId);
        checkPatientAuditStatus(followUp.getPatientId());

        if (!AuditStatusConstants.PENDING_RELEASE.equals(followUp.getAuditStatus())) {
            throw new BusinessException("当前状态不可发放，请先审核通过");
        }

        LambdaUpdateWrapper<PatientFollowUp> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PatientFollowUp::getId, followUpId)
                .eq(PatientFollowUp::getAuditStatus, AuditStatusConstants.PENDING_RELEASE)
                .set(PatientFollowUp::getAuditStatus, AuditStatusConstants.RELEASED)
                .set(PatientFollowUp::getReleaseBy, SecurityUtil.getCurrentUsername())
                .set(PatientFollowUp::getReleaseTime, LocalDateTime.now());

        int rows = followUpMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("状态已变更，请刷新后重试");
        }
        log.info("发放随访: followUpId={}, operator={}", followUpId, SecurityUtil.getCurrentUsername());
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 基线统计
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(Patient::getDelFlg, "1");
        applyDataScope(patientWrapper);

        stats.put("patientPendingReview", countPatientsByStatus(AuditStatusConstants.PENDING_REVIEW));
        stats.put("patientPendingRelease", countPatientsByStatus(AuditStatusConstants.PENDING_RELEASE));
        stats.put("patientReleased", countPatientsByStatus(AuditStatusConstants.RELEASED));
        stats.put("patientRejected", countPatientsByStatus(AuditStatusConstants.REJECTED));

        // 随访统计
        stats.put("followUpPendingReview", countFollowUpsByStatus(AuditStatusConstants.PENDING_REVIEW));
        stats.put("followUpPendingRelease", countFollowUpsByStatus(AuditStatusConstants.PENDING_RELEASE));

        return stats;
    }

    // ==================== 私有方法 ====================

    private PatientFollowUp getFollowUpOrThrow(Long id) {
        PatientFollowUp followUp = followUpMapper.selectById(id);
        if (followUp == null || "0".equals(followUp.getDelFlg())) {
            throw new BusinessException("随访记录不存在");
        }
        return followUp;
    }

    private void checkPatientAuditStatus(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("关联的病例不存在");
        }
        if (AuditStatusConstants.PENDING_REVIEW.equals(patient.getAuditStatus())
                || AuditStatusConstants.REJECTED.equals(patient.getAuditStatus())) {
            throw new BusinessException("请先审核通过该患者的基线信息");
        }
    }

    private void applyDataScope(LambdaQueryWrapper<Patient> wrapper) {
        String role = SecurityUtil.getCurrentRole();
        if (RoleConstants.SUPER_ADMIN.equals(role)) {
            return;
        }
        if (RoleConstants.HOSPITAL_ADMIN.equals(role)) {
            String hospitalName = SecurityUtil.getCurrentHospitalName();
            if (StringUtils.isNotBlank(hospitalName)) {
                wrapper.eq(Patient::getHospitalName, hospitalName);
            }
        } else {
            wrapper.eq(Patient::getImpPer, SecurityUtil.getCurrentUsername());
        }
    }

    private List<FollowUpVO> applyFollowUpDataScope(List<FollowUpVO> voList) {
        String role = SecurityUtil.getCurrentRole();
        if (RoleConstants.SUPER_ADMIN.equals(role)) {
            return voList;
        }
        if (RoleConstants.HOSPITAL_ADMIN.equals(role)) {
            String hospitalName = SecurityUtil.getCurrentHospitalName();
            return voList.stream()
                    .filter(vo -> hospitalName != null && hospitalName.equals(vo.getPatientHospitalName()))
                    .collect(Collectors.toList());
        }
        // doctor: 只看本人的患者
        String username = SecurityUtil.getCurrentUsername();
        return voList.stream()
                .filter(vo -> username != null && username.equals(vo.getPatientImpPer()))
                .collect(Collectors.toList());
    }

    private long countPatientsByStatus(String status) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getDelFlg, "1")
                .eq(Patient::getAuditStatus, status);
        applyDataScope(wrapper);
        return patientMapper.selectCount(wrapper);
    }

    private long countFollowUpsByStatus(String status) {
        LambdaQueryWrapper<PatientFollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientFollowUp::getDelFlg, "1")
                .eq(PatientFollowUp::getAuditStatus, status);
        return followUpMapper.selectCount(wrapper);
    }

    private PatientVO convertToPatientVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo, "id");
        vo.setId(HashidsUtil.encode(patient.getId()));

        if ("1".equals(patient.getSex())) {
            vo.setSexName("男");
        } else if ("2".equals(patient.getSex())) {
            vo.setSexName("女");
        } else {
            vo.setSexName("未知");
        }
        return vo;
    }

    private FollowUpVO convertToFollowUpVO(PatientFollowUp followUp, Map<Long, Patient> patientMap) {
        FollowUpVO vo = new FollowUpVO();
        BeanUtils.copyProperties(followUp, vo, "id", "patientId");
        vo.setId(HashidsUtil.encode(followUp.getId()));
        vo.setPatientId(HashidsUtil.encode(followUp.getPatientId()));

        Patient patient = patientMap.get(followUp.getPatientId());
        if (patient != null) {
            vo.setPatientCaseNum(patient.getCaseNum());
            vo.setPatientName(patient.getName());
            vo.setPatientSex("1".equals(patient.getSex()) ? "男" : "2".equals(patient.getSex()) ? "女" : "未知");
            vo.setPatientAuditStatus(patient.getAuditStatus());
            vo.setPatientHospitalName(patient.getHospitalName());
            vo.setPatientImpPer(patient.getImpPer());
            vo.setPatientDisClass(patient.getDisClass());
        }
        return vo;
    }
}
