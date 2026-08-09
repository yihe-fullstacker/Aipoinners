package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.dto.AnalysisResultDTO;
import com.itheyi.springbootwebquick.dto.AttentionRegionDTO;
import com.itheyi.springbootwebquick.dto.FindingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PythonAIService {

    private static final Logger log = LoggerFactory.getLogger(PythonAIService.class);
    private static final long HEALTH_CHECK_TTL_MS = 10_000L;
    private static final String DEFAULT_HEATMAP_DESCRIPTION = "红色为高风险区域，橙色为可疑区域，蓝色为相对低风险区域。";

    @Value("${python.ai.service.url:http://127.0.0.1:5000}")
    private String pythonServiceUrl;

    @Value("${python.ai.service.prefer-v2:true}")
    private boolean preferV2;

    @Value("${python.ai.service.allow-mock-fallback:true}")
    private boolean allowMockFallback;

    private final RestTemplate restTemplate;
    private volatile long lastHealthCheckAt = 0L;
    private volatile boolean cachedServiceAvailable = false;

    public PythonAIService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2_000);
        requestFactory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        String baseUrl = getBaseUrl();
        long startedAt = System.currentTimeMillis();

        status.put("configuredUrl", baseUrl);
        status.put("preferV2", preferV2);
        status.put("allowMockFallback", allowMockFallback);
        status.put("analyzeEndpoints", getAnalyzeEndpoints());
        status.put("healthEndpoint", baseUrl + "/health");

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/health", Map.class);
            boolean available = response.getStatusCode() == HttpStatus.OK;
            cachedServiceAvailable = available;
            lastHealthCheckAt = System.currentTimeMillis();

            status.put("available", available);
            status.put("httpStatus", response.getStatusCode().value());
            status.put("latencyMs", System.currentTimeMillis() - startedAt);
            status.put("response", response.getBody() == null ? Collections.emptyMap() : response.getBody());
        } catch (Exception error) {
            cachedServiceAvailable = false;
            lastHealthCheckAt = System.currentTimeMillis();

            status.put("available", false);
            status.put("latencyMs", System.currentTimeMillis() - startedAt);
            status.put("error", error.getMessage());
        }

        return status;
    }

    public AnalysisResultDTO analyzeMedicalImage(MultipartFile imageFile) {
        if (!isServiceAvailable()) {
            log.warn("Python AI service is unavailable, using mock fallback");
            return buildFallbackResult();
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource resource = imageFile.getResource();
        body.add("image", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        for (String endpoint : getAnalyzeEndpoints()) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        getBaseUrl() + endpoint,
                        HttpMethod.POST,
                        requestEntity,
                        Map.class
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    AnalysisResultDTO parsed = parseToAnalysisResult(response.getBody());
                    if (parsed != null) {
                        return parsed;
                    }
                }
            } catch (RestClientException error) {
                log.warn("Calling Python AI endpoint {} failed: {}", endpoint, error.getMessage());
            } catch (Exception error) {
                log.error("Unexpected error while parsing Python AI response from {}: {}", endpoint, error.getMessage(), error);
            }
        }

        log.warn("Python AI analyze endpoints all failed, using mock fallback");
        return buildFallbackResult();
    }

    public Map<String, Object> analyzeMedicalImageByFilePath(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Image file does not exist: " + imagePath);
        }
        if (!isServiceAvailable()) {
            throw new IllegalStateException("Python AI service is unavailable: " + getBaseUrl());
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

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
                log.warn("Calling Python AI file endpoint {} failed: {}", endpoint, error.getMessage());
            }
        }

        throw new IllegalStateException("Python AI analyze endpoints all failed: " + getAnalyzeEndpoints());
    }

    public Map<String, Object> forwardJsonRequest(String endpoint, Map<String, Object> payload) {
        if (!isServiceAvailable()) {
            throw new IllegalStateException("Python AI service is unavailable: " + getBaseUrl());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(
                payload == null ? Collections.emptyMap() : payload,
                headers
        );

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
            log.warn("Forwarding JSON request to Python endpoint {} failed: {}", endpoint, error.getMessage());
            throw error;
        }

        return Collections.emptyMap();
    }

    public Map<String, Object> generateHeatmap(MultipartFile imageFile) {
        if (!isServiceAvailable()) {
            throw new IllegalStateException("Python AI service is unavailable: " + getBaseUrl());
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageFile.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                getBaseUrl() + "/heatmap",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        return Collections.emptyMap();
    }

    public Map<String, Object> getHeatmapByFilePath(String filePath) {
        try {
            if (!isServiceAvailable()) {
                return Collections.emptyMap();
            }

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    getBaseUrl() + "/heatmap",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception error) {
            log.warn("Calling Python heatmap endpoint failed: {}", error.getMessage());
        }

        return Collections.emptyMap();
    }

    private String getBaseUrl() {
        String url = pythonServiceUrl == null || pythonServiceUrl.isBlank()
                ? "http://127.0.0.1:5000"
                : pythonServiceUrl.trim();
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

    private boolean isServiceAvailable() {
        long now = System.currentTimeMillis();
        if (now - lastHealthCheckAt < HEALTH_CHECK_TTL_MS) {
            return cachedServiceAvailable;
        }

        synchronized (this) {
            now = System.currentTimeMillis();
            if (now - lastHealthCheckAt < HEALTH_CHECK_TTL_MS) {
                return cachedServiceAvailable;
            }

            cachedServiceAvailable = probeServiceAvailability();
            lastHealthCheckAt = now;
            return cachedServiceAvailable;
        }
    }

    private boolean probeServiceAvailability() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/health", Map.class);
            boolean available = response.getStatusCode() == HttpStatus.OK;
            if (available) {
                log.info("Python AI service is available: {}", getBaseUrl());
            }
            return available;
        } catch (Exception error) {
            log.warn("Python AI health check failed at {}: {}", getBaseUrl(), error.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private AnalysisResultDTO parseToAnalysisResult(Map<String, Object> response) {
        try {
            AnalysisResultDTO result = new AnalysisResultDTO();
            result.setConfidence(asDouble(firstNonNull(response.get("confidence_calibrated"), response.get("confidence")), 0.85));
            result.setAnalysisTime(asDouble(response.get("analysisTime"), 2.3));
            result.setModelUsed(asString(response.get("modelUsed"), "DenseNet121_Enhanced_v3_TTA_TempScale"));
            result.setAnalysisMethod(asString(response.get("analysisMethod"), "深度学习 + 图像处理"));
            result.setAnalysisSource(asString(response.get("analysisSource"), "python-real"));
            result.setUncertainty(asDouble(response.get("uncertainty"), 0.0));
            result.setMockFallback(asBoolean(response.get("mockFallback"), false));

            List<FindingDTO> findings = new ArrayList<>();
            if (response.get("findings") instanceof List<?> findingList) {
                for (Object item : findingList) {
                    if (!(item instanceof Map<?, ?> findingMap)) {
                        continue;
                    }

                    FindingDTO finding = new FindingDTO();
                    finding.setType(asString(findingMap.get("type"), "异常影像征象"));
                    finding.setLocation(asString(findingMap.get("location"), "待复核部位"));
                    finding.setConfidence(asDouble(findingMap.get("confidence"), result.getConfidence() == null ? 0.85 : result.getConfidence()));
                    finding.setSize(asString(findingMap.get("size"), null));

                    Object characteristics = findingMap.get("characteristics");
                    if (characteristics instanceof List<?> values) {
                        finding.setCharacteristics(values.stream().map(String::valueOf).toList());
                    } else {
                        finding.setCharacteristics(Arrays.asList("边界待复核", "密度待复核"));
                    }
                    findings.add(finding);
                }
            }
            result.setFindings(findings);

            if (response.get("recommendations") instanceof List<?> recommendations) {
                result.setRecommendations(recommendations.stream().map(String::valueOf).toList());
            } else {
                result.setRecommendations(defaultRecommendations());
            }

            if (response.get("heatmap_base64") instanceof String heatmapBase64) {
                result.setHeatmapBase64(heatmapBase64);
            }

            String heatmapDescription = asString(response.get("heatmap_description"), null);
            if (heatmapDescription == null && result.getHeatmapBase64() != null) {
                heatmapDescription = DEFAULT_HEATMAP_DESCRIPTION;
            }
            result.setHeatmapDescription(heatmapDescription);

            if (response.get("risk_stats") instanceof Map<?, ?> riskStats) {
                Map<String, Double> normalizedRiskStats = new LinkedHashMap<>();
                riskStats.forEach((key, value) -> normalizedRiskStats.put(String.valueOf(key), asDouble(value, 0.0)));
                result.setRiskStats(normalizedRiskStats);
            }

            result.setAttentionRegions(parseAttentionRegions(response.get("attentionRegions")));
            result.setLesionPoints(parseObjectList(response.get("lesion_points")));
            if (response.get("annotated_image_base64") instanceof String annotatedImageBase64) {
                result.setAnnotatedImageBase64(annotatedImageBase64);
            }
            return result;
        } catch (Exception error) {
            log.error("Failed to parse Python AI response: {}", error.getMessage(), error);
            return buildFallbackResult();
        }
    }

    private List<AttentionRegionDTO> parseAttentionRegions(Object rawAttentionRegions) {
        if (!(rawAttentionRegions instanceof List<?> regionList)) {
            return Collections.emptyList();
        }

        List<AttentionRegionDTO> regions = new ArrayList<>();
        for (Object item : regionList) {
            if (!(item instanceof Map<?, ?> regionMap)) {
                continue;
            }

            AttentionRegionDTO region = new AttentionRegionDTO();
            region.setLocation(asString(regionMap.get("location"), "关注区域"));
            region.setSize(asString(regionMap.get("size"), "待复核"));
            region.setScore(asDouble(firstNonNull(regionMap.get("score"), regionMap.get("confidence")), 0.0));
            region.setDescription(asString(regionMap.get("description"), "AI 关注区域，请结合影像与临床信息复核。"));
            region.setX(asNullableDouble(regionMap.get("x")));
            region.setY(asNullableDouble(regionMap.get("y")));
            region.setWidth(asNullableDouble(regionMap.get("width")));
            region.setHeight(asNullableDouble(regionMap.get("height")));
            regions.add(region);
        }
        return regions;
    }

    private List<Map<String, Object>> parseObjectList(Object rawList) {
        if (!(rawList instanceof List<?> values)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> normalized = new ArrayList<>();
        for (Object item : values) {
            if (!(item instanceof Map<?, ?> rawMap)) {
                continue;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            rawMap.forEach((key, value) -> map.put(String.valueOf(key), value));
            normalized.add(map);
        }
        return normalized;
    }

    private AnalysisResultDTO buildFallbackResult() {
        if (!allowMockFallback) {
            throw new IllegalStateException("Python AI service is unavailable and mock fallback is disabled");
        }
        return getMockAnalysisResult();
    }

    private AnalysisResultDTO getMockAnalysisResult() {
        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setConfidence(0.87);
        result.setAnalysisTime(2.3);
        result.setModelUsed("Mock_Medical_Demo_v1");
        result.setAnalysisMethod("本地模拟分析兜底");
        result.setAnalysisSource("python-mock");
        result.setUncertainty(0.18);
        result.setMockFallback(true);
        result.setHeatmapDescription("当前为模拟兜底结果，Python 服务恢复后会自动返回真实热力图。");

        List<FindingDTO> findings = new ArrayList<>();

        FindingDTO finding1 = new FindingDTO();
        finding1.setType("肺结节");
        finding1.setLocation("右上肺");
        finding1.setConfidence(0.85);
        finding1.setSize("1.2cm");
        finding1.setCharacteristics(Arrays.asList("边界较清晰", "密度相对稳定", "形态较规则"));
        findings.add(finding1);

        FindingDTO finding2 = new FindingDTO();
        finding2.setType("钙化灶");
        finding2.setLocation("左下肺");
        finding2.setConfidence(0.92);
        finding2.setSize("点状");
        finding2.setCharacteristics(Arrays.asList("高密度", "边界较清晰"));
        findings.add(finding2);

        result.setFindings(findings);
        result.setRecommendations(defaultRecommendations());
        return result;
    }

    private List<String> defaultRecommendations() {
        return Arrays.asList(
                "建议 3 到 6 个月后复查 CT，动态观察病灶变化。",
                "若临床风险较高，可结合 PET-CT 或专科进一步评估。",
                "请结合症状、吸烟史、既往影像和实验室检查综合判断。",
                "如持续不适或风险升高，建议尽快到专科门诊复诊。"
        );
    }

    private Object firstNonNull(Object first, Object second) {
        return first != null ? first : second;
    }

    private double asDouble(Object value, double fallback) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return value == null ? fallback : Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException error) {
            return fallback;
        }
    }

    private Double asNullableDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private boolean asBoolean(Object value, boolean fallback) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value == null) {
            return fallback;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private String asString(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String stringValue = String.valueOf(value).trim();
        return stringValue.isEmpty() ? fallback : stringValue;
    }
}
