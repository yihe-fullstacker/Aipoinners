package com.itheyi.springbootwebquick.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/*
* consultation-service 中的 PythonAIService 主要是 医生端辅助诊断
* 时调用 AI 服务，功能相对简单；而 imaging-service 中的 PythonAIService 功能更完整，负责 完整的医学影像分析流程 。
* */
@Service
public class PythonAIService {

    private static final Logger log = LoggerFactory.getLogger(PythonAIService.class);

    @Value("${python.ai.service.url:http://127.0.0.1:5000}")
    private String pythonServiceUrl;

    @Value("${python.ai.service.prefer-v2:true}")
    private boolean preferV2;

    private final RestTemplate restTemplate;
    //准备远程调用的工具41-46
    public PythonAIService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2_000);
        requestFactory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("configuredUrl", getBaseUrl());
        status.put("analyzeEndpoints", getAnalyzeEndpoints());
        return status;
    }

    public Map<String, Object> analyzeMedicalImageByFilePath(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Image file does not exist: " + imagePath);
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    //发起远程调用
        for (String endpoint : getAnalyzeEndpoints()) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        getBaseUrl() + endpoint,
                        HttpMethod.POST,
                        requestEntity,
                        Map.class
                );
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                }
            } catch (RestClientException error) {
                log.warn("Calling Python AI endpoint {} failed: {}", endpoint, error.getMessage());
            }
        }

        throw new IllegalStateException("Python AI analyze endpoints all failed: " + getAnalyzeEndpoints());
    }

    private String getBaseUrl() {
        String url = pythonServiceUrl == null || pythonServiceUrl.isBlank() ? "http://127.0.0.1:5000" : pythonServiceUrl.trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private List<String> getAnalyzeEndpoints() {
        return preferV2
                ? Arrays.asList("/v2/analyze", "/analyze")
                : Arrays.asList("/analyze", "/v2/analyze");
    }
}
