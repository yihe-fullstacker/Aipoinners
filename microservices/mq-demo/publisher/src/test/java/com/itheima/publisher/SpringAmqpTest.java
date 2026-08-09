package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@SpringBootTest
class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void testSimpleQueue() {
        //1,队列名
        String queueName = "simple.queue";
        //2,消息
        String message = "hello,spring amqp!";
        // 3,发送消息
        rabbitTemplate.convertAndSend("simple.queue","hello,spring amqp!");
    }
    @Test
    void testWorkQueue() {
        //1,队列名
        String queueName = "work.queue";
        for (int i = 0; i <= 50; i++) {
            //2,消息
            String message = "hello,spring amqp_" + i;
            // 3,发送消息
            rabbitTemplate.convertAndSend("work.queue",message);
        }
    }
    @Test
    void testFanoutQueue() {
        //1,交换机名
        String exchangeName = "hmall.fanout";
        //2,消息
        String message = "hello,spring everyone!";
        // 3,发送消息
        rabbitTemplate.convertAndSend(exchangeName,null,message);
    }
    @Test
    void testDirectQueue() {
        //1,交换机名
        String exchangeName = "hmall.direct";
        //2,消息
        String message = "蓝色 ：!";
        // 3,发送消息
        rabbitTemplate.convertAndSend(exchangeName,"blue",message);
    }

    @Test
    void testTopicQueue() {
        //1,交换机名
        String exchangeName = "hmall.topic";
        //2,消息
        String message = "天气：今天天气真不错，我的心情好极了!";
        // 3,发送消息
        rabbitTemplate.convertAndSend(exchangeName,"china.weather",message);
    }
    @Test
    void testSendobject() {
        //1.准备消息
        Map<String,Object> msg= new HashMap<>(2);
        msg.put("name","张三");
        msg.put("age",18);
        //2.发送信息
        rabbitTemplate.convertAndSend("object.queue",msg);
    }
    @Test
    void testConfirmCallback() {
        //0.创建correlationData
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        cd.getFuture().whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println("发送异常: " + ex.getMessage());
                return;
            }
            if (result != null && result.isAck()) {
                System.out.println("交换机接功");
            } else {
                String reason = result == null ? "confirm result is null" : result.getReason();
                System.out.println("交换机接收失败: " + reason);
            }
        });
        //1,交换机名
        String exchangeName = "hmall.direct";
        //2,消息
        String message = "蓝色 ：!";
        // 3,发送消息
        rabbitTemplate.convertAndSend(exchangeName,"blue",message,cd);
    }
}
