package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 统计分析服务实现
 * <p>数据量级较小（数千病例），采用内存聚合，避免手写表名/列名 SQL</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final Map<String, String> DISEASE_NAME_MAP = Map.of(
            "10000001", "性发育异常(DSD)",
            "10000002", "遗传性骨病(FSS)",
            "10000003", "中枢性性早熟(CPP)",
            "10000004", "McCune-Albright(MAS)",
            "10000005", "小于胎龄儿(SGA)",
            "10000006", "家族性矮小(SSS)",
            "10000007", "E路童萌(ELTM)"
    );

    private static final String UNASSIGNED = "未分配";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final PatientMapper patientMapper;

    @Override
    public Map<String, Object> hospitalMonthly(String startDate, String endDate) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .select(Patient::getId, Patient::getHospitalName, Patient::getCTime);
        applyTimeRange(wrapper, startDate, endDate);
        List<Patient> patients = patientMapper.selectList(wrapper);

        TreeSet<String> monthSet = new TreeSet<>();
        Map<String, Map<String, Long>> byHospital = new TreeMap<>();
        Map<String, Long> monthTotals = new TreeMap<>();
        for (Patient p : patients) {
            String hospital = isBlank(p.getHospitalName()) ? UNASSIGNED : p.getHospitalName();
            String month = p.getCTime() == null ? "未知" : p.getCTime().format(MONTH_FMT);
            monthSet.add(month);
            byHospital.computeIfAbsent(hospital, k -> new TreeMap<>()).merge(month, 1L, Long::sum);
            monthTotals.merge(month, 1L, Long::sum);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        long grand = 0L;
        for (Map.Entry<String, Map<String, Long>> entry : byHospital.entrySet()) {
            Map<String, Long> counts = entry.getValue();
            long total = counts.values().stream().mapToLong(Long::longValue).sum();
            grand += total;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("hospitalName", entry.getKey());
            row.put("counts", counts);
            row.put("total", total);
            rows.add(row);
        }
        rows.sort((a, b) -> Long.compare(
                ((Number) b.get("total")).longValue(), ((Number) a.get("total")).longValue()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("months", new ArrayList<>(monthSet));
        result.put("rows", rows);
        result.put("monthTotals", monthTotals);
        result.put("grandTotal", grand);
        return result;
    }

    @Override
    public List<Map<String, Object>> diseaseNewly(String startDate, String endDate) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1")
                .select(Patient::getId, Patient::getDisClass);
        applyTimeRange(wrapper, startDate, endDate);
        List<Patient> patients = patientMapper.selectList(wrapper);

        Map<String, Long> counts = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> DISEASE_NAME_MAP.getOrDefault(
                                p.getDisClass(),
                                p.getDisClass() == null ? "未知" : p.getDisClass()),
                        Collectors.counting()));

        List<Map<String, Object>> list = new ArrayList<>();
        counts.forEach((name, cnt) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("diseaseName", name);
            row.put("count", cnt);
            list.add(row);
        });
        list.sort((a, b) -> Long.compare(
                ((Number) b.get("count")).longValue(), ((Number) a.get("count")).longValue()));
        return list;
    }

    @Override
    public Map<String, Object> customSearch(Map<String, Object> criteria) {
        if (criteria == null) {
            criteria = new LinkedHashMap<>();
        }
        String disClass = str(criteria.get("disClass"));
        String sex = str(criteria.get("sex"));

        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>()
                .eq(Patient::getDelFlg, "1");
        if (!isBlank(disClass)) {
            wrapper.eq(Patient::getDisClass, disClass);
        }
        if (!isBlank(sex)) {
            wrapper.eq(Patient::getSex, sex);
        }
        applyTimeRange(wrapper, str(criteria.get("startDate")), str(criteria.get("endDate")));
        List<Patient> patients = patientMapper.selectList(wrapper);

        Double minAge = dbl(criteria.get("minAge"));
        Double maxAge = dbl(criteria.get("maxAge"));
        Double minHeight = dbl(criteria.get("minHeight"));
        Double maxHeight = dbl(criteria.get("maxHeight"));
        Double minBone = dbl(criteria.get("minBoneAge"));
        Double maxBone = dbl(criteria.get("maxBoneAge"));
        Boolean finalHeight = bool(criteria.get("finalHeight"));

        List<Patient> filtered = patients.stream()
                .filter(p -> inRange(num(p.getAgeY()), minAge, maxAge))
                .filter(p -> inRange(num(p.getHeight()), minHeight, maxHeight))
                .filter(p -> inRange(num(p.getRboneAge()), minBone, maxBone))
                .filter(p -> finalHeight == null || finalHeight.equals(isFinalHeight(p)))
                .collect(Collectors.toList());

        long total = filtered.size();
        List<Map<String, Object>> list = filtered.stream().limit(200).map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", p.getName());
            m.put("sex", p.getSex());
            m.put("age", p.getAgeY());
            m.put("height", p.getHeight());
            m.put("boneAge", p.getRboneAge());
            m.put("hospitalName", p.getHospitalName());
            m.put("diseaseName", DISEASE_NAME_MAP.getOrDefault(p.getDisClass(), p.getDisClass()));
            m.put("createTime", p.getCTime());
            m.put("finalHeight", isFinalHeight(p));
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    private void applyTimeRange(LambdaQueryWrapper<Patient> wrapper, String startDate, String endDate) {
        if (!isBlank(startDate)) {
            wrapper.ge(Patient::getCTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (!isBlank(endDate)) {
            wrapper.le(Patient::getCTime, LocalDate.parse(endDate).atTime(23, 59, 59));
        }
    }

    private boolean inRange(Double value, Double min, Double max) {
        if (min == null && max == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (min != null && value < min) {
            return false;
        }
        return max == null || value <= max;
    }

    private Double num(String s) {
        if (isBlank(s)) {
            return null;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean isFinalHeight(Patient p) {
        String v = p.getIsFinalhei();
        if (v == null) {
            return false;
        }
        String t = v.trim();
        return "1".equals(t) || "是".equals(t) || "true".equalsIgnoreCase(t);
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private Double dbl(Object o) {
        return o == null ? null : num(String.valueOf(o));
    }

    private Boolean bool(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Boolean b) {
            return b;
        }
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) {
            return null;
        }
        return "1".equals(s) || "是".equals(s) || "true".equalsIgnoreCase(s);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
