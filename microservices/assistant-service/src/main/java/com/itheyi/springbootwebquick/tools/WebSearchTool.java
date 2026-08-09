package com.itheyi.springbootwebquick.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联网搜索工具
 * 1. 优先调用百度千帆 web_search 获取结构化网页引用
 * 2. 当结构化搜索不可用时，再退回 ai_search 生成式搜索
 */
@Component
public class WebSearchTool {

    @Value("${baidu.ai-search.url:https://qianfan.baidubce.com/v2/ai_search/chat/completions}")
    private String baiduSearchUrl;

    @Value("${baidu.ai-search.web-search-url:https://qianfan.baidubce.com/v2/ai_search/web_search}")
    private String baiduWebSearchUrl;

    @Value("${baidu.ai-search.api-key:}")
    private String baiduApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool("搜索真实互联网信息，如最新医疗资讯、研究进展、指南更新、论文网址和官网链接")
    public String searchMedicalInfo(@P("搜索关键词") String query) {
        System.out.println("执行百度千帆联网搜索，关键词: " + query);

        List<SearchReference> references = searchMedicalReferences(query, 8);
        if (!references.isEmpty()) {
            return formatReferences(query, references);
        }

        try {
            String normalizedUrl = normalizeConfigValue(baiduSearchUrl);
            String normalizedApiKey = normalizeApiKey(baiduApiKey);
            if (normalizedApiKey.isEmpty()) {
                return "调用百度千帆搜索失败: baidu.ai-search.api-key 为空或格式无效";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + normalizedApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", query + "。请尽量返回最新网页名称、网址和简要说明。");
            messages.add(message);

            requestBody.put("messages", messages);
            requestBody.put("model", "ai_search");
            requestBody.put("stream", false);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(normalizedUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> respMessage = (Map<String, Object>) firstChoice.get("message");
                    if (respMessage != null) {
                        return String.valueOf(respMessage.getOrDefault("content", ""));
                    }
                }
            }

            return "联网搜索已执行，但没有拿到可展示的网页结果。";
        } catch (HttpClientErrorException.Unauthorized e) {
            String body = e.getResponseBodyAsString();
            String errorMsg = "调用百度千帆搜索失败: 401 Unauthorized"
                    + (body != null && !body.isEmpty() ? (", 响应体: " + body) : "")
                    + "。请检查 baidu.ai-search.api-key 是否正确，且不要手动拼 Bearer 前缀。当前 Key 特征: "
                    + maskKeyForLog(normalizeApiKey(baiduApiKey));
            System.err.println(errorMsg);
            return errorMsg;
        } catch (Exception e) {
            String errorMsg = "调用百度千帆搜索失败: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            return errorMsg;
        }
    }

    public List<SearchReference> searchMedicalReferences(String query, int topK) {
        List<SearchReference> references = new ArrayList<>();

        try {
            String normalizedUrl = normalizeConfigValue(baiduWebSearchUrl);
            String normalizedApiKey = normalizeApiKey(baiduApiKey);
            if (normalizedApiKey.isEmpty()) {
                return references;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + normalizedApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", query);
            messages.add(message);

            requestBody.put("messages", messages);
            requestBody.put("search_source", "baidu_search_v2");

            List<Map<String, Object>> resourceTypeFilter = new ArrayList<>();
            Map<String, Object> webFilter = new HashMap<>();
            webFilter.put("type", "web");
            webFilter.put("top_k", Math.max(1, topK));
            resourceTypeFilter.add(webFilter);
            requestBody.put("resource_type_filter", resourceTypeFilter);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(normalizedUrl, request, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return references;
            }

            Object referencesNode = response.getBody().get("references");
            if (!(referencesNode instanceof List<?> referenceList)) {
                return references;
            }

            for (Object item : referenceList) {
                if (!(item instanceof Map<?, ?> referenceMap)) {
                    continue;
                }

                String url = stringValue(referenceMap.get("url"));
                if (url.isBlank()) {
                    continue;
                }

                references.add(new SearchReference(
                        stringValue(referenceMap.get("title")),
                        url,
                        stringValue(referenceMap.get("website")),
                        stringValue(referenceMap.get("date")),
                        stringValue(referenceMap.get("content"))
                ));
            }
        } catch (Exception e) {
            System.err.println("调用百度千帆 web_search 失败: " + e.getMessage());
        }

        return references;
    }

    private String formatReferences(String query, List<SearchReference> references) {
        StringBuilder builder = new StringBuilder();
        builder.append("已为您执行联网搜索。以下是与“")
                .append(query)
                .append("”相关的网页结果，点击网址即可跳转：\n\n");

        int index = 1;
        for (SearchReference reference : references) {
            builder.append(index).append(". ")
                    .append(reference.title().isBlank() ? "未命名网页" : reference.title())
                    .append('\n');
            builder.append("网址: ").append(reference.url()).append('\n');

            if (!reference.website().isBlank() || !reference.date().isBlank()) {
                builder.append("来源: ");
                if (!reference.website().isBlank()) {
                    builder.append(reference.website());
                }
                if (!reference.website().isBlank() && !reference.date().isBlank()) {
                    builder.append(" | ");
                }
                if (!reference.date().isBlank()) {
                    builder.append(reference.date());
                }
                builder.append('\n');
            }

            String summary = trimSummary(reference.content());
            if (!summary.isBlank()) {
                builder.append("摘要: ").append(summary).append('\n');
            }

            builder.append('\n');
            index++;
        }

        builder.append("如果您希望我继续解读其中某个网页内容，可以直接回复对应序号或标题。");
        return builder.toString().trim();
    }

    private String normalizeConfigValue(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replace("`", "");
    }

    private String normalizeApiKey(String apiKey) {
        String normalized = normalizeConfigValue(apiKey);
        if (normalized.isEmpty()) {
            return "";
        }
        if (normalized.regionMatches(true, 0, "Bearer ", 0, 7)) {
            normalized = normalized.substring(7).trim();
        }
        return normalized;
    }

    private String maskKeyForLog(String key) {
        if (key == null || key.isEmpty()) {
            return "[empty]";
        }
        int length = key.length();
        int keep = Math.min(10, length);
        String prefix = key.substring(0, keep);
        return "[len=" + length + ",prefix=" + prefix + "***]";
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String trimSummary(String content) {
        String normalized = stringValue(content).replaceAll("\\s+", " ");
        if (normalized.length() <= 120) {
            return normalized;
        }
        return normalized.substring(0, 120) + "...";
    }

    public record SearchReference(
            String title,
            String url,
            String website,
            String date,
            String content
    ) {
    }
}
