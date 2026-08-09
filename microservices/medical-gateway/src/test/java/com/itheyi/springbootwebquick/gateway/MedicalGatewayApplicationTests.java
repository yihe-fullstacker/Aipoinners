package com.itheyi.springbootwebquick.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = MedicalGatewayApplication.class,
        properties = {
                "spring.cloud.nacos.discovery.enabled=false",
                "spring.cloud.discovery.enabled=false"
        }
)
class MedicalGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
