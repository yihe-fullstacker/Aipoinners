package com.itheyi.springbootwebquick.mapper;

import java.time.LocalDateTime;

public interface PatientConsultationSummaryProjection {
    Long getId();

    String getStatus();

    String getDoctorDiagnosis();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getMedicalImageId();
}
