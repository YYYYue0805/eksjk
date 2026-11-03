package com.eksjk.service.sync;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ELTM 外部表单处理注册表
 *
 * <p>集中维护 EltmModuleMigrationService 可处理的表单名称，供未处理数据采集器判断
 * “该表单是否已有处理逻辑”，避免两份白名单漂移。</p>
 *
 * @author eksjk
 */
public final class EltmFormRegistry {

    public static final Set<String> DEMOGRAPHIC_FORMS = Set.of(
            "人口学信息", "人口学资料_医生填写", "儿童基本信息", "儿童基础信息",
            "病例基本信息采集（研究对象）", "人口学及基线资料", "患者基线信息");

    public static final Set<String> FOLLOWUP_FORMS = Set.of(
            "GH用药记录", "合并用药记录", "合并用药&非药物治疗记录（随访期）",
            "生长激素治疗相关病史：临床症状和体征", "随访检查记录", "治疗随访-实验室检查",
            "基础测量（身高、体重、头围、BMI）", "体重记录", "长高记录");

    public static final Set<String> AE_FORMS = Set.of("不良事件", "不良反应收集");

    public static final Set<String> CONSENT_FORMS = Set.of("知情同意书上传");

    public static final Set<String> PHYSICAL_EXAM_FORMS = Set.of("体格检查", "儿童体格检查");

    public static final Set<String> OTHER_HANDLED_FORMS = Set.of(
            "其他检查（骨龄X线诊断）", "其他检查（垂体MRI/CT）", "主要转归和诊断", "术后辅助治疗");

    private static final Set<String> HANDLED_FORMS = new LinkedHashSet<>();

    static {
        HANDLED_FORMS.addAll(DEMOGRAPHIC_FORMS);
        HANDLED_FORMS.addAll(FOLLOWUP_FORMS);
        HANDLED_FORMS.addAll(AE_FORMS);
        HANDLED_FORMS.addAll(CONSENT_FORMS);
        HANDLED_FORMS.addAll(PHYSICAL_EXAM_FORMS);
        HANDLED_FORMS.addAll(OTHER_HANDLED_FORMS);
    }

    private EltmFormRegistry() {
    }

    /**
     * 表单名是否已有处理逻辑；忽略首尾空格
     */
    public static boolean isHandledForm(String formName) {
        if (formName == null) {
            return false;
        }
        return HANDLED_FORMS.contains(formName.trim());
    }

    public static boolean isHandledForm(String formName, String formKey) {
        return isHandledForm(formName);
    }
}
