package cn.cheakin.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 使用RabbitMq
 * 1. 引入amqp场景 :RabbitAutoConfiguration就会自动生效
 * 2. 给容器中自动配置了
 * 		RabbitTemplate, AmqpAdmin, CachingConnectionFactory, RabbitMessagingTemplate
 * 		所有的属性都@Configuration P偶然烹饪贴身(prefix="spring.rabbitmq")
 * 3. 给配置文件中配置 spring.rabbitmq 信息
 * 4. @EnableRabbit 开启功能
 * 5. 监听消息: 使用@RabbitListener, 前置是必须有 @EnableRabbit 注解
 * 		@RabbitListener： 类+方法(监听哪些队列即可)
 * 		@RabbitHandler： 标在方法上（重载区分不同的消息）
 *
 */
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
