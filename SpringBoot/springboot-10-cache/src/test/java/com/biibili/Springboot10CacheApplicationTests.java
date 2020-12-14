package com.biibili;

import com.biibili.dao.UserDao;
import com.biibili.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Springboot10CacheApplicationTests {

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        /*User user = new User();
        user.setName("ybm1");
        user.setPassword("12346");
        userDao.insert(user);*/

//        User user = new User();
//        user.setPassword("admin");
        User user = userDao.selectByName("admin");
        System.out.println("user = " + user);
    }

}
