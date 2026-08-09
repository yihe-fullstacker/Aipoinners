package com.itheyi.springbootwebquick.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheyi.springbootwebquick.dto.DifyAssistantRequest;
import com.itheyi.springbootwebquick.dto.DifyAssistantResponse;
import com.itheyi.springbootwebquick.tools.WebSearchTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DifyWorkflowService {
    private static final Logger log = LoggerFactory.getLogger(DifyWorkflowService.class);
    private static final List<String> OUTPUT_CANDIDATE_KEYS = List.of(
            "answer", "text", "result", "output", "content", "message"
    );
    private static final List<String> WEB_SEARCH_HINTS = List.of(
            "最新", "最近", "联网", "搜索", "查一下", "查一查", "网址", "链接", "官网", "论文", "文献",
            "latest", "search", "url", "website", "paper", "journal", "news"
    );

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WebSearchTool webSearchTool;
    //service层复杂核心的业务配置
    @Value("${dify.workflow.base-url:}")
    private String baseUrl;

    @Value("${dify.workflow.api-key:}")
    private String apiKey;

    @Value("${dify.workflow.input-key:query}")
    private String inputKey;

    @Value("${dify.workflow.timeout-seconds:60}")
    private long timeoutSeconds;

    public DifyWorkflowService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, WebSearchTool webSearchTool) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.webSearchTool = webSearchTool;
    }
    //Dify API调用
    public DifyAssistantResponse runWorkflow(DifyAssistantRequest request) {
        String normalizedMessage = normalize(request.getMessage());
        if (normalizedMessage.isEmpty()) {
            throw new IllegalArgumentException("Assistant message cannot be empty");
        }
        //// 在runWorkflow中使用
        if (shouldUseWebSearch(normalizedMessage)) {
            log.info("[Dify Assistant] detected web-search intent, query: {}", normalizedMessage);
            String searchAnswer = webSearchTool.searchMedicalInfo(normalizedMessage);
            if (!searchAnswer.isBlank() && !searchAnswer.startsWith("调用百度千帆搜索失败")) {
                return DifyAssistantResponse.builder()
                        .answer(searchAnswer)
                        .workflowRunId("web-search")
                        .taskId("web-search")
                        .build();
            }
            log.warn("[Dify Assistant] web-search fallback to workflow, reason: {}", searchAnswer);
        }
        //验证配置
        validateConfig();
        //构建请求payload
        Map<String, Object> payload = buildPayload(request, normalizedMessage, "blocking");

        log.info("[Dify Assistant] calling workflow, route: {}, page: {}", request.getRoutePath(), request.getPageTitle());
        //调用Dify API
        String rawResponse = webClient.post()
                .uri(baseUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(Math.max(10L, timeoutSeconds)));
        //解析响应
        return parseResponse(rawResponse);
    }
    //响应式流式输出
    public Flux<String> runWorkflowStream(DifyAssistantRequest request) {
        String normalizedMessage = normalize(request.getMessage());
        if (normalizedMessage.isEmpty()) {
            return Flux.error(new IllegalArgumentException("Assistant message cannot be empty"));
        }

        if (shouldUseWebSearch(normalizedMessage)) {
            log.info("[Dify Assistant] stream request uses web-search shortcut, query: {}", normalizedMessage);
            String searchAnswer = webSearchTool.searchMedicalInfo(normalizedMessage);
            if (!searchAnswer.isBlank()) {
                return Flux.just(searchAnswer);
            }
            log.warn("[Dify Assistant] web-search fallback to streaming workflow, reason: {}", searchAnswer);
        }

        try {
            validateConfig();
        } catch (Exception e) {
            return Flux.error(e);
        }

        Map<String, Object> payload = buildPayload(request, normalizedMessage, "streaming");
        log.info("[Dify Assistant] calling streaming workflow, route: {}, page: {}", request.getRoutePath(), request.getPageTitle());
        //构建并执行对 Dify API 的 HTTP 请求
        return webClient.post()
                .uri(baseUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(Math.max(30L, timeoutSeconds * 3)))
                .flatMapIterable(this::extractStreamingTextChunks)
                .filter(chunk -> chunk != null && !chunk.isBlank());
    }
    // 智能体输入构建（构建请求）
    /*- 支持 **动态key-value** 输入参数
    - 支持 **对话历史** 传递，提供上下文理解
    - 支持 **路由上下文** 信息（页面标题、路由路径等）
    - 使用 **LinkedHashMap** 保持参数顺序*/
    private Map<String, Object> buildPayload(DifyAssistantRequest request, String normalizedMessage, String responseMode) {
        Map<String, Object> inputs = new LinkedHashMap<>();
        inputs.put(inputKey, normalizedMessage);
        inputs.put("message", normalizedMessage);
        inputs.put("query", normalizedMessage);
        inputs.put("page_title", normalize(request.getPageTitle()));
        inputs.put("route_name", normalize(request.getRouteName()));
        inputs.put("route_path", normalize(request.getRoutePath()));
        inputs.put("context_title", normalize(request.getContextTitle()));
        inputs.put("conversation_history", buildHistoryText(request.getHistory()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("inputs", inputs);
        payload.put("response_mode", responseMode);
        payload.put("user", buildUserId(request.getUserId()));
        return payload;
    }

    private DifyAssistantResponse parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new IllegalStateException("Dify workflow returned an empty response");
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode dataNode = root.path("data");
            JsonNode outputsNode = dataNode.path("outputs");

            String answer = extractAnswer(outputsNode);
            if (answer.isBlank()) {
                answer = extractAnswer(root);
            }
            if (answer.isBlank()) {
                throw new IllegalStateException("Dify workflow succeeded but no readable output was found");
            }

            return DifyAssistantResponse.builder()
                    .answer(answer)
                    .workflowRunId(textValue(root, "workflow_run_id"))
                    .taskId(textValue(root, "task_id"))
                    .build();
        } catch (Exception e) {
            log.error("[Dify Assistant] failed to parse workflow response: {}", rawResponse, e);
            throw new IllegalStateException("Failed to parse Dify workflow response", e);
        }
    }

    private List<String> extractStreamingTextChunks(String rawChunk) {
        List<String> chunks = new ArrayList<>();
        if (rawChunk == null || rawChunk.isBlank()) {
            return chunks;
        }

        String[] lines = rawChunk.split("\\r?\\n");
        for (String line : lines) {
            String normalizedLine = line.trim();
            if (normalizedLine.isEmpty() || normalizedLine.startsWith(":") || normalizedLine.startsWith("event:")) {
                continue;
            }

            String data = normalizedLine.startsWith("data:")
                    ? normalizedLine.substring(5).trim()
                    : normalizedLine;

            if (data.isEmpty() || "[DONE]".equalsIgnoreCase(data)) {
                continue;
            }

            String text = extractStreamingText(data);
            if (!text.isBlank()) {
                chunks.add(text);
            }
        }
        return chunks;
    }
    // JSON响应解析
    private String extractStreamingText(String rawData) {
        try {
            JsonNode root = objectMapper.readTree(rawData);
            String event = textValue(root, "event");
            JsonNode dataNode = root.path("data");

            if ("text_chunk".equals(event)) {
                String chunkText = firstNonBlank(
                        textValue(dataNode, "text"),
                        textValue(dataNode, "answer"),
                        textValue(root, "text"),
                        textValue(root, "answer")
                );
                if (!chunkText.isBlank()) {
                    return chunkText;
                }
            }

            if ("workflow_finished".equals(event)) {
                String finalAnswer = extractAnswer(dataNode.path("outputs"));
                if (!finalAnswer.isBlank()) {
                    return finalAnswer;
                }
            }
        } catch (Exception ignored) {
            if (!rawData.startsWith("{") && !rawData.startsWith("[")) {
                return rawData;
            }
        }
        return "";
    }
    //JSON响应解析
    private String extractAnswer(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "";
        }
        for (String key : OUTPUT_CANDIDATE_KEYS) {
            String value = textValue(node, key);
            if (!value.isBlank()) {
                return value;
            }
        }
        if (node.isTextual()) {
            return node.asText("");
        }
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String nested = extractAnswer(entry.getValue());
                if (!nested.isBlank()) {
                    return nested;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode item : node) {
                String nested = extractAnswer(item);
                if (!nested.isBlank()) {
                    return nested;
                }
            }
        }
        return "";
    }
    //对话历史管理
    /*
    * **功能说明**：
- 将 **对话列表** 转为结构化文本
- 支持 **role: content** 格式
- 提供 **上下文理解** 能力，提升对话连贯性
    * */
    private String buildHistoryText(List<Map<String, String>> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map<String, String> item : history) {
            if (item == null) {
                continue;
            }
            String role = normalize(item.get("role"));
            String content = normalize(item.get("content"));
            if (content.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(role.isEmpty() ? "user" : role).append(": ").append(content);
        }
        return builder.toString();
    }

    private String buildUserId(String userId) {
        String normalized = normalize(userId);
        return normalized.isEmpty() ? "medical-assistant-web" : normalized;
    }

    private String textValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.path(fieldName);
        return valueNode.isMissingNode() || valueNode.isNull() ? "" : valueNode.asText("");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
    //意图识别与工具路由
    /*
    * **功能说明**：
- 使用 **关键词匹配** 识别搜索意图
- 优先调用 **自定义工具** 处理特定请求
- 实现 **灵活的工具路由** 机制
    * */
    private boolean shouldUseWebSearch(String message) {
        for (String hint : WEB_SEARCH_HINTS) {
            if (message.toLowerCase().contains(hint.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void validateConfig() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Dify workflow base URL is missing");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Dify workflow API key is missing");
        }
    }
}
