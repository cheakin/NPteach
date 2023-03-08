package cn.cheakin.gulimall.order.service;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.vo.OrderConfirmVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:17:47
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;
}

