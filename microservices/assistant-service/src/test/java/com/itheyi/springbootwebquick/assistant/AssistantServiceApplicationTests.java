package com.itheyi.springbootwebquick.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = AssistantServiceApplication.class,
        properties = {
                "eureka.client.enabled=false",
                "spring.datasource.url=jdbc:h2:mem:assistant;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "openai.api-key=test-key"
        }
)
class AssistantServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
