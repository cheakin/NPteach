package cn.cheakin.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * SpringSession核心原理
 * 1）、@EnableRedisHttpSession导入RedisHttpSessionConfiguration配置
 *      1. 给容器中添加了一个组件
 *          SessionRepository -> RedisOperationsSessionRepository: redis操作session, session的增删改查封装类
 *      2. SessionRepositoryFilter ==> Filter: session存储过滤器每个请求过来都要经过Filter
 *          1. 创建的时候。就自动从容器中获取SessionRepository
 *          2. 原始的request, response 被包装为 SessionRepositoryRequestWrapper, SessionRepositoryResponseWrapper
 *          3. 以后获取session(request.getSession()) 就是sessionRepositoryRequestWrapper.getSession()
 *          4. 即用了装饰者模式，使用getSession是被包装过的session
 *
 *  自动延期：redis中的数据也是有过期时间的
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }


}
