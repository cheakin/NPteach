package com.biibili.service;

import com.biibili.dao.UserDao;
import com.biibili.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Cacheable(cacheNames = {"user"})
    @Caching(
            cacheable = {
                    @Cacheable(value = "user", key = "#name")
            },
            put = {
                    @CachePut(value = "user", key = "#result.id"),
                    @CachePut(value = "user", key = "#result.password")
            }
    )
    public User findUserById(Integer id) {
        System.out.println("查询User>>>>>>>>>>");
        User user = userDao.selectByPrimaryKey(id);
        return user;

    }
}
