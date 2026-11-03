package com.eksjk.service.sync;

import java.util.List;

/**
 * ELTM 未处理数据的目标模块归类
 *
 * <p>外部表单没有题面，无法完全自动映射；归类结果用于在「暂未处理数据」中
 * 按目标模块聚合，便于人工确认后再配置字段映射。</p>
 *
 * @author eksjk
 */
public final class EltmModuleClassifier {

    public static final String BASIC_INFO = "基本信息";
    public static final String BASELINE_CLINICAL = "基线-临床信息";
    public static final String BASELINE_EXAM = "基线-辅助检查";
    public static final String GENETICS = "遗传学检查";
    public static final String FOLLOWUP = "随访记录";
    public static final String DIAGNOSIS = "诊断";
    public static final String ADVERSE = "GH不良事件";
    public static final String CONSENT = "知情同意书";
    public static final String OTHER = "其他/保留";

    private record Rule(String module, List<String> keywords) {
    }

    /**
     * 按顺序匹配，先命中先返回；量表/问卷类放到最后归入「其他/保留」。
     */
    private static final List<Rule> RULES = List.of(
            new Rule(CONSENT, List.of("知情同意")),
            new Rule(ADVERSE, List.of("不良事件", "不良反应")),
            new Rule(DIAGNOSIS, List.of("主要转归", "术后辅助治疗", "诊断")),
            new Rule(GENETICS, List.of("染色体", "基因")),
            new Rule(BASELINE_EXAM, List.of(
                    "实验室检查", "影像学", "检查记录", "骨龄", "垂体", "超声", "心电图",
                    "血常规", "尿常规", "肝肾功能", "血糖", "糖尿病指标", "甲状腺功能",
                    "血脂", "肿瘤标志物", "激发试验")),
            new Rule(FOLLOWUP, List.of(
                    "GH用药", "合并用药", "用药记录", "随访", "基础测量", "体重记录",
                    "长高记录", "运动记录")),
            new Rule(BASELINE_CLINICAL, List.of(
                    "体格检查", "病史", "临床症状", "体征", "家族史", "生长发育")),
            new Rule(BASIC_INFO, List.of(
                    "人口学", "基本信息", "患者基线信息", "出生史", "产妇信息", "儿童基本")),
            new Rule(OTHER, List.of(
                    "量表", "问卷", "评估", "ADL", "SDS", "DDST", "GESELL", "Piers",
                    "睡眠", "营养", "心理", "行为", "长处和困难", "负担", "运动记录"))
    );

    private EltmModuleClassifier() {
    }

    public static String classify(String category, String sourceName, String sourceKey) {
        if (EltmUnprocessedCollector.CATEGORY_UNMAPPED_FIELD.equals(category)
                || EltmUnprocessedCollector.CATEGORY_UNHANDLED_FORM.equals(category)) {
            String formName = formNameOf(sourceName);
            return byText(formName);
        }
        if (EltmUnprocessedCollector.CATEGORY_UNPARSED_JSON.equals(category)) {
            String name = sourceName == null ? "" : sourceName;
            if (name.startsWith("physical_json")) {
                return physicalModule(sourceKey);
            }
            if (name.startsWith("extend_data_json")) {
                return BASIC_INFO;
            }
        }
        return OTHER;
    }

    private static String formNameOf(String sourceName) {
        if (sourceName == null) {
            return "";
        }
        int idx = sourceName.indexOf(" / ");
        return idx > 0 ? sourceName.substring(0, idx) : sourceName;
    }

    private static String physicalModule(String sourceKey) {
        String key = sourceKey == null ? "" : sourceKey.toLowerCase();
        if (key.contains("fatherheight") || key.contains("motherheight")
                || key.contains("height") || key.contains("weight") || key.contains("bmi")
                || key.contains("standarddeviation")) {
            return BASELINE_CLINICAL;
        }
        return OTHER;
    }

    private static String byText(String text) {
        if (text == null || text.isBlank()) {
            return OTHER;
        }
        for (Rule rule : RULES) {
            for (String keyword : rule.keywords()) {
                if (text.contains(keyword)) {
                    return rule.module();
                }
            }
        }
        return OTHER;
    }
}
