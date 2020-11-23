package com.bilibili.ticket.service;


import org.apache.dubbo.config.annotation.DubboService;

@DubboService   //发布到注册中心
public class TicketServiceImpl implements TicketService {
    /** 提供票的服务 */
    @Override
    public String getTicket() {
        return "《TENET》";
    }
}
