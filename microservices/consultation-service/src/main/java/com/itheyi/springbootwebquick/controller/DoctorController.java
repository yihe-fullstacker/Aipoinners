package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.ConsultationViewService;
import com.itheyi.springbootwebquick.Service.DoctorService;
import com.itheyi.springbootwebquick.dto.ConsultationViewDTO;
import com.itheyi.springbootwebquick.entity.Consultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
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
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ConsultationViewService consultationViewService;

    @GetMapping("/queue")
    public ResponseEntity<List<ConsultationViewDTO>> getPendingQueue() {
        List<Consultation> list = doctorService.getPendingQueue();
        return ResponseEntity.ok(consultationViewService.toDtoList(list));
    }

    @PostMapping("/diagnose")
    public ResponseEntity<ConsultationViewDTO> submitDiagnosis(
            @RequestParam Long consultationId,
            @RequestParam Long doctorId,
            @RequestParam String diagnosisText
    ) {
        Consultation updated = doctorService.submitDiagnosis(consultationId, doctorId, diagnosisText);
        return ResponseEntity.ok(consultationViewService.toDto(updated));
    }

    @PostMapping("/consultations/{id}/ai")
    public ResponseEntity<ConsultationViewDTO> reAnalyze(@PathVariable Long id) {
        Consultation updated = doctorService.callAiService(id);
        return ResponseEntity.ok(consultationViewService.toDto(updated));
    }

    // AI 助手对话接口
    @PostMapping("/assistant/chat")
    public ResponseEntity<String> chat(@RequestParam String message) {
        String result = doctorService.callAssistantService(message);
        return ResponseEntity.ok(result);
    }
}