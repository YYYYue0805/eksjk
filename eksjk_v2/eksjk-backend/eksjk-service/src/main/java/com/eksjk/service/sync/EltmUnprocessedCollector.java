package com.eksjk.service.sync;

import com.eksjk.model.entity.EltmUnprocessedData;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * ELTM 外部接口未处理数据采集器
 *
 * <p>只构造内存记录，不写数据库；由同步编排服务在患者事务提交后统一落库。</p>
 *
 * @author eksjk
 */
@Component
public class EltmUnprocessedCollector {

    public static final String API_CX_LIST = "CXLIST";
    public static final String API_GETTPBYID = "GETTPBYID";
    public static final String API_GETZQBYID = "GETZQBYID";

    public static final String CATEGORY_UNKNOWN_COLUMN = "UNKNOWN_COLUMN";
    public static final String CATEGORY_UNPARSED_JSON = "UNPARSED_JSON";
    public static final String CATEGORY_UNHANDLED_FORM = "UNHANDLED_FORM";
    public static final String CATEGORY_UNMAPPED_FIELD = "UNMAPPED_FIELD";
    public static final String CATEGORY_UNKNOWN_INTERFACE_FIELD = "UNKNOWN_INTERFACE_FIELD";

    /**
     * 采集 cxList 的未识别数据
     *
     * @param mappedFieldsByFormKey 已启用的表单字段映射：formKey -> fieldKey 集合
     */
    public List<EltmUnprocessedData> collectCxList(CxListRow row, Long patientId,
                                                   Map<String, Set<String>> mappedFieldsByFormKey) {
        List<EltmUnprocessedData> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        long externalId = row.id();

        // 1. 含义待确认的标量列
        for (int index : DatamainFieldCatalog.UNKNOWN_CX_COLUMNS) {
            String value = row.str(index);
            if (value != null && !value.isBlank()) {
                add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNKNOWN_COLUMN,
                        "cxList[" + index + "]", DatamainFieldCatalog.CX_COLUMN_LABELS.get(index),
                        "字段含义待确认", value, null);
            }
        }

