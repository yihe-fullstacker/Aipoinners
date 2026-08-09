package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectConfiguration {
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("hmall.direct");
        //第二种方式：return ExchangeBuilder.fanoutExchange("hmall.fanout").durable(true).build();
    }
    @Bean
    public Queue directQueue1() {
        return new Queue("direct.queue1");
    }
    @Bean
    public Binding directBinding1(DirectExchange directExchange, Queue directQueue1) {
        return BindingBuilder.bind(directQueue1).to(directExchange).with("red");
    }
    @Bean
    public Queue directQueue2() {
        return new Queue("direct.queue2");
    }
    @Bean
    public Binding directBinding2(DirectExchange directExchange, Queue directQueue2) {
        return BindingBuilder.bind(directQueue2).to(directExchange).with("blue");
    }
}
