package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.GhAdverseEventMapper;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.mapper.UnitMapper;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.entity.GhAdverseEvent;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.entity.Unit;
import com.eksjk.model.entity.User;
import com.eksjk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    /** 未分配医院的归集名称 */
    private static final String UNASSIGNED = "未分配";

    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final GhAdverseEventMapper ghAdverseEventMapper;
    private final UnitMapper unitMapper;
    private final PatientFollowUpMapper followUpMapper;

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

        // ==================== 3. 不良反应上报次数（累计有效上报例数） ====================
        Long adverseEventCount = ghAdverseEventMapper.selectCount(
                new LambdaQueryWrapper<GhAdverseEvent>().eq(GhAdverseEvent::getIsDeleted, 0));
        result.put("adverseEventCount", adverseEventCount);

        // ==================== 4. 医生用户数（role_code=doctor 的未删除账号） ====================
        Long doctorUserCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRoleCode, "doctor"));
        result.put("doctorUserCount", doctorUserCount);

        // ==================== 5. 疾病分布 ====================
        result.put("diseaseDistribution", getDiseaseDistribution());

        // ==================== 6. 用户角色分布 ====================
        result.put("userRoleDistribution", getUserRoleDistribution());

        return result;
    }

    /**
     * 各疾病类型病例数量分布
     */
    private Map<String, Long> getDiseaseDistribution() {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        // 只查 dis_class 一列，避免加载全部 192 列到内存
        wrapper.select(Patient::getDisClass)
                .eq(Patient::getDelFlg, "1")
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

    @Override
    public List<Map<String, Object>> getHospitalStats() {
        // 医院清单
        List<Unit> units = unitMapper.selectList(new LambdaQueryWrapper<Unit>()
                .eq(Unit::getDelFlg, "1")
                .select(Unit::getId, Unit::getUnitName));

        // 有效病例按医院名分组计数（带 id 防止整行全空时 MyBatis 返回 null 元素）
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .select(Patient::getId, Patient::getHospitalName, Patient::getDoctorName));
        Map<String, Long> patientCountByHospital = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> hospitalKey(p.getHospitalName()),
                        Collectors.counting()));

        // 各医院参与医生数：该院病例中去重的录入医生（排除“未归属医生”）
        Map<String, Set<String>> doctorSetByHospital = new LinkedHashMap<>();
        for (Patient p : patients) {
            String doctor = normalizeDoctor(p.getDoctorName());
            if (doctor != null) {
                doctorSetByHospital.computeIfAbsent(hospitalKey(p.getHospitalName()), k -> new HashSet<>()).add(doctor);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        Set<String> listedNames = new HashSet<>();
        for (Unit unit : units) {
            if (isBlank(unit.getUnitName()) || !listedNames.add(unit.getUnitName())) {
                continue;
            }
            list.add(buildHospitalRow(unit.getUnitName(),
                    patientCountByHospital.getOrDefault(unit.getUnitName(), 0L),
                    doctorCountOf(doctorSetByHospital, unit.getUnitName())));
        }
        // 病例中出现但机构表里没有的医院名，也列出
        patientCountByHospital.forEach((name, cnt) -> {
            if (!UNASSIGNED.equals(name) && listedNames.add(name)) {
                list.add(buildHospitalRow(name, cnt, doctorCountOf(doctorSetByHospital, name)));
            }
        });
        // 未分配沉底
        Long unassigned = patientCountByHospital.get(UNASSIGNED);
        if (unassigned != null && unassigned > 0) {
            list.add(buildHospitalRow(UNASSIGNED, unassigned, doctorCountOf(doctorSetByHospital, UNASSIGNED)));
        }

        // 按入组数倒序，「未分配」始终最后
        list.sort((a, b) -> {
            boolean aUn = UNASSIGNED.equals(a.get("unitName"));
            boolean bUn = UNASSIGNED.equals(b.get("unitName"));
            if (aUn != bUn) {
                return aUn ? 1 : -1;
            }
            return Long.compare(((Number) b.get("patientCount")).longValue(),
                    ((Number) a.get("patientCount")).longValue());
        });
        return list;
    }

    private Map<String, Object> buildHospitalRow(String unitName, long patientCount, long doctorCount) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("unitName", unitName);
        row.put("patientCount", patientCount);
        row.put("doctorCount", doctorCount);
        return row;
    }

    @Override
    public Map<String, Object> getHospitalDetail(String unitName) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("unitName", unitName);

        // 该院有效病例 ID
        LambdaQueryWrapper<Patient> pw = new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .select(Patient::getId);
        if (UNASSIGNED.equals(unitName)) {
            pw.and(w -> w.isNull(Patient::getHospitalName).or().eq(Patient::getHospitalName, ""));
        } else {
            pw.eq(Patient::getHospitalName, unitName);
        }
        List<Long> patientIds = patientMapper.selectList(pw).stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
        long patientCount = patientIds.size();
        result.put("patientCount", patientCount);

        // 医生人数：该院病例中去重的录入医生（排除“未归属医生”）
        LambdaQueryWrapper<Patient> dw = new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .select(Patient::getId, Patient::getDoctorName);
        if (UNASSIGNED.equals(unitName)) {
            dw.and(w -> w.isNull(Patient::getHospitalName).or().eq(Patient::getHospitalName, ""));
        } else {
            dw.eq(Patient::getHospitalName, unitName);
        }
        long doctorCount = patientMapper.selectList(dw).stream()
                .map(p -> normalizeDoctor(p.getDoctorName()))
                .filter(Objects::nonNull)
                .distinct()
                .count();
        result.put("doctorCount", doctorCount);

        // 随访完成度：至少有一条有效随访记录的病例数 / 入组病例数
        long followUpCompleted = 0L;
        if (!patientIds.isEmpty()) {
            List<PatientFollowUp> followUps = followUpMapper.selectList(new LambdaQueryWrapper<PatientFollowUp>()
                    .eq(PatientFollowUp::getDelFlg, "1")
                    .in(PatientFollowUp::getPatientId, patientIds)
                    .select(PatientFollowUp::getPatientId));
            followUpCompleted = followUps.stream()
                    .map(PatientFollowUp::getPatientId)
                    .distinct()
                    .count();
        }
        result.put("followUpCompleted", followUpCompleted);
        result.put("followUpTotal", patientCount);
        double rate = patientCount == 0 ? 0.0
                : Math.round(followUpCompleted * 1000.0 / patientCount) / 10.0;
        result.put("followUpRate", rate);
        return result;
    }

    @Override
    public List<Map<String, Object>> getDiseaseHospitals(String diseaseName) {
        String disClass = resolveDisClass(diseaseName);
        if (disClass == null) {
            return new ArrayList<>();
        }
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .eq(Patient::getDisClass, disClass)
                .select(Patient::getId, Patient::getHospitalName));
        Map<String, Long> countByHospital = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> isBlank(p.getHospitalName()) ? UNASSIGNED : p.getHospitalName(),
                        Collectors.counting()));
        long total = patients.size();
        List<Map<String, Object>> list = new ArrayList<>();
        countByHospital.forEach((name, cnt) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("hospitalName", name);
            row.put("count", cnt);
            row.put("percent", total == 0 ? 0.0 : Math.round(cnt * 1000.0 / total) / 10.0);
            list.add(row);
        });
        list.sort((a, b) -> Long.compare(((Number) b.get("count")).longValue(),
                ((Number) a.get("count")).longValue()));
        return list;
    }

    /**
     * 病种显示名 → disClass 编码。兼容「未知类型(10000009)」格式，无法识别时返回 null
     */
    private String resolveDisClass(String diseaseName) {
        if (diseaseName == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : DISEASE_NAME_MAP.entrySet()) {
            if (entry.getValue().equals(diseaseName)) {
                return entry.getKey();
            }
        }
        String prefix = "未知类型(";
        int l = diseaseName.indexOf(prefix);
        int r = diseaseName.lastIndexOf(")");
        if (l >= 0 && r > l) {
            return diseaseName.substring(l + prefix.length(), r);
        }
        return null;
    }

    private String hospitalKey(String name) {
        return isBlank(name) ? UNASSIGNED : name;
    }

    /** 规范化录入医生名：空或“未归属医生”视为无效，返回 null */
    private String normalizeDoctor(String doctorName) {
        if (isBlank(doctorName)) {
            return null;
        }
        String d = doctorName.trim();
        return "未归属医生".equals(d) ? null : d;
    }

    private long doctorCountOf(Map<String, Set<String>> map, String key) {
        Set<String> set = map.get(key);
        return set == null ? 0L : set.size();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
