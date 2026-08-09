package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.SmsService;
import com.itheyi.springbootwebquick.dto.SendSmsRequest;
import com.itheyi.springbootwebquick.dto.SmsResponse;
import com.itheyi.springbootwebquick.dto.VerifySmsRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
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
public class SmsController {

    private static final Logger log = LoggerFactory.getLogger(SmsController.class);

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public SmsResponse sendSms(@Valid @RequestBody SendSmsRequest request) {
        log.info("[SMS Controller] 收到发送短信请求，手机号: {}", request.getPhoneNumber());
        return smsService.sendSms(request.getPhoneNumber());
    }

    @PostMapping("/verify")
    public SmsResponse verifySms(@Valid @RequestBody VerifySmsRequest request) {
        log.info("[SMS Controller] 收到校验短信请求，手机号: {}", request.getPhoneNumber());
        return smsService.verifySms(request.getPhoneNumber(), request.getVerifyCode());
    }
}
