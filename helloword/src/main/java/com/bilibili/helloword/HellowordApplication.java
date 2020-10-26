package com.bilibili.helloword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//本身就是一个spring的组件
//程序的主入口
@SpringBootApplication
public class HellowordApplication {

    public static void main(String[] args) {
        SpringApplication.run(HellowordApplication.class, args);
    }

}
