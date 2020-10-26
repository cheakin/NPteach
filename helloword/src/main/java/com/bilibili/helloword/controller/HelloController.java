package com.bilibili.helloword.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping
    public String hello(){
        //调用业务，接收前端参数
        return "hello world";
    }
}
