package com.itheyi.springbootwebquick.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationViewDTO {
    private Long id;
    private String status;
    private String patientComplaint;
    private String aiAnalysisJson;
    private String doctorDiagnosis;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long medicalImageId;
    private String imageUrl;
}
