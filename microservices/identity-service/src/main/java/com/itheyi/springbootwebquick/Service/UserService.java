package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.dto.LoginRequest;
import com.itheyi.springbootwebquick.dto.RegisterRequest;
import com.itheyi.springbootwebquick.entity.User;
import com.itheyi.springbootwebquick.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "密码长度至少为 6 位";
        }
        return null;
    }

    public User register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername() == null ? null : registerRequest.getUsername().trim();
        String phone = normalizePhone(registerRequest.getPhone());

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setEmail(buildPhoneEmail(phone));
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(registerRequest.getRole());

        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在或密码错误");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户不存在或密码错误");
        }

        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public String loginByPhone(String phoneNumber) {
        String normalizedPhone = normalizePhone(phoneNumber);
        Optional<User> userOptional = userRepository.findByPhone(normalizedPhone);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("该手机号未注册，无法使用短信验证码登录");
        }

        User user = userOptional.get();
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    private String normalizePhone(String phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.trim();
    }

    private String buildPhoneEmail(String phoneNumber) {
        return phoneNumber + "@phone.local";
    }
}