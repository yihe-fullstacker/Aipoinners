/* 配置类在 Spring Boot 项目中扮演着 解决跨域问题（CORS）*/
package com.itheyi.springbootwebquick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. 对所有 /api/ 开头的接口生效
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://47.108.52.206",
                        "http://47.108.52.206:*",
                        "https://47.108.52.206",
                        "https://47.108.52.206:*"
                ) // 2. 允许本地开发和当前公网主机访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")   // 3. 允许的 HTTP 方法
                .allowedHeaders("*")     // 4. 允许所有请求头
                .allowCredentials(true);   // 5. 允许携带 Cookie（如 Session/JWT）
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
