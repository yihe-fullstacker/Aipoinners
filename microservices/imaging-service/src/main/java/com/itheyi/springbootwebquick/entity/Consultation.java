package com.itheyi.springbootwebquick.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = true)
    private MedicalImage medicalImage;

    @Column(name = "patient_complaint", columnDefinition = "TEXT")
    private String patientComplaint;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "ai_analysis_json", columnDefinition = "TEXT")
    private String aiAnalysisJson;

    @Column(name = "doctor_diagnosis", columnDefinition = "TEXT")
    private String doctorDiagnosis;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}