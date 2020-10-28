package com.bilibili.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class Controller {

    @RequestMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

}
