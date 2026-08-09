package com.itheyi.springbootwebquick.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = "com.itheyi.springbootwebquick")
@EntityScan(basePackages = "com.itheyi.springbootwebquick.entity")
@EnableJpaRepositories(basePackages = "com.itheyi.springbootwebquick.mapper")
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
