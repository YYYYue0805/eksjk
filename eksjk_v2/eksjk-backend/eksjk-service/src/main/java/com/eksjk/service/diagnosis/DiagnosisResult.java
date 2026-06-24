package com.eksjk.service.diagnosis;

import java.util.List;

/**
 * 诊断结果 DTO
 *
 * @author eksjk
 */
public class DiagnosisResult {

    /** 目标疾病代码 */
    private String targetDisClass;

    /** 目标疾病名称 */
    private String targetDisName;

    /** 总得分 */
    private int score;

    /** 置信度: HIGH / MEDIUM / LOW */
    private String confidence;

    /** 匹配到的指标列表（含说明） */
    private List<String> matchedIndicators;

    /** 诊断说明 */
    private String note;

    public DiagnosisResult() {}

    public DiagnosisResult(String targetDisClass, String targetDisName, int score,
                           String confidence, List<String> matchedIndicators, String note) {
        this.targetDisClass = targetDisClass;
        this.targetDisName = targetDisName;
        this.score = score;
        this.confidence = confidence;
        this.matchedIndicators = matchedIndicators;
        this.note = note;
    }

    /** 是否达到自动分类阈值 */
    public boolean isAutoClassifiable() {
        return "HIGH".equals(confidence) && score >= 70;
    }

    /** 是否有建议 */
    public boolean hasSuggestion() {
        return score >= 40;
    }

    // ===== getters / setters =====

    public String getTargetDisClass() { return targetDisClass; }
    public void setTargetDisClass(String v) { this.targetDisClass = v; }

    public String getTargetDisName() { return targetDisName; }
    public void setTargetDisName(String v) { this.targetDisName = v; }

    public int getScore() { return score; }
    public void setScore(int v) { this.score = v; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String v) { this.confidence = v; }

    public List<String> getMatchedIndicators() { return matchedIndicators; }
    public void setMatchedIndicators(List<String> v) { this.matchedIndicators = v; }

    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
