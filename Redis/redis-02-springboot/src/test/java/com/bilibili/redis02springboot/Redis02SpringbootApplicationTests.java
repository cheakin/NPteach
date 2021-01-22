package com.bilibili.redis02springboot;

import com.bilibili.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class Redis02SpringbootApplicationTests {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        //RedisTemplate
        //可以获取redis连接对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.flushDb();       //清空当前数据库
        connection.flushAll();      //清空所有数据库



        //redisTemplate.opsForValue()   操作字符串
        //redisTemplate.opsForList()    操作List
        //redisTemplate.opsForSet()     操作Set
        //.....     再链式编程继续写就可以了
        redisTemplate.opsForValue().set("k1","v1");
        System.out.println(redisTemplate.opsForValue().get("k1"));
    }

    @Test
    void test() {
        User user = new User("小陈", 16);
    }
}
