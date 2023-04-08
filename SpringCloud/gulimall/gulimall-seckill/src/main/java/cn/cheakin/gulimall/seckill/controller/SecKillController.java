package cn.cheakin.gulimall.seckill.controller;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.seckill.service.SecKillService;
import cn.cheakin.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    /**
     * 当前时间可以参与秒杀的商品信息
     * @return
     */
    @GetMapping(value = "/currentSeckillSkus")
    @ResponseBody
    public R currentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = secKillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }

    /**
     * 根据skuId获取该商品是否有秒杀活动
     *
     * @param skuId skuId
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfoById(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo skuRedisTos = secKillService.getSkuSeckillInfoById(skuId);
        return R.ok().setData(skuRedisTos);
    }


    /**
     * 秒杀商品加入购物车
     *
     * @param seckillId 商品在redis中的key
     * @param num       数量
     * @param code      随机码
     * @return R
     */
    @GetMapping("/seckill")
    public String seckill(@RequestParam("seckillId") String seckillId,
                          @RequestParam("num") String num,
                          @RequestParam("code") String code,
                          Model model) {

        return null;
    }

}