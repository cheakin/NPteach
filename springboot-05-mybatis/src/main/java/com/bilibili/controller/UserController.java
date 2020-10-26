package com.bilibili.controller;

import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;

    @GetMapping("/querUserList")
    public List<User> querUserList() {
        List<User> userList = userMapper.queryUserList();
        return userList;
    }
}
