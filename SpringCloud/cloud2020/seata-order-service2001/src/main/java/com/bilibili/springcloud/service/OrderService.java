package com.bilibili.springcloud.service;

import com.bilibili.springcloud.domain.Order;

public interface OrderService {
    /**
     * 创建订单
     * @param order
     */
    void create(Order order);
}