package com.itheyi.springbootwebquick.controller;
// REST控制器设计
/*
* **功能说明**：
- 使用 **@PostMapping** 暴露API端点
- 支持 **请求验证** (@Valid + @NotBlank)
- 支持 **跨域** (@CrossOrigin)
- 支持 **流式响应** (produces = MediaType.TEXT_EVENT_STREAM_VALUE)
* */
import com.itheyi.springbootwebquick.Service.DifyWorkflowService;
import com.itheyi.springbootwebquick.dto.DifyAssistantRequest;
import com.itheyi.springbootwebquick.dto.DifyAssistantResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
// AI辅助生成：Dify, 2026-04-26
@RestController
@RequestMapping("/api/assistant")
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
public class DifyAssistantController {

    private static final Logger log = LoggerFactory.getLogger(DifyAssistantController.class);

    private final DifyWorkflowService difyWorkflowService;

    public DifyAssistantController(DifyWorkflowService difyWorkflowService) {
        this.difyWorkflowService = difyWorkflowService;
    }

    // 阻塞式API
    @PostMapping("/workflow")
    public DifyAssistantResponse runWorkflow(@Valid @RequestBody DifyAssistantRequest request) {
        log.info("[Assistant Controller] workflow request received, route: {}, page: {}",
                request.getRoutePath(), request.getPageTitle());
        return difyWorkflowService.runWorkflow(request);
    }

    // 流式API
    @PostMapping(value = "/workflow-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> runWorkflowStream(@Valid @RequestBody DifyAssistantRequest request) {
        log.info("[Assistant Controller] streaming workflow request received, route: {}, page: {}",
                request.getRoutePath(), request.getPageTitle());
        return Flux.concat(
                Flux.just(ServerSentEvent.<String>builder()
                        .event("status")
                        .data("processing")
                        .build()),
                difyWorkflowService.runWorkflowStream(request)
                        .map(chunk -> ServerSentEvent.<String>builder()
                                .event("message")
                                .data(chunk)
                                .build())
        );
    }
}
