package com.bilibili.user.service;

import com.bilibili.ticket.service.TicketService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @DubboReference //声明提供者的服务
    TicketService ticketService;

    public void hello() {
        String ticket = ticketService.getTicket();
        System.out.println("买了票了，"+ticket);
    }
}
