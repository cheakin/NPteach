package com.bilibili.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    //模拟一个线程延迟
    @Async  //标识此此方法是异步的方法
    public void hello() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("----数据正在处理。。。。。。------");
    }

}
