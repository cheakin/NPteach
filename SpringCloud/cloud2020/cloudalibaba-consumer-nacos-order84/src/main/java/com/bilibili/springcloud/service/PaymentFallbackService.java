package com.bilibili.springcloud.service;

import com.bilibili.springcloud.springcloud.entities.CommonResult;
import com.bilibili.springcloud.springcloud.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentFallbackService implements PaymentService {
    @Override
    public CommonResult<Payment> paymentSQL(Long id) {
        return new CommonResult().fail(444, "fallback");
    }
}