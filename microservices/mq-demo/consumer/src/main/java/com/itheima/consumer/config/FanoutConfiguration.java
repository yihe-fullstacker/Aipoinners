package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfiguration {
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("hmall.fanout");
        //第二种方式：return ExchangeBuilder.fanoutExchange("hmall.fanout").durable(true).build();
    }
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1");
    }
    @Bean
    public Binding fanoutBinding1(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2");
    }
    @Bean
    public Binding fanoutBinding2(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
