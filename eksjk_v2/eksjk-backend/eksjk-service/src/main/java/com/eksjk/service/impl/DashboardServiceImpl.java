package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.entity.User;
import com.eksjk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现
 * <p>统计病例总数、新增趋势、待随访数量、疾病分布及用户角色分布</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final Map<String, String> DISEASE_NAME_MAP = Map.of(
            "10000001", "性发育异常(DSD)",
            "10000002", "遗传性骨病(FSS)",
            "10000003", "中枢性性早熟(CPP)",
            "10000004", "McCune-Albright(MAS)",
            "10000005", "小于胎龄儿(SGA)",
            "10000006", "家族性矮小(SSS)",
            "10000007", "E路童萌(ELTM)"
    );

    /** 待随访阈值（天）：最近多少天内无随访记录视为待随访 */
    private static final int PENDING_FOLLOWUP_DAYS = 30;

    private final PatientMapper patientMapper;
    private final PatientFollowUpMapper followUpMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        // 本周起止时间（用于本月新增）
        LocalDateTime monthStart = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // ==================== 1. 病例总数 ====================
        Long totalCases = patientMapper.selectCount(
                new LambdaQueryWrapper<Patient>().eq(Patient::getDelFlg, "1"));
        result.put("totalCases", totalCases);

        // ==================== 2. 本月新增 ====================
        Long monthlyNewCases = patientMapper.selectCount(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDelFlg, "1")
                        .ge(Patient::getCTime, monthStart));
        result.put("monthlyNewCases", monthlyNewCases);

        // ==================== 3. 待随访（真实查询） ====================
        long pendingFollowups = calcPendingFollowups();
        result.put("pendingFollowups", pendingFollowups);

        // ==================== 4. 注册用户数 ====================
        Long totalUsers = userMapper.selectCount(null);
        result.put("totalUsers", totalUsers);

        // ==================== 5. 疾病分布 ====================
        result.put("diseaseDistribution", getDiseaseDistribution());

        // ==================== 6. 用户角色分布 ====================
        result.put("userRoleDistribution", getUserRoleDistribution());

        return result;
    }

    /**
     * 计算待随访数量
     * 规则：有效患者中，没有随访记录或最近一次随访距今超过阈值天数的记为待随访
     */
    private long calcPendingFollowups() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(PENDING_FOLLOWUP_DAYS);

        // 获取最近N天内有随访记录的患者ID列表
        LambdaQueryWrapper<PatientFollowUp> recentWrapper = new LambdaQueryWrapper<>();
        recentWrapper.eq(PatientFollowUp::getDelFlg, "1")
                .ge(PatientFollowUp::getFollTime, threshold)
                .isNotNull(PatientFollowUp::getPatientId);
        List<Long> recentPatientIds = followUpMapper.selectList(recentWrapper)
                .stream()
                .map(PatientFollowUp::getPatientId)
                .distinct()
                .toList();

        // 总有效患者数 - 近期有随访的患者数 = 待随访数
        Long activeTotal = patientMapper.selectCount(
                new LambdaQueryWrapper<Patient>().eq(Patient::getDelFlg, "1"));

        return Math.max(0, activeTotal - recentPatientIds.size());
    }

    /**
     * 各疾病类型病例数量分布
     */
    private Map<String, Long> getDiseaseDistribution() {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getDelFlg, "1")
                .isNotNull(Patient::getDisClass)
                .ne(Patient::getDisClass, "");
        List<Patient> patients = patientMapper.selectList(wrapper);

        return patients.stream()
                .collect(Collectors.groupingBy(
                        p -> DISEASE_NAME_MAP.getOrDefault(p.getDisClass(), "未知类型(" + p.getDisClass() + ")"),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * 各角色用户数量分布
     */
    private Map<String, Long> getUserRoleDistribution() {
        List<User> users = userMapper.selectList(null);

        Map<String, String> roleNameMap = Map.of(
                "super_admin", "超级管理员",
                "hospital_admin", "医院管理员",
                "doctor", "普通医生",
                "parent", "家长"
        );

        return users.stream()
                .collect(Collectors.groupingBy(
                        u -> roleNameMap.getOrDefault(u.getRoleCode(), u.getRoleCode() == null ? "未分配" : u.getRoleCode()),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }
}
