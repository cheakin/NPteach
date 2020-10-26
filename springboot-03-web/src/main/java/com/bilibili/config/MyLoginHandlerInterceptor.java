package com.bilibili.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class MyLoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("------LoginHandlerInterceptor--------");

        //登录成功之后会保存session
        Object loginUser = request.getSession().getAttribute("loginUser");

        if (loginUser==null){   //用户未登录，重定向回主页
            request.setAttribute("msg","没有权限，请先登录！");
            request.getRequestDispatcher("/index.html").forward(request,response);
            return false;
        }else {
            return true;
        }

    }
}
