package cn.cheakin.gulimall.seckill.feign;

import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("coupon/seckillsession/lates3DaySession")
    R getLates3DaySession();

}