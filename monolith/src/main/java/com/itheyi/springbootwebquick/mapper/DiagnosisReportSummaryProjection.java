package com.itheyi.springbootwebquick.mapper;

import java.time.LocalDateTime;

public interface DiagnosisReportSummaryProjection {
    Long getId();

    LocalDateTime getDate();

    Long getMedicalImageId();

    Double getConfidence();

    Integer getFindingsCount();
}
