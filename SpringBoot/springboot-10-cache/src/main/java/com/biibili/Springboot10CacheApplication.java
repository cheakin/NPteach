package com.biibili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Springboot10CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot10CacheApplication.class, args);
    }

}
