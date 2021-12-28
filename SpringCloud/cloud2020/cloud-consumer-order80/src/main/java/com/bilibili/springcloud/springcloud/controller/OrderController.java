package com.bilibili.springcloud.springcloud.controller;

import com.bilibili.springcloud.springcloud.entities.CommonResult;
import com.bilibili.springcloud.springcloud.entities.Payment;
import com.bilibili.springcloud.springcloud.lb.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
public class OrderController {

//        public static final String PAYMENT_URL = "http://localhost:8001";     //单机
    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";       //服务集群

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/consumer/payment/create")
    public CommonResult creat(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create/", payment, CommonResult.class);
    }

    @PostMapping("/consumer/payment/postForEntity")
    public CommonResult<Payment> creat2(Payment payment) {
        ResponseEntity<CommonResult> entity = restTemplate.postForEntity(PAYMENT_URL + "/payment/create/postForEntity/", payment, CommonResult.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity.getBody();
        } else {
            return new CommonResult<Payment>().fail(400, "操作失败！");
        }
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult getPayment(@PathVariable("id") Long id) {
        log.info("******/consumer/payment/get/{id}********");
        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
    }

    @GetMapping("/consumer/payment/getForEntity/{id}")
    public CommonResult<Payment> getPayment2(@PathVariable("id") Long id){
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/getForEntity/" + id, CommonResult.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            log.info(entity.getStatusCode() + "\t" + entity.getBody());
            return entity.getBody();
        } else {
            return new CommonResult<Payment>().fail(400, "操作失败！");
        }

    }



    /** 自定义的负载均衡 */
    @Resource
    private LoadBalancer loadBalancer;
    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping("/consumer/payment/lb")
    public String getPaymentLB() {
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        if (instances == null || instances.size() <= 0) {
            return null;
        }
        ServiceInstance serviceInstance = loadBalancer.instances(instances);
        URI uri = serviceInstance.getUri();

        return restTemplate.getForObject(uri+"/payment/lb", String.class);
    }

    /**
     * 链路跟踪 zipkin+sleuth
     * http://localhost/consumer/payment/zipkin
     *
     * @return
     */
    @GetMapping("/consumer/payment/zipkin")
    public String paymentZipkin() {
        return restTemplate.getForObject("http://localhost:8001/payment/zipkin/", String.class);
    }



}
