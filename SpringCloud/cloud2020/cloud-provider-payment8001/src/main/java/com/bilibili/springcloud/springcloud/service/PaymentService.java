package com.bilibili.springcloud.springcloud.service;

import com.bilibili.springcloud.springcloud.entities.Payment;


public interface PaymentService {

    int create(Payment payment);

    Payment getPaymentById(Long id);

}
