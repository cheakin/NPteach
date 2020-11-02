package com.bilibili.controller;

import com.bilibili.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

    @Autowired
    AsyncService asyncService;

    @RequestMapping({"/","index"})
    public String toIndex(){
        return "idnex";
    }

    @RequestMapping("/hello")
    public String hello(){
        asyncService.hello();
        return "Thanks for you Test！";
    }

}
