package cn.cheakin.gulimall.seckill.schedule;

import cn.cheakin.gulimall.seckill.service.SecKillService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架
 *  每天王航3点：上架最近三天需要秒杀的商品
 *  当天 00:00:00 - 23:59:59
 *  明天 00:00:00 - 23:59:59
 *  后天 00:00:00 - 23:59:59
 */
@Component
public class SecKillScheduled {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SecKillService secKillService;

    //秒杀商品上架功能的锁
    private final String upload_lock = "seckill:upload:lock";

    /**
     * 定时任务
     * 每天三点上架最近三天的秒杀商品
     */
    @Async
//    @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(cron = "0 55 22 * * ?")
    public void uploadSeckillSkuLatest3Days() {
        // 重复上架无需处理
        //为避免分布式情况下多服务同时上架的情况，使用分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            secKillService.uploadSeckillSkuLatest3Days();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}