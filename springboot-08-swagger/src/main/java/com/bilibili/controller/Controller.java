package com.bilibili.controller;

import com.bilibili.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

//接口描述
@Api(tags = "用户相关")
@RestController
public class Controller {
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

    //只要接口中存在实体类，接口就会被扫描到swagger中
    @ApiOperation("post测试")
    @PostMapping("/user")
    public User user(User user) {
        return new User();
    }

    //接口描述
    @ApiOperation("Hello控制类")
    @PostMapping("/hello")
    public String hello2(@ApiParam("用户名") String username) {
        return "index"+username;
    }
}
