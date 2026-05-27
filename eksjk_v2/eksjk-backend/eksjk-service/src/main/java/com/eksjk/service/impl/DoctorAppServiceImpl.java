package com.eksjk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.constant.RoleConstants;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.DoctorAppLoginDTO;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.DoctorAppLoginVO;
import com.eksjk.service.DoctorAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序医生端服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorAppServiceImpl implements DoctorAppService {

    private final UserMapper userMapper;
    private final PatientMapper patientMapper;
    private final PatientFollowUpMapper patientFollowUpMapper;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Value("${miniapp.doctor.appid:}")
    private String doctorAppid;

    @Value("${miniapp.doctor.secret:}")
    private String doctorSecret;

    // ==================== 登录认证 ====================

    @Override
    public DoctorAppLoginVO wxLogin(DoctorAppLoginDTO loginDTO) {
        // 调用微信接口获取 openid
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                doctorAppid, doctorSecret, loginDTO.getCode());

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, Object> wxResult = restTemplate.getForObject(url, Map.class);

        if (wxResult == null || !wxResult.containsKey("openid")) {
            throw new BusinessException("微信登录失败，无法获取 openid");
        }

        String openid = (String) wxResult.get("openid");

        // 查找已绑定该 openid 的医生账号
        User doctor = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWxOpenid, openid));

        if (doctor == null) {
            // 未绑定，返回 needBind=true
            DoctorAppLoginVO vo = new DoctorAppLoginVO();
            vo.setNeedBind(true);
            vo.setOpenid(openid);
            return vo;
        }

        // 检查账号状态
        if (doctor.getIsActive() == null || !doctor.getIsActive()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // Sa-Token 登录
        return doLogin(doctor);
    }

    @Override
    public DoctorAppLoginVO accountLogin(DoctorAppLoginDTO loginDTO) {
        if (StringUtils.isBlank(loginDTO.getUsername()) || StringUtils.isBlank(loginDTO.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }

        User doctor = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, loginDTO.getUsername()));

        if (doctor == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号状态
        if (doctor.getIsActive() == null || !doctor.getIsActive()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 校验密码
        boolean passwordMatch = false;
        try {
            passwordMatch = PASSWORD_ENCODER.matches(loginDTO.getPassword(), doctor.getPassword());
        } catch (Exception e) {
            log.debug("BCrypt 校验异常: {}", e.getMessage());
        }

        // 兼容 Django pbkdf2_sha256 格式
        if (!passwordMatch && doctor.getPassword() != null && doctor.getPassword().startsWith("pbkdf2_sha256$")) {
            passwordMatch = verifyDjangoPassword(loginDTO.getPassword(), doctor.getPassword());
            if (passwordMatch) {
                doctor.setPassword(PASSWORD_ENCODER.encode(loginDTO.getPassword()));
                userMapper.updateById(doctor);
            }
        }

        if (!passwordMatch) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查角色（必须是医生或管理员）
        String roleCode = resolveRoleCode(doctor);
        if (RoleConstants.PARENT.equals(roleCode)) {
            throw new BusinessException("家长账号请使用家长端小程序");
        }

        return doLogin(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DoctorAppLoginVO bindWxAccount(DoctorAppLoginDTO loginDTO) {
        if (StringUtils.isBlank(loginDTO.getOpenid())) {
            throw new BusinessException("微信信息缺失，请重新登录");
        }
        if (StringUtils.isBlank(loginDTO.getUsername()) || StringUtils.isBlank(loginDTO.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }

        // 检查 openid 是否已被其他账号绑定
        User existBind = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWxOpenid, loginDTO.getOpenid()));
        if (existBind != null) {
            throw new BusinessException("该微信已绑定其他账号");
        }

        // 验证账号密码
        User doctor = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, loginDTO.getUsername()));
        if (doctor == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (doctor.getIsActive() == null || !doctor.getIsActive()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        boolean passwordMatch = false;
        try {
            passwordMatch = PASSWORD_ENCODER.matches(loginDTO.getPassword(), doctor.getPassword());
        } catch (Exception e) {
            log.debug("BCrypt 校验异常: {}", e.getMessage());
        }

        if (!passwordMatch && doctor.getPassword() != null && doctor.getPassword().startsWith("pbkdf2_sha256$")) {
            passwordMatch = verifyDjangoPassword(loginDTO.getPassword(), doctor.getPassword());
            if (passwordMatch) {
                doctor.setPassword(PASSWORD_ENCODER.encode(loginDTO.getPassword()));
            }
        }

        if (!passwordMatch) {
            throw new BusinessException("用户名或密码错误");
        }

        String roleCode = resolveRoleCode(doctor);
        if (RoleConstants.PARENT.equals(roleCode)) {
            throw new BusinessException("家长账号请使用家长端小程序");
        }

        // 绑定微信 openid
        doctor.setWxOpenid(loginDTO.getOpenid());
        userMapper.updateById(doctor);

        log.info("医生绑定微信成功: username={}, openid={}", doctor.getUsername(), loginDTO.getOpenid());
        return doLogin(doctor);
    }

    // ==================== 个人信息 ====================

    @Override
    public Map<String, Object> getProfile(Long doctorId) {
        User doctor = userMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生信息不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", HashidsUtil.encode(doctor.getId()));
        result.put("username", doctor.getUsername());
        result.put("realName", doctor.getName());
        result.put("sex", doctor.getSex());
        result.put("phone", doctor.getPhone());
        result.put("email", doctor.getEmail());
        result.put("department", doctor.getDepartment());
        result.put("professional", doctor.getProfessional());
        result.put("professionalName", getProfessionalName(doctor.getProfessional()));
        result.put("jobNumber", doctor.getJobNumber());
        result.put("hospitalId", doctor.getUnit());
        result.put("roleCode", resolveRoleCode(doctor));
        return result;
    }

    // ==================== 工作台 ====================

    @Override
    public Map<String, Object> getDashboardStats(Long doctorId) {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 患者总数
        long totalPatients = patientMapper.selectCount(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDelFlg, "1")
                        .eq(Patient::getImpPer, String.valueOf(doctorId)));

        // 本月新增
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long monthNew = patientMapper.selectCount(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDelFlg, "1")
                        .eq(Patient::getImpPer, String.valueOf(doctorId))
                        .ge(Patient::getCTime, monthStart));

        // 待随访数（距上次随访超过 90 天的患者）
        // 简化实现：统计有随访记录但最近 90 天无新随访的患者数
        stats.put("totalPatients", totalPatients);
        stats.put("monthNew", monthNew);
        stats.put("pendingFollowUp", 0); // 简化，后续可优化
        stats.put("pendingReview", 0); // 待审核数据数量

        // 最近 7 天新增病例
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Patient> recentPatients = patientMapper.selectList(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDelFlg, "1")
                        .eq(Patient::getImpPer, String.valueOf(doctorId))
                        .ge(Patient::getCTime, weekAgo)
                        .orderByDesc(Patient::getCTime)
                        .last("LIMIT 5"));

        List<Map<String, Object>> recentList = recentPatients.stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", HashidsUtil.encode(p.getId()));
            item.put("name", p.getName());
            item.put("sex", p.getSex());
            item.put("disClass", p.getDisClass());
            item.put("cTime", p.getCTime() != null ? p.getCTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) : "");
            return item;
        }).collect(Collectors.toList());

        stats.put("recentPatients", recentList);
        return stats;
    }

    // ==================== 患者管理 ====================

    @Override
    public Map<String, Object> getPatientList(Long doctorId, String keyword, String disClass, int pageNum, int pageSize) {
        Page<Patient> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getDelFlg, "1")
                .eq(Patient::getImpPer, String.valueOf(doctorId));

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Patient::getName, keyword)
                    .or().like(Patient::getCaseNum, keyword)
                    .or().like(Patient::getMedrecNum, keyword));
        }

        if (StringUtils.isNotBlank(disClass)) {
            wrapper.eq(Patient::getDisClass, disClass);
        }

        wrapper.orderByDesc(Patient::getModifyTime);

        Page<Patient> result = patientMapper.selectPage(page, wrapper);

        List<Map<String, Object>> records = result.getRecords().stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", HashidsUtil.encode(p.getId()));
            item.put("name", p.getName());
            item.put("sex", p.getSex());
            item.put("sexName", "1".equals(p.getSex()) ? "男" : "2".equals(p.getSex()) ? "女" : "未知");
            item.put("caseNum", p.getCaseNum());
            item.put("medrecNum", p.getMedrecNum());
            item.put("disClass", p.getDisClass());
            item.put("disClassName", getDisClassName(p.getDisClass()));
            item.put("height", p.getHeight());
            item.put("weight", p.getWeight());
            if (p.getBirthTime() != null) {
                item.put("birthTime", p.getBirthTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                long months = java.time.temporal.ChronoUnit.MONTHS.between(p.getBirthTime(), LocalDateTime.now());
                long years = months / 12;
                long remainMonths = months % 12;
                item.put("ageDesc", years > 0 ? years + "岁" + (remainMonths > 0 ? remainMonths + "月" : "") : remainMonths + "月");
            }
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("records", records);
        resultMap.put("total", result.getTotal());
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);
        return resultMap;
    }

    @Override
    public Map<String, Object> getPatientDetail(Long doctorId, Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || !"1".equals(patient.getDelFlg())) {
            throw new BusinessException("患者记录不存在");
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", HashidsUtil.encode(patient.getId()));
        detail.put("name", patient.getName());
        detail.put("sex", patient.getSex());
        detail.put("sexName", "1".equals(patient.getSex()) ? "男" : "2".equals(patient.getSex()) ? "女" : "未知");
        detail.put("caseNum", patient.getCaseNum());
        detail.put("medrecNum", patient.getMedrecNum());
        detail.put("disClass", patient.getDisClass());
        detail.put("disClassName", getDisClassName(patient.getDisClass()));
        detail.put("height", patient.getHeight());
        detail.put("weight", patient.getWeight());
        detail.put("selfTel", patient.getSelfTel());
        detail.put("fht", patient.getFht());
        detail.put("mht", patient.getMht());

        if (patient.getBirthTime() != null) {
            detail.put("birthTime", patient.getBirthTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            long months = java.time.temporal.ChronoUnit.MONTHS.between(patient.getBirthTime(), LocalDateTime.now());
            long years = months / 12;
            long remainMonths = months % 12;
            detail.put("ageDesc", years > 0 ? years + "岁" + (remainMonths > 0 ? remainMonths + "月" : "") : remainMonths + "月");
        }

        // 随访记录列表
        List<PatientFollowUp> followUps = patientFollowUpMapper.selectList(
                new LambdaQueryWrapper<PatientFollowUp>()
                        .eq(PatientFollowUp::getPatientId, patientId)
                        .eq(PatientFollowUp::getDelFlg, "1")
                        .orderByDesc(PatientFollowUp::getFollTime));

        List<Map<String, Object>> followUpList = followUps.stream().map(f -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", HashidsUtil.encode(f.getId()));
            item.put("ht", f.getHt());
            item.put("wt", f.getWt());
            item.put("follTime", f.getFollTime() != null ? f.getFollTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
            // 计算 BMI
            try {
                if (StringUtils.isNotBlank(f.getHt()) && StringUtils.isNotBlank(f.getWt())) {
                    BigDecimal h = new BigDecimal(f.getHt()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    BigDecimal w = new BigDecimal(f.getWt());
                    BigDecimal bmi = w.divide(h.multiply(h), 1, RoundingMode.HALF_UP);
                    item.put("bmi", bmi.toString());
                }
            } catch (Exception e) {
                item.put("bmi", "");
            }
            return item;
        }).collect(Collectors.toList());

        detail.put("followUps", followUpList);
        return detail;
    }

    // ==================== 快捷随访 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quickCreateFollowUp(Long doctorId, Map<String, Object> data) {
        String patientIdStr = (String) data.get("patientId");
        if (StringUtils.isBlank(patientIdStr)) {
            throw new BusinessException("患者ID不能为空");
        }

        Long patientId = HashidsUtil.decode(patientIdStr);
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || !"1".equals(patient.getDelFlg())) {
            throw new BusinessException("患者记录不存在");
        }

        PatientFollowUp followUp = new PatientFollowUp();
        followUp.setPatientId(patientId);
        followUp.setHt((String) data.get("height"));
        followUp.setWt((String) data.get("weight"));

        String measureDate = (String) data.get("measureDate");
        if (StringUtils.isNotBlank(measureDate)) {
            followUp.setFollTime(LocalDateTime.parse(measureDate + "T00:00:00"));
        } else {
            followUp.setFollTime(LocalDateTime.now());
        }

        followUp.setDelFlg("1");
        patientFollowUpMapper.insert(followUp);

        // 更新患者当前身高体重
        if (StringUtils.isNotBlank((String) data.get("height"))) {
            patient.setHeight((String) data.get("height"));
        }
        if (StringUtils.isNotBlank((String) data.get("weight"))) {
            patient.setWeight((String) data.get("weight"));
        }
        patient.setModifyTime(LocalDateTime.now());
        patientMapper.updateById(patient);

        log.info("医生快捷新增随访: doctorId={}, patientId={}", doctorId, patientId);
    }

    // ==================== 数据审核 ====================

    @Override
    public List<Map<String, Object>> getPendingReviewList(Long doctorId) {
        // 简化实现：查询绑定该医生的家长提交的待审核数据
        // 实际需要查询 ChartUser 表中 doctor=doctorId 的家长，再查询其提交的评测数据
        return new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewData(Long doctorId, Long recordId, boolean approved, String rejectReason) {
        // 简化实现：更新审核状态
        log.info("医生审核数据: doctorId={}, recordId={}, approved={}", doctorId, recordId, approved);
    }

    // ==================== 统计分析 ====================

    @Override
    public Map<String, Object> getStatistics(Long doctorId) {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 疾病分布
        List<Patient> allPatients = patientMapper.selectList(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDelFlg, "1")
                        .eq(Patient::getImpPer, String.valueOf(doctorId)));

        // 按疾病类型分组统计
        Map<String, Long> disDistribution = allPatients.stream()
                .filter(p -> StringUtils.isNotBlank(p.getDisClass()))
                .collect(Collectors.groupingBy(Patient::getDisClass, Collectors.counting()));

        List<Map<String, Object>> disChart = new ArrayList<>();
        disDistribution.forEach((disClass, count) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", getDisClassName(disClass));
            item.put("value", count);
            disChart.add(item);
        });
        stats.put("diseaseDistribution", disChart);

        // 性别分布
        long maleCount = allPatients.stream().filter(p -> "1".equals(p.getSex())).count();
        long femaleCount = allPatients.stream().filter(p -> "2".equals(p.getSex())).count();
        List<Map<String, Object>> genderChart = new ArrayList<>();
        genderChart.add(Map.of("name", "男", "value", maleCount));
        genderChart.add(Map.of("name", "女", "value", femaleCount));
        stats.put("genderDistribution", genderChart);

        // 近 6 个月患者增长趋势
        List<Map<String, Object>> trendChart = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1);
            long count = allPatients.stream()
                    .filter(p -> p.getCTime() != null && !p.getCTime().isBefore(start) && p.getCTime().isBefore(end))
                    .count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", start.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            item.put("count", count);
            trendChart.add(item);
        }
        stats.put("monthlyTrend", trendChart);

        stats.put("totalPatients", allPatients.size());
        return stats;
    }

    // ==================== 二维码 ====================

    @Override
    public Map<String, Object> getQrCodeData(Long doctorId) {
        User doctor = userMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生信息不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("doctorId", HashidsUtil.encode(doctorId));
        result.put("realName", doctor.getName());
        result.put("department", doctor.getDepartment());
        result.put("hospitalId", doctor.getUnit());
        // 二维码内容：医生ID的 Hashids 编码，家长端扫码后用于绑定
        result.put("qrContent", "eksjk_doctor:" + HashidsUtil.encode(doctorId));
        return result;
    }

    // ==================== 私有方法 ====================

    private DoctorAppLoginVO doLogin(User doctor) {
        StpUtil.login(doctor.getId());
        String roleCode = resolveRoleCode(doctor);
        StpUtil.getSession()
                .set("roleCode", roleCode)
                .set("hospitalId", doctor.getUnit())
                .set("username", doctor.getUsername())
                .set("realName", doctor.getName());

        doctor.setLastLogin(LocalDateTime.now());
        userMapper.updateById(doctor);

        DoctorAppLoginVO vo = new DoctorAppLoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setNeedBind(false);

        Map<String, Object> doctorInfo = new LinkedHashMap<>();
        doctorInfo.put("id", HashidsUtil.encode(doctor.getId()));
        doctorInfo.put("realName", doctor.getName());
        doctorInfo.put("department", doctor.getDepartment());
        doctorInfo.put("professional", doctor.getProfessional());
        doctorInfo.put("professionalName", getProfessionalName(doctor.getProfessional()));
        doctorInfo.put("roleCode", roleCode);
        vo.setDoctorInfo(doctorInfo);

        log.info("医生端登录成功: username={}, role={}", doctor.getUsername(), roleCode);
        return vo;
    }

    private String resolveRoleCode(User user) {
        if (user.getRoleCode() != null && !user.getRoleCode().isEmpty()) {
            return user.getRoleCode();
        }
        if (user.getLevel() != null && user.getLevel() == 1) {
            return RoleConstants.SUPER_ADMIN;
        }
        return RoleConstants.DOCTOR;
    }

    private boolean verifyDjangoPassword(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String hash = parts[3];
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            java.security.spec.KeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    rawPassword.toCharArray(), salt.getBytes(), iterations, 256);
            byte[] derived = factory.generateSecret(spec).getEncoded();
            String computedHash = java.util.Base64.getEncoder().encodeToString(derived);
            return hash.equals(computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String getDisClassName(String disClass) {
        if (disClass == null) return "未知";
        return switch (disClass) {
            case "10000001" -> "性发育异常(DSD)";
            case "10000002" -> "遗传性骨病(FSS)";
            case "10000003" -> "中枢性性早熟(CPP)";
            case "10000004" -> "McCune-Albright(MAS)";
            case "10000005" -> "小于胎龄儿(SGA)";
            case "10000006" -> "家族性矮小(SSS)";
            case "10000007" -> "E路童萌(ELTM)";
            default -> "其他";
        };
    }

    private String getProfessionalName(String code) {
        if (code == null) return null;
        return switch (code) {
            case "10040001" -> "助理医师";
            case "10040002" -> "医师";
            case "10040003" -> "主治医师";
            case "10040004" -> "副主任医师";
            case "10040005" -> "主任医师";
            default -> null;
        };
    }
}
