package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.dto.ConsultationViewDTO;
import com.itheyi.springbootwebquick.dto.ConsultationSummaryDTO;
import com.itheyi.springbootwebquick.entity.Consultation;
import com.itheyi.springbootwebquick.mapper.PatientConsultationSummaryProjection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationViewService {

    public ConsultationViewDTO toDto(Consultation consultation) {
        if (consultation == null) {
            return null;
        }

        ConsultationViewDTO dto = new ConsultationViewDTO();
        dto.setId(consultation.getId());
        dto.setStatus(consultation.getStatus());
        dto.setPatientComplaint(consultation.getPatientComplaint());
        dto.setAiAnalysisJson(consultation.getAiAnalysisJson());
        dto.setDoctorDiagnosis(consultation.getDoctorDiagnosis());
        dto.setCreatedAt(consultation.getCreatedAt());
        dto.setUpdatedAt(consultation.getUpdatedAt());

        if (consultation.getPatient() != null) {
            dto.setPatientId(consultation.getPatient().getId());
            dto.setPatientName(resolveDisplayName(
                    consultation.getPatient().getFullName(),
                    consultation.getPatient().getUsername()
            ));
        }

        if (consultation.getDoctor() != null) {
            dto.setDoctorId(consultation.getDoctor().getId());
            dto.setDoctorName(resolveDisplayName(
                    consultation.getDoctor().getFullName(),
                    consultation.getDoctor().getUsername()
            ));
        }

        if (consultation.getMedicalImage() != null) {
            dto.setMedicalImageId(consultation.getMedicalImage().getId());
            dto.setImageUrl("/api/images/" + consultation.getMedicalImage().getId());
        }

        return dto;
    }

    public List<ConsultationViewDTO> toDtoList(List<Consultation> consultations) {
        return consultations.stream().map(this::toDto).toList();
    }

    public List<ConsultationSummaryDTO> toPatientSummaryList(List<PatientConsultationSummaryProjection> consultations) {
        return consultations.stream().map(this::toPatientSummaryDto).toList();
    }

    public ConsultationSummaryDTO toPatientSummaryDto(PatientConsultationSummaryProjection consultation) {
        ConsultationSummaryDTO dto = new ConsultationSummaryDTO();
        dto.setId(consultation.getId());
        dto.setStatus(consultation.getStatus());
        dto.setDoctorDiagnosis(consultation.getDoctorDiagnosis());
        dto.setCreatedAt(consultation.getCreatedAt());
        dto.setUpdatedAt(consultation.getUpdatedAt());
        dto.setMedicalImageId(consultation.getMedicalImageId());
        if (consultation.getMedicalImageId() != null) {
            dto.setImageUrl("/api/images/" + consultation.getMedicalImageId());
        }
        return dto;
    }

    private String resolveDisplayName(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }
}
