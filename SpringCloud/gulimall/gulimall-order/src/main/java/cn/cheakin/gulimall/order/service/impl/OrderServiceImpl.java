package cn.cheakin.gulimall.order.service.impl;

import cn.cheakin.common.constant.CartConstant;
import cn.cheakin.common.exception.NoStockException;
import cn.cheakin.common.to.SkuHasStockVo;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.common.utils.R;
import cn.cheakin.common.vo.MemberResponseVo;
import cn.cheakin.gulimall.order.constant.OrderConstant;
import cn.cheakin.gulimall.order.dao.OrderDao;
import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.entity.OrderItemEntity;
import cn.cheakin.gulimall.order.enume.OrderStatusEnum;
import cn.cheakin.gulimall.order.feign.CartFeignService;
import cn.cheakin.gulimall.order.feign.MemberFeignService;
import cn.cheakin.gulimall.order.feign.ProductFeignService;
import cn.cheakin.gulimall.order.feign.WareFeignService;
import cn.cheakin.gulimall.order.interceptor.LoginInterceptor;
import cn.cheakin.gulimall.order.service.OrderItemService;
import cn.cheakin.gulimall.order.service.OrderService;
import cn.cheakin.gulimall.order.to.OrderCreateTo;
import cn.cheakin.gulimall.order.to.SpuInfoTo;
import cn.cheakin.gulimall.order.vo.*;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /*@Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;*/

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
        System.out.println("主线程..." + Thread.currentThread().getId());

        //1. 远程查出所有收货地址
        /*List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
        confirmVo.setMemberAddressVos(addressByUserId);*/
        //2. 远程查出所有选中购物项
        /*List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(items);*/

        // 获取主线程的请求头
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //1. 查出所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            System.out.println("member线程..." + Thread.currentThread().getId());
            // 异步调用请求头共享：每一个线程都共享之前的请求头
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
            confirmVo.setMemberAddressVos(addressByUserId);
        }, executor);


        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("member线程..." + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);   // 异步调用请求头共享
            //2. 查出所有选中购物项
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            return items;
        }, executor).thenAcceptAsync((items) -> {
            //4. 库存
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R hasStocks = wareFeignService.getSkuHasStocks(skuIds);
            /*TypeReference<List<SkuStockVo>> typeReference = new TypeReference<List<SkuStockVo>>() {};
            List<SkuStockVo> data = hasStocks.getData(typeReference);
            if (data != null) {
                Map<Long, Boolean> hasStockMap = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(hasStockMap);
            }*/
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            List<SkuHasStockVo> data = hasStocks.getData(typeReference);
            Map<Long, Boolean>hasStockMap = data.stream()
                    .collect(Collectors.toMap(t -> t.getSkuId(), t -> t.getHasStock()));

            confirmVo.setStocks(hasStockMap);
        }, executor);

        //3. 查询用户积分
        confirmVo.setIntegration(memberResponseVo.getIntegration());

        //5. 总价自动计算
        //6. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();

        return confirmVo;
    }

    // 本地事务在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
    // 分布式事务：最大问题，网络网体+分布式机器
    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        //1. 验证防重令牌【令牌的对比和删除必须保证原子性】
        //  0令牌失败  1删除成功
        MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script,Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId()), submitVo.getOrderToken());
        if (execute == 0L) {
            //1.1 防重令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        }else {
            //2. 创建订单、订单项
            OrderCreateTo order = createOrderTo(memberResponseVo,submitVo);

            //3. 验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = submitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //4. 保存订单
                saveOrder(order);
                //5. 锁定库存, 只要有异常就回滚订单数据
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    return orderItemVo;
                }).collect(Collectors.toList());
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                lockVo.setLocks(orderItemVos);

                // 库存成功了，但是网络原因超时了，订单回滚，库存不回滚
                // 为了保证高并发。库存服务自己回滚。可以发消息给库存服务
                // 库存服务本身也可以使用自动解锁模式 消息队列
                R r = wareFeignService.orderLockStock(lockVo);
                //5.1 锁定库存成功
                if (r.getCode()==0){
//                    int i = 10 / 0;   // 订单回滚，库存不会滚
                    responseVo.setOrder(order.getOrder());
                    responseVo.setCode(0);

                    //发送消息到订单延迟队列，判断过期订单
//                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());

                    //清除购物车记录
                    BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + memberResponseVo.getId());
                    for (OrderItemEntity orderItem : order.getOrderItems()) {
                        ops.delete(orderItem.getSkuId().toString());
                    }
                    return responseVo;
                }else {
                    //5.1 锁定库存失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }

            }else {
                //验价失败
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    private OrderCreateTo createOrderTo(MemberResponseVo memberResponseVo, OrderSubmitVo submitVo) {
        //用IdWorker生成订单号
        String orderSn = IdWorker.getTimeId();
        //构建订单
        OrderEntity entity = buildOrder(memberResponseVo, submitVo,orderSn);
        //构建订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        //计算价格
        compute(entity, orderItemEntities);
        OrderCreateTo createTo = new OrderCreateTo();
        createTo.setOrder(entity);
        createTo.setOrderItems(orderItemEntities);
        return createTo;
    }

    /**
     * 构建订单
     * @param memberResponseVo
     * @param submitVo
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(MemberResponseVo memberResponseVo, OrderSubmitVo submitVo, String orderSn) {

        OrderEntity orderEntity =new OrderEntity();

        orderEntity.setOrderSn(orderSn);

        //2) 设置用户信息
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setMemberUsername(memberResponseVo.getUsername());

        //3) 获取邮费和收件人信息并设置
        R r = wareFeignService.getFare(submitVo.getAddrId());
        FareVo fareVo = r.getData(FareVo.class);
        BigDecimal fare = fareVo.getFare();
        orderEntity.setFreightAmount(fare);
        MemberAddressVo address = fareVo.getAddress();
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());

        //4) 设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(0);
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @param orderSn
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最后确定每个购物项的价格
        List<OrderItemVo> checkedItems = cartFeignService.getCurrentUserCartItems();
        List<OrderItemEntity> orderItemEntities = checkedItems.stream().map((item) -> {
            OrderItemEntity orderItemEntity = buildOrderItem(item);
            //1) 设置订单号
            orderItemEntity.setOrderSn(orderSn);
            return orderItemEntity;
        }).collect(Collectors.toList());
        return orderItemEntities;
    }

    /**
     * 构建某一个订单项
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        Long skuId = item.getSkuId();
        //2) 设置sku相关属性
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        //3) 通过skuId查询spu相关属性并设置
        R r = productFeignService.getSpuBySkuId(skuId);
        if (r.getCode() == 0) {
            SpuInfoTo spuInfo = r.getData(new TypeReference<SpuInfoTo>() {
            });
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setSpuBrand(spuInfo.getBrandName());
            orderItemEntity.setCategoryId(spuInfo.getCatalogId());
        }
        //4) 商品的优惠信息(不做)

        //5) 商品的积分成长，为价格x数量
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());

        //6) 订单项订单价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //7) 实际价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        return orderItemEntity;
    }

    private void compute(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = BigDecimal.ZERO;
        //优惠价格
        BigDecimal promotion=new BigDecimal("0.0");
        BigDecimal integration=new BigDecimal("0.0");
        BigDecimal coupon=new BigDecimal("0.0");
        //积分
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total=total.add(orderItemEntity.getRealAmount());
            promotion=promotion.add(orderItemEntity.getPromotionAmount());
            integration=integration.add(orderItemEntity.getIntegrationAmount());
            coupon=coupon.add(orderItemEntity.getCouponAmount());
            integrationTotal += orderItemEntity.getGiftIntegration();
            growthTotal += orderItemEntity.getGiftGrowth();
        }

        entity.setTotalAmount(total);
        entity.setPromotionAmount(promotion);
        entity.setIntegrationAmount(integration);
        entity.setCouponAmount(coupon);
        entity.setIntegration(integrationTotal);
        entity.setGrowth(growthTotal);

        //付款价格=商品价格+运费
        entity.setPayAmount(entity.getFreightAmount().add(total));

        //设置删除状态(0-未删除，1-已删除)
        entity.setDeleteStatus(0);
    }

    /**
     * 保存订单数据
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        try {
            OrderEntity order = orderCreateTo.getOrder();
            order.setCreateTime(new Date());
            order.setModifyTime(new Date());
            this.save(order);
            orderItemService.saveBatch(orderCreateTo.getOrderItems());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}