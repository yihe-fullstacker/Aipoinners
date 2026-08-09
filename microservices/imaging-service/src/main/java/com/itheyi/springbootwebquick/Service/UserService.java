package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.entity.User;
import com.itheyi.springbootwebquick.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
