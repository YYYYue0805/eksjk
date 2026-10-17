package com.eksjk.service.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * datamain 同步解析工具
 *
 * @author eksjk
 */
@Slf4j
public final class DatamainParseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };

    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    private DatamainParseUtil() {
    }

    /** 提取文本，null/空串统一返回 null */
    public static String text(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isValueNode()) {
            String value = node.asText();
            return value == null || value.isEmpty() ? null : value;
        }
        return node.toString();
    }

    /** 解析时间，支持带 T、带毫秒的格式 */
    public static LocalDateTime parseDateTime(JsonNode node) {
        return parseDateTime(text(node));
    }

    public static LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim();
        if (value.contains(".")) {
            value = value.substring(0, value.indexOf('.'));
        }
        value = value.replace('T', ' ');
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (Exception ignored) {
                // 尝试下一个格式
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, formatter).atStartOfDay();
            } catch (Exception ignored) {
                // 尝试下一个格式
            }
        }
        log.debug("无法解析时间: {}", raw);
        return null;
    }

    public static LocalDate parseDate(JsonNode node) {
        LocalDateTime dateTime = parseDateTime(node);
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    /** 解析 JSON 字符串，失败返回 null */
    public static JsonNode parseJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readTree(raw);
        } catch (Exception e) {
            log.debug("JSON 解析失败: {}", e.getMessage());
            return null;
        }
    }

    public static Long longValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return node.asLong();
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer intValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return node.asInt();
        } catch (Exception e) {
            return null;
        }
    }

    public static BigDecimal decimalValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return new BigDecimal(node.asText());
        } catch (Exception e) {
            return null;
        }
    }

    /** 性别标准化：1/2；男/女、M/F 兜底 */
    public static String normalizeSex(String genderCode, String genderText) {
        if ("1".equals(genderCode)) {
            return "1";
        }
        if ("2".equals(genderCode)) {
            return "2";
        }
        if ("男".equals(genderText) || "M".equalsIgnoreCase(genderText)) {
            return "1";
        }
        if ("女".equals(genderText) || "F".equalsIgnoreCase(genderText)) {
            return "2";
        }
        return null;
    }

    /** 根据出生日期计算年龄，格式 X岁Y月 */
    public static String calcAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        Period period = Period.between(birthday, LocalDate.now());
        if (period.getYears() > 0) {
            return period.getYears() + "岁" + (period.getMonths() > 0 ? period.getMonths() + "月" : "");
        }
        return Math.max(period.getMonths(), 0) + "月";
    }

    public static String sha256(String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("计算哈希失败", e);
        }
    }

    public static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