        // 2. extend_data_json 中未纳入兜底处理的键
        JsonNode extend = row.json(54);
        if (extend != null && extend.isObject()) {
            extend.fields().forEachRemaining(entry -> {
                if (!DatamainFieldCatalog.EXTEND_DATA_KEYS.contains(entry.getKey())) {
                    add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNPARSED_JSON,
                            "extend_data_json." + entry.getKey(),
                            "extend_data_json / " + entry.getKey(),
                            "扩展字段未处理", valueText(entry.getValue()), jsonText(entry.getValue()));
                }
            });
        }

        // 3. 表单：未处理表单 / 已配置映射表单中的未映射字段
        collectForms(row, patientId, externalId, mappedFieldsByFormKey, out, seen);

        // 4. 体征属性：未识别的 attribute_code 单独列出
        forEachItem(row.json(59), item -> {
            String code = DatamainParseUtil.text(item.get("attribute_code"));
            if (code != null && !code.isBlank()
                    && !DatamainFieldCatalog.KNOWN_PHYSICAL_ATTRIBUTES.contains(code)) {
                add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNPARSED_JSON,
                        "physical_json.attribute_code." + code,
                        "physical_json / " + code,
                        "体征属性未处理", DatamainParseUtil.text(item.get("attribute_value")),
                        jsonText(item));
            }
        });

        // 5. 其他明细 JSON 中的未知键
        collectUnknownKeys(row, patientId, externalId, 55, "hospital_bind_json",
                DatamainFieldCatalog.HOSPITAL_BIND_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 56, "project_json",
                DatamainFieldCatalog.PROJECT_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 57, "form_json",
                DatamainFieldCatalog.FORM_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 58, "pic_json",
                DatamainFieldCatalog.PIC_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 59, "physical_json",
                DatamainFieldCatalog.PHYSICAL_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 60, "report_json",
                DatamainFieldCatalog.REPORT_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 61, "exercise_json",
                DatamainFieldCatalog.EXERCISE_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 62, "exercise_summary_json",
                DatamainFieldCatalog.EXERCISE_SUMMARY_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 63, "sleep_json",
                DatamainFieldCatalog.SLEEP_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 64, "sleep_summary_json",
                DatamainFieldCatalog.SLEEP_SUMMARY_KEYS, out, seen);
        collectUnknownKeys(row, patientId, externalId, 65, "consent_json",
                DatamainFieldCatalog.CONSENT_KEYS, out, seen);

        return out;
    }

    private void collectForms(CxListRow row, Long patientId, long externalId,
                              Map<String, Set<String>> mappedFieldsByFormKey,
                              List<EltmUnprocessedData> out, Set<String> seen) {
        forEachItem(row.json(57), item -> {
            Long formDataId = DatamainParseUtil.longValue(item.get("form_data_id"));
            String formKey = DatamainParseUtil.text(item.get("form_key"));
            String formName = DatamainParseUtil.text(item.get("form_name"));
            String businessKey = formDataId != null ? String.valueOf(formDataId)
                    : "H-" + DatamainParseUtil.sha256(item.toString()).substring(0, 32);
            Set<String> mapped = formKey == null ? null : mappedFieldsByFormKey.get(formKey);
            boolean handled = EltmFormRegistry.isHandledForm(formName, formKey);

            if (mapped == null && !handled) {
                add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNHANDLED_FORM,
                        businessKey, formName != null ? formName : formKey,
                        "表单未在字段映射或处理注册表中",
                        null, jsonText(item.get("original_data")));
                return;
            }

            if (mapped != null && !handled) {
                JsonNode original = item.get("original_data");
                if (original != null && original.isTextual()) {
                    original = DatamainParseUtil.parseJson(original.asText());
                }
                if (original != null && original.isObject()) {
                    JsonNode data = original;
                    data.fields().forEachRemaining(entry -> {
                        if (!mapped.contains(entry.getKey())) {
                            add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNMAPPED_FIELD,
                                    businessKey + ":" + entry.getKey(),
                                    (formName != null ? formName + " / " : "") + entry.getKey(),
                                    "字段未配置映射", valueText(entry.getValue()),
                                    jsonText(entry.getValue()));
                        }
                    });
                }
            }
        });
    }

    private void collectUnknownKeys(CxListRow row, Long patientId, long externalId,
                                    int column, String jsonName, Set<String> knownKeys,
                                    List<EltmUnprocessedData> out, Set<String> seen) {
        forEachItem(row.json(column), item -> item.fields().forEachRemaining(entry -> {
            if (!knownKeys.contains(entry.getKey())) {
                add(out, seen, patientId, externalId, API_CX_LIST, CATEGORY_UNPARSED_JSON,
                        jsonName + "." + entry.getKey(), jsonName + " / " + entry.getKey(),
                        "JSON 字段未处理", valueText(entry.getValue()), jsonText(entry.getValue()));
            }
        }));
    }

    /**
     * gettpbyid 16 列中的未知列
     */
    public List<EltmUnprocessedData> collectGetTpById(Long patientId, long externalId,
                                                      String picId, List<JsonNode> rows) {
        List<EltmUnprocessedData> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (JsonNode row : rows) {
            String businessKey = picId != null && !picId.isBlank() ? picId
                    : "H-" + DatamainParseUtil.sha256(row.toString()).substring(0, 32);
            for (Map.Entry<Integer, String> entry : DatamainFieldCatalog.UNKNOWN_GETTP_COLUMNS.entrySet()) {
                String value = DatamainParseUtil.text(row.get(entry.getKey()));
                if (value != null && !value.isBlank()) {
                    add(out, seen, patientId, externalId, API_GETTPBYID,
                            CATEGORY_UNKNOWN_INTERFACE_FIELD,
                            "gettpbyid:" + businessKey + ":col" + entry.getKey(),
                            "gettpbyid 列 " + entry.getKey() + "（" + entry.getValue() + "）",
                            "列语义待确认", value, jsonText(row));
                }
            }
        }
        return out;
    }

    /**
     * getzqbyid 25 列中的未知/语义待确认列
     */
    public List<EltmUnprocessedData> collectGetZqById(Long patientId, long externalId,
                                                      String signId, JsonNode row) {
        List<EltmUnprocessedData> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        String businessKey = signId != null && !signId.isBlank() ? signId
                : "H-" + DatamainParseUtil.sha256(row.toString()).substring(0, 32);
        for (Map.Entry<Integer, String> entry : DatamainFieldCatalog.UNKNOWN_GETZQ_COLUMNS.entrySet()) {
            String value = DatamainParseUtil.text(row.get(entry.getKey()));
            if (value != null && !value.isBlank()) {
                add(out, seen, patientId, externalId, API_GETZQBYID,
                        CATEGORY_UNKNOWN_INTERFACE_FIELD,
                        "getzqbyid:" + businessKey + ":col" + entry.getKey(),
                        "getzqbyid 列 " + entry.getKey() + "（" + entry.getValue() + "）",
                        "列语义待确认", value, jsonText(row));
            }
        }
        return out;
    }

    /**
     * 同意书签名图片过大未落库时记录
     */
    public void recordSkippedConsentImage(List<EltmUnprocessedData> out, Set<String> seen,
                                          Long patientId, long externalId, String signId,
                                          String fieldName, int length) {
        add(out, seen, patientId, externalId, API_GETZQBYID, CATEGORY_UNKNOWN_INTERFACE_FIELD,
                "getzqbyid:" + signId + ":" + fieldName,
                "getzqbyid / " + fieldName,
                "签名图片 base64 超过内联阈值未落库", "length=" + length, null);
    }

    private void add(List<EltmUnprocessedData> out, Set<String> seen, Long patientId,
                     Long externalId, String apiCode, String category, String sourceKey,
                     String sourceName, String reason, String rawValue, String rawJson) {
        if (sourceKey == null || sourceKey.isBlank()) {
            return;
        }
        if (!seen.add(category + "|" + sourceKey)) {
            return;
        }
        EltmUnprocessedData data = new EltmUnprocessedData();
        data.setPatientId(patientId);
        data.setExternalPatientId(externalId);
        data.setApiCode(apiCode);
        data.setCategory(category);
        data.setSourceKey(sourceKey);
        data.setSourceName(sourceName);
        data.setSuggestedModule(EltmModuleClassifier.classify(category, sourceName, sourceKey));
        data.setReason(reason);
        data.setRawValue(rawValue);
        data.setRawJson(rawJson);
        out.add(data);
    }

    private void forEachItem(JsonNode node, Consumer<JsonNode> consumer) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item != null && item.isObject()) {
                    consumer.accept(item);
                }
            }
        } else if (node.isObject()) {
            consumer.accept(node);
        }
    }

    private String valueText(JsonNode node) {
        return DatamainParseUtil.text(node);
    }

    private String jsonText(JsonNode node) {
        return node == null || node.isNull() ? null : node.toString();
    }
}
