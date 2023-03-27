package cn.cheakin.gulimall.ware.feign;

import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-order")
public interface OrderFeignService {
    @RequestMapping("order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String OrderSn);
}