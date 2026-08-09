package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.HealthConsultationService;
import com.itheyi.springbootwebquick.pojo.HealthConsultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health-consultation")
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
public class HealthConsultationController {

    @Autowired
    private HealthConsultationService healthConsultationService;

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserve(@RequestBody ReserveRequest request) {
        try {
            HealthConsultation consultation = new HealthConsultation();
            consultation.setPatientName(request.getPatientName());
            consultation.setPatientGender(request.getPatientGender());
            consultation.setPatientPhone(request.getPatientPhone());
            consultation.setCommunicationTime(LocalDateTime.parse(request.getCommunicationTime()));
            consultation.setProvince(request.getProvince());
            consultation.setMainSymptoms(request.getMainSymptoms());

            healthConsultationService.insert(consultation);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "预约成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "预约失败：" + e.getMessage()
            ));
        }
    }

    @GetMapping("/query/{phone}")
    public ResponseEntity<HealthConsultation> query(@PathVariable String phone) {
        HealthConsultation consultation = healthConsultationService.findByPhone(phone);
        if (consultation != null) {
            return ResponseEntity.ok(consultation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static class ReserveRequest {
        private String patientName;
        private String patientGender;
        private String patientPhone;
        private String communicationTime;
        private String province;
        private String mainSymptoms;

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getPatientGender() {
            return patientGender;
        }

        public void setPatientGender(String patientGender) {
            this.patientGender = patientGender;
        }

        public String getPatientPhone() {
            return patientPhone;
        }

        public void setPatientPhone(String patientPhone) {
            this.patientPhone = patientPhone;
        }

        public String getCommunicationTime() {
            return communicationTime;
        }

        public void setCommunicationTime(String communicationTime) {
            this.communicationTime = communicationTime;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getMainSymptoms() {
            return mainSymptoms;
        }

        public void setMainSymptoms(String mainSymptoms) {
            this.mainSymptoms = mainSymptoms;
        }
    }
}
