package com.itheyi.springbootwebquick.imaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.itheyi.springbootwebquick")
@EnableJpaRepositories(basePackages = "com.itheyi.springbootwebquick.mapper")
@EntityScan(basePackages = "com.itheyi.springbootwebquick.entity")
public class ImagingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImagingServiceApplication.class, args);
    }
}
