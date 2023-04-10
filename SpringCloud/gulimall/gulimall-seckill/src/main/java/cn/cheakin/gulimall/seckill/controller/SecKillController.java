package cn.cheakin.gulimall.seckill.controller;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.seckill.service.SecKillService;
import cn.cheakin.gulimall.seckill.to.SecKillSkuRedisTo;
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
        System.out.println("正在执行");

        //获取到当前可以参加秒杀商品的信息
        List<SecKillSkuRedisTo> vos = secKillService.getCurrentSeckillSkus();

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
        /*try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        SecKillSkuRedisTo skuRedisTos = secKillService.getSkuSeckillInfoById(skuId);
        return R.ok().setData(skuRedisTos);
    }


    /**
     * 商品进行秒杀(秒杀开始)
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping(value = "/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {

        String orderSn = null;
        try {
            //1、判断是否登录
            orderSn = secKillService.kill(killId,key,num);
            model.addAttribute("orderSn",orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

}