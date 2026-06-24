package com.eksjk.service.diagnosis;

import com.eksjk.model.entity.Patient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 临床指标提取器 — 从 ELTM 原始数据 JSON 和 Patient 实体中提取临床指标
 *
 * @author eksjk
 */
@Slf4j
public class IndicatorExtractor {

    private static final ObjectMapper mapper = new ObjectMapper();

    /** 指标别名映射：标准名 → 别名列表 */
    private static final Map<String, List<String>> ALIASES = new LinkedHashMap<>();

    static {
        // @formatter:off
        ALIASES.put("lhPeak",     List.of("LH峰值", "LHmax", "lhMax", "lh_max", "GnRH激发LHmax", "促黄体生成素峰值", "LH激发峰值"));
        ALIASES.put("fshPeak",    List.of("FSH峰值", "FSHmax", "fshMax", "fsh_max", "GnRH激发FSHmax", "促卵泡激素峰值"));
        ALIASES.put("lh",         List.of("lh", "LH", "促黄体生成素", "黄体生成素"));
        ALIASES.put("fsh",        List.of("fsh", "FSH", "促卵泡激素", "卵泡刺激素"));
        ALIASES.put("e2",         List.of("e2", "E2", "雌二醇"));
        ALIASES.put("t",          List.of("t", "T", "睾酮", "testosterone"));
        ALIASES.put("boneAge",    List.of("骨龄", "boneAge", "bone_age", "rboneAge", "cboneAge"));
        ALIASES.put("heightSds",  List.of("heightSds", "height_sds", "身高SDS", "身高标准差", "HSDS"));
        ALIASES.put("weightSds",  List.of("weightSds", "weight_sds", "体重SDS", "体重标准差", "WSDS"));
        ALIASES.put("birthWeight",List.of("出生体重", "birthWeight", "bwt", "birth_weight", "bw"));
        ALIASES.put("gestWeek",   List.of("胎龄", "gestationalAge", "gesWeek", "ges_week", "gestational_age"));
        ALIASES.put("birthLength",List.of("出生身长", "birthLength", "bl", "birth_length"));
        ALIASES.put("fatherHt",   List.of("父身高", "fatherHeight", "fht", "father_height", "父亲身高"));
        ALIASES.put("motherHt",   List.of("母身高", "motherHeight", "mht", "mother_height", "母亲身高"));
        ALIASES.put("karyotype",  List.of("核型", "karyotype", "染色体核型", "染色体"));
        ALIASES.put("amh",        List.of("amh", "AMH", "抗缪勒管激素"));
        ALIASES.put("inhb",       List.of("inhb", "INHB", "抑制素B"));
        ALIASES.put("igf1",       List.of("igf1", "IGF1", "IGF-1", "胰岛素样生长因子1"));
        ALIASES.put("igfbp3",     List.of("igfbp3", "IGFBP3", "IGFBP-3", "胰岛素样生长因子结合蛋白3"));
        ALIASES.put("tsh",        List.of("tsh", "TSH", "促甲状腺激素"));
        ALIASES.put("dht",        List.of("dht", "DHT", "双氢睾酮"));
        ALIASES.put("cafeAuLait", List.of("咖啡牛奶斑", "cafeAuLait", "cafe_au_lait", "咖啡斑", "牛奶咖啡斑"));
        ALIASES.put("fibrousDys", List.of("骨纤维异常", "fibrousDysplasia", "fibrous_dysplasia", "骨纤维发育不良", "纤维性骨发育不良"));
        ALIASES.put("genitalia",  List.of("外生殖器", "externalGenitalia", "genitalia", "外生殖器描述"));
        ALIASES.put("height",     List.of("height", "Height", "身高"));
        ALIASES.put("weight",     List.of("weight", "Weight", "体重"));
        ALIASES.put("bmi",        List.of("bmi", "BMI"));
        ALIASES.put("chiCom",     List.of("主诉", "chiCom", "chiefComplaint", "chief_complaint"));
        ALIASES.put("diagnosis",  List.of("诊断", "diagnosis", "初步诊断"));
        ALIASES.put("onsetAge",   List.of("发病年龄", "onsetAge", "onset_age", "起病年龄"));
        // @formatter:on
    }

