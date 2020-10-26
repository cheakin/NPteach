package com.bilibili.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
public class IndexController {

    @RequestMapping("/test")
    public String test(Model model){
        model.addAttribute("msg","<h3>Hello Word!!</h3>");

        model.addAttribute("users", Arrays.asList("yangyang","benben","momo"));
        return "test";
    }
}
