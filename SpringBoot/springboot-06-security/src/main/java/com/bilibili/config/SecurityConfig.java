package com.bilibili.config;


import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权。规则：首页开放，功能页做权限校验
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/level1/**").hasRole("vip1")
                .antMatchers("/level2/**").hasRole("vip2")
                .antMatchers("/level3/**").hasRole("vip3");

        //没有权限的定向到登录页
//        http.formLogin();
        http.formLogin().loginPage("/toLogin") //也可以指定登出成功页
                .usernameParameter("user").passwordParameter("pwd") //指定登录表单参数
                .loginProcessingUrl("/login");  //指定登录的url接口地址

        //关闭csrf功能
        http.csrf().disable();

        //注销，重定向到(/logout)
        //http.logout();
        http.logout().logoutSuccessUrl("/login"); //也可以指定登出成功页

        //记住我功能(cookie)
        http.rememberMe()
            .rememberMeParameter("remember");   //指定登录表单'记住我'参数，缺省值为'rememberMe'
    }


    //认证
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //一般是从数据库中读取数据，也可以自定义从内存中读取（如下）
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())  //编码加密
                .withUser("admin").password(new BCryptPasswordEncoder().encode("123456")).roles("vip1","vip2")
                .and()
                .withUser("root").password(new BCryptPasswordEncoder().encode("123456")).roles("vip1","vip2","vip3");
    }
}
