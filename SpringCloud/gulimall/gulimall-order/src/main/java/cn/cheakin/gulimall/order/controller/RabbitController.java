package cn.cheakin.gulimall.order.controller;

import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/senMq")
    public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num) {
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
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
        }

        return "OK";
    }

}
