package com.bilibili.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Configuration
public class MyLocaleResolver implements LocaleResolver {

    //解析请求
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        //解析请求中的语言参数
        String language = request.getParameter("language");
//        System.out.println("language = " + language);

        Locale locale = Locale.getDefault();    //  默认的

        //如果请求连接的国际化语言参数为空
        if (language!=null){
            //zh_CN||en_US
            String[] split = language.split("_");
            //国家，地区
            locale = new Locale(split[0],split[1]);
        }

        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
