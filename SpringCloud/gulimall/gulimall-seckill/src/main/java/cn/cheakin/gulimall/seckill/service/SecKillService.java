package cn.cheakin.gulimall.seckill.service;

import cn.cheakin.gulimall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

public interface SecKillService {
    void uploadSeckillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    SecKillSkuRedisTo getSkuSeckillInfoById(Long skuId);

    String kill(String killId, String key, Integer num) throws InterruptedException;
}