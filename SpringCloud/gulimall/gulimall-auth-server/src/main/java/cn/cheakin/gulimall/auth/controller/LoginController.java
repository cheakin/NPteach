package cn.cheakin.gulimall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Create by botboy on 2022/10/07.
 **/
@Controller
public class LoginController {


    /**
     * 发送一个请求直接跳到一个页面。
     * SpringMVC viewcontroller: 将请求和页面映射过来
     */
    /*@GetMapping(value = "/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/reg.html")
    public String regPage() {
        return "reg";
    }*/

}
