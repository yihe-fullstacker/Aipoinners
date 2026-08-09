package com.itheima.consumer.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Component
public class SpringRabbitListener {
/*    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("SpringRabbitListener 接收到消息：{}", message);
    }
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) {
        System.out.println("work.queue1 接收到消息：" + message+ ","+ LocalTime.now());
    }
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) {
        System.err.println("work.queue2........ 接收到消息：" + message+ ","+ LocalTime.now());
    }*/
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        log.info("消费者1监听到fanout.queue1的消息：{}", message);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        log.info("消费者2监听到fanout.queue2的消息：{}", message);
    }
    @RabbitListener(queues = "direct.queue1")
    public void listenDirectQueue1(String message) {
        log.info("消费者1监听到direct.queue1的消息：{}", message);
    }

    @RabbitListener(queues = "direct.queue2")
    public void listenDirectQueue2(String message) {
        log.info("消费者2监听到direct.queue2的消息：{}", message);
    }
    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(String message) {
        log.info("消费者1监听到topic.queue1的消息：{}", message);
    }

    @RabbitListener(queues = "topic.queue2")
    public void listenTopicQueue2(String message) {
        log.info("消费者2监听到topic.queue2的消息：{}", message);
    }
    @RabbitListener(queues = "object.queue")
    public void listenobjectQueue(Map<String,Object> msg) {
        log.info("消费者2监听到object.queue的消息：{}", msg);
    }
}
