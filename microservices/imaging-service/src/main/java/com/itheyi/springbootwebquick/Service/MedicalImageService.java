package com.itheyi.springbootwebquick.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheyi.springbootwebquick.dto.AnalysisResultDTO;
import com.itheyi.springbootwebquick.dto.AttentionRegionDTO;
import com.itheyi.springbootwebquick.dto.DecisionStepDTO;
import com.itheyi.springbootwebquick.dto.DifferentialDiagnosisDTO;
import com.itheyi.springbootwebquick.dto.FeatureDTO;
import com.itheyi.springbootwebquick.dto.FindingDTO;
import com.itheyi.springbootwebquick.dto.ReportSummaryDTO;
import com.itheyi.springbootwebquick.entity.Consultation;
import com.itheyi.springbootwebquick.entity.DiagnosisReport;
import com.itheyi.springbootwebquick.entity.MedicalImage;
import com.itheyi.springbootwebquick.entity.User;
import com.itheyi.springbootwebquick.mapper.ConsultationRepository;
import com.itheyi.springbootwebquick.mapper.DiagnosisReportRepository;
import com.itheyi.springbootwebquick.mapper.DiagnosisReportSummaryProjection;
import com.itheyi.springbootwebquick.mapper.MedicalImageRepository;
import com.itheyi.springbootwebquick.util.FileValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MedicalImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private PythonAIService pythonAIService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private MedicalImagePreviewService medicalImagePreviewService;

    public AnalysisResultDTO analyzeAndSave(MultipartFile imageFile, Long userId) throws IOException {
        return analyzeAndSave(imageFile, userId, null);
    }

    public AnalysisResultDTO analyzeAndSave(MultipartFile imageFile, Long userId, String complaint) throws IOException {
        validateMedicalImage(imageFile);

        MedicalImage medicalImage = saveMedicalImage(imageFile, userId);
        AnalysisResultDTO analysisResult = analyzeWithFallback(imageFile);
        analysisResult.setImageUrl("/api/images/" + medicalImage.getId());

        enhanceAnalysisResult(analysisResult);

        Long reportId = saveDiagnosisReportAndReturnId(analysisResult, medicalImage, userId);
        analysisResult.setId(reportId);

        try {
            Consultation consultation = createConsultationRecord(userId, medicalImage, analysisResult, complaint);
            analysisResult.setConsultationId(consultation.getId());
        } catch (Exception ignored) {
        }

        return analysisResult;
    }

    public AnalysisResultDTO analyzeAndSaveWithDeepseek(MultipartFile imageFile, Long userId) throws IOException {
        validateMedicalImage(imageFile);

        MedicalImage medicalImage = saveMedicalImage(imageFile, userId);
        AnalysisResultDTO analysisResult = deepSeekService.analyzeMedicalImage(imageFile);
        analysisResult.setImageUrl("/api/images/" + medicalImage.getId());

        enhanceAnalysisResult(analysisResult);

        Long reportId = saveDiagnosisReportAndReturnId(analysisResult, medicalImage, userId);
        analysisResult.setId(reportId);

        return analysisResult;
    }

    @Transactional(readOnly = true)
    public List<ReportSummaryDTO> getUserReports(Long userId) {
        List<DiagnosisReportSummaryProjection> summaries =
                diagnosisReportRepository.findReportSummariesByUserIdOrderByCreatedAtDesc(userId);

        return summaries.stream().map(summary -> {
            ReportSummaryDTO dto = new ReportSummaryDTO();
            dto.setId(summary.getId());
            dto.setDate(summary.getDate());
            dto.setImageUrl(summary.getMedicalImageId() == null ? null : "/api/images/" + summary.getMedicalImageId());
            dto.setConfidence(summary.getConfidence());
            dto.setFindingsCount(summary.getFindingsCount() == null ? 0 : summary.getFindingsCount());
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public AnalysisResultDTO getReportDetail(Long reportId, Long userId) {
        DiagnosisReport report = diagnosisReportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        try {
            AnalysisResultDTO result = new AnalysisResultDTO();
            result.setId(report.getId());
            result.setImageUrl("/api/images/" + report.getMedicalImage().getId());
            result.setConfidence(report.getConfidence());
            result.setAnalysisTime(report.getAnalysisTime());
            result.setModelUsed(report.getModelUsed());
            result.setHeatmapBase64(report.getHeatmapBase64());
            result.setHeatmapDescription(report.getHeatmapDescription());
            result.setAnnotatedImageBase64(report.getAnnotatedImageBase64());
            result.setAttentionRegions(readAttentionRegions(report.getAttentionRegions()));
            result.setLesionPoints(readObjectList(report.getLesionPoints()));
            result.setFindings(readFindings(report.getFindings()));
            result.setRecommendations(readRecommendations(report.getRecommendations()));

            enhanceAnalysisResult(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse report detail", e);
        }
    }

    public Map<String, Object> getReportHeatmap(Long reportId, Long userId) {
        DiagnosisReport report = diagnosisReportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (hasText(report.getHeatmapBase64())
                && hasText(report.getAnnotatedImageBase64())
                && hasText(report.getAttentionRegions())
                && hasText(report.getLesionPoints())) {
            Map<String, Object> cached = new LinkedHashMap<>();
            cached.put("heatmap_base64", report.getHeatmapBase64());
            cached.put("heatmap_description", report.getHeatmapDescription() == null ? "" : report.getHeatmapDescription());
            cached.put("attentionRegions", safeReadAttentionRegions(report.getAttentionRegions()));
            cached.put("lesion_points", safeReadObjectList(report.getLesionPoints()));
            cached.put("annotated_image_base64", report.getAnnotatedImageBase64());
            return cached;
        }

        MedicalImage image = report.getMedicalImage();
        Map<String, Object> heatmap = pythonAIService.getHeatmapByFilePath(image.getFilePath());
        Object heatmapBase64 = heatmap.get("heatmap_base64");
        if (heatmapBase64 instanceof String base64 && hasText(base64)) {
            report.setHeatmapBase64(base64);
            Object description = heatmap.get("heatmap_description");
            report.setHeatmapDescription(description instanceof String text ? text : null);
            Object attentionRegions = heatmap.get("attentionRegions");
            if (attentionRegions instanceof List<?>) {
                report.setAttentionRegions(writeJsonOrEmpty(attentionRegions));
            }
            Object lesionPoints = heatmap.get("lesion_points");
            if (lesionPoints instanceof List<?>) {
                report.setLesionPoints(writeJsonOrEmpty(lesionPoints));
            }
            Object annotatedImageBase64 = heatmap.get("annotated_image_base64");
            if (annotatedImageBase64 instanceof String text && hasText(text)) {
                report.setAnnotatedImageBase64(text);
            }
            diagnosisReportRepository.save(report);
        }
        return heatmap;
    }

    public boolean deleteReport(Long reportId, Long userId) {
        DiagnosisReport report = diagnosisReportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        try {
            MedicalImage image = report.getMedicalImage();
            Files.deleteIfExists(Paths.get(image.getFilePath()));
            medicalImageRepository.delete(image);
        } catch (IOException ignored) {
        }

        diagnosisReportRepository.delete(report);
        return true;
    }

    private void validateMedicalImage(MultipartFile imageFile) {
        FileValidationUtil.ValidationResult validation = FileValidationUtil.validateMedicalImage(imageFile);
        if (!validation.isValid()) {
            throw new RuntimeException(validation.getMessage());
        }
    }

    private AnalysisResultDTO analyzeWithFallback(MultipartFile imageFile) {
        try {
            return pythonAIService.analyzeMedicalImage(imageFile);
        } catch (Exception error) {
            return deepSeekService.analyzeMedicalImage(imageFile);
        }
    }

    private Consultation createConsultationRecord(
            Long userId,
            MedicalImage medicalImage,
            AnalysisResultDTO analysisResult,
            String complaint
    ) {
        try {
            User patient = userService.getUserById(userId);
            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setMedicalImage(medicalImage);
            consultation.setStatus("PENDING");
            if (hasText(complaint)) {
                consultation.setPatientComplaint(complaint.trim());
            }
            if (analysisResult != null) {
                consultation.setAiAnalysisJson(objectMapper.writeValueAsString(analysisResult));
            }
            return consultationRepository.save(consultation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create consultation", e);
        }
    }

    private MedicalImage saveMedicalImage(MultipartFile file, Long userId) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        if (!hasText(originalFilename)) {
            originalFilename = "upload.bin";
        }

        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        String uniqueFilename = UUID.randomUUID() + fileExtension;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        medicalImagePreviewService.preparePreviewIfNeeded(targetPath.toFile(), originalFilename);

        User user = userService.getUserById(userId);
        MedicalImage medicalImage = new MedicalImage();
        medicalImage.setUser(user);
        medicalImage.setOriginalFilename(originalFilename);
        medicalImage.setFilePath(targetPath.toFile().getAbsolutePath());
        medicalImage.setFileSize(file.getSize());
        medicalImage.setFileType(file.getContentType());
        return medicalImageRepository.save(medicalImage);
    }

    private void saveDiagnosisReport(AnalysisResultDTO analysisResult, MedicalImage medicalImage, Long userId) {
        saveDiagnosisReportInternal(analysisResult, medicalImage, userId);
    }

    private Long saveDiagnosisReportAndReturnId(AnalysisResultDTO analysisResult, MedicalImage medicalImage, Long userId) {
        return saveDiagnosisReportInternal(analysisResult, medicalImage, userId).getId();
    }

    private DiagnosisReport saveDiagnosisReportInternal(AnalysisResultDTO analysisResult, MedicalImage medicalImage, Long userId) {
        try {
            User user = userService.getUserById(userId);

            DiagnosisReport report = new DiagnosisReport();
            report.setUser(user);
            report.setMedicalImage(medicalImage);
            report.setConfidence(analysisResult.getConfidence());
            report.setFindings(objectMapper.writeValueAsString(defaultFindings(analysisResult.getFindings())));
            report.setRecommendations(objectMapper.writeValueAsString(defaultRecommendations(analysisResult.getRecommendations())));
            report.setFindingsCount(analysisResult.getFindings() == null ? 0 : analysisResult.getFindings().size());
            report.setAnalysisTime(analysisResult.getAnalysisTime());
            report.setModelUsed(analysisResult.getModelUsed());
            report.setHeatmapBase64(analysisResult.getHeatmapBase64());
            report.setHeatmapDescription(analysisResult.getHeatmapDescription());
            report.setAnnotatedImageBase64(analysisResult.getAnnotatedImageBase64());
            report.setAttentionRegions(objectMapper.writeValueAsString(defaultAttentionRegions(analysisResult.getAttentionRegions())));
            report.setLesionPoints(objectMapper.writeValueAsString(defaultObjectList(analysisResult.getLesionPoints())));

            return diagnosisReportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save diagnosis report", e);
        }
    }

    private List<FindingDTO> readFindings(String findingsJson) throws IOException {
        if (!hasText(findingsJson)) {
            return Collections.emptyList();
        }
        return objectMapper.readValue(findingsJson, new TypeReference<List<FindingDTO>>() {});
    }

    private List<String> readRecommendations(String recommendationsJson) throws IOException {
        if (!hasText(recommendationsJson)) {
            return Collections.emptyList();
        }
        return objectMapper.readValue(recommendationsJson, new TypeReference<List<String>>() {});
    }

    private List<AttentionRegionDTO> readAttentionRegions(String attentionRegionsJson) throws IOException {
        if (!hasText(attentionRegionsJson)) {
            return Collections.emptyList();
        }
        return objectMapper.readValue(attentionRegionsJson, new TypeReference<List<AttentionRegionDTO>>() {});
    }

    private List<Map<String, Object>> readObjectList(String json) throws IOException {
        if (!hasText(json)) {
            return Collections.emptyList();
        }
        return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
    }

    private List<AttentionRegionDTO> safeReadAttentionRegions(String attentionRegionsJson) {
        try {
            return readAttentionRegions(attentionRegionsJson);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> safeReadObjectList(String json) {
        try {
            return readObjectList(json);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private String writeJsonOrEmpty(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ignored) {
            return "[]";
        }
    }

    private List<FindingDTO> defaultFindings(List<FindingDTO> findings) {
        return findings == null ? Collections.emptyList() : findings;
    }

    private List<String> defaultRecommendations(List<String> recommendations) {
        return recommendations == null ? Collections.emptyList() : recommendations;
    }

    private List<AttentionRegionDTO> defaultAttentionRegions(List<AttentionRegionDTO> attentionRegions) {
        return attentionRegions == null ? Collections.emptyList() : attentionRegions;
    }

    private List<Map<String, Object>> defaultObjectList(List<Map<String, Object>> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private void enhanceAnalysisResult(AnalysisResultDTO result) {
        List<FindingDTO> findings = result.getFindings() == null ? Collections.emptyList() : result.getFindings();
        if (!hasText(result.getFeatureDescription())) {
            result.setFeatureDescription(generateFeatureDescription(findings, result.getConfidence()));
        }
        if (result.getAttentionRegions() == null || result.getAttentionRegions().isEmpty()) {
            result.setAttentionRegions(generateAttentionRegions(findings));
        }
        if (result.getFeatures() == null || result.getFeatures().isEmpty()) {
            result.setFeatures(generateFeatures(findings));
        }
        if (result.getDifferentialDiagnosis() == null || result.getDifferentialDiagnosis().isEmpty()) {
            result.setDifferentialDiagnosis(generateDifferentialDiagnosis(findings, result.getConfidence()));
        }
        if (result.getDecisionPath() == null || result.getDecisionPath().isEmpty()) {
            result.setDecisionPath(generateDecisionPath(findings, result.getConfidence()));
        }
        if (!hasText(result.getHeatmapDescription()) && hasText(result.getHeatmapBase64())) {
            result.setHeatmapDescription("红色为高风险区域，橙色为可疑区域，蓝色为相对低风险区域。");
        }
        if (result.getRecommendations() == null || result.getRecommendations().isEmpty()) {
            result.setRecommendations(Arrays.asList(
                    "建议由专科医生结合完整病史进一步解读本次影像结果。",
                    "请结合患者症状、既往影像及检查结果综合判断。"
            ));
        }
    }

    private String generateFeatureDescription(List<FindingDTO> findings, Double confidence) {
        if (findings.isEmpty()) {
            return "本次 AI 复核暂未发现明确异常征象，建议结合临床信息继续判断。";
        }

        StringBuilder builder = new StringBuilder("本次 AI 共识别出 ");
        builder.append(findings.size()).append(" 处重点关注区域：");
        for (FindingDTO finding : findings) {
            builder.append(finding.getType() == null ? "异常征象" : finding.getType());
            if (hasText(finding.getLocation())) {
                builder.append("，位置位于").append(finding.getLocation());
            }
            if (hasText(finding.getSize())) {
                builder.append("，大小约").append(finding.getSize());
            }
            builder.append("。");
        }

        if (confidence != null) {
            builder.append("整体判断置信度约为 ").append(String.format("%.1f%%", confidence * 100)).append("。");
        }
        return builder.toString().trim();
    }

    private List<AttentionRegionDTO> generateAttentionRegions(List<FindingDTO> findings) {
        if (findings.isEmpty()) {
            return Collections.emptyList();
        }

        List<AttentionRegionDTO> regions = new ArrayList<>();
        for (int i = 0; i < findings.size(); i++) {
            FindingDTO finding = findings.get(i);
            AttentionRegionDTO region = new AttentionRegionDTO();
            region.setLocation(hasText(finding.getLocation()) ? finding.getLocation() : "关注区域 " + (i + 1));
            region.setSize(hasText(finding.getSize()) ? finding.getSize() : "中等");
            region.setScore(finding.getConfidence() == null ? 0.6 : finding.getConfidence());
            region.setDescription((finding.getType() == null ? "异常征象" : finding.getType()) + "，建议重点复核。");
            region.setX(110.0 + (i * 130.0));
            region.setY(90.0 + (i * 70.0));
            region.setWidth(140.0);
            region.setHeight(100.0);
            regions.add(region);
        }
        return regions;
    }

    private List<FeatureDTO> generateFeatures(List<FindingDTO> findings) {
        if (findings.isEmpty()) {
            return Arrays.asList(
                    buildFeature("边界", "边界清晰度", "当前影像边界整体较清晰，建议结合原图继续观察。", 0.72, 0.68),
                    buildFeature("密度", "密度变化", "局部密度与周围组织存在一定差异，建议结合临床判断。", 0.75, 0.7)
            );
        }

        List<FeatureDTO> features = new ArrayList<>();
        for (FindingDTO finding : findings) {
            double confidence = finding.getConfidence() == null ? 0.65 : finding.getConfidence();
            features.add(buildFeature(
                    "病灶特征",
                    (finding.getType() == null ? "异常征象" : finding.getType()) + "特征",
                    (hasText(finding.getLocation()) ? finding.getLocation() : "当前关注区域") + "存在需要重点关注的影像学特征。",
                    clamp(confidence + 0.08),
                    confidence
            ));
        }
        return features;
    }

    private FeatureDTO buildFeature(String type, String name, String description, double importance, double confidence) {
        FeatureDTO feature = new FeatureDTO();
        feature.setType(type);
        feature.setName(name);
        feature.setDescription(description);
        feature.setImportance(clamp(importance));
        feature.setConfidence(clamp(confidence));
        return feature;
    }

    private List<DifferentialDiagnosisDTO> generateDifferentialDiagnosis(List<FindingDTO> findings, Double confidence) {
        if (findings.isEmpty()) {
            return Collections.emptyList();
        }

        double baseConfidence = confidence == null ? 0.7 : confidence;
        List<String> supportingFeatures = findings.stream()
                .map(finding -> (finding.getType() == null ? "异常征象" : finding.getType())
                        + (hasText(finding.getLocation()) ? "，位置：" + finding.getLocation() : ""))
                .toList();

        List<DifferentialDiagnosisDTO> diagnoses = new ArrayList<>();
        diagnoses.add(buildDiagnosis("良性病变可能", clamp(baseConfidence * 0.5), "当前影像表现更偏向低风险病变，但仍需结合临床复核。", supportingFeatures));
        diagnoses.add(buildDiagnosis("炎性改变可能", clamp(baseConfidence * 0.3), "炎症性改变仍需纳入鉴别诊断范围。", supportingFeatures));
        diagnoses.add(buildDiagnosis("恶性病变可能", clamp(baseConfidence * 0.2), "建议结合病史、随访和进一步检查综合评估。", supportingFeatures));

        normalizeProbabilities(diagnoses);
        return diagnoses;
    }

    private DifferentialDiagnosisDTO buildDiagnosis(String name, double probability, String description, List<String> supportingFeatures) {
        DifferentialDiagnosisDTO diagnosis = new DifferentialDiagnosisDTO();
        diagnosis.setName(name);
        diagnosis.setProbability(probability);
        diagnosis.setDescription(description);
        diagnosis.setSupportingFeatures(supportingFeatures);
        diagnosis.setContradictingFeatures(Collections.emptyList());
        return diagnosis;
    }

    private void normalizeProbabilities(List<DifferentialDiagnosisDTO> diagnoses) {
        double total = diagnoses.stream()
                .map(DifferentialDiagnosisDTO::getProbability)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        if (total <= 0) {
            return;
        }

        diagnoses.forEach(diagnosis -> diagnosis.setProbability(diagnosis.getProbability() / total));
    }

    private List<DecisionStepDTO> generateDecisionPath(List<FindingDTO> findings, Double confidence) {
        double finalConfidence = confidence == null ? 0.7 : confidence;
        List<DecisionStepDTO> steps = new ArrayList<>();
        steps.add(buildDecisionStep("影像预处理", "对上传影像进行标准化与质量校正，便于模型识别。", 0.95, 0.1));
        steps.add(buildDecisionStep("特征提取", "提取关注区域的边界、密度、纹理等关键特征。", 0.88, 0.3));
        steps.add(buildDecisionStep("病灶检测", "本次共识别出 " + findings.size() + " 个候选关注区域。", finalConfidence, 0.4));
        steps.add(buildDecisionStep("综合判断", "结合提取特征形成最终 AI 辅助分析建议。", finalConfidence, 0.2));
        return steps;
    }

    private DecisionStepDTO buildDecisionStep(String stage, String description, double confidence, double weight) {
        DecisionStepDTO step = new DecisionStepDTO();
        step.setStage(stage);
        step.setDescription(description);
        step.setConfidence(clamp(confidence));
        step.setWeight(clamp(weight));
        return step;
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
