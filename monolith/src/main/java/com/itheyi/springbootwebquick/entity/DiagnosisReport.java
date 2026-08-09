package com.itheyi.springbootwebquick.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis_reports")
@Data
public class DiagnosisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private MedicalImage medicalImage;

    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "findings_count")
    private Integer findingsCount;

    @Column(name = "analysis_time")
    private Double analysisTime;

    @Column(name = "model_used", length = 100)
    private String modelUsed;

    @Lob
    @Column(name = "heatmap_base64", columnDefinition = "LONGTEXT")
    private String heatmapBase64;

    @Column(name = "heatmap_description", columnDefinition = "TEXT")
    private String heatmapDescription;

    @Lob
    @Column(name = "annotated_image_base64", columnDefinition = "LONGTEXT")
    private String annotatedImageBase64;

    @Column(name = "attention_regions", columnDefinition = "LONGTEXT")
    private String attentionRegions;

    @Column(name = "lesion_points", columnDefinition = "LONGTEXT")
    private String lesionPoints;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
