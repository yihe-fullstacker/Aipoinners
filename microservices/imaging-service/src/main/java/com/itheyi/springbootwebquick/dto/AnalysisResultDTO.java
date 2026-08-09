package com.itheyi.springbootwebquick.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalysisResultDTO {
    private Long id;
    private Long consultationId;
    private String imageUrl;
    private Double confidence;
    private List<FindingDTO> findings;
    private List<String> recommendations;
    private Double analysisTime;
    private String modelUsed;

    private String featureDescription;
    private List<AttentionRegionDTO> attentionRegions;
    private List<FeatureDTO> features;
    private List<DifferentialDiagnosisDTO> differentialDiagnosis;
    private List<DecisionStepDTO> decisionPath;

    private String heatmapBase64;
    private String heatmapDescription;
    private Map<String, Double> riskStats;
    private String annotatedImageBase64;
    private List<Map<String, Object>> lesionPoints;

    private String analysisMethod;
    private String analysisSource;
    private Double uncertainty;
    private Boolean mockFallback;
}
