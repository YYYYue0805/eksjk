package com.eksjk.service.sync;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * datamain 外部接口客户端
 *
 * <p>cxList 响应约 49.6MB，使用 Jackson 流式解析，逐行回调，避免构建全量对象树。</p>
 *
 * @author eksjk
 */
@Slf4j
@Component
public class DatamainApiClient {

    private static final int EXPECTED_COLUMNS = 66;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonFactory jsonFactory = new JsonFactory();

    @Value("${eksjk.datamain.base-url:http://36.26.56.94:8026}")
    private String baseUrl;

    @Value("${eksjk.datamain.cx-list-path:/datamain/cxList}")
    private String cxListPath;

    @Value("${eksjk.datamain.connect-timeout-seconds:10}")
    private int connectTimeoutSeconds;

    @Value("${eksjk.datamain.read-timeout-seconds:180}")
    private int readTimeoutSeconds;

    @Value("${eksjk.datamain.max-retry:3}")
    private int maxRetry;

    /**
     * 流式读取 cxList，每行回调一次
     *
     * @return 外部返回 code 与行数
     */
    public FetchResult streamCxList(Consumer<CxListRow> consumer) {
        IOException lastError = null;
        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                return doStream(consumer);
            } catch (IOException | InterruptedException e) {
                lastError = e instanceof IOException io ? io : new IOException(e);
                log.warn("调用 cxList 失败（第 {}/{} 次）：{}", attempt, maxRetry, e.getMessage());
                if (attempt < maxRetry) {
                    sleepQuietly(new long[]{5, 30, 120}[Math.min(attempt - 1, 2)] * 1000L);
                }
            }
        }
        throw new IllegalStateException("调用 cxList 失败（已重试 " + maxRetry + " 次）："
                + (lastError == null ? "未知错误" : lastError.getMessage()), lastError);
    }

    private FetchResult doStream(Consumer<CxListRow> consumer) throws IOException, InterruptedException {
        String url = baseUrl + cxListPath;
        log.info("开始请求 datamain cxList: {}", url);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(readTimeoutSeconds))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode());
        }

        int code = -1;
        int count = 0;
        try (InputStream in = response.body(); JsonParser parser = jsonFactory.createParser(in)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("响应顶层不是对象");
            }
            while (parser.nextToken() == JsonToken.FIELD_NAME) {
                String field = parser.currentName();
                JsonToken token = parser.nextToken();
                if ("code".equals(field)) {
                    code = readCode(parser, token);
                    continue;
                }
                if ("data".equals(field) && token == JsonToken.START_OBJECT) {
                    return readWrapper(parser, consumer, code);
                }
                if (token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT) {
                    parser.skipChildren();
                }
            }
        }
        return new FetchResult(code, count);
    }

    private FetchResult readWrapper(JsonParser parser, Consumer<CxListRow> consumer, int code)
            throws IOException {
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String field = parser.currentName();
            JsonToken token = parser.nextToken();
            if ("data".equals(field) && token == JsonToken.START_ARRAY) {
                int count = readRows(parser, consumer);
                return new FetchResult(code, count);
            }
            if ("code".equals(field)) {
                code = readCode(parser, token);
                continue;
            }
            if (token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT) {
                parser.skipChildren();
            }
        }
        return new FetchResult(code, 0);
    }

    private int readRows(JsonParser parser, Consumer<CxListRow> consumer) throws IOException {
        int count = 0;
        JsonToken token;
        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.END_ARRAY) {
                break;
            }
            if (token == JsonToken.START_ARRAY) {
                ArrayNode row = objectMapper.readTree(parser);
                if (row.size() != EXPECTED_COLUMNS) {
                    log.error("cxList 列数异常：期望 {}，实际 {}，已跳过", EXPECTED_COLUMNS, row.size());
                    continue;
                }
                consumer.accept(new CxListRow(row, objectMapper));
                count++;
                if (count % 100 == 0) {
                    log.info("cxList 已读取 {} 行", count);
                }
            } else if (token == JsonToken.START_OBJECT) {
                parser.skipChildren();
            }
        }
        return count;
    }

    /**
     * 拉取单个患者的知情同意书完整记录（getzqbyid）
     *
     * <p>响应较小（约 0.2MB），直接读为字符串解析。</p>
     */
    public com.fasterxml.jackson.databind.JsonNode fetchConsentRecords(long externalId) {
        String url = baseUrl + "/datamain/getzqbyid?queryId=" + externalId;
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(readTimeoutSeconds))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode());
            }
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(response.body());
            int code = root.path("code").asInt(-1);
            if (code != 0) {
                throw new IOException("外部接口返回 code=" + code);
            }
            com.fasterxml.jackson.databind.JsonNode data = root.path("data").path("data");
            return data.isArray() ? data : objectMapper.createArrayNode();
        } catch (Exception e) {
            throw new IllegalStateException("获取知情同意书失败 externalId=" + externalId + ": " + e.getMessage(), e);
        }
    }

    private int readCode(JsonParser parser, JsonToken token) throws IOException {
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getIntValue();
        }
        if (token == JsonToken.VALUE_STRING) {
            try {
                return Integer.parseInt(parser.getText());
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }
        return -1;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** 外部返回 code 与行数 */
    public record FetchResult(int code, int rowCount) {
    }
}
