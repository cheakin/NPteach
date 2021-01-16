package com.bilibili.springcloud.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon的配置类
 */

@Configuration
public class MySelfRule {

    @Bean
    public IRule myRule() {
        return new RandomRule();    //选择用随机
    }
}
