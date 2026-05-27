package com.eksjk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.*;
import com.eksjk.model.dto.StudentDTO;
import com.eksjk.model.dto.StudentQueryDTO;
import com.eksjk.model.entity.*;
import com.eksjk.model.vo.StudentVO;
import com.eksjk.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 学校健康筛查服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final StudentMapper studentMapper;
    private final CchknMapper cchknMapper;
    private final CbqMapper cbqMapper;
    private final MqzyfsMapper mqzyfsMapper;
    private final QzhdMapper qzhdMapper;
    private final PmblMapper pmblMapper;
    private final SthdMapper sthdMapper;
    private final SmxgMapper smxgMapper;

    @Override
    public PageResult<StudentVO> queryPage(StudentQueryDTO queryDTO) {
        Page<Student> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getDelFlg, "1");

        if (StringUtils.isNotBlank(queryDTO.getNum())) {
            wrapper.like(Student::getNum, queryDTO.getNum());
        }
        if (StringUtils.isNotBlank(queryDTO.getSclass())) {
            wrapper.like(Student::getSclass, queryDTO.getSclass());
        }
        if (StringUtils.isNotBlank(queryDTO.getName())) {
            wrapper.like(Student::getName, queryDTO.getName());
        }
        if (StringUtils.isNotBlank(queryDTO.getSex())) {
            wrapper.eq(Student::getSex, queryDTO.getSex());
        }

        // 数据范围过滤
        applyDataScope(wrapper);

        wrapper.orderByDesc(Student::getModifyTime);

        Page<Student> result = studentMapper.selectPage(page, wrapper);

        List<StudentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(voList, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public StudentVO getDetail(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null || "0".equals(student.getDelFlg())) {
            throw new BusinessException("学生记录不存在");
        }

        StudentVO vo = convertToVO(student);

        // 加载问卷填写状态和数据
        Map<String, Boolean> status = new LinkedHashMap<>();
        Map<String, Object> questionnaires = new LinkedHashMap<>();

        Cchkn cchkn = cchknMapper.selectOne(new LambdaQueryWrapper<Cchkn>().eq(Cchkn::getStudentId, id));
        status.put("cchkn", cchkn != null);
        questionnaires.put("cchkn", cchkn != null ? BeanUtil.beanToMap(cchkn, false, true) : null);

        Cbq cbq = cbqMapper.selectOne(new LambdaQueryWrapper<Cbq>().eq(Cbq::getStudentId, id));
        status.put("cbq", cbq != null);
        questionnaires.put("cbq", cbq != null ? BeanUtil.beanToMap(cbq, false, true) : null);

        Mqzyfs mqzyfs = mqzyfsMapper.selectOne(new LambdaQueryWrapper<Mqzyfs>().eq(Mqzyfs::getStudentId, id));
        status.put("mqzyfs", mqzyfs != null);
        questionnaires.put("mqzyfs", mqzyfs != null ? BeanUtil.beanToMap(mqzyfs, false, true) : null);

        Qzhd qzhd = qzhdMapper.selectOne(new LambdaQueryWrapper<Qzhd>().eq(Qzhd::getStudentId, id));
        status.put("qzhd", qzhd != null);
        questionnaires.put("qzhd", qzhd != null ? BeanUtil.beanToMap(qzhd, false, true) : null);

        Pmbl pmbl = pmblMapper.selectOne(new LambdaQueryWrapper<Pmbl>().eq(Pmbl::getStudentId, id));
        status.put("pmbl", pmbl != null);
        questionnaires.put("pmbl", pmbl != null ? BeanUtil.beanToMap(pmbl, false, true) : null);

        Sthd sthd = sthdMapper.selectOne(new LambdaQueryWrapper<Sthd>().eq(Sthd::getStudentId, id));
        status.put("sthd", sthd != null);
        questionnaires.put("sthd", sthd != null ? BeanUtil.beanToMap(sthd, false, true) : null);

        Smxg smxg = smxgMapper.selectOne(new LambdaQueryWrapper<Smxg>().eq(Smxg::getStudentId, id));
        status.put("smxg", smxg != null);
        questionnaires.put("smxg", smxg != null ? BeanUtil.beanToMap(smxg, false, true) : null);

        vo.setQuestionnaireStatus(status);
        vo.setQuestionnaires(questionnaires);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(StudentDTO studentDTO) {
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student, "birthTime");
        // 手动转换 birthTime: String → LocalDateTime
        parseBirthTime(studentDTO.getBirthTime(), student);
        student.setImpPer(SecurityUtil.getCurrentUsername());
        student.setUpMec(SecurityUtil.getCurrentHospitalName());
        student.setDoctor(SecurityUtil.getCurrentUsername());
        student.setCTime(LocalDateTime.now());
        student.setModifyTime(LocalDateTime.now());
        student.setDelFlg("1");

        studentMapper.insert(student);

        // 创建7张空问卷记录
        createEmptyQuestionnaires(student.getId());

        log.info("新增学生成功: id={}, name={}", student.getId(), student.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentDTO studentDTO) {
        Student student = studentMapper.selectById(id);
        if (student == null || "0".equals(student.getDelFlg())) {
            throw new BusinessException("学生记录不存在");
        }

        BeanUtils.copyProperties(studentDTO, student, "id", "impPer", "upMec", "doctor", "cTime", "delFlg", "birthTime");
        // 手动转换 birthTime: String → LocalDateTime
        parseBirthTime(studentDTO.getBirthTime(), student);
        student.setModifyTime(LocalDateTime.now());

        studentMapper.updateById(student);
        log.info("编辑学生成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生记录不存在");
        }

        LambdaUpdateWrapper<Student> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Student::getId, id).set(Student::getDelFlg, "0");
        studentMapper.update(null, wrapper);

        log.info("删除学生成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveQuestionnaire(Long studentId, String type, Map<String, Object> data) {
        Student student = studentMapper.selectById(studentId);
        if (student == null || "0".equals(student.getDelFlg())) {
            throw new BusinessException("学生记录不存在");
        }

        switch (type) {
            case "cchkn" -> saveQuestionnaireData(cchknMapper, studentId, data, Cchkn.class, Cchkn::getStudentId);
            case "cbq" -> saveQuestionnaireData(cbqMapper, studentId, data, Cbq.class, Cbq::getStudentId);
            case "mqzyfs" -> saveQuestionnaireData(mqzyfsMapper, studentId, data, Mqzyfs.class, Mqzyfs::getStudentId);
            case "qzhd" -> saveQuestionnaireData(qzhdMapper, studentId, data, Qzhd.class, Qzhd::getStudentId);
            case "pmbl" -> saveQuestionnaireData(pmblMapper, studentId, data, Pmbl.class, Pmbl::getStudentId);
            case "sthd" -> saveQuestionnaireData(sthdMapper, studentId, data, Sthd.class, Sthd::getStudentId);
            case "smxg" -> saveQuestionnaireData(smxgMapper, studentId, data, Smxg.class, Smxg::getStudentId);
            default -> throw new BusinessException("不支持的问卷类型: " + type);
        }

        log.info("保存问卷成功: studentId={}, type={}", studentId, type);
    }

    // ==================== 私有方法 ====================

    private void applyDataScope(LambdaQueryWrapper<Student> wrapper) {
        String role = SecurityUtil.getCurrentRole();
        if (RoleConstants.SUPER_ADMIN.equals(role)) {
            return;
        }
        if (RoleConstants.HOSPITAL_ADMIN.equals(role)) {
            String hospitalName = SecurityUtil.getCurrentHospitalName();
            if (StringUtils.isNotBlank(hospitalName)) {
                wrapper.eq(Student::getUpMec, hospitalName);
            }
        } else {
            String username = SecurityUtil.getCurrentUsername();
            wrapper.eq(Student::getDoctor, username);
        }
    }

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private StudentVO convertToVO(Student student) {
        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo, "birthTime", "cTime");
        vo.setId(HashidsUtil.encode(student.getId()));
        // 手动转换 LocalDateTime → String（BeanUtils 无法自动转换不同类型）
        if (student.getBirthTime() != null) {
            vo.setBirthTime(student.getBirthTime().format(DATE_TIME_FMT));
        }
        if (student.getCTime() != null) {
            vo.setCTime(student.getCTime().format(DATE_TIME_FMT));
        }
        if ("1".equals(student.getSex())) {
            vo.setSexName("男");
        } else if ("2".equals(student.getSex())) {
            vo.setSexName("女");
        } else {
            vo.setSexName("未知");
        }
        return vo;
    }


    /**
     * 创建7张空问卷记录
     */
    private void createEmptyQuestionnaires(Long studentId) {
        Cchkn cchkn = new Cchkn();
        cchkn.setStudentId(studentId);
        cchkn.setCTime(LocalDateTime.now());
        cchkn.setModifyTime(LocalDateTime.now());
        cchkn.setDelFlg("1");
        cchknMapper.insert(cchkn);

        Cbq cbq = new Cbq();
        cbq.setStudentId(studentId);
        cbq.setCTime(LocalDateTime.now());
        cbq.setModifyTime(LocalDateTime.now());
        cbq.setDelFlg("1");
        cbqMapper.insert(cbq);

        Mqzyfs mqzyfs = new Mqzyfs();
        mqzyfs.setStudentId(studentId);
        mqzyfs.setCTime(LocalDateTime.now());
        mqzyfs.setModifyTime(LocalDateTime.now());
        mqzyfs.setDelFlg("1");
        mqzyfsMapper.insert(mqzyfs);

        Qzhd qzhd = new Qzhd();
        qzhd.setStudentId(studentId);
        qzhd.setCTime(LocalDateTime.now());
        qzhd.setModifyTime(LocalDateTime.now());
        qzhd.setDelFlg("1");
        qzhdMapper.insert(qzhd);

        Pmbl pmbl = new Pmbl();
        pmbl.setStudentId(studentId);
        pmbl.setCTime(LocalDateTime.now());
        pmbl.setModifyTime(LocalDateTime.now());
        pmbl.setDelFlg("1");
        pmblMapper.insert(pmbl);

        Sthd sthd = new Sthd();
        sthd.setStudentId(studentId);
        sthd.setCTime(LocalDateTime.now());
        sthd.setModifyTime(LocalDateTime.now());
        sthd.setDelFlg("1");
        sthdMapper.insert(sthd);

        Smxg smxg = new Smxg();
        smxg.setStudentId(studentId);
        smxg.setCTime(LocalDateTime.now());
        smxg.setModifyTime(LocalDateTime.now());
        smxg.setDelFlg("1");
        smxgMapper.insert(smxg);
    }

    /**
     * 解析 birthTime 字符串并设置到 Student 实体
     */
    private void parseBirthTime(String birthTimeStr, Student student) {
        if (StringUtils.isNotBlank(birthTimeStr)) {
            try {
                student.setBirthTime(LocalDateTime.parse(birthTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                try {
                    student.setBirthTime(LocalDateTime.parse(birthTimeStr, DATE_TIME_FMT));
                } catch (Exception e2) {
                    log.warn("无法解析出生日期: {}", birthTimeStr);
                }
            }
        }
    }

    /**
     * 通用问卷数据保存方法
     */
    @SuppressWarnings("unchecked")
    private <T> void saveQuestionnaireData(
            com.baomidou.mybatisplus.core.mapper.BaseMapper<T> mapper,
            Long studentId,
            Map<String, Object> data,
            Class<T> clazz,
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, Long> studentIdGetter) {

        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(studentIdGetter, studentId);
        T existing = mapper.selectOne(wrapper);

        if (existing != null) {
            // 更新
            org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
            org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(existing);
            binder.bind(pvs);
            // 更新修改时间
            try {
                clazz.getMethod("setModifyTime", LocalDateTime.class).invoke(existing, LocalDateTime.now());
            } catch (Exception ignored) {}
            mapper.updateById(existing);
        } else {
            // 新增
            try {
                T entity = clazz.getDeclaredConstructor().newInstance();
                org.springframework.beans.MutablePropertyValues pvs = new org.springframework.beans.MutablePropertyValues(data);
                org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(entity);
                binder.bind(pvs);
                // 通过反射设置studentId
                clazz.getMethod("setStudentId", Long.class).invoke(entity, studentId);
                clazz.getMethod("setCTime", LocalDateTime.class).invoke(entity, LocalDateTime.now());
                clazz.getMethod("setModifyTime", LocalDateTime.class).invoke(entity, LocalDateTime.now());
                clazz.getMethod("setDelFlg", String.class).invoke(entity, "1");
                mapper.insert(entity);
            } catch (Exception e) {
                throw new BusinessException("保存问卷数据失败: " + e.getMessage());
            }
        }
    }
}
