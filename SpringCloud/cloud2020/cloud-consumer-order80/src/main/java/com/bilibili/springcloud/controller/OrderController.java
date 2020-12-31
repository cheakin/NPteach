package com.bilibili.springcloud.controller;

import com.bilibili.springcloud.entities.CommonResult;
import com.bilibili.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class OrderController {

    //    public static final String PAYMENT_URL = "http://localhost:8001";     //单机
    public static final String PAYMENT_URL = "CLOUD-PAYMENT-SERVICE";       //集群

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public CommonResult creat(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create/", payment, CommonResult.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult getPayment(@PathVariable("id") Long id) {
        log.info("******/consumer/payment/get/{id}********");
        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
    }


}
