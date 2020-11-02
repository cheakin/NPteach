package com.bilibili.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ScheduledService {

    //cron表达式
    /*
        30 15 10 * * ？  每天10点15分30秒执行一次
        0 0/5 10，18 * * ？  每天10点和18点，每隔5分钟执行一次
        0 15 10 ？ * 1-6     每个月周1-6,10点15执行一次
     */
    @Scheduled(cron = "0 51 23 * * ?")
    public void hello() {
        System.out.println("hello被执行了"+ new Date());
    }
}
