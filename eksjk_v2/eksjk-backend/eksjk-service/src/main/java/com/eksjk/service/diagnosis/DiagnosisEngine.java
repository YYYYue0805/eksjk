package com.eksjk.service.diagnosis;

import com.eksjk.model.entity.Patient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 诊断引擎 — 根据临床指标和规则进行疾病匹配评分
 *
 * @author eksjk
 */
@Slf4j
public class DiagnosisEngine {

    private final IndicatorExtractor extractor;
    private final List<DiagnosisRule> rules;

    public DiagnosisEngine() {
        this.extractor = new IndicatorExtractor();
        this.rules = DiagnosisRule.buildAll();
    }

    /**
     * 对单个患者执行诊断
     * @return 最佳匹配的 DiagnosisResult，如果没有匹配到任何规则则返回空的 LOW 结果
     */
    public DiagnosisResult diagnose(String rawDataJson, Patient patient) {
        Map<String, Object> indicators = extractor.extract(rawDataJson, patient);
        log.debug("提取到的指标: {}", indicators.keySet());

        DiagnosisResult best = null;

        for (DiagnosisRule rule : rules) {
            int score = 0;
            List<String> matched = new ArrayList<>();

            for (RuleItem item : rule.items) {
                Object value = indicators.get(item.indicatorName);
                if (value == null) continue;

                boolean hit = evaluateRule(item, value);
                if (hit) {
                    score += item.score;
                    matched.add(item.description + " (+" + item.score + ")");
                }
            }

            if (score > 0 && (best == null || score > best.getScore())) {
                String confidence = score >= 70 ? "HIGH" : score >= 40 ? "MEDIUM" : "LOW";
                String note = buildNote(rule.disName, matched, indicators);
                best = new DiagnosisResult(rule.disClass, rule.disName, score, confidence, matched, note);
            }
        }

        if (best == null) {
            return new DiagnosisResult(null, null, 0, "LOW",
                    Collections.emptyList(), "未匹配到任何病种的诊断规则，请人工判断");
        }

        // 检查是否有多个病种得分接近（分数相同或相差 < 10）
        DiagnosisResult finalBest = best;
        boolean hasConflict = rules.stream()
                .anyMatch(r -> !r.disClass.equals(finalBest.getTargetDisClass())
                        && calculateScore(r, indicators) >= finalBest.getScore() - 5);

        if (hasConflict && best.getScore() >= 70) {
            best.setConfidence("MEDIUM");
            best.setNote(best.getNote() + "（注意：多个病种得分接近，建议人工确认）");
        }

        return best;
    }

    private int calculateScore(DiagnosisRule rule, Map<String, Object> indicators) {
        int score = 0;
        for (RuleItem item : rule.items) {
            Object value = indicators.get(item.indicatorName);
            if (value != null && evaluateRule(item, value)) {
                score += item.score;
            }
        }
        return score;
    }

    private boolean evaluateRule(RuleItem item, Object value) {
        return switch (item.type) {
            case NUMERIC_GT, NUMERIC_GTE, NUMERIC_LT, NUMERIC_LTE -> {
                Double num = IndicatorExtractor.toNumeric(value);
                if (num == null) yield false;
                yield switch (item.type) {
                    case NUMERIC_GT  -> num >  item.threshold;
                    case NUMERIC_GTE -> num >= item.threshold;
                    case NUMERIC_LT  -> num <  item.threshold;
                    case NUMERIC_LTE -> num <= item.threshold;
                    default -> false;
                };
            }
            case TEXT_ABNORMAL -> IndicatorExtractor.textIndicatesAbnormal(value);
            case KARYOTYPE_ABNORMAL -> IndicatorExtractor.isKaryotypeAbnormal(value);
            case TEXT_CONTAINS -> {
                if (item.textPattern == null) yield false;
                yield value.toString().contains(item.textPattern);
            }
        };
    }

    private String buildNote(String diseaseName, List<String> matched, Map<String, Object> indicators) {
        StringBuilder sb = new StringBuilder();
        sb.append("匹配到「").append(diseaseName).append("」相关指标：\n");
        for (String m : matched) {
            sb.append("  · ").append(m).append("\n");
        }
        sb.append("可用的临床指标: ").append(indicators.keySet());
        return sb.toString();
    }
}
