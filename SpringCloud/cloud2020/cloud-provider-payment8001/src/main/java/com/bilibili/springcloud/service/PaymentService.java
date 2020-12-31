package com.bilibili.springcloud.service;

import com.bilibili.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;


public interface PaymentService {

    int create(Payment payment);

    Payment getPaymentById(Long id);

}
