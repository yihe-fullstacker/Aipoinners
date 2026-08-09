package com.itheyi.springbootwebquick.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.itheyi.springbootwebquick")
public class MedicalGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalGatewayApplication.class, args);
    }
}
