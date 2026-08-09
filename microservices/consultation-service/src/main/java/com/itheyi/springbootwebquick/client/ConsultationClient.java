package com.itheyi.springbootwebquick.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "assistant-service")
public interface ConsultationClient {

    @PostMapping("/api/chat")
    String chat(@RequestBody Map<String, String> params);
}