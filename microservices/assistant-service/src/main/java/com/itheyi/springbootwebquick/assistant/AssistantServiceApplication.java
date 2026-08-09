package com.itheyi.springbootwebquick.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.itheyi.springbootwebquick")
@MapperScan(basePackages = "com.itheyi.springbootwebquick.mapper")
public class AssistantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantServiceApplication.class, args);
    }
}
