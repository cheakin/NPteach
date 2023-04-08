package cn.cheakin.gulimall.seckill.service;

import cn.cheakin.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

public interface SecKillService {
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfoById(Long skuId);

    /*String kill(String killId, String key, Integer num) throws InterruptedException;*/
}