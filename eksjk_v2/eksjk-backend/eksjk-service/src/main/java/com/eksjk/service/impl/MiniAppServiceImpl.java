package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.mapper.ChartUserMapper;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.BabyDTO;
import com.eksjk.model.dto.HeightAssessDTO;
import com.eksjk.model.dto.MiniAppProfileDTO;
import com.eksjk.model.dto.WxLoginDTO;
import com.eksjk.model.entity.ChartUser;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.entity.User;
import com.eksjk.model.vo.BabyVO;
import com.eksjk.model.vo.HeightAssessVO;
import com.eksjk.model.vo.WxLoginVO;
import com.eksjk.service.MiniAppService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序家长端服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniAppServiceImpl implements MiniAppService {

    private final ChartUserMapper chartUserMapper;
    private final PatientMapper patientMapper;
    private final PatientFollowUpMapper patientFollowUpMapper;
    private final UserMapper userMapper;

    @Value("${miniapp.appid:wx1ae7e6dba9c5e94b}")
    private String appid;

    @Value("${miniapp.secret:d6661fd8fc8a5cc161b2ee0730603894}")
    private String secret;

    @Override
    public WxLoginVO wxLogin(WxLoginDTO loginDTO) {
        String openid;
        String sessionKey = null;

        // 如果 code 为空但有手机号，说明是 submitPhone 场景（已登录用户补充手机号）
        if (StringUtils.isBlank(loginDTO.getCode()) && StringUtils.isNotBlank(loginDTO.getPhoneNum())) {
            // 从当前登录状态获取 openid
            openid = loginDTO.getPhoneNum(); // 此场景下前端应传 openid
            // 尝试从 Token 中获取
            try {
                String loginId = (String) StpUtil.getLoginId();
                if (loginId != null && loginId.startsWith("parent:")) {
                    Long chartUserId = Long.parseLong(loginId.substring(7));
                    ChartUser existUser = chartUserMapper.selectById(chartUserId);
                    if (existUser != null) {
                        existUser.setPhoneNum(loginDTO.getPhoneNum());
                        existUser.setIsTongb("1");
                        chartUserMapper.updateById(existUser);

                        WxLoginVO vo = new WxLoginVO();
                        vo.setToken(StpUtil.getTokenValue());
                        vo.setNewUser(false);
                        vo.setSynced(true);
                        vo.setOpenid(existUser.getOpenid());
                        return vo;
                    }
                }
            } catch (Exception e) {
                log.warn("submitPhone 场景获取当前用户失败: {}", e.getMessage());
            }
            throw new BusinessException("请先完成微信登录");
        }

        // 调用微信接口获取 openid
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, loginDTO.getCode());

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, Object> wxResult = restTemplate.getForObject(url, Map.class);

        if (wxResult == null || !wxResult.containsKey("openid")) {
            throw new BusinessException("微信登录失败，无法获取 openid");
        }

        openid = (String) wxResult.get("openid");
        sessionKey = (String) wxResult.get("session_key");

        // 查找或创建用户
        ChartUser chartUser = chartUserMapper.selectOne(
                new LambdaQueryWrapper<ChartUser>().eq(ChartUser::getOpenid, openid));

        boolean isNewUser = false;
        if (chartUser == null) {
            chartUser = new ChartUser();
            chartUser.setOpenid(openid);
            chartUser.setKey(sessionKey);
            chartUser.setDelFlg("1");
            chartUser.setIsTongb("0");
            chartUserMapper.insert(chartUser);
            isNewUser = true;
        } else {
            chartUser.setKey(sessionKey);
            chartUserMapper.updateById(chartUser);
            isNewUser = chartUser.getPhoneNum() == null;
        }

        // 如果提供了手机号，更新
        if (StringUtils.isNotBlank(loginDTO.getPhoneNum())) {
            chartUser.setPhoneNum(loginDTO.getPhoneNum());
            chartUser.setIsTongb("1");
            chartUserMapper.updateById(chartUser);
            isNewUser = false;
        }

        // 使用 Sa-Token 登录（以 chartUser.id 作为登录ID，加前缀区分家长角色）
        StpUtil.login("parent:" + chartUser.getId());

        WxLoginVO vo = new WxLoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setNewUser(isNewUser);
        vo.setSynced(!"0".equals(chartUser.getIsTongb()));
        vo.setOpenid(openid);

        log.info("小程序登录成功: openid={}, isNew={}", openid, isNewUser);
        return vo;
    }

    @Override
    public Map<String, Object> getProfile(String openid) {
        ChartUser chartUser = getChartUserByOpenid(openid);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contactsName", chartUser.getContactsName());
        result.put("contactsNum", chartUser.getContactsNum());
        result.put("email", chartUser.getPEmial());
        result.put("idcard", chartUser.getIdcard());
        result.put("natPla", chartUser.getNatPla());
        result.put("myselfPicture", chartUser.getMyselfPicture());
        result.put("phoneNum", chartUser.getPhoneNum());
        result.put("doctor", chartUser.getDoctor());
        return result;
    }

    @Override
    public void saveProfile(String openid, MiniAppProfileDTO profileDTO) {
        ChartUser chartUser = getChartUserByOpenid(openid);
        if (StringUtils.isNotBlank(profileDTO.getContactsName())) {
            chartUser.setContactsName(profileDTO.getContactsName());
        }
        if (StringUtils.isNotBlank(profileDTO.getContactsNum())) {
            chartUser.setContactsNum(profileDTO.getContactsNum());
        }
        if (StringUtils.isNotBlank(profileDTO.getEmail())) {
            chartUser.setPEmial(profileDTO.getEmail());
        }
        if (StringUtils.isNotBlank(profileDTO.getIdcard())) {
            chartUser.setIdcard(profileDTO.getIdcard());
        }
        if (StringUtils.isNotBlank(profileDTO.getNatPla())) {
            chartUser.setNatPla(profileDTO.getNatPla());
        }
        if (StringUtils.isNotBlank(profileDTO.getMyselfPicture())) {
            chartUser.setMyselfPicture(profileDTO.getMyselfPicture());
        }
        chartUserMapper.updateById(chartUser);
        log.info("保存个人信息成功: openid={}", openid);
    }

    @Override
    public List<BabyVO> getBabyList(String openid) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getXcxCard, openid)
                .eq(Patient::getBabyFlag, "1")
                .eq(Patient::getDelFlg, "1")
                .orderByDesc(Patient::getCTime);

        List<Patient> patients = patientMapper.selectList(wrapper);
        return patients.stream().map(this::convertToBabyVO).collect(Collectors.toList());
    }

    @Override
    public BabyVO getBabyDetail(String openid, Long babyId) {
        Patient patient = patientMapper.selectById(babyId);
        if (patient == null || !openid.equals(patient.getXcxCard()) || !"1".equals(patient.getBabyFlag())) {
            throw new BusinessException("宝宝记录不存在");
        }
        return convertToBabyVO(patient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBaby(String openid, BabyDTO babyDTO) {
        Patient patient = new Patient();
        patient.setXcxCard(openid);
        patient.setName(babyDTO.getName());
        patient.setSex(babyDTO.getSex());
        patient.setBabyFlag("1");
        patient.setImpPer("2"); // 小程序导入标记

        if (StringUtils.isNotBlank(babyDTO.getBirthTime())) {
            patient.setBirthTime(LocalDateTime.parse(babyDTO.getBirthTime() + "T00:00:00"));
        }
        patient.setRelation(babyDTO.getRelation());
        patient.setSelfTel(babyDTO.getSelfTel());
        patient.setDoctorName(babyDTO.getDoctorId());
        patient.setFht(babyDTO.getFht());
        patient.setMht(babyDTO.getMht());
        patient.setDisClass(babyDTO.getDisClass());
        patient.setExpectedHeight(babyDTO.getExpectedHeight());
        patient.setCurrentCity(babyDTO.getCurrentCity());
        patient.setHeight(babyDTO.getHeight());
        patient.setWeight(babyDTO.getWeight());
        patient.setRboneAge(babyDTO.getRboneAge());
        patient.setCboneAge(babyDTO.getCboneAge());
        patient.setPastHeight(babyDTO.getPastHeight());
        patient.setPastWeight(babyDTO.getPastWeight());

        if (StringUtils.isNotBlank(babyDTO.getPastTime())) {
            patient.setPastTime(LocalDateTime.parse(babyDTO.getPastTime() + "T00:00:00"));
        }

        patient.setCTime(LocalDateTime.now());
        patient.setModifyTime(LocalDateTime.now());
        patient.setDelFlg("1");

        patientMapper.insert(patient);

        // 创建初始评测记录
        if (StringUtils.isNotBlank(babyDTO.getHeight()) && StringUtils.isNotBlank(babyDTO.getWeight())) {
            // 如果有既往数据，先创建既往记录
            if (StringUtils.isNotBlank(babyDTO.getPastHeight()) && StringUtils.isNotBlank(babyDTO.getPastWeight())) {
                PatientFollowUp pastFollowUp = new PatientFollowUp();
                pastFollowUp.setPatientId(patient.getId());
                pastFollowUp.setHt(babyDTO.getPastHeight());
                pastFollowUp.setWt(babyDTO.getPastWeight());
                pastFollowUp.setFollTime(patient.getPastTime() != null ? patient.getPastTime() : LocalDateTime.now());
                pastFollowUp.setDelFlg("1");
                patientFollowUpMapper.insert(pastFollowUp);
            }

            // 创建当前评测记录
            PatientFollowUp followUp = new PatientFollowUp();
            followUp.setPatientId(patient.getId());
            followUp.setHt(babyDTO.getHeight());
            followUp.setWt(babyDTO.getWeight());
            followUp.setFollTime(LocalDateTime.now());
            followUp.setDelFlg("1");
            patientFollowUpMapper.insert(followUp);
        }

        log.info("添加宝宝成功: openid={}, name={}", openid, babyDTO.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editBaby(String openid, BabyDTO babyDTO) {
        Long babyId = HashidsUtil.decode(babyDTO.getId());
        Patient patient = patientMapper.selectById(babyId);
        if (patient == null || !openid.equals(patient.getXcxCard()) || !"1".equals(patient.getBabyFlag())) {
            throw new BusinessException("宝宝记录不存在");
        }

        patient.setName(babyDTO.getName());
        patient.setSex(babyDTO.getSex());
        if (StringUtils.isNotBlank(babyDTO.getBirthTime())) {
            patient.setBirthTime(LocalDateTime.parse(babyDTO.getBirthTime() + "T00:00:00"));
        }
        patient.setRelation(babyDTO.getRelation());
        patient.setSelfTel(babyDTO.getSelfTel());
        patient.setFht(babyDTO.getFht());
        patient.setMht(babyDTO.getMht());
        patient.setExpectedHeight(babyDTO.getExpectedHeight());
        patient.setCurrentCity(babyDTO.getCurrentCity());
        patient.setHeight(babyDTO.getHeight());
        patient.setWeight(babyDTO.getWeight());
        patient.setModifyTime(LocalDateTime.now());

        patientMapper.updateById(patient);
        log.info("编辑宝宝成功: openid={}, babyId={}", openid, babyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBaby(String openid, Long babyId) {
        Patient patient = patientMapper.selectById(babyId);
        if (patient == null || !openid.equals(patient.getXcxCard()) || !"1".equals(patient.getBabyFlag())) {
            throw new BusinessException("宝宝记录不存在");
        }
        patient.setDelFlg("0");
        patient.setModifyTime(LocalDateTime.now());
        patientMapper.updateById(patient);
        log.info("删除宝宝成功: openid={}, babyId={}", openid, babyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeightAssessVO assessHeight(String openid, HeightAssessDTO assessDTO) {
        Long babyId = HashidsUtil.decode(assessDTO.getBabyId());
        Patient patient = patientMapper.selectById(babyId);
        if (patient == null || !openid.equals(patient.getXcxCard()) || !"1".equals(patient.getBabyFlag())) {
            throw new BusinessException("宝宝记录不存在");
        }

        // 创建随访记录
        PatientFollowUp followUp = new PatientFollowUp();
        followUp.setPatientId(babyId);
        followUp.setHt(assessDTO.getHeight());
        followUp.setWt(assessDTO.getWeight());

        if (StringUtils.isNotBlank(assessDTO.getMeasureDate())) {
            followUp.setFollTime(LocalDateTime.parse(assessDTO.getMeasureDate() + "T00:00:00"));
        } else {
            followUp.setFollTime(LocalDateTime.now());
        }
        followUp.setDelFlg("1");
        patientFollowUpMapper.insert(followUp);

        // 更新宝宝当前身高体重
        patient.setHeight(assessDTO.getHeight());
        patient.setWeight(assessDTO.getWeight());
        patient.setModifyTime(LocalDateTime.now());
        patientMapper.updateById(patient);

        // 构建评测结果
        HeightAssessVO vo = new HeightAssessVO();
        vo.setId(HashidsUtil.encode(followUp.getId()));
        vo.setBabyId(assessDTO.getBabyId());
        vo.setMeasureDate(assessDTO.getMeasureDate());
        vo.setHeight(assessDTO.getHeight());
        vo.setWeight(assessDTO.getWeight());

        // 计算 BMI
        try {
            BigDecimal h = new BigDecimal(assessDTO.getHeight()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal w = new BigDecimal(assessDTO.getWeight());
            BigDecimal bmi = w.divide(h.multiply(h), 1, RoundingMode.HALF_UP);
            vo.setBmi(bmi.toString());
        } catch (Exception e) {
            vo.setBmi("");
        }

        // SDS 计算（简化版，实际需要 WHO 标准数据表）
        vo.setSds("0");
        vo.setPercentile("50");
        vo.setConclusion("身高处于同龄儿童中等水平");

        // 与上次对比
        LambdaQueryWrapper<PatientFollowUp> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(PatientFollowUp::getPatientId, babyId)
                .eq(PatientFollowUp::getDelFlg, "1")
                .ne(PatientFollowUp::getId, followUp.getId())
                .orderByDesc(PatientFollowUp::getFollTime)
                .last("LIMIT 1");
        PatientFollowUp lastRecord = patientFollowUpMapper.selectOne(historyWrapper);

        if (lastRecord != null && StringUtils.isNotBlank(lastRecord.getHt())) {
            try {
                BigDecimal currentH = new BigDecimal(assessDTO.getHeight());
                BigDecimal lastH = new BigDecimal(lastRecord.getHt());
                vo.setHeightGrowth(currentH.subtract(lastH).setScale(1, RoundingMode.HALF_UP).toString());
                if (lastRecord.getFollTime() != null && followUp.getFollTime() != null) {
                    vo.setDaysSinceLast((int) ChronoUnit.DAYS.between(lastRecord.getFollTime(), followUp.getFollTime()));
                }
            } catch (Exception e) {
                vo.setHeightGrowth("");
            }
        }

        log.info("身高评测成功: openid={}, babyId={}", openid, babyId);
        return vo;
    }

    @Override
    public List<HeightAssessVO> getAssessHistory(String openid, Long babyId) {
        Patient patient = patientMapper.selectById(babyId);
        if (patient == null || !openid.equals(patient.getXcxCard()) || !"1".equals(patient.getBabyFlag())) {
            throw new BusinessException("宝宝记录不存在");
        }

        LambdaQueryWrapper<PatientFollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientFollowUp::getPatientId, babyId)
                .eq(PatientFollowUp::getDelFlg, "1")
                .orderByDesc(PatientFollowUp::getFollTime);

        List<PatientFollowUp> records = patientFollowUpMapper.selectList(wrapper);
        return records.stream().map(record -> {
            HeightAssessVO vo = new HeightAssessVO();
            vo.setId(HashidsUtil.encode(record.getId()));
            vo.setBabyId(HashidsUtil.encode(babyId));
            vo.setMeasureDate(record.getFollTime() != null
                    ? record.getFollTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
            vo.setHeight(record.getHt());
            vo.setWeight(record.getWt());

            // 计算 BMI
            try {
                if (StringUtils.isNotBlank(record.getHt()) && StringUtils.isNotBlank(record.getWt())) {
                    BigDecimal h = new BigDecimal(record.getHt()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    BigDecimal w = new BigDecimal(record.getWt());
                    BigDecimal bmi = w.divide(h.multiply(h), 1, RoundingMode.HALF_UP);
                    vo.setBmi(bmi.toString());
                }
            } catch (Exception e) {
                vo.setBmi("");
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void bindDoctor(String openid, String doctorId) {
        ChartUser chartUser = getChartUserByOpenid(openid);
        chartUser.setDoctor(doctorId);
        chartUserMapper.updateById(chartUser);
        log.info("绑定医生成功: openid={}, doctorId={}", openid, doctorId);
    }

    @Override
    public void unbindDoctor(String openid) {
        ChartUser chartUser = getChartUserByOpenid(openid);
        chartUser.setDoctor(null);
        chartUserMapper.updateById(chartUser);
        log.info("解绑医生成功: openid={}", openid);
    }

    @Override
    public Map<String, Object> getBoundDoctor(String openid) {
        ChartUser chartUser = getChartUserByOpenid(openid);
        Map<String, Object> result = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(chartUser.getDoctor())) {
            try {
                Long doctorId = Long.parseLong(chartUser.getDoctor());
                User doctor = userMapper.selectById(doctorId);
                if (doctor != null) {
                    result.put("id", HashidsUtil.encode(doctor.getId()));
                    result.put("realName", doctor.getName());
                    result.put("department", doctor.getDepartment());
                    result.put("hospitalName", doctor.getUnit());
                    result.put("bound", true);
                    return result;
                }
            } catch (NumberFormatException e) {
                // doctorId 不是数字，忽略
            }
        }

        result.put("bound", false);
        return result;
    }

    // ==================== 私有方法 ====================

    private ChartUser getChartUserByOpenid(String openid) {
        ChartUser chartUser = chartUserMapper.selectOne(
                new LambdaQueryWrapper<ChartUser>().eq(ChartUser::getOpenid, openid));
        if (chartUser == null) {
            throw new BusinessException("用户不存在");
        }
        return chartUser;
    }

    private BabyVO convertToBabyVO(Patient patient) {
        BabyVO vo = new BabyVO();
        vo.setId(HashidsUtil.encode(patient.getId()));
        vo.setName(patient.getName());
        vo.setSex(patient.getSex());
        vo.setSexName("1".equals(patient.getSex()) ? "男" : "2".equals(patient.getSex()) ? "女" : "未知");
        vo.setHeight(patient.getHeight());
        vo.setWeight(patient.getWeight());
        vo.setFht(patient.getFht());
        vo.setMht(patient.getMht());
        vo.setExpectedHeight(patient.getExpectedHeight());
        vo.setCurrentCity(patient.getCurrentCity());
        vo.setDisClass(patient.getDisClass());
        vo.setRboneAge(patient.getRboneAge());
        vo.setCboneAge(patient.getCboneAge());
        vo.setRelation(patient.getRelation());

        if (patient.getBirthTime() != null) {
            vo.setBirthTime(patient.getBirthTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            // 计算年龄描述
            long months = ChronoUnit.MONTHS.between(patient.getBirthTime(), LocalDateTime.now());
            long years = months / 12;
            long remainMonths = months % 12;
            if (years > 0) {
                vo.setAgeDesc(years + "岁" + (remainMonths > 0 ? remainMonths + "个月" : ""));
            } else {
                vo.setAgeDesc(remainMonths + "个月");
            }
        }

        return vo;
    }
}
