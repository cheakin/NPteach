package cn.cheakin.gulimall.seckill.service.impl;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.seckill.feign.CouponFeignService;
import cn.cheakin.gulimall.seckill.feign.ProductFeignService;
import cn.cheakin.gulimall.seckill.service.SecKillService;
import cn.cheakin.gulimall.seckill.to.SeckillSkuRedisTo;
import cn.cheakin.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import cn.cheakin.gulimall.seckill.vo.SkuInfoVo;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    //前时间可以参与秒杀的商品信息
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
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
                    List<SeckillSkuRedisTo> collect = list.stream().map((item) -> {
                        SeckillSkuRedisTo to = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
//                        to.setRandomCode(null);//当前秒杀开始就需要随机码
                        return to;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }

    //根据skuId获取该商品是否有秒杀活动
    @Override
    public SeckillSkuRedisTo getSkuSeckillInfoById(Long skuId) {
        List<SeckillSkuRedisTo> skuRedisTos = new ArrayList<>();

        //1、获取redis中所有参与秒杀的key信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //定义正则
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                if (key.matches(regx)) {
                    //正则匹配成功返回数据
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
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

    /*@Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String json = ops.get(killId);
        String orderSn = null;
        if (!StringUtils.isEmpty(json)){
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //1. 验证时效
            long current = System.currentTimeMillis();
            if (current >= redisTo.getStartTime() && current <= redisTo.getEndTime()) {
                //2. 验证商品和商品随机码是否对应
                String redisKey = redisTo.getPromotionSessionId() + "-" + redisTo.getSkuId();
                if (redisKey.equals(killId) && redisTo.getRandomCode().equals(key)) {
                    //3. 验证当前用户是否购买过
                    MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
                    long ttl = redisTo.getEndTime() - System.currentTimeMillis();
                    //3.1 通过在redis中使用 用户id-skuId 来占位看是否买过
                    Boolean occupy = redisTemplate.opsForValue().setIfAbsent(memberResponseVo.getId()+"-"+redisTo.getSkuId(), num.toString(), ttl, TimeUnit.MILLISECONDS);
                    //3.2 占位成功，说明该用户未秒杀过该商品，则继续
                    if (occupy){
                        //4. 校验库存和购买量是否符合要求
                        if (num <= redisTo.getSeckillLimit()) {
                            //4.1 尝试获取库存信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + redisTo.getRandomCode());
                            boolean acquire = semaphore.tryAcquire(num,100,TimeUnit.MILLISECONDS);
                            //4.2 获取库存成功
                            if (acquire) {
                                //5. 发送消息创建订单
                                //5.1 创建订单号
                                orderSn = IdWorker.getTimeId();
                                //5.2 创建秒杀订单to
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setMemberId(memberResponseVo.getId());
                                orderTo.setNum(num);
                                orderTo.setOrderSn(orderSn);
                                orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                orderTo.setSkuId(redisTo.getSkuId());
                                //5.3 发送创建订单的消息
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                            }
                        }
                    }
                }
            }
            return orderSn;
        }
        return null;
    }*/


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
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
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