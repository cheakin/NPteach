package cn.cheakin.gulimall.member.feign;

import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Coupon远程调用
 * Create by botboy on 2022/07/17.
 **/
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")   // 需要完整的路径
    public R membercouponList();

}
