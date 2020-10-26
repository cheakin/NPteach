package com.bilibili.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    //后台监控配置
    //因为SpringBoot 内置了servlet容器，所以没有web.xml,代替方法是ServletRegistrationBean
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(),"/druid/*");

        //后台需要有人登录，账号密码设置
        HashMap<String, String> initParameters = new HashMap<>();

        //添加配置
        initParameters.put("loginUsername","admin");    //登录的key是固定的 loginUsername和loginPassword
        initParameters.put("loginPassword","123456");

        //允许谁可以访问
        initParameters.put("allow","");

        //禁止谁访问
//        initParameters.put("xiaoming","192.168.0.1");

        bean.setInitParameters(initParameters);   //设置初始化参数
        return bean;
    }


    //filter
    @Bean
    public FilterRegistrationBean wevStatFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());

        //过滤的请求
        Map<String, String> initParameters = new HashMap<>();

        //排除统计的路径
        initParameters.put("exclusions","*.js,*.css,/druid/*");

        bean.setInitParameters(initParameters);
        return bean;
    }
}
