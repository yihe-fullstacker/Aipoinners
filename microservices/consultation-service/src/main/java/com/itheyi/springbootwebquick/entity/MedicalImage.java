package com.itheyi.springbootwebquick.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_images")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MedicalImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @CreationTimestamp
    @Column(name = "upload_time", updatable = false)
    private LocalDateTime uploadTime;
}