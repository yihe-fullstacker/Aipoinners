package com.itheyi.springbootwebquick.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthConsultation {
    private Long id;
    private String patientName;
    private String patientGender;
    private String patientPhone;
    private LocalDateTime communicationTime;
    private String province;
    private String mainSymptoms;
    private LocalDateTime createdAt;
}
