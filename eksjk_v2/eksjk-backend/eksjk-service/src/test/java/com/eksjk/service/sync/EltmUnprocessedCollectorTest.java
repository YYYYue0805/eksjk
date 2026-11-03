package com.eksjk.service.sync;

import com.eksjk.model.entity.EltmUnprocessedData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 未识别数据采集器单元测试
 *
 * @author eksjk
 */
class EltmUnprocessedCollectorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EltmUnprocessedCollector collector = new EltmUnprocessedCollector();

    @Test
    void shouldCollectUnknownColumnsJsonFieldsAndForms() {
        ArrayNode row = emptyRow();
        row.set(0, JsonNodeFactory.instance.numberNode(123));
        row.set(22, TextNode.valueOf("1001"));
        row.set(23, TextNode.valueOf("2002"));
        row.set(54, TextNode.valueOf("{\"name\":\"张三\",\"unknownKey\":\"x\"}"));
        row.set(55, TextNode.valueOf("[{\"doctor_id\":1,\"unknown_bind\":\"x\"}]"));
        row.set(57, TextNode.valueOf(formsJson().toString()));
        row.set(59, TextNode.valueOf("[{\"attribute_code\":\"height:Tab\",\"attribute_value\":\"120\"},"
                + "{\"attribute_code\":\"unknown:code\",\"attribute_value\":\"7\"},{\"two_level\":\"x\"}]"));

        Map<String, Set<String>> mappingIndex = Map.of(
                "KNOWN_FORM", Set.of("mapped1"));

        List<EltmUnprocessedData> records = collector.collectCxList(
                new CxListRow(row, objectMapper), 1L, mappingIndex);

        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNKNOWN_COLUMN, "cxList[22]")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNKNOWN_COLUMN, "cxList[23]")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNPARSED_JSON,
                "extend_data_json.unknownKey")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNPARSED_JSON,
                "hospital_bind_json.unknown_bind")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNPARSED_JSON,
                "physical_json.attribute_code.unknown:code")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNPARSED_JSON,
                "physical_json.two_level")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNHANDLED_FORM, "9001")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNMAPPED_FIELD, "9002:unmapped1")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNMAPPED_FIELD, "9002:mapped1")).isFalse();
        // 已知表单即使没有映射配置，也不应误报为未处理表单
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNHANDLED_FORM, "9003")).isFalse();
    }

    @Test
    void shouldCollectGetTpByIdUnknownColumns() {
        ArrayNode picRow = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < 16; i++) {
            picRow.addNull();
        }
        picRow.set(0, TextNode.valueOf("pic-1"));
        picRow.set(2, TextNode.valueOf("3001"));
        picRow.set(8, TextNode.valueOf("doctor-1"));

        List<EltmUnprocessedData> records = collector.collectGetTpById(
                1L, 123L, "pic-1", List.of(picRow));

        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNKNOWN_INTERFACE_FIELD,
                "gettpbyid:pic-1:col2")).isTrue();
        assertThat(has(records, EltmUnprocessedCollector.CATEGORY_UNKNOWN_INTERFACE_FIELD,
                "gettpbyid:pic-1:col8")).isTrue();
    }

    private ArrayNode emptyRow() {
        ArrayNode row = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < 66; i++) {
            row.addNull();
        }
        return row;
    }

    private ArrayNode formsJson() {
        ArrayNode forms = JsonNodeFactory.instance.arrayNode();
        forms.add(form(9001, "UNKNOWN_FORM", "未知表单", "{\"q1\":\"a\"}"));
        forms.add(form(9002, "KNOWN_FORM", "有映射表单", "{\"mapped1\":\"v1\",\"unmapped1\":\"v2\"}"));
        forms.add(form(9003, "NO_MAPPING", "人口学信息", "{\"q1\":\"a\"}"));
        return forms;
    }

    private com.fasterxml.jackson.databind.node.ObjectNode form(long id, String key,
                                                                 String name, String originalData) {
        com.fasterxml.jackson.databind.node.ObjectNode node = objectMapper.createObjectNode();
        node.put("form_data_id", id);
        node.put("form_key", key);
        node.put("form_name", name);
        node.put("original_data", originalData);
        return node;
    }

    private boolean has(List<EltmUnprocessedData> records, String category, String sourceKey) {
        return records.stream()
                .anyMatch(r -> category.equals(r.getCategory()) && sourceKey.equals(r.getSourceKey()));
    }
}
