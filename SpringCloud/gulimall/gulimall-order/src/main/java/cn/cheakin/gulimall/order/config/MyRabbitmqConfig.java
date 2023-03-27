package cn.cheakin.gulimall.order.config;

import cn.cheakin.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;

/**
 * 容器中的 Binding, Queue, Exchange 都会自动创建在 RabbitMQ 服务器上
 * RabbitMQ 上有， 属性发生了变化也不会覆盖
 */
@Configuration
public class MyRabbitmqConfig {

//    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息，准备关闭订单" + entity.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        /**
         Queue(String name,  队列名字
         boolean durable,  是否持久化
         boolean exclusive,  是否排他
         boolean autoDelete, 是否自动删除
         Map<String, Object> arguments) 属性
         */
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");    //死信交换机
        arguments.put("x-dead-letter-routing-key", "order.release.order");  //死信路由键
        arguments.put("x-message-ttl", 60000); // 消息过期时间 1分钟
        return new Queue("order.delay.queue",true,false,false, arguments);
    }

    /**
     * 普通队列
     *
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        /**
         *   String name,
         *   boolean durable,
         *   boolean autoDelete,
         *   Map<String, Object> arguments
         */
        return new TopicExchange("order-event-exchange", true, false);
    }

    /**
     * 创建订单的binding
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding() {
        /**
         * String destination, 目的地（队列名或者交换机名字）
         * DestinationType destinationType, 目的地类型（Queue、Exhcange）
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         * */
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    /**
     * 商品秒杀队列
     * @return
     */
    /*@Bean
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue("order.seckill.order.queue", true, false, false);
        return queue;
    }*/

    /*@Bean
    public Binding orderSecKillOrrderQueueBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        Binding binding = new Binding(
                "order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);

        return binding;
    }*/
}