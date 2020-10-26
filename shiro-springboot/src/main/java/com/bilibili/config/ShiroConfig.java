package com.bilibili.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    //ShrioFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager manager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //关联安全管理器
        bean.setSecurityManager(manager);

        //关联shiro的内置过滤器
        /*
            anno:   无需认证就可以访问
            authc:  必须认证才能访问
            user:   必须拥有 记住我 功能才能用
            perms:  拥有对某个资源的权限才能访问
            role：   拥有某个角色权限才能访问
         */
        //拦截，没登录跳到登录页面
        Map<String, String> filterMap = new LinkedHashMap<>();

        //授权,没有授权会跳到未授权页面
        filterMap.put("/user/add", "perms[user:add]");
        filterMap.put("/user/add", "perms[user:update]");

        filterMap.put("/user/*", "authc");
        bean.setFilterChainDefinitionMap(filterMap);

        //设置登录请求
        bean.setLoginUrl("/toLogin");
        //未授权页面
        bean.setUnauthorizedUrl("/noauth");
        return bean;
    }

    //DeafaultWebSecurityManager
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(UserRealm userRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        //关联UserRealm
        manager.setRealm(userRealm);
        return manager;
    }

    //创建 realm 对象,需要自定义类
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    //整合ShiroDialect:用来整合shiro-thymeleaf
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }
}
