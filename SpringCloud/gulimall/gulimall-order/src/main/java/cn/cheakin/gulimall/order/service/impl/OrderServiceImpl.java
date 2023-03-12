package cn.cheakin.gulimall.order.service.impl;

import cn.cheakin.common.to.SkuHasStockVo;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.common.utils.R;
import cn.cheakin.common.vo.MemberResponseVo;
import cn.cheakin.gulimall.order.constant.OrderConstant;
import cn.cheakin.gulimall.order.dao.OrderDao;
import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.feign.CartFeignService;
import cn.cheakin.gulimall.order.feign.MemberFeignService;
import cn.cheakin.gulimall.order.feign.WareFeignService;
import cn.cheakin.gulimall.order.interceptor.LoginInterceptor;
import cn.cheakin.gulimall.order.service.OrderService;
import cn.cheakin.gulimall.order.vo.MemberAddressVo;
import cn.cheakin.gulimall.order.vo.OrderConfirmVo;
import cn.cheakin.gulimall.order.vo.OrderItemVo;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private ThreadPoolExecutor executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /*@Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
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

}