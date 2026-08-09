package com.itheyi.springbootwebquick.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationSummaryDTO {
    private Long id;
    private String status;
    private String doctorDiagnosis;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long medicalImageId;
    private String imageUrl;
}
