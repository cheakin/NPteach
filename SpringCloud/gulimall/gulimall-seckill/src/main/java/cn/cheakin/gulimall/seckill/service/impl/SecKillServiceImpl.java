package cn.cheakin.gulimall.seckill.service.impl;

import cn.cheakin.common.to.mq.SeckillOrderTo;
import cn.cheakin.common.utils.R;
import cn.cheakin.common.vo.MemberResponseVo;
import cn.cheakin.gulimall.seckill.feign.CouponFeignService;
import cn.cheakin.gulimall.seckill.feign.ProductFeignService;
import cn.cheakin.gulimall.seckill.interceptor.LoginUserInterceptor;
import cn.cheakin.gulimall.seckill.service.SecKillService;
import cn.cheakin.gulimall.seckill.to.SecKillSkuRedisTo;
import cn.cheakin.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import cn.cheakin.gulimall.seckill.vo.SkuInfoVo;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service("SecKillService")
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //K: SESSION_CACHE_PREFIX + startTime + "_" + endTime
    //V: sessionId+"-"+skuId的List
    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    //K: 固定值SECKILL_CHARE_PREFIX
    //V: hash，k为sessionId+"-"+skuId，v为对应的商品信息SeckillSkuRedisTo
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    //K: SKU_STOCK_SEMAPHORE+商品随机码
    //V: 秒杀的库存件数
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码


    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 扫描最近三天所需要参与秒杀的活动
        R r = couponFeignService.getLates3DaySession();
        if (r.getCode() == 0) {
            // 上架商品
            List<SeckillSessionWithSkusVo> sessions = r.getData(new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            //缓存活动信息
            saveSessionInfos(sessions);
            //缓存活动的关联信息
            saveSessionSkuInfos(sessions);
        }
    }

    public  List<SecKillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkusResource被限流了..");
        return null;
    }

    /**
     * blockHandler 函数会在原方法被限流/降级/系统保护的时候调用，而 fallback 函数会针对所有类型的异常。
     * @return
     */
    //当前时间可以参与秒杀的商品信息
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        try (Entry entry = SphU.entry("seckillSku")) {
            //1.确定当前时间与那个秒杀场次
            Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
            long time = System.currentTimeMillis();
            for (String key : keys) {
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] split = replace.split("_");
                long start = Long.parseLong(split[0]);
                long endTime = Long.parseLong(split[1]);
                //当前秒杀活动处于有效期内
                if (time > start && time < endTime) {
                    //2.获取当前场需要的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if (list != null) {
                        List<SecKillSkuRedisTo> collect = list.stream().map((item) -> {
                            SecKillSkuRedisTo to = JSON.parseObject((String) item, SecKillSkuRedisTo.class);
//                        to.setRandomCode(null);//当前秒杀开始就需要随机码
                            return to;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        } catch (BlockException e) {
            log.error("资源被限流", e.getMessage());
        }
        return null;
    }

    //根据skuId获取该商品是否有秒杀活动
    @Override
    public SecKillSkuRedisTo getSkuSeckillInfoById(Long skuId) {
        List<SecKillSkuRedisTo> skuRedisTos = new ArrayList<>();

        //1、获取redis中所有参与秒杀的key信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //定义正则
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    //正则匹配成功返回数据
                    String json = hashOps.get(key);
                    SecKillSkuRedisTo skuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    //2、处理随机码，只有到商品秒杀时间才能显示随机码
                    if (skuRedisTo != null) {
                        Long startTime = skuRedisTo.getStartTime();
                        long currentTime = System.currentTimeMillis();
                        if (currentTime < startTime) {
                            //秒杀还未开始，将随机码置空
                            skuRedisTo.setRandomCode(null);
                        }
                        return skuRedisTo;
                    }
                }
            }
        }
        return null;
    }

    //当前商品进行秒杀（秒杀开始）
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        long s1 = System.currentTimeMillis();
        MemberResponseVo respVo = LoginUserInterceptor.loginUser.get();

        //1、获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            SecKillSkuRedisTo redis = JSON.parseObject(json, SecKillSkuRedisTo.class);
            //校验合法性
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long time = new Date().getTime();

            long ttl = endTime - time;

            //1、校验时间的合法性
            if (time >= startTime && time <= endTime) {
                //2、校验随机码和商品id
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "_" + redis.getSkuId();
                if (randomCode.equals(key) && killId.equals(skuId)) {
                    //3、验证购物数量是否合理
                    if (num <= redis.getSeckillLimit()) {
                        //4、验证这个人是否已经购买过。幂等性; 如果只要秒杀成功，就去占位。  userId_SessionId_skuId
                        //SETNX
                        String redisKey = respVo.getId() + "_" + skuId;
                        //自动过期
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            //占位成功说明从来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            //120  20ms
                            boolean b = semaphore.tryAcquire(num);
                            if (b) {
                                //秒杀成功;
                                //快速下单。发送MQ消息  10ms
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setMemberId(respVo.getId());
                                orderTo.setNum(num);
                                orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                orderTo.setSkuId(redis.getSkuId());
                                orderTo.setSeckillPrice(redis.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                                long s2 = System.currentTimeMillis();
                                log.info("耗时...{}", (s2 - s1));
                                return timeId;
                            }
                            return null;

                        } else {
                            //说明已经买过了
                            return null;
                        }

                    }
                } else {
                    return null;
                }

            } else {
                return null;
            }
        }

        return null;
    }


    private void saveSessionInfos(List<SeckillSessionWithSkusVo> sessions) {
        sessions.stream().forEach(session->{
            String key = SESSION_CACHE_PREFIX + session.getStartTime().getTime() + "_" + session.getEndTime().getTime();
            //当前活动信息未保存过
            if (!redisTemplate.hasKey(key)){
                List<String> values = session.getRelationSkus().stream()
//                        .map(item -> item.getSkuId().toString())
                        .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId())
                        .collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, values);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkusVo> sessions) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        sessions.stream().forEach(session->{
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
//                String key = seckillSkuVo.getSkuId().toString();
                String key = seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId();
                if (!ops.hasKey(key)){
                    // 缓存商品
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                    //2.sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    //1.sku的基本数据
                    R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo info = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfoVo(info);
                    }

                    //3.设置上当前上坪的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    //4.生成商品随机码，防止恶意攻击
                    String token = UUID.randomUUID().toString().replace("-", "");
                    redisTo.setRandomCode(token);

                    //5.使用库存作为Redisson信号量限制库存 限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());

                    //序列化为json并保存
                    String jsonString = JSON.toJSONString(redisTo);
                    ops.put(key, jsonString);
                }
            });
        });
    }

}