package com.bilibili.springcloud.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyLB implements LoadBalancer {

     private AtomicInteger atomicInteger = new AtomicInteger(0);

     public final int getAndIncrement() {
         int current;
         int next;  //第几次访问此接口
         do {   //自旋锁
             current = this.atomicInteger.get();
             next = current >= Integer.MAX_VALUE ? 0 : current + 1;
         } while (!atomicInteger.compareAndSet(current, next)); //比较并更新
         System.out.println("****next:" + next);
         return next;
     }

    /**
     * 自定义负载均衡-轮询的实现
     * @param serviceInstances
     * @return
     */
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstances) {
        int index =  getAndIncrement() % serviceInstances.size();   //访问次数对集群数取余
        return serviceInstances.get(index);
    }
}
