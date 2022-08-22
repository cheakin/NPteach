package cn.cheakin.gulimall.product.feign;

import cn.cheakin.common.to.SkuHasStockVo;
import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 库存服务
 * Create by botboy on 2022/08/18.
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 1、R设计的时候可以加上泛型
     * 2、直接返回我们想要的结果
     * 3、自己封装解析结果
     * @param skuIds
     * @return
     */
    @PostMapping(value = "/ware/waresku/hasStock")
//    R getSkuHasStock(@RequestBody List<Long> skuIds);
    R getSkuHasStock(@RequestBody List<Long> skuIds);

}
