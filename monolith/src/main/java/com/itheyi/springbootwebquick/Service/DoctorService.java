package com.itheyi.springbootwebquick.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheyi.springbootwebquick.entity.Consultation;
import com.itheyi.springbootwebquick.entity.MedicalImage;
import com.itheyi.springbootwebquick.entity.User;
import com.itheyi.springbootwebquick.mapper.ConsultationRepository;
import com.itheyi.springbootwebquick.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PythonAIService pythonAIService;

    public List<Consultation> getPendingQueue() {
        return consultationRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    public Consultation submitDiagnosis(Long consultationId, Long doctorId, String diagnosisText) {
        Consultation consultation = consultationRepository.findWithRelationsById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        consultation.setDoctor(doctor);
        consultation.setDoctorDiagnosis(diagnosisText);
        consultation.setStatus("COMPLETED");
        return consultationRepository.save(consultation);
    }

    public Consultation callAiService(Long consultationId) {
        Consultation consultation = consultationRepository.findWithRelationsById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

        MedicalImage image = consultation.getMedicalImage();
        if (image == null) {
            throw new IllegalStateException("Consultation image is missing");
        }

        Map<String, Object> aiResult = analyzeImageByPath(image.getFilePath());

        try {
            consultation.setAiAnalysisJson(objectMapper.writeValueAsString(aiResult));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AI analysis result", e);
        }

        return consultationRepository.save(consultation);
    }

    private Map<String, Object> analyzeImageByPath(String imagePath) {
        try {
            Map<String, Object> result = pythonAIService.analyzeMedicalImageByFilePath(imagePath);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Python AI service: " + e.getMessage(), e);
        }

        return new HashMap<>();
    }
}
