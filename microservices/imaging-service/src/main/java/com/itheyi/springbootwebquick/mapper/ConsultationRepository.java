package com.itheyi.springbootwebquick.mapper;

import com.itheyi.springbootwebquick.entity.Consultation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    @EntityGraph(attributePaths = {"patient", "doctor", "medicalImage"})
    List<Consultation> findByStatusOrderByCreatedAtDesc(String status);

    @EntityGraph(attributePaths = {"patient", "doctor", "medicalImage"})
    List<Consultation> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    @EntityGraph(attributePaths = {"patient", "doctor", "medicalImage"})
    Optional<Consultation> findWithRelationsById(Long id);

    @Query("""
            select
                c.id as id,
                c.status as status,
                c.doctorDiagnosis as doctorDiagnosis,
                c.createdAt as createdAt,
                c.updatedAt as updatedAt,
                mi.id as medicalImageId
            from Consultation c
            left join c.medicalImage mi
            where c.patient.id = :patientId
            order by c.createdAt desc
            """)
    List<PatientConsultationSummaryProjection> findPatientSummariesByPatientIdOrderByCreatedAtDesc(Long patientId);
}
