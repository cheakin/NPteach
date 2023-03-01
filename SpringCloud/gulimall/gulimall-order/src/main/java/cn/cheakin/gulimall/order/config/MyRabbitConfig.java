package cn.cheakin.gulimall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

//@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 使用JSON序列化机制，进行消息转换
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 1. 服务器收到消息就回调
     *  1)spring.rabbitmq.publisher-confirms=true
     *  2)设置确认回调ConfirmCallback
     * 2. 消息正确抵达队列
     *  1)spring.rabbitmq.publisher-returns=true
     *    spring.rabbitmq.template.mandatory=true
     *  2)设置确认回调ReturnsCallback
     *
     * 3. 消息端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）
     */
    @PostConstruct  // MyRabbitConfig对象创建完成以后，执行此方法
    public void initRabbitTemplate() {
        //确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 1. 只要消息抵达Brocker就ack=true
             *
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param ack   消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm......correlationData[" + correlationData +"]==>ack["+ack+"]==>cause["+cause+"]");
            }
        });

        // 设置消息抵达队列的确认回调:
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * @param returnedMessage
             *          returnedMessage.getMessage()    投递失败的消息详细信息
             *          returnedMessage.getReplyCode()  回复的状态码
             *          returnedMessage.getReplyText()  回复的文本内容
             *          returnedMessage.getExchange()   当时这个消息发给哪个交换机
             *          returnedMessage.getRoutingKey() 当时这个消息用哪个路由键
             *
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("Fail Message[" + returnedMessage +"]");
            }
        });
    }

}
