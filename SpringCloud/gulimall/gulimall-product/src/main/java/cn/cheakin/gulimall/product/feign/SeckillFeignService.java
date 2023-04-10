package cn.cheakin.gulimall.product.feign;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(value = "gulimall-seckill")
@FeignClient(value = "gulimall-seckill", fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {

    /**
     * 根据skuId获取该商品是否有秒杀活动
     * @param skuId skuId
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);

}