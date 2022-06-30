package com.bilibili.config;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    //此处可以参照RedisAutoConfiguration类
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate();

        //配置具体的序列化方式
        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        template.setKeySerializer(objectJackson2JsonRedisSerializer);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);       //设置字符串key的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);    //设置Hash key的序列化方式
        //......

        template.setValueSerializer(objectJackson2JsonRedisSerializer);     //设置字符串value的序列化方式为jackson方式
        template.setValueSerializer(objectJackson2JsonRedisSerializer);      //设置Hash value的序列化方式为jackson方式
        //......

        return template;
    }
}
