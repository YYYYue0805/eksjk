package com.eksjk.service.diagnosis;

import java.util.List;

/**
 * 单条诊断规则项
 *
 * @author eksjk
 */
class RuleItem {
    enum Type { NUMERIC_GT, NUMERIC_LT, NUMERIC_GTE, NUMERIC_LTE, TEXT_ABNORMAL, KARYOTYPE_ABNORMAL, TEXT_CONTAINS }

    String indicatorName;   // 标准指标名
    Type type;              // 比较类型
    Double threshold;       // 数值阈值
    String textPattern;     // 文本匹配模式
    int score;              // 得分
    String description;     // 说明

    RuleItem(String indicatorName, Type type, Double threshold, int score, String description) {
        this.indicatorName = indicatorName;
        this.type = type;
        this.threshold = threshold;
        this.score = score;
        this.description = description;
    }

    RuleItem(String indicatorName, Type type, String textPattern, int score, String description) {
        this.indicatorName = indicatorName;
        this.type = type;
        this.textPattern = textPattern;
        this.score = score;
        this.description = description;
    }

    RuleItem(String indicatorName, Type type, int score, String description) {
        this.indicatorName = indicatorName;
        this.type = type;
        this.score = score;
        this.description = description;
    }
}

/**
 * 各病种诊断规则定义
 *
 * @author eksjk
 */
class DiagnosisRule {

    String disClass;
    String disName;
    List<RuleItem> items;

    DiagnosisRule(String disClass, String disName, List<RuleItem> items) {
        this.disClass = disClass;
        this.disName = disName;
        this.items = items;
    }

    /** 构建所有 6 个病种的诊断规则 */
    static List<DiagnosisRule> buildAll() {
        return List.of(
            // ===== CPP 中枢性性早熟 =====
            new DiagnosisRule("10000003", "中枢性性早熟 (CPP)", List.of(
                new RuleItem("lhPeak",  RuleItem.Type.NUMERIC_GTE, 5.0, 40, "LH峰值 ≥ 5.0 IU/L"),
                new RuleItem("lhPeak",  RuleItem.Type.NUMERIC_GTE, 3.0, 25, "LH峰值 ≥ 3.0 IU/L（临界升高）"),
                new RuleItem("boneAge", RuleItem.Type.NUMERIC_GT,  1.0, 20, "骨龄超前 > 1年"),
                new RuleItem("e2",      RuleItem.Type.NUMERIC_GTE, 50.0, 15, "E2达到青春期水平"),
                new RuleItem("t",       RuleItem.Type.NUMERIC_GTE, 1.0, 15, "T达到青春期水平"),
                new RuleItem("onsetAge",RuleItem.Type.NUMERIC_LT,  8.0, 15, "发病年龄 < 8岁")
            )),

            // ===== DSD 性发育异常 =====
            new DiagnosisRule("10000001", "性发育异常 (DSD)", List.of(
                new RuleItem("karyotype", RuleItem.Type.KARYOTYPE_ABNORMAL, 40, "染色体核型异常"),
                new RuleItem("amh",       RuleItem.Type.TEXT_ABNORMAL, 25, "AMH异常"),
                new RuleItem("inhb",      RuleItem.Type.TEXT_ABNORMAL, 25, "INHB异常"),
                new RuleItem("t",         RuleItem.Type.NUMERIC_LT, 0.5, 20, "T水平显著偏低"),
                new RuleItem("dht",       RuleItem.Type.TEXT_ABNORMAL, 20, "DHT异常"),
                new RuleItem("e2",        RuleItem.Type.TEXT_ABNORMAL, 20, "E2显著异常"),
                new RuleItem("genitalia", RuleItem.Type.TEXT_ABNORMAL, 15, "外生殖器描述异常")
            )),

            // ===== FSS 遗传性骨病 =====
            new DiagnosisRule("10000002", "遗传性骨病 (FSS)", List.of(
                new RuleItem("heightSds", RuleItem.Type.NUMERIC_LT, -2.0, 35, "身高SDS < -2.0"),
                new RuleItem("heightSds", RuleItem.Type.NUMERIC_LT, -1.5, 20, "身高SDS < -1.5（临界矮小）"),
                new RuleItem("boneAge",   RuleItem.Type.TEXT_ABNORMAL, 30, "骨龄显著异常"),
                new RuleItem("fibrousDys",RuleItem.Type.TEXT_CONTAINS, "异常", 20, "骨骼发育异常描述"),
                new RuleItem("karyotype", RuleItem.Type.TEXT_ABNORMAL, 15, "基因检测异常")
            )),

            // ===== MAS McCune-Albright =====
            new DiagnosisRule("10000004", "McCune-Albright (MAS)", List.of(
                new RuleItem("cafeAuLait",RuleItem.Type.TEXT_CONTAINS, "有", 35, "咖啡牛奶斑阳性"),
                new RuleItem("cafeAuLait",RuleItem.Type.TEXT_ABNORMAL, 30, "皮肤色素异常"),
                new RuleItem("fibrousDys",RuleItem.Type.TEXT_CONTAINS, "有", 30, "纤维性骨发育不良"),
                new RuleItem("fibrousDys",RuleItem.Type.TEXT_ABNORMAL, 25, "骨发育异常"),
                new RuleItem("tsh",       RuleItem.Type.TEXT_ABNORMAL, 15, "甲状腺功能异常"),
                new RuleItem("igf1",      RuleItem.Type.TEXT_ABNORMAL, 15, "生长因子异常")
            )),

            // ===== SGA 小于胎龄儿 =====
            new DiagnosisRule("10000005", "小于胎龄儿 (SGA)", List.of(
                new RuleItem("birthWeight",RuleItem.Type.NUMERIC_LT, 2500.0, 40, "出生体重 < 2500g"),
                new RuleItem("birthWeight",RuleItem.Type.NUMERIC_LT, 2000.0, 45, "出生体重 < 2000g（显著低体重）"),
                new RuleItem("gestWeek",   RuleItem.Type.NUMERIC_LT, 37.0, 25, "胎龄 < 37周"),
                new RuleItem("birthLength",RuleItem.Type.NUMERIC_LT, 45.0, 20, "出生身长偏小"),
                new RuleItem("heightSds",  RuleItem.Type.NUMERIC_LT, -2.0, 15, "身高持续 < P3（追赶不足）")
            )),

            // ===== SSS 家族性矮小 =====
            new DiagnosisRule("10000006", "家族性矮小 (SSS)", List.of(
                new RuleItem("heightSds", RuleItem.Type.NUMERIC_LT, -2.0, 35, "身高 < 同年龄P3"),
                new RuleItem("heightSds", RuleItem.Type.NUMERIC_LT, -1.5, 20, "身高 < 同年龄P10"),
                new RuleItem("fatherHt",  RuleItem.Type.NUMERIC_LT, 160.0, 25, "父身高偏矮"),
                new RuleItem("motherHt",  RuleItem.Type.NUMERIC_LT, 150.0, 25, "母身高偏矮"),
                new RuleItem("boneAge",   RuleItem.Type.NUMERIC_LTE, 1.0, 20, "骨龄与实际年龄接近"),
                new RuleItem("igf1",      RuleItem.Type.NUMERIC_GTE, 50.0, 20, "IGF1正常（排除GHD）")
            ))
        );
    }
}
