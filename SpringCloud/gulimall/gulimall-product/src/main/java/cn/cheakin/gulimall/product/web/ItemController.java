package cn.cheakin.gulimall.product.web;

import cn.cheakin.gulimall.product.service.SkuInfoService;
import cn.cheakin.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Create by botboy on 2022/10/03.
 **/
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     *
     * @param skuId
     * @return
     */
    /*@GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId) {
        SkuItemVo vos = skuInfoService.item(skuId);
        model.addAttribute("item", vos);
        return "item";
    }*/

}
