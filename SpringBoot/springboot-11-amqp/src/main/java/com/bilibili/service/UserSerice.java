package com.bilibili.service;

import com.bilibili.entity.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserSerice {
    @RabbitListener(queues = "bilibili.news")
    public void receive(User user){
        System.out.println("接收到消息user：" + user);
    }

    @RabbitListener(queues = "bilibili")
    public void receive(Message message){
        //消息头
        System.out.println("message.getMessageProperties() = " + message.getMessageProperties());
        //消息体
        System.out.println("message.getBody() = " + message.getBody());
    }
}
