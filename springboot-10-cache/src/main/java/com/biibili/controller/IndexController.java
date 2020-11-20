package com.biibili.controller;

import com.biibili.entity.User;
import com.biibili.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    UserService userServicel;

    @RequestMapping("/user/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        User user = userServicel.findUserById(id);
        return user;

    }


}
