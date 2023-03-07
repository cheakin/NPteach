package cn.cheakin.gulimall.order.feign;

import cn.cheakin.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();

    @ResponseBody
    @RequestMapping("/getCheckedItems")
    List<OrderItemVo> getCheckedItems();
}