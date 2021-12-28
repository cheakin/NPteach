package com.bilibili.springcloud.springcloud.service;

import com.bilibili.springcloud.springcloud.entities.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("CLOUD-PAYMENT-SERVICE")
public interface PaymentFeignService {

    @GetMapping("/payment/get/{id}")
    CommonResult getPaymentById(@PathVariable("id") Long id);

    /**
     * 长处理服务
     * @return
     */
    @GetMapping("/payment/feign/timeout")
    String paymentFeignTimeout();

}
