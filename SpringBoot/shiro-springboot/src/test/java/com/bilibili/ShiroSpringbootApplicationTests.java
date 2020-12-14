package com.bilibili;

import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.User;
import com.bilibili.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShiroSpringbootApplicationTests {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserServiceImpl userService;

    @Test
    void contextLoads() {
        User user = userService.queryUserByName("ybm");
        System.out.println("user = " + user);


    }

}
