package com.bilibili;

import com.bilibili.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class Springboot11AmqpApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    void createExchange() {
        //创建exchange
        amqpAdmin.declareExchange(new DirectExchange("amqpadmin.exchange"));

        //创建消息队列
        amqpAdmin.declareQueue(new Queue("amqpadmin.queue", true));

        //创建绑定规则
        amqpAdmin.declareBinding(new Binding(
                "amqpadmin.queue",  //地址
                Binding.DestinationType.QUEUE,  //消息队列
                "amqpadmin.exchange",   //交换器
                "amqp.binding",     //路由键
                null));
    }


    /**
     * 1.diret单播（点对点）
     */
    @Test
    void directTest() {
        //message需要自己构造，定义消息体内容和消息头
        //rabbitTemplate.convertAndSend(exchange, routerKey, message);

        //object为默认消息体，传入要发送的对象，自动序列化发送个rabbitmq
        //rabbitTemplate.convertAndSend(exchange, routerKey, message);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", "消息");
        map.put("data", Arrays.asList("hello", 123, "world", true));
        rabbitTemplate.convertAndSend("exchange.direct", "bilibili.news", map);
    }

    /**
     * 2fonout广播
     */
    @Test
    void fonout广播Test() {
        rabbitTemplate.convertAndSend("exchange.fanout", "bilibili.news", new User("YBM", "委员长"));
    }

    /**
     * 0接收消息
 */
    @Test
    void receive() {
        Object o = rabbitTemplate.receiveAndConvert("bilibili.news");
        System.out.println(o.getClass());
        System.out.println(o);
    }




    @Test
    void contextLoads() {

    }

}
