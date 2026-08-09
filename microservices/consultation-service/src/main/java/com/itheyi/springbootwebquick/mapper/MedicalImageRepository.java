package com.itheyi.springbootwebquick.mapper;

import com.itheyi.springbootwebquick.entity.MedicalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalImageRepository extends JpaRepository<MedicalImage, Long> {
    List<MedicalImage> findByUserIdOrderByUploadTimeDesc(Long userId);
}
