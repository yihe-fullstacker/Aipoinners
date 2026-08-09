package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqConfig {
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback((returned) -> {
            log.error("消息发送失败：{}", returned);
            log.debug("exchange:{}", returned.getExchange());
        });
    }
}
