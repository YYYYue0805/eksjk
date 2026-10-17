package com.eksjk.service.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * cxList 的一行数据（66 列）
 *
 * @author eksjk
 */
public class CxListRow {

    private final ArrayNode row;
    private final ObjectMapper objectMapper;

    public CxListRow(ArrayNode row, ObjectMapper objectMapper) {
        this.row = row;
        this.objectMapper = objectMapper;
    }

    public ArrayNode node() {
        return row;
    }

    public int size() {
        return row.size();
    }

    public long id() {
        return row.get(0).asLong();
    }

    public String str(int index) {
        return DatamainParseUtil.text(value(index));
    }

    public Integer intValue(int index) {
        return DatamainParseUtil.intValue(value(index));
    }

    public LocalDateTime dateTime(int index) {
        return DatamainParseUtil.parseDateTime(value(index));
    }

    public LocalDate date(int index) {
        return DatamainParseUtil.parseDate(value(index));
    }

    /** JSON 明细列：文本会二次解析成对象，原样返回对象/数组 */
    public JsonNode json(int index) {
        JsonNode value = value(index);
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isTextual()) {
            return DatamainParseUtil.parseJson(value.asText());
        }
        return value;
    }

    public String rawJson() {
        return row.toString();
    }

    private JsonNode value(int index) {
        return index >= 0 && index < row.size() ? row.get(index) : null;
    }
}
