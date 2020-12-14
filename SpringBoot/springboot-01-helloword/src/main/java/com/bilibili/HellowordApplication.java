package com.bilibili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//标注此类是一个springboot的应用，启动类下的资源被导入
@SpringBootApplication
public class HellowordApplication {
    //将SpringBoot启动
    public static void main(String[] args) {
        SpringApplication.run(HellowordApplication.class, args);
    }

}
