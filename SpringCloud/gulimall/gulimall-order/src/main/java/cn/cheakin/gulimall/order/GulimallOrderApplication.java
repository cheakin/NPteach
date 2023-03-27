package cn.cheakin.gulimall.order;

import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
 *
 * Seata控制分布式事务
 * 1. 每一个微服务必须创建 undo_log
 * 2. 安装事务协调器: seata-server:
 * 3. 整合
 * 	1) 导入依赖 spring-cloud-starter-alibaba-seata
 * 	2) 解压并启动seata-server
 * 		registry.conf: 注册中心配置	registry.type=nacos
 * 		file.conf: 事务协调器配置		vgroup_mapping.my_test_tx_group=default
 * 	3) 所有想要用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
 * 	4) 每个微服务,都必须导入registry.conf, file.conf, 启动时加载相关配置
 * 	5) 启动测试分布式事务
 * 	6) 给分布式大事务入口标注 @GlobalTransactional
 * 	7) 每一个远程的小事务用 @Transactional
 *
 */
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication(exclude = SeataAutoConfiguration.class)
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
