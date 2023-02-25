package cn.cheakin.gulimall.order;

import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void sendMessageTest() {
        // 1. 发消息(任意消息)
        /*String msg = "Hello World";
        rabbitTemplate.convertAndSend(
                "hello-java-exchange",
                "hello.java",
                msg
        );
        log.info("消息发送完成{}", msg);*/

        /*发消息(对象)
            如果发送的消息是个对象，我们会使用序列化机制，将对象写出去，那么对象就必须实现Serializable
            也可以配置rabbitmq的序列化方式配置
        */
        /*OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("哈哈");
        rabbitTemplate.convertAndSend(
                "hello-java-exchange",
                "hello.java",
                reasonEntity
        );
        log.info("消息发送完成{}", reasonEntity);*/

        /*for (int i = 0; i < 10; i++) {
            OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
            reasonEntity.setId(1L);
            reasonEntity.setCreateTime(new Date());
            reasonEntity.setName("哈哈-" + i);
            rabbitTemplate.convertAndSend(
                    "hello-java-exchange",
                    "hello.java",
                    reasonEntity
            );
            log.info("消息发送完成{}", reasonEntity);
        }*/

        for (int i = 0; i < 10; i++) {
            if (i %2 == 0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈-" + i);
                rabbitTemplate.convertAndSend(
                        "hello-java-exchange",
                        "hello.java",
                        reasonEntity
                );
            } else {
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend(
                        "hello-java-exchange",
                        "hello.java",
                        entity
                );
            }

            log.info("消息发送完成{}");
        }
    }


    /**
     * 1. 如何创建Exchange[hello-java-exchange], Queue[hello-java-queue], Binding[hello.java]
     * 1) 使用 AmqpAdmin 进行创建
     * 2. 如何收发消息
     */
    @Test
    void createExchange() {
        Exchange directExchange = new DirectExchange(
                "hello-java-exchange",    // 名称
                true,    // 是否持久化
                false   // 是否自动删除
        );
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }

    @Test
    void createQueue() {
        Queue queue = new Queue(
                "hello-java-queue",
                true, // 是否持久化
                false,  // 是否排他
                false // 是否自动删除
        );
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-queue");
    }

    @Test
    void createBinding() {
        Binding binding = new Binding(
                "hello-java-queue", // 目的地
                Binding.DestinationType.QUEUE, // 目的地类型[交换机或队列]
                "hello-java-exchange",  // 交换机
                "hello.java", // 路由键
                null// 自定义参数
        );
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "hello-java-binding");
    }

}
