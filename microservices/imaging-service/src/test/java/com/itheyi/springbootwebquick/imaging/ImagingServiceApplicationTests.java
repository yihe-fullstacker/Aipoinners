package com.itheyi.springbootwebquick.imaging;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = ImagingServiceApplication.class,
        properties = {
                "eureka.client.enabled=false",
                "spring.datasource.url=jdbc:h2:mem:imaging;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class ImagingServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
