package cn.cheakin.gulimall.seckill.controller;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.seckill.service.SecKillService;
import cn.cheakin.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    /*@ResponseBody
    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
    public R getSeckillSkuInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = secKillService.getSeckillSkuInfo(skuId);
        return R.ok().setData(to);
    }*/


    /*@GetMapping("/kill")
    public String kill(@RequestParam("killId") String killId,
                       @RequestParam("key")String key,
                       @RequestParam("num")Integer num,
                       Model model) {
        String orderSn= null;
        try {
            orderSn = secKillService.kill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }*/


}