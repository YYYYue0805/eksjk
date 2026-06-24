package com.eksjk.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.eksjk.mapper.*;
import com.eksjk.model.dto.PatientDTO;
import com.eksjk.model.dto.PatientQueryDTO;
import com.eksjk.model.entity.*;
import com.eksjk.model.vo.PatientVO;
import com.eksjk.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 患者病例管理服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientMapper patientMapper;
    private final DsdCaseMapper dsdCaseMapper;
    private final FssCaseMapper fssCaseMapper;
    private final CppCaseMapper cppCaseMapper;
    private final MasCaseMapper masCaseMapper;
    private final SgaCaseMapper sgaCaseMapper;
    private final SssCaseMapper sssCaseMapper;
    private final EltmCaseMapper eltmCaseMapper;
    private final PatientFollowUpMapper followUpMapper;

    /** 疾病分类编码与名称映射 */
    private static final Map<String, String> DIS_CLASS_MAP = new LinkedHashMap<>();
    /** 疾病分类编码与编号前缀映射 */
    private static final Map<String, String> DIS_PREFIX_MAP = new LinkedHashMap<>();

    static {
        DIS_CLASS_MAP.put("10000001", "性发育异常 (DSD)");
        DIS_CLASS_MAP.put("10000002", "遗传性骨病 (FSS)");
        DIS_CLASS_MAP.put("10000003", "中枢性性早熟 (CPP)");
        DIS_CLASS_MAP.put("10000004", "McCune-Albright (MAS)");
        DIS_CLASS_MAP.put("10000005", "小于胎龄儿 (SGA)");
        DIS_CLASS_MAP.put("10000006", "家族性矮小 (SSS)");
        DIS_CLASS_MAP.put("10000007", "E路童萌 (ELTM)");

        DIS_PREFIX_MAP.put("10000001", "DSD");
        DIS_PREFIX_MAP.put("10000002", "FSS");
        DIS_PREFIX_MAP.put("10000003", "CPP");
        DIS_PREFIX_MAP.put("10000004", "MAS");
        DIS_PREFIX_MAP.put("10000005", "SGA");
        DIS_PREFIX_MAP.put("10000006", "SSS");
        DIS_PREFIX_MAP.put("10000007", "ELTM");
    }

    @Override
    public PageResult<PatientVO> queryPage(PatientQueryDTO queryDTO) {
        Page<Patient> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        // 列表查询仅选取必要列，避免拉取 TEXT/JSON 大字段（~150 列 → ~20 列）
        wrapper.select(Patient::getId, Patient::getCaseNum, Patient::getName, Patient::getSex,
                Patient::getAge, Patient::getAgeY, Patient::getAgeM, Patient::getMedrecNum,
                Patient::getHeight, Patient::getWeight, Patient::getBmi, Patient::getChiCom,
                Patient::getIcd, Patient::getDisClass, Patient::getImpPer, Patient::getCTime,
                Patient::getHospitalName, Patient::getAuditStatus, Patient::getDiagnosisStatus,
                Patient::getDelFlg);
        // 只查询有效数据
        wrapper.eq(Patient::getDelFlg, "1");

        // 审核状态过滤：默认只展示已发放的病例，审核模块使用独立的 AuditService 接口
        if (StringUtils.isNotBlank(queryDTO.getAuditStatus())) {
            wrapper.eq(Patient::getAuditStatus, queryDTO.getAuditStatus());
        } else {
            wrapper.eq(Patient::getAuditStatus, AuditStatusConstants.RELEASED);
        }

        // 疾病分类过滤 — ELTM(10000007) 查询全部病例
        if (StringUtils.isNotBlank(queryDTO.getDisClass())
                && !"10000007".equals(queryDTO.getDisClass())) {
            wrapper.eq(Patient::getDisClass, queryDTO.getDisClass());
        }

        // 病例编号精确搜索
        if (StringUtils.isNotBlank(queryDTO.getCaseNum())) {
            wrapper.like(Patient::getCaseNum, queryDTO.getCaseNum());
        }

        // 病历号搜索
        if (StringUtils.isNotBlank(queryDTO.getMedrecNum())) {
            wrapper.like(Patient::getMedrecNum, queryDTO.getMedrecNum());
        }

        // 姓名模糊搜索
        if (StringUtils.isNotBlank(queryDTO.getName())) {
            wrapper.like(Patient::getName, queryDTO.getName());
        }

        // 性别过滤
        if (StringUtils.isNotBlank(queryDTO.getSex())) {
            wrapper.eq(Patient::getSex, queryDTO.getSex());
        }

        // 时间范围过滤
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Patient::getCTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Patient::getCTime, queryDTO.getEndTime());
        }

        // 数据范围过滤
        applyDataScope(wrapper);

        // 按创建时间倒序
        wrapper.orderByDesc(Patient::getCTime);

        Page<Patient> result = patientMapper.selectPage(page, wrapper);

        List<PatientVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(voList, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PatientVO getDetail(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }
        // 病例管理中只能查看已发放的病例
        if (!AuditStatusConstants.RELEASED.equals(patient.getAuditStatus())) {
            throw new BusinessException("病例尚未发放，无法查看");
        }

        PatientVO vo = convertToVO(patient);

        // 加载疾病专项数据
        vo.setDiseaseData(loadDiseaseData(patient.getId(), patient.getDisClass()));

        // 统计随访记录数量
        Long followUpCount = followUpMapper.selectCount(
                new LambdaQueryWrapper<PatientFollowUp>()
                        .eq(PatientFollowUp::getPatientId, patient.getId())
                        .eq(PatientFollowUp::getDelFlg, "1")
        );
        vo.setFollowUpCount(followUpCount.intValue());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(PatientDTO patientDTO) {
        // 创建患者主表记录
        Patient patient = new Patient();
        BeanUtils.copyProperties(patientDTO, patient);

        // 自动生成病例编号
        String caseNum = generateCaseNum(patientDTO.getDisClass());
        patient.setCaseNum(caseNum);

        // 设置管理字段
        patient.setImpPer(SecurityUtil.getCurrentUsername());
        patient.setUpMec(SecurityUtil.getCurrentHospitalName());
        patient.setHospitalName(SecurityUtil.getCurrentHospitalName());
        patient.setCTime(LocalDateTime.now());
        patient.setModifyTime(LocalDateTime.now());
        patient.setOneTime(LocalDateTime.now());
        patient.setDelFlg("1");
        patient.setAuditStatus(AuditStatusConstants.PENDING_REVIEW);

        // 自动生成家庭分组ID
        if (StringUtils.isBlank(patient.getFamilyId())) {
            patient.setFamilyId(UUID.randomUUID().toString());
        }

        // 计算BMI
        if (StringUtils.isNotBlank(patientDTO.getHeight()) && StringUtils.isNotBlank(patientDTO.getWeight())) {
            try {
                double h = Double.parseDouble(patientDTO.getHeight()) / 100.0;
                double w = Double.parseDouble(patientDTO.getWeight());
                if (h > 0) {
                    patient.setBmi(String.format("%.1f", w / (h * h)));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        patientMapper.insert(patient);

        // 创建疾病子表记录
        if (patientDTO.getDiseaseData() != null) {
            saveDiseaseData(patient.getId(), patientDTO.getDisClass(), patientDTO.getDiseaseData());
        }

        log.info("新建病例成功: caseNum={}, disClass={}, operator={}", caseNum, patientDTO.getDisClass(), SecurityUtil.getCurrentUsername());
        return caseNum;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PatientDTO patientDTO) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("病例不存在");
        }

        // 保护字段：不允许前端篡改疾病类型与审计字段
        String originalDisClass = patient.getDisClass();
        String originalCaseNum = patient.getCaseNum();
        String originalImpPer = patient.getImpPer();
        String originalUpMec = patient.getUpMec();
        String originalHospitalName = patient.getHospitalName();
        LocalDateTime originalCTime = patient.getCTime();
        LocalDateTime originalOneTime = patient.getOneTime();

        // 更新主表字段（忽略 DTO 中的 null 字段，避免覆盖原有有效值）
        BeanUtils.copyProperties(patientDTO, patient, getNullPropertyNames(patientDTO));
        patient.setId(id);
        patient.setDisClass(originalDisClass);
        patient.setCaseNum(originalCaseNum);
        patient.setImpPer(originalImpPer);
        patient.setUpMec(originalUpMec);
        patient.setHospitalName(originalHospitalName);
        patient.setCTime(originalCTime);
        patient.setOneTime(originalOneTime);
        patient.setModifyTime(LocalDateTime.now());
        patient.setModifyPer(SecurityUtil.getCurrentUsername());

        // 驳回后编辑自动重置为待审核
        if (AuditStatusConstants.REJECTED.equals(patient.getAuditStatus())) {
            patient.setAuditStatus(AuditStatusConstants.PENDING_REVIEW);
            patient.setAuditTime(null);
            patient.setAuditBy(null);
        }

        // 重新计算BMI
        if (StringUtils.isNotBlank(patientDTO.getHeight()) && StringUtils.isNotBlank(patientDTO.getWeight())) {
            try {
                double h = Double.parseDouble(patientDTO.getHeight()) / 100.0;
                double w = Double.parseDouble(patientDTO.getWeight());
                if (h > 0) {
                    patient.setBmi(String.format("%.1f", w / (h * h)));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        patientMapper.updateById(patient);

        // 更新疾病子表数据
        if (patientDTO.getDiseaseData() != null) {
            saveDiseaseData(id, originalDisClass, patientDTO.getDiseaseData());
        }

        log.info("编辑病例成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new BusinessException("病例不存在");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Patient> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Patient::getId, id).set(Patient::getDelFlg, "0");
        patientMapper.update(null, wrapper);

        log.info("删除病例成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    @Override
    public byte[] exportExcel(PatientQueryDTO queryDTO) {
        // Excel导出功能 - 使用EasyExcel实现
        // 此处返回空字节数组作为占位，后续完善
        log.info("导出Excel: disClass={}", queryDTO.getDisClass());
        return new byte[0];
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<Patient> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(Patient::getDelFlg, "1");
        applyDataScope(baseWrapper);

        // 病例总数
        Long totalCount = patientMapper.selectCount(baseWrapper);
        stats.put("totalCount", totalCount);

        // 本月新增
        LambdaQueryWrapper<Patient> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Patient::getDelFlg, "1");
        applyDataScope(monthWrapper);
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        monthWrapper.ge(Patient::getCTime, monthStart);
        Long monthCount = patientMapper.selectCount(monthWrapper);
        stats.put("monthCount", monthCount);

        // 待随访数（简化：统计最近30天内没有随访记录的患者数）
        stats.put("pendingFollowUp", 0);

        return stats;
    }

    // ==================== 私有方法 ====================

    /**
     * 应用数据范围过滤
     */
    private void applyDataScope(LambdaQueryWrapper<Patient> wrapper) {
        String role = SecurityUtil.getCurrentRole();
        if (RoleConstants.SUPER_ADMIN.equals(role)) {
            // 超级管理员：全局数据
            return;
        }
        if (RoleConstants.HOSPITAL_ADMIN.equals(role)) {
            // 医院管理员：本院数据
            String hospitalName = SecurityUtil.getCurrentHospitalName();
            if (StringUtils.isNotBlank(hospitalName)) {
                wrapper.eq(Patient::getHospitalName, hospitalName);
            }
        } else {
            // 普通医生：本人数据
            String username = SecurityUtil.getCurrentUsername();
            wrapper.eq(Patient::getImpPer, username);
        }
    }

    /**
     * 生成病例编号（格式：疾病类型前缀 + 年月 + 序号）
     */
    private String generateCaseNum(String disClass) {
        String prefix = DIS_PREFIX_MAP.getOrDefault(disClass, "UNK");
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 查询当月最大编号
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Patient::getCaseNum, prefix + yearMonth)
                .orderByDesc(Patient::getCaseNum)
                .last("LIMIT 1");
        Patient lastPatient = patientMapper.selectOne(wrapper);

        int seq = 1;
        if (lastPatient != null && lastPatient.getCaseNum() != null) {
            String lastNum = lastPatient.getCaseNum();
            String seqStr = lastNum.substring(prefix.length() + yearMonth.length());
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }

        return prefix + yearMonth + String.format("%03d", seq);
    }

    /**
     * Patient -> PatientVO 转换
     * 注意：Patient.id 为 Long，PatientVO.id 为 String。
     * BeanUtils.copyProperties 遇到类型不兼容会抛异常（或静默忽略），
     * 这里显式排除 id 字段，后续由 setId() 重新赋值为 Hashids 编码。
     */
    private PatientVO convertToVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo, "id");
        vo.setId(HashidsUtil.encode(patient.getId()));
        vo.setDisClassName(DIS_CLASS_MAP.getOrDefault(patient.getDisClass(), "未知"));

        // 非ELTM病种视为"已诊断"
        if (patient.getDisClass() != null && !"10000007".equals(patient.getDisClass())) {
            vo.setDiagnosisStatus("diagnosed");
        }

        // 性别名称
        if ("1".equals(patient.getSex())) {
            vo.setSexName("男");
        } else if ("2".equals(patient.getSex())) {
            vo.setSexName("女");
        } else {
            vo.setSexName("未知");
        }

        return vo;
    }

    /**
     * 加载疾病专项数据
     */
    private Map<String, Object> loadDiseaseData(Long patientId, String disClass) {
        if (StringUtils.isBlank(disClass)) {
            return new HashMap<>();
        }
        Object entity = null;
        switch (disClass) {
            case "10000001" -> entity = dsdCaseMapper.selectOne(
                    new LambdaQueryWrapper<DsdCase>().eq(DsdCase::getPatientId, patientId));
            case "10000002" -> entity = fssCaseMapper.selectOne(
                    new LambdaQueryWrapper<FssCase>().eq(FssCase::getPatientId, patientId));
            case "10000003" -> entity = cppCaseMapper.selectOne(
                    new LambdaQueryWrapper<CppCase>().eq(CppCase::getPatientId, patientId));
            case "10000004" -> entity = masCaseMapper.selectOne(
                    new LambdaQueryWrapper<MasCase>().eq(MasCase::getPatientId, patientId));
            case "10000005" -> entity = sgaCaseMapper.selectOne(
                    new LambdaQueryWrapper<SgaCase>().eq(SgaCase::getPatientId, patientId));
            case "10000006" -> entity = sssCaseMapper.selectOne(
                    new LambdaQueryWrapper<SssCase>().eq(SssCase::getPatientId, patientId));
            case "10000007" -> entity = eltmCaseMapper.selectOne(
                    new LambdaQueryWrapper<EltmCase>().eq(EltmCase::getPatientId, patientId));
        }
        if (entity != null) {
            return BeanUtil.beanToMap(entity, false, false);
        }
        return new HashMap<>();
    }

    /**
     * 保存疾病专项数据（新增或更新）
     */
    private void saveDiseaseData(Long patientId, String disClass, Map<String, Object> diseaseData) {
        if (StringUtils.isBlank(disClass) || diseaseData == null || diseaseData.isEmpty()) {
            return;
        }
        switch (disClass) {
            case "10000001" -> saveDsdData(patientId, diseaseData);
            case "10000002" -> saveFssData(patientId, diseaseData);
            case "10000003" -> saveCppData(patientId, diseaseData);
            case "10000004" -> saveMasData(patientId, diseaseData);
            case "10000005" -> saveSgaData(patientId, diseaseData);
            case "10000006" -> saveSssData(patientId, diseaseData);
            case "10000007" -> saveEltmData(patientId, diseaseData);
        }
    }

    private void saveDsdData(Long patientId, Map<String, Object> data) {
        DsdCase existing = dsdCaseMapper.selectOne(
                new LambdaQueryWrapper<DsdCase>().eq(DsdCase::getPatientId, patientId));
        DsdCase dsd = existing != null ? existing : new DsdCase();
        dsd.setPatientId(patientId);
        // 通过反射或手动设置字段（简化处理，使用BeanUtils）
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(dsd);
        binder.bind(pvs);
        if (existing != null) {
            dsdCaseMapper.updateById(dsd);
        } else {
            dsdCaseMapper.insert(dsd);
        }
    }

    private void saveFssData(Long patientId, Map<String, Object> data) {
        FssCase existing = fssCaseMapper.selectOne(
                new LambdaQueryWrapper<FssCase>().eq(FssCase::getPatientId, patientId));
        FssCase entity = existing != null ? existing : new FssCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            fssCaseMapper.updateById(entity);
        } else {
            fssCaseMapper.insert(entity);
        }
    }

    private void saveCppData(Long patientId, Map<String, Object> data) {
        CppCase existing = cppCaseMapper.selectOne(
                new LambdaQueryWrapper<CppCase>().eq(CppCase::getPatientId, patientId));
        CppCase entity = existing != null ? existing : new CppCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            cppCaseMapper.updateById(entity);
        } else {
            cppCaseMapper.insert(entity);
        }
    }

    private void saveMasData(Long patientId, Map<String, Object> data) {
        MasCase existing = masCaseMapper.selectOne(
                new LambdaQueryWrapper<MasCase>().eq(MasCase::getPatientId, patientId));
        MasCase entity = existing != null ? existing : new MasCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            masCaseMapper.updateById(entity);
        } else {
            masCaseMapper.insert(entity);
        }
    }

    private void saveSgaData(Long patientId, Map<String, Object> data) {
        SgaCase existing = sgaCaseMapper.selectOne(
                new LambdaQueryWrapper<SgaCase>().eq(SgaCase::getPatientId, patientId));
        SgaCase entity = existing != null ? existing : new SgaCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            sgaCaseMapper.updateById(entity);
        } else {
            sgaCaseMapper.insert(entity);
        }
    }

    private void saveSssData(Long patientId, Map<String, Object> data) {
        SssCase existing = sssCaseMapper.selectOne(
                new LambdaQueryWrapper<SssCase>().eq(SssCase::getPatientId, patientId));
        SssCase entity = existing != null ? existing : new SssCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            sssCaseMapper.updateById(entity);
        } else {
            sssCaseMapper.insert(entity);
        }
    }

    private void saveEltmData(Long patientId, Map<String, Object> data) {
        EltmCase existing = eltmCaseMapper.selectOne(
                new LambdaQueryWrapper<EltmCase>().eq(EltmCase::getPatientId, patientId));
        EltmCase entity = existing != null ? existing : new EltmCase();
        entity.setPatientId(patientId);
        org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
        org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
        binder.bind(pvs);
        if (existing != null) {
            eltmCaseMapper.updateById(entity);
        } else {
            eltmCaseMapper.insert(entity);
        }
    }

    // ==================== 家庭关联 ====================

    @Override
    public PatientVO searchByMedrecNum(String medrecNum) {
        if (StringUtils.isBlank(medrecNum)) {
            throw new BusinessException("病历号不能为空");
        }
        LambdaQueryWrapper<Patient> searchWrapper = new LambdaQueryWrapper<>();
        searchWrapper.select(Patient::getId, Patient::getCaseNum, Patient::getName, Patient::getSex,
                        Patient::getAge, Patient::getMedrecNum, Patient::getChiCom,
                        Patient::getIcd, Patient::getDisClass, Patient::getImpPer, Patient::getCTime,
                        Patient::getHospitalName, Patient::getFamilyId, Patient::getDelFlg)
                .eq(Patient::getMedrecNum, medrecNum)
                .eq(Patient::getDelFlg, "1");
        Patient patient = patientMapper.selectOne(searchWrapper);
        if (patient == null) {
            return null;
        }
        return convertToVO(patient);
    }

    @Override
    public List<PatientVO> getFamilyMembers(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Patient> familyWrapper = new LambdaQueryWrapper<>();
        familyWrapper.select(Patient::getId, Patient::getCaseNum, Patient::getName, Patient::getSex,
                        Patient::getAge, Patient::getMedrecNum, Patient::getChiCom,
                        Patient::getIcd, Patient::getDisClass, Patient::getImpPer, Patient::getCTime,
                        Patient::getHospitalName, Patient::getFamilyId, Patient::getDelFlg)
                .eq(Patient::getFamilyId, familyId)
                .eq(Patient::getDelFlg, "1");
        List<Patient> patients = patientMapper.selectList(familyWrapper);
        return patients.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void linkFamily(Long id, String targetMedrecNum) {
        if (StringUtils.isBlank(targetMedrecNum)) {
            throw new BusinessException("目标病历号不能为空");
        }

        Patient currentPatient = patientMapper.selectById(id);
        if (currentPatient == null || "0".equals(currentPatient.getDelFlg())) {
            throw new BusinessException("当前患者不存在");
        }

        // 查找目标患者
        Patient targetPatient = patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getMedrecNum, targetMedrecNum)
                        .eq(Patient::getDelFlg, "1")
        );
        if (targetPatient == null) {
            throw new BusinessException("未找到目标患者，请检查病历号");
        }

        if (targetPatient.getId().equals(id)) {
            throw new BusinessException("不能关联自身");
        }

        String targetFamilyId = targetPatient.getFamilyId();
        if (StringUtils.isBlank(targetFamilyId)) {
            targetFamilyId = UUID.randomUUID().toString();
            targetPatient.setFamilyId(targetFamilyId);
            patientMapper.updateById(targetPatient);
        }

        // 更新当前患者的 familyId
        currentPatient.setFamilyId(targetFamilyId);
        patientMapper.updateById(currentPatient);

        log.info("家庭关联成功: patientId={}, targetMedrecNum={}, familyId={}", id, targetMedrecNum, targetFamilyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlinkFamily(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("患者不存在");
        }

        // 生成新的独立 familyId
        patient.setFamilyId(UUID.randomUUID().toString());
        patientMapper.updateById(patient);

        log.info("解除家庭关联: patientId={}, newFamilyId={}", id, patient.getFamilyId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String reclassify(Long patientId, String targetDisClass) {
        if (targetDisClass == null || !DIS_CLASS_MAP.containsKey(targetDisClass)) {
            throw new BusinessException("无效的目标疾病类型: " + targetDisClass);
        }
        if ("10000007".equals(targetDisClass)) {
            throw new BusinessException("不能重新分类为ELTM");
        }

        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("患者不存在");
        }
        if (!"10000007".equals(patient.getDisClass())) {
            throw new BusinessException("仅ELTM患者支持重新分类，当前类型: " + patient.getDisClass());
        }

        String oldDisClass = patient.getDisClass();
        String newCaseNum = generateCaseNum(targetDisClass);

        patient.setDisClass(targetDisClass);
        patient.setCaseNum(newCaseNum);
        patient.setModifyTime(LocalDateTime.now());
        patient.setModifyPer(SecurityUtil.getCurrentUsername());
        patientMapper.updateById(patient);

        // 创建目标病种子表空记录
        LocalDateTime now = LocalDateTime.now();
        switch (targetDisClass) {
            case "10000001" -> {
                DsdCase dsd = new DsdCase();
                dsd.setPatientId(patientId);
                dsd.setCreateTime(now);
                dsdCaseMapper.insert(dsd);
            }
            case "10000002" -> {
                FssCase fss = new FssCase();
                fss.setPatientId(patientId);
                fss.setCreateTime(now);
                fssCaseMapper.insert(fss);
            }
            case "10000003" -> {
                CppCase cpp = new CppCase();
                cpp.setPatientId(patientId);
                cpp.setCreateTime(now);
                cppCaseMapper.insert(cpp);
            }
            case "10000004" -> {
                MasCase mas = new MasCase();
                mas.setPatientId(patientId);
                mas.setCreateTime(now);
                masCaseMapper.insert(mas);
            }
            case "10000005" -> {
                SgaCase sga = new SgaCase();
                sga.setPatientId(patientId);
                sga.setCreateTime(now);
                sgaCaseMapper.insert(sga);
            }
            case "10000006" -> {
                SssCase sss = new SssCase();
                sss.setPatientId(patientId);
                sss.setCreateTime(now);
                sssCaseMapper.insert(sss);
            }
        }

        log.info("重新分类成功: patientId={}, {} -> {}, newCaseNum={}, operator={}",
                patientId, oldDisClass, targetDisClass, newCaseNum,
                SecurityUtil.getCurrentUsername() != null ? SecurityUtil.getCurrentUsername() : "unknown");
        return newCaseNum;
    }

    /**
     * 获取源对象中值为 null 的属性名称数组，用于 BeanUtils.copyProperties 的忽略参数。
     * 避免用 null 覆盖目标对象中原有的有效值。
     */
    private String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);
        return Arrays.stream(wrapper.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> wrapper.getPropertyValue(name) == null)
                .collect(Collectors.toSet())
                .toArray(new String[0]);
    }
}
