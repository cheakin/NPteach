package cn.cheakin.gulimall.order.service.impl;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.gulimall.order.dao.OrderItemDao;
import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.entity.OrderItemEntity;
import cn.cheakin.gulimall.order.entity.OrderReturnReasonEntity;
import cn.cheakin.gulimall.order.service.OrderItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues: 声明需要监听的所有队列
     *
     * 参数可以写一些类型
     * 1. Message message: 原生消息详细信息，头+体
     * 2. T<发送的消息的类型>
     * 3，Channel channel：当前传输数据的通道
     *
     * Queue: 可以很多人都来监听。只要收到消息，队列就删除消息，而且只能有一个收到此消息
     * 场景：
     *      1）订单服务启动多个:同一个消息，只能有一个客户端收到
     *      2）只有一个消息完全处理完，方法运行结束我们就可以接受下一个消息
     */
//    @RabbitListener(queues = {"hello-java-queue"})
    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException {
        byte[] body = message.getBody();
        // 消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
//        System.out.println("接收到消息...:" + message + "==>类型," + message);

        System.out.println("接收到消息...:" + message + "==>内容," + content);

        //Thread.sleep(3000);
        System.out.println("消息处理完成=>" + content.getName());

        // channel内按顺序自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag ==> " + deliveryTag);

        // 签收货物，非批量模式
        try {
            if (deliveryTag % 2 == 0) {
                // 收货
                channel.basicAck(deliveryTag, false);
                System.out.println("签收了货物..." + deliveryTag);
            } else {
                // 退货 requeue=false丢弃  requeue=true发回服务器，服务器重新入队
                //long deliveryTag, boolean multiple, boolean requeue
                channel.basicNack(deliveryTag, false, true);
                //long deliveryTag, boolean requeue
//                channel.basicReject();
                System.out.println("没有签收货物..." + deliveryTag);
            }
        } catch (IOException e) {
            // 网络终端
        }
    }

    @RabbitHandler
    public void receiveMessage2(OrderEntity content) throws InterruptedException {
        System.out.println("消息处理完成=>" + content);
    }

}