package cn.cheakin.gulimall.order.service.impl;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.common.vo.MemberResponseVo;
import cn.cheakin.gulimall.order.dao.OrderDao;
import cn.cheakin.gulimall.order.entity.OrderEntity;
import cn.cheakin.gulimall.order.feign.CartFeignService;
import cn.cheakin.gulimall.order.feign.MemberFeignService;
import cn.cheakin.gulimall.order.interceptor.LoginInterceptor;
import cn.cheakin.gulimall.order.service.OrderService;
import cn.cheakin.gulimall.order.vo.MemberAddressVo;
import cn.cheakin.gulimall.order.vo.OrderConfirmVo;
import cn.cheakin.gulimall.order.vo.OrderItemVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    /*@Autowired
    private WareFeignService wareFeignService;*/

    @Autowired
    private ThreadPoolExecutor executor;
    /*@Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
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
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();

        //1. 远程查出所有收货地址
        List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
        confirmVo.setMemberAddressVos(addressByUserId);
        //2. 远程查出所有选中购物项
        List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(items);
        //3. 查询用户积分
        confirmVo.setIntegration(memberResponseVo.getIntegration());
        //4. 其他数据自动计算
        //5. 放重令牌

        /*RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1. 查出所有选中购物项
            List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems();
            confirmVo.setItems(checkedItems);
            return checkedItems;
        }, executor).thenAcceptAsync((items) -> {
            //4. 库存
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
           *//* Map<Long, Boolean> hasStockMap = wareFeignService.getSkuHasStocks(skuIds).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            confirmVo.setStocks(hasStockMap);*//*
        }, executor);

        //2. 查出所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
            confirmVo.setMemberAddressVos(addressByUserId);
        }, executor);*/



        //5. 总价自动计算
        //6. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        /*redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);*/
        /*try {
            CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        return confirmVo;
    }

}