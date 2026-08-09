package com.itheyi.springbootwebquick.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SmartQaService {

    private static final Logger logger = LoggerFactory.getLogger(SmartQaService.class);

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024;
    private static final int MAX_TEXT_CHARS = 12000;
    private static final int MAX_PDF_PAGES = 2;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 240000;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.base-url:https://dashscope.aliyuncs.com/compatible-mode}")
    private String baseUrl;

    @Value("${openai.chat.options.model-name:qwen-plus}")
    private String modelName;

    @Value("${openai.vision.model-name:qwen3-vl-plus}")
    private String visionModelName;

    @Value("${openai.chat.fallback.model-name:qwen-turbo}")
    private String fallbackModelName;

    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public SmartQaService() {
        this.objectMapper = new ObjectMapper();
        this.systemPrompt = loadSystemPrompt();
        logger.info("SmartQaService initialized");
    }

    private String loadSystemPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource("system.txt");
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (Exception e) {
            logger.error("Failed to load system prompt", e);
            return "你是一名专业的医疗 AI 助手，请基于用户提供的信息，用简体中文给出清晰、准确、谨慎的分析与建议。";
        }
    }

    public String chat(String userMessage) {
        return executeTextCompletion(normalizeUserMessage(userMessage), modelName);
    }

    public String chatWithVision(String userMessage, String base64Image) {
        AttachmentPayload payload = new AttachmentPayload();
        if (StringUtils.hasText(base64Image)) {
            payload.imageDataUrls.add("data:image/jpeg;base64," + base64Image);
            payload.sourceDescription = "医学影像图片";
        }
        return executeAttachmentCompletion(normalizeUserMessage(userMessage), payload);
    }

    public String chatWithVision(String userMessage, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return chat(userMessage);
        }
        AttachmentPayload payload = prepareAttachmentPayload(file);
        return executeAttachmentCompletion(normalizeUserMessage(userMessage), payload);
    }

    public void chatStream(String userMessage, StreamCallback callback) {
        chatStreamWithVision(userMessage, null, callback);
    }

    public void chatStreamWithVision(String userMessage, String base64Image, StreamCallback callback) {
        doChatStreamWithVision(userMessage, base64Image, callback, false);
    }

    public void chatStreamWithAttachment(String userMessage, MultipartFile file, StreamCallback callback) {
        if (file == null || file.isEmpty()) {
            chatStream(userMessage, callback);
            return;
        }

        AttachmentPayload payload = prepareAttachmentPayload(file);
        executeAttachmentStream(normalizeUserMessage(userMessage), payload, callback);
    }

    private String executeAttachmentCompletion(String userMessage, AttachmentPayload payload) {
        validateAttachmentPayload(payload);
        String attachmentPrompt = buildAttachmentPrompt(userMessage, payload);
        String preferredModel = visionModelName;
        return executeMultimodalCompletion(attachmentPrompt, payload.imageDataUrls, preferredModel);
    }

    private void executeAttachmentStream(String userMessage, AttachmentPayload payload, StreamCallback callback) {
        validateAttachmentPayload(payload);
        String attachmentPrompt = buildAttachmentPrompt(userMessage, payload);
        String preferredModel = visionModelName;
        executeMultimodalStream(attachmentPrompt, payload.imageDataUrls, preferredModel, callback, false);
    }

    private void validateAttachmentPayload(AttachmentPayload payload) {
        if (payload.imageDataUrls.isEmpty() && !StringUtils.hasText(payload.extractedText)) {
            throw new IllegalArgumentException("无法从上传文件中提取可分析内容。");
        }
    }

    private AttachmentPayload prepareAttachmentPayload(MultipartFile file) {
        try {
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new IllegalArgumentException("上传文件不能超过 50MB。");
            }

            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new IllegalArgumentException("上传文件内容为空。");
            }

            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin");
            String mimeType = resolveMimeType(fileName, file.getContentType());
            logger.info("Preparing attachment payload. name={}, mimeType={}, size={}", fileName, mimeType, bytes.length);

            if (isImageMimeType(mimeType)) {
                AttachmentPayload payload = new AttachmentPayload();
                payload.imageDataUrls.add(buildDataUrl(mimeType, bytes));
                payload.sourceDescription = "医学影像图片";
                return payload;
            }

            if (isPdfMimeType(mimeType, fileName)) {
                return preparePdfPayload(bytes);
            }

            if (isDocxMimeType(mimeType, fileName)) {
                AttachmentPayload payload = new AttachmentPayload();
                payload.extractedText = truncateText(extractDocxText(bytes));
                payload.sourceDescription = "Word 检查报告";
                return payload;
            }

            if (isPlainTextMimeType(mimeType, fileName)) {
                AttachmentPayload payload = new AttachmentPayload();
                payload.extractedText = truncateText(new String(bytes, StandardCharsets.UTF_8));
                payload.sourceDescription = "文本检查报告";
                return payload;
            }

            throw new IllegalArgumentException("暂不支持该文件类型，请上传图片、PDF、DOCX、TXT、CSV 或 JSON 文件。");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to prepare attachment payload", e);
            throw new IllegalStateException("文件处理失败: " + e.getMessage(), e);
        }
    }

    private AttachmentPayload preparePdfPayload(byte[] bytes) throws Exception {
        AttachmentPayload payload = new AttachmentPayload();
        payload.sourceDescription = "PDF 检查报告";

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            payload.extractedText = truncateText(stripper.getText(document));

            if (StringUtils.hasText(payload.extractedText)) {
                return payload;
            }

            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = Math.min(document.getNumberOfPages(), MAX_PDF_PAGES);
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 96, ImageType.RGB);
                payload.imageDataUrls.add(bufferedImageToDataUrl(image));
            }
        }

        validateAttachmentPayload(payload);
        return payload;
    }

    private String extractDocxText(byte[] bytes) throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!"word/document.xml".equals(entry.getName())) {
                    continue;
                }

                String xml = new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8);
                String text = xml
                        .replaceAll("</w:p>", "\n")
                        .replaceAll("<[^>]+>", " ")
                        .replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replaceAll("\\s+", " ")
                        .trim();

                if (!StringUtils.hasText(text)) {
                    throw new IllegalArgumentException("Word 文档中未提取到可分析文本。");
                }
                return text;
            }
        }

        throw new IllegalArgumentException("未在 DOCX 文件中找到可识别的正文内容。");
    }

    private String buildAttachmentPrompt(String userMessage, AttachmentPayload payload) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请你作为专业医疗报告解读助手，使用简体中文分析用户上传的")
                .append(StringUtils.hasText(payload.sourceDescription) ? payload.sourceDescription : "附件")
                .append("。\n");
        prompt.append("请遵循以下要求：\n");
        prompt.append("1. 优先提炼关键检查项目、异常指标、影像可见异常和重要结论。\n");
        prompt.append("2. 用患者容易理解的语言解释含义，不要夸大，也不要替代医生做最终诊断。\n");
        prompt.append("3. 明确指出哪些内容需要继续到医院复查或咨询专科医生。\n");
        prompt.append("4. 如果材料不完整、分辨率不足或无法确认，请直接说明不确定性。\n");
        prompt.append("5. 输出尽量结构化，适合直接展示给患者。\n");
        prompt.append("用户问题：").append(userMessage).append("\n");

        if (StringUtils.hasText(payload.extractedText)) {
            prompt.append("\n以下是从附件中提取出的文本内容：\n");
            prompt.append(payload.extractedText);
        } else if (!payload.imageDataUrls.isEmpty()) {
            prompt.append("\n请直接基于上传的图片内容进行 OCR 识别、医学信息提取和结果解读。");
        }

        return prompt.toString();
    }

    private String executeTextCompletion(String prompt, String selectedModel) {
        return executeMultimodalCompletion(prompt, Collections.emptyList(), selectedModel);
    }

    private String executeMultimodalCompletion(String prompt, List<String> imageDataUrls, String selectedModel) {
        String url = resolveChatCompletionsUrl();
        RestTemplate restTemplate = buildRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", selectedModel);
        requestBody.put("temperature", 0.2);
        requestBody.put("messages", buildMessages(prompt, imageDataUrls));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            int statusCode = response.getStatusCode().value();
            String responseBody = response.getBody();

            logger.info("Completion response status: {}", statusCode);
            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("AI 接口返回异常，HTTP " + statusCode);
            }

            String assistantText = extractAssistantText(responseBody);
            if (!StringUtils.hasText(assistantText)) {
                throw new IllegalStateException("AI 返回内容为空。");
            }
            return assistantText;
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            logger.error("AI API HTTP error. status={}, body={}", e.getStatusCode(), body);

            if (shouldFallback(selectedModel, imageDataUrls, e.getStatusCode().value(), body)) {
                logger.warn("Model {} unavailable, falling back to {}", selectedModel, fallbackModelName);
                return executeMultimodalCompletion(prompt, imageDataUrls, fallbackModelName);
            }

            throw new IllegalStateException("AI 接口请求失败: " + e.getStatusCode().value() + " " + body, e);
        } catch (Exception e) {
            logger.error("Error calling AI API", e);
            throw new IllegalStateException("AI 调用失败: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> buildMessages(String prompt, List<String> imageDataUrls) {
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        if (imageDataUrls == null || imageDataUrls.isEmpty()) {
            userMessage.put("content", prompt);
        } else {
            List<Map<String, Object>> content = new ArrayList<>();

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", prompt);
            content.add(textPart);

            for (String imageDataUrl : imageDataUrls) {
                Map<String, Object> imagePart = new HashMap<>();
                imagePart.put("type", "image_url");

                Map<String, String> imageUrl = new HashMap<>();
                imageUrl.put("url", imageDataUrl);
                imagePart.put("image_url", imageUrl);

                content.add(imagePart);
            }

            userMessage.put("content", content);
        }

        messages.add(userMessage);
        return messages;
    }

    private String extractAssistantText(String responseBody) throws Exception {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return "";
        }

        JsonNode contentNode = choices.get(0).path("message").path("content");
        if (contentNode.isTextual()) {
            return contentNode.asText("");
        }

        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            contentNode.forEach(item -> {
                JsonNode textNode = item.path("text");
                if (textNode.isTextual()) {
                    builder.append(textNode.asText(""));
                }
            });
            return builder.toString();
        }

        return "";
    }

    private String normalizeUserMessage(String userMessage) {
        if (StringUtils.hasText(userMessage)) {
            return userMessage.trim();
        }
        return "请分析我上传的体检报告，并给出简体中文解读和建议。";
    }

    private String resolveChatCompletionsUrl() {
        String url = baseUrl;
        if (!url.endsWith("/chat/completions")) {
            if (url.endsWith("/")) {
                url += "chat/completions";
            } else {
                url += "/chat/completions";
            }
        }
        return url;
    }

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MS);
        return new RestTemplate(requestFactory);
    }

    private String resolveMimeType(String fileName, String rawContentType) {
        if (StringUtils.hasText(rawContentType) && !"application/octet-stream".equalsIgnoreCase(rawContentType)) {
            return rawContentType.toLowerCase(Locale.ROOT);
        }

        String lowerFileName = fileName.toLowerCase(Locale.ROOT);
        if (lowerFileName.endsWith(".png")) return "image/png";
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerFileName.endsWith(".gif")) return "image/gif";
        if (lowerFileName.endsWith(".bmp")) return "image/bmp";
        if (lowerFileName.endsWith(".webp")) return "image/webp";
        if (lowerFileName.endsWith(".pdf")) return "application/pdf";
        if (lowerFileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lowerFileName.endsWith(".txt")) return "text/plain";
        if (lowerFileName.endsWith(".csv")) return "text/csv";
        if (lowerFileName.endsWith(".json")) return "application/json";
        return rawContentType == null ? "application/octet-stream" : rawContentType.toLowerCase(Locale.ROOT);
    }

    private boolean isImageMimeType(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private boolean isPdfMimeType(String mimeType, String fileName) {
        return "application/pdf".equals(mimeType) || fileName.toLowerCase(Locale.ROOT).endsWith(".pdf");
    }

    private boolean isDocxMimeType(String mimeType, String fileName) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)
                || fileName.toLowerCase(Locale.ROOT).endsWith(".docx");
    }

    private boolean isPlainTextMimeType(String mimeType, String fileName) {
        String lowerFileName = fileName.toLowerCase(Locale.ROOT);
        return (mimeType != null && mimeType.startsWith("text/"))
                || "application/json".equals(mimeType)
                || lowerFileName.endsWith(".txt")
                || lowerFileName.endsWith(".csv")
                || lowerFileName.endsWith(".json");
    }

    private String buildDataUrl(String mimeType, byte[] bytes) {
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    private String bufferedImageToDataUrl(BufferedImage image) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return buildDataUrl("image/jpeg", outputStream.toByteArray());
    }

    private String truncateText(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }

        String normalized = text.replace("\u0000", " ").replaceAll("\\s+", " ").trim();
        if (normalized.length() <= MAX_TEXT_CHARS) {
            return normalized;
        }
        return normalized.substring(0, MAX_TEXT_CHARS);
    }

    private void doChatStreamWithVision(String userMessage, String base64Image, StreamCallback callback, boolean isFallback) {
        List<String> imageDataUrls = new ArrayList<>();
        if (StringUtils.hasText(base64Image)) {
            imageDataUrls.add("data:image/jpeg;base64," + base64Image);
        }

        String selectedModel = !imageDataUrls.isEmpty() ? visionModelName : (isFallback ? fallbackModelName : modelName);
        executeMultimodalStream(normalizeUserMessage(userMessage), imageDataUrls, selectedModel, callback, isFallback);
    }

    private void executeMultimodalStream(String prompt,
                                         List<String> imageDataUrls,
                                         String selectedModel,
                                         StreamCallback callback,
                                         boolean isFallback) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(resolveChatCompletionsUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", selectedModel);
            requestBody.put("stream", true);
            requestBody.put("temperature", 0.2);
            requestBody.put("messages", buildMessages(prompt, imageDataUrls));

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                String errorBody = readConnectionBody(connection.getErrorStream());
                logger.error("Streaming API error. status={}, body={}", responseCode, errorBody);

                if (shouldFallback(selectedModel, imageDataUrls, responseCode, errorBody) && !isFallback) {
                    logger.warn("Streaming model {} unavailable, falling back to {}", selectedModel, fallbackModelName);
                    executeMultimodalStream(prompt, imageDataUrls, fallbackModelName, callback, true);
                    return;
                }

                callback.onError(new IllegalStateException("AI 流式请求失败: HTTP " + responseCode + " " + errorBody));
                return;
            }

            boolean completed = false;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) {
                        continue;
                    }

                    String data = line.substring(5).trim();
                    if (data.isEmpty()) {
                        continue;
                    }

                    if ("[DONE]".equals(data)) {
                        completed = true;
                        callback.onComplete();
                        break;
                    }

                    try {
                        JsonNode root = objectMapper.readTree(data);
                        JsonNode choices = root.path("choices");
                        if (choices.isArray() && !choices.isEmpty()) {
                            JsonNode delta = choices.get(0).path("delta");
                            String content = extractStreamingDeltaText(delta);
                            if (!content.isEmpty()) {
                                callback.onChunk(content);
                            }
                        }
                    } catch (Exception parseException) {
                        logger.debug("Ignore non-text stream chunk: {}", data, parseException);
                    }
                }
            }

            if (!completed) {
                callback.onComplete();
            }
        } catch (Exception e) {
            if (shouldFallback(selectedModel, imageDataUrls, 404, e.getMessage()) && !isFallback) {
                logger.warn("Streaming model {} unavailable, falling back to {}", selectedModel, fallbackModelName);
                executeMultimodalStream(prompt, imageDataUrls, fallbackModelName, callback, true);
                return;
            }
            callback.onError(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractStreamingDeltaText(JsonNode delta) {
        if (delta == null || delta.isMissingNode()) {
            return "";
        }

        JsonNode contentNode = delta.path("content");
        if (contentNode.isTextual()) {
            return contentNode.asText("");
        }

        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            contentNode.forEach(item -> {
                JsonNode textNode = item.path("text");
                if (textNode.isTextual()) {
                    builder.append(textNode.asText(""));
                }
            });
            return builder.toString();
        }

        return "";
    }

    private boolean shouldFallback(String selectedModel, List<String> imageDataUrls, int statusCode, String message) {
        if (!imageDataUrls.isEmpty()) {
            return false;
        }

        if (fallbackModelName == null || fallbackModelName.equals(selectedModel)) {
            return false;
        }

        String text = message == null ? "" : message;
        return statusCode == 404 || text.contains("model_not_found") || text.contains("Model not exist");
    }

    private String readConnectionBody(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            logger.debug("Failed to read connection body", e);
        }
        return builder.toString();
    }

    public interface StreamCallback {
        void onChunk(String chunk);
        void onComplete();
        void onError(Exception e);
    }

    private static final class AttachmentPayload {
        private final List<String> imageDataUrls = new ArrayList<>();
        private String extractedText;
        private String sourceDescription;
    }
}
