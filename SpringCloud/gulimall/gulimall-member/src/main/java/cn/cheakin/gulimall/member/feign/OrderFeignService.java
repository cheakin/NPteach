package cn.cheakin.gulimall.member.feign;

import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient("gulimall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")   // 需要完整的路径
    public R listWithItem(@RequestBody Map<String, Object> params);

}
