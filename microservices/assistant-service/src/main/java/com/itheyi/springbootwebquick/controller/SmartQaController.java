package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.SmartQaService;
import com.itheyi.springbootwebquick.aiservice.HealthConsultantService;
import com.itheyi.springbootwebquick.tools.WebSearchTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/smart-qa")
@CrossOrigin(
        originPatterns = {
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://47.108.52.206",
                "http://47.108.52.206:*",
                "https://47.108.52.206",
                "https://47.108.52.206:*"
        },
        allowCredentials = "true"
)
public class SmartQaController {

    private static final Logger log = LoggerFactory.getLogger(SmartQaController.class);

    @Autowired
    @Qualifier("healthConsultantService")
    private HealthConsultantService healthConsultantService;

    @Autowired
    @Qualifier("healthConsultantServiceWithSearch")
    private HealthConsultantService healthConsultantServiceWithSearch;

    @Autowired
    @Qualifier("healthConsultantServiceFallback")
    private HealthConsultantService healthConsultantServiceFallback;

    @Autowired
    @Qualifier("healthConsultantServiceWithSearchFallback")
    private HealthConsultantService healthConsultantServiceWithSearchFallback;

    @Autowired
    private SmartQaService smartQaService;

    @Autowired
    private WebSearchTool webSearchTool;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
    public Flux<String> chat(@RequestParam String memoryId,
                             @RequestParam String message,
                             @RequestParam(defaultValue = "false") boolean webSearchEnabled) {
        log.info("Received smart QA chat request. memoryId={}, webSearchEnabled={}", memoryId, webSearchEnabled);

        if (webSearchEnabled) {
            String finalMessage = buildSearchAugmentedMessage(message);
            return healthConsultantServiceWithSearch.chat(memoryId, finalMessage)
                    .onErrorResume(error -> isModelNotFound(error)
                            ? healthConsultantServiceWithSearchFallback.chat(memoryId, finalMessage)
                            : Flux.error(error));
        }

        return healthConsultantService.chat(memoryId, message)
                .onErrorResume(error -> isModelNotFound(error)
                        ? healthConsultantServiceFallback.chat(memoryId, message)
                        : Flux.error(error));
    }

    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
    public Flux<String> chatStream(@RequestParam String memoryId,
                                   @RequestParam String message,
                                   @RequestParam(defaultValue = "false") boolean webSearchEnabled) {
        log.info("Received smart QA streaming request. memoryId={}, webSearchEnabled={}", memoryId, webSearchEnabled);

        if (webSearchEnabled) {
            String finalMessage = buildSearchAugmentedMessage(message);
            return healthConsultantServiceWithSearch.chat(memoryId, finalMessage)
                    .onErrorResume(error -> isModelNotFound(error)
                            ? healthConsultantServiceWithSearchFallback.chat(memoryId, finalMessage)
                            : Flux.error(error));
        }

        return healthConsultantService.chat(memoryId, message)
                .onErrorResume(error -> isModelNotFound(error)
                        ? healthConsultantServiceFallback.chat(memoryId, message)
                        : Flux.error(error));
    }

    @PostMapping("/chat-with-image")
    public Map<String, Object> chatWithImage(@RequestParam(required = false) String userMessage,
                                             @RequestParam(required = false) MultipartFile image,
                                             @RequestParam(required = false) MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        MultipartFile uploadedFile = (file != null && !file.isEmpty()) ? file : image;

        try {
            String safeMessage = userMessage == null ? "" : userMessage.trim();
            if (safeMessage.isEmpty() && (uploadedFile == null || uploadedFile.isEmpty())) {
                throw new IllegalArgumentException("请至少输入问题或上传一个文件。");
            }

            log.info("Received attachment QA request. messageLength={}, file={}, size={}, contentType={}",
                    safeMessage.length(),
                    uploadedFile != null ? uploadedFile.getOriginalFilename() : "none",
                    uploadedFile != null ? uploadedFile.getSize() : 0L,
                    uploadedFile != null ? uploadedFile.getContentType() : "none");

            String response = smartQaService.chatWithVision(safeMessage, uploadedFile);
            result.put("success", true);
            result.put("response", response);
            result.put("message", "ok");
            return result;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid attachment QA request: {}", e.getMessage());
            result.put("success", false);
            result.put("response", "");
            result.put("message", e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Attachment QA failed", e);
            result.put("success", false);
            result.put("response", "");
            result.put("message", "文件分析失败: " + e.getMessage());
            return result;
        }
    }

    @PostMapping(value = "/chat-with-image-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatWithImageStream(@RequestParam(required = false) String userMessage,
                                                             @RequestParam(required = false) MultipartFile image,
                                                             @RequestParam(required = false) MultipartFile file) {
        MultipartFile uploadedFile = (file != null && !file.isEmpty()) ? file : image;
        String safeMessage = userMessage == null ? "" : userMessage.trim();

        if (safeMessage.isEmpty() && (uploadedFile == null || uploadedFile.isEmpty())) {
            return Flux.error(new IllegalArgumentException("请至少输入问题或上传一个文件。"));
        }

        log.info("Received streaming attachment QA request. messageLength={}, file={}, size={}, contentType={}",
                safeMessage.length(),
                uploadedFile != null ? uploadedFile.getOriginalFilename() : "none",
                uploadedFile != null ? uploadedFile.getSize() : 0L,
                uploadedFile != null ? uploadedFile.getContentType() : "none");

        return Flux.<ServerSentEvent<String>>create(sink -> {
                    sink.next(ServerSentEvent.<String>builder()
                            .event("status")
                            .data("processing")
                            .build());

                    smartQaService.chatStreamWithAttachment(safeMessage, uploadedFile, new SmartQaService.StreamCallback() {
                        @Override
                        public void onChunk(String chunk) {
                            if (!sink.isCancelled() && chunk != null && !chunk.isEmpty()) {
                                sink.next(ServerSentEvent.<String>builder()
                                        .event("message")
                                        .data(chunk)
                                        .build());
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (!sink.isCancelled()) {
                                sink.complete();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            if (!sink.isCancelled()) {
                                sink.error(e);
                            }
                        }
                    });
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String buildSearchAugmentedMessage(String message) {
        String searchResult = webSearchTool.searchMedicalInfo(message);
        return "【系统提示】用户开启了联网搜索。以下是系统自动检索到的参考信息：\n\n"
                + searchResult
                + "\n\n请你扮演专业医疗 AI 助手，结合以上信息回答用户问题：\n"
                + message;
    }

    private boolean isModelNotFound(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (message != null
                    && (message.contains("model_not_found")
                    || message.contains("Model not exist")
                    || message.contains("OpenAiHttpException"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
