package cn.cheakin.gulimall.ware.listener;

import cn.cheakin.common.to.mq.OrderTo;
import cn.cheakin.common.to.mq.StockLockedTo;
import cn.cheakin.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        log.info("************************收到库存解锁的消息********************************");
        try {
            //当前消息是否被第二次及以后（重新）派发过来了
//            Boolean redelivered = message.getMessageProperties().getRedelivered();
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("************************从订单模块收到库存解锁的消息********************************");
        try {
            wareSkuService.unLockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}