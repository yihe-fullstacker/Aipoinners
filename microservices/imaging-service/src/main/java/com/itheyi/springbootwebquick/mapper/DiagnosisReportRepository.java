package com.itheyi.springbootwebquick.mapper;

import com.itheyi.springbootwebquick.entity.DiagnosisReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisReportRepository extends JpaRepository<DiagnosisReport, Long> {

    @EntityGraph(attributePaths = {"medicalImage"})
    List<DiagnosisReport> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"medicalImage"})
    Optional<DiagnosisReport> findByIdAndUserId(Long id, Long userId);

    @Query("""
            select
                r.id as id,
                r.createdAt as date,
                mi.id as medicalImageId,
                r.confidence as confidence,
                r.findingsCount as findingsCount
            from DiagnosisReport r
            join r.medicalImage mi
            where r.user.id = :userId
            order by r.createdAt desc
            """)
    List<DiagnosisReportSummaryProjection> findReportSummariesByUserIdOrderByCreatedAtDesc(Long userId);
}
