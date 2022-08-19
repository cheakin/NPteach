package cn.cheakin.gulimall.product.feign;

import cn.cheakin.common.to.es.SkuEsModel;
import cn.cheakin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * ES服务
 * Create by botboy on 2022/08/19.
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping(value = "/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
