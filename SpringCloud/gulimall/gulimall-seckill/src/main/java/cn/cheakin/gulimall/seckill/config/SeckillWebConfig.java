package cn.cheakin.gulimall.seckill.config;

import cn.cheakin.gulimall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springWEB自定义配置
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {
    /**
     * 添加自定义的拦截器
     * @param registry 注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/**");
    }
}