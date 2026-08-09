package com.itheima.consumer.config;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ErrorMessageConfiguration {
    private final RabbitTemplate rabbitTemplate;
    @Bean
    public DirectExchange errorExchange() {
        return new DirectExchange("error.direct");
    }
    @Bean
    public Queue errorQueue() {
        return new Queue("error.queue");
    }
    @Bean
    public Binding errorBinding(DirectExchange errorExchange, Queue errorQueue) {
        return BindingBuilder.bind(errorQueue).to(errorExchange).with("error");
    }
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error") {
            @Override
            public void recover(org.springframework.amqp.core.Message message, Throwable cause) {
                rabbitTemplate.send(message);
            }
        };
    }
}