    /**
     * 从 rawData JSON + Patient 实体中提取所有已知指标
     * @return Map<标准指标名, 提取值>  值可能是 Number 或 String
     */
    public Map<String, Object> extract(String rawDataJson, Patient patient) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, String> rawFields = parseRawData(rawDataJson);

        for (Map.Entry<String, List<String>> entry : ALIASES.entrySet()) {
            String standardName = entry.getKey();
            List<String> aliases = entry.getValue();

            // 先尝试从 rawData 中匹配
            Object value = findInRawData(rawFields, aliases);
            // 再从 Patient 实体中匹配
            if (value == null) {
                value = findInPatient(patient, standardName, aliases);
            }
            if (value != null) {
                result.put(standardName, value);
            }
        }

        return result;
    }

    /**
     * 从指标值中提取数值（尽可能转换）
     */
    public static Double toNumeric(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        String s = value.toString().trim();
        // 去除单位后缀
        s = s.replaceAll("(?i)(cm|kg|mm|g|岁|周|年|月|天|IU/L|ng/ml|nmol/L|pmol/L|mIU/L|uIU/ml)\\s*$", "").trim();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 检查文本值是否包含异常关键词
     */
    public static boolean textIndicatesAbnormal(Object value) {
        if (value == null) return false;
        String s = value.toString();
        return s.contains("异常") || s.contains("阳性") || s.contains("有")
                || s.contains("是") || s.contains("模糊") || s.contains("不明")
                || s.contains("present") || s.contains("abnormal");
    }

    /**
     * 检查核型是否异常（非典型46,XX和46,XY）
     */
    public static boolean isKaryotypeAbnormal(Object value) {
        if (value == null) return false;
        String s = value.toString().replaceAll("\\s+", "").toUpperCase();
        if (s.isEmpty()) return false;
        return !s.equals("46,XX") && !s.equals("46,XY");
    }

    // ===== 私有方法 =====

    private Map<String, String> parseRawData(String rawDataJson) {
        if (rawDataJson == null || rawDataJson.isBlank()) return Collections.emptyMap();
        try {
            return mapper.readValue(rawDataJson, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.debug("解析 rawData JSON 失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Object findInRawData(Map<String, String> rawFields, List<String> aliases) {
        for (String alias : aliases) {
            for (Map.Entry<String, String> rf : rawFields.entrySet()) {
                if (rf.getKey().equalsIgnoreCase(alias) || rf.getKey().trim().equals(alias)) {
                    String val = rf.getValue();
                    if (val != null && !val.isBlank() && !"null".equalsIgnoreCase(val)) {
                        // 尝试转为数字
                        Double num = tryParse(val);
                        return num != null ? num : val;
                    }
                }
            }
        }
        return null;
    }

    private Object findInPatient(Patient p, String standardName, List<String> aliases) {
        if (p == null) return null;
        // 直接按标准名反射获取
        Object val = getPatientField(p, standardName);
        if (val != null && !isEmptyValue(val)) {
            return val;
        }
        // 按别名尝试
        for (String alias : aliases) {
            Object av = getPatientField(p, alias);
            if (av != null && !isEmptyValue(av)) {
                return av;
            }
        }
        return null;
    }

    private Object getPatientField(Patient p, String fieldName) {
        try {
            var field = Patient.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(p);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEmptyValue(Object val) {
        if (val == null) return true;
        if (val instanceof String s) return s.isBlank() || "null".equalsIgnoreCase(s);
        if (val instanceof Number n) return n.doubleValue() == 0.0;
        return false;
    }

    private Double tryParse(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
