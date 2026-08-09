package com.itheyi.springbootwebquick.consultation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableFeignClients(basePackages = "com.itheyi.springbootwebquick.client")
@SpringBootApplication(scanBasePackages = "com.itheyi.springbootwebquick")
@EntityScan(basePackages = "com.itheyi.springbootwebquick.entity")
@EnableJpaRepositories(basePackages = "com.itheyi.springbootwebquick.mapper")
@MapperScan(basePackages = "com.itheyi.springbootwebquick.mapper",
        annotationClass = org.apache.ibatis.annotations.Mapper.class)
@EnableDiscoveryClient  // 开启服务发现与注册
public class ConsultationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultationServiceApplication.class, args);
    }
}
