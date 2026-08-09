package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.ConsultationViewService;
import com.itheyi.springbootwebquick.dto.ConsultationSummaryDTO;
import com.itheyi.springbootwebquick.dto.ConsultationViewDTO;
import com.itheyi.springbootwebquick.entity.Consultation;
import com.itheyi.springbootwebquick.mapper.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(
        originPatterns = {
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://47.108.52.206",
                "http://47.108.52.206:*",
                "https://47.108.52.206",
                "https://47.108.52.206:*"
        },
        allowCredentials = "true"
)
public class PatientController {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ConsultationViewService consultationViewService;

    @GetMapping("/consultations")
    public ResponseEntity<List<ConsultationSummaryDTO>> listConsultations(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(
                consultationViewService.toPatientSummaryList(
                        consultationRepository.findPatientSummariesByPatientIdOrderByCreatedAtDesc(userId)
                )
        );
    }

    @GetMapping("/consultations/{id}")
    public ResponseEntity<ConsultationViewDTO> getConsultation(@PathVariable("id") Long id) {
        Consultation consultation = consultationRepository.findWithRelationsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        return ResponseEntity.ok(consultationViewService.toDto(consultation));
    }
}
