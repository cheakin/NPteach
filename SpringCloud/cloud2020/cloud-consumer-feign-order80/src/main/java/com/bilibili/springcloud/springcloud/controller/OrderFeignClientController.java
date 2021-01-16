package com.bilibili.springcloud.springcloud.controller;

import com.bilibili.springcloud.springcloud.entities.CommonResult;
import com.bilibili.springcloud.springcloud.entities.Payment;
import com.bilibili.springcloud.springcloud.service.PaymentFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class OrderFeignClientController {

    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }

    /**
     * 长处理服务
     * @return
     */
    @GetMapping("/consumer/payment/feign/timeout")
    public String paymentFeignTimeout() {
        //OpenFeign-ribbon，客户端一般默认等待1秒钟
        return paymentFeignService.paymentFeignTimeout();
    }
}
