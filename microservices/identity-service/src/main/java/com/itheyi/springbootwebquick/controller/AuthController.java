package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.Service.CaptchaService;
import com.itheyi.springbootwebquick.Service.SmsService;
import com.itheyi.springbootwebquick.Service.UserService;
import com.itheyi.springbootwebquick.dto.AuthResponse;
import com.itheyi.springbootwebquick.dto.CaptchaResponse;
import com.itheyi.springbootwebquick.dto.ErrorResponse;
import com.itheyi.springbootwebquick.dto.LoginRequest;
import com.itheyi.springbootwebquick.dto.PhoneLoginRequest;
import com.itheyi.springbootwebquick.dto.RegisterRequest;
import com.itheyi.springbootwebquick.dto.SmsResponse;
import com.itheyi.springbootwebquick.entity.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
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
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SmsService smsService;

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = captchaService.generateCaptcha();
        return ResponseEntity.ok(captcha);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (registerRequest.getCaptchaId() != null && registerRequest.getCaptchaCode() != null) {
                if (!captchaService.validateCaptcha(registerRequest.getCaptchaId(), registerRequest.getCaptchaCode())) {
                    ErrorResponse error = new ErrorResponse("验证码错误或已过期", "INVALID_CAPTCHA", 400);
                    return ResponseEntity.badRequest().body(error);
                }
            }

            String passwordError = userService.validatePasswordStrength(registerRequest.getPassword());
            if (passwordError != null && registerRequest.getPassword().length() < 6) {
                ErrorResponse error = new ErrorResponse("密码长度至少为 6 位", "WEAK_PASSWORD", 400);
                return ResponseEntity.badRequest().body(error);
            }

            User user = userService.register(registerRequest);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getFullName(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Register failed for username={}, phone={}, reason={}",
                    registerRequest.getUsername(), registerRequest.getPhone(), e.getMessage());
            ErrorResponse error = new ErrorResponse(e.getMessage(), "REGISTRATION_FAILED", 400);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Register failed with unexpected error. username={}, phone={}",
                    registerRequest.getUsername(), registerRequest.getPhone(), e);
            ErrorResponse error = new ErrorResponse("注册失败: " + e.getMessage(), "INTERNAL_ERROR", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getCaptchaId() != null && loginRequest.getCaptchaCode() != null) {
                if (!captchaService.validateCaptcha(loginRequest.getCaptchaId(), loginRequest.getCaptchaCode())) {
                    ErrorResponse error = new ErrorResponse("验证码错误或已过期", "INVALID_CAPTCHA", 400);
                    return ResponseEntity.badRequest().body(error);
                }
            }

            String token = userService.login(loginRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getFullName(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login failed for username={}, reason={}", loginRequest.getUsername(), e.getMessage());
            ErrorResponse error = new ErrorResponse(e.getMessage(), "LOGIN_FAILED", 400);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Login failed with unexpected error. username={}", loginRequest.getUsername(), e);
            ErrorResponse error = new ErrorResponse("登录失败: " + e.getMessage(), "INTERNAL_ERROR", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login/phone")
    public ResponseEntity<?> loginByPhone(@Valid @RequestBody PhoneLoginRequest request) {
        try {
            SmsResponse verifyResult = smsService.verifySms(request.getPhoneNumber(), request.getVerifyCode());
            if (verifyResult.getCode() != 200) {
                ErrorResponse error = new ErrorResponse(verifyResult.getMessage(), "INVALID_VERIFY_CODE", 400);
                return ResponseEntity.badRequest().body(error);
            }

            String token = userService.loginByPhone(request.getPhoneNumber());
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getFullName(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Phone login failed for phone={}, reason={}", request.getPhoneNumber(), e.getMessage());
            ErrorResponse error = new ErrorResponse(e.getMessage(), "LOGIN_FAILED", 400);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Phone login failed with unexpected error. phone={}", request.getPhoneNumber(), e);
            ErrorResponse error = new ErrorResponse("登录失败: " + e.getMessage(), "INTERNAL_ERROR", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
