package cn.cheakin.gulimall.ware.service.impl;

import cn.cheakin.common.exception.NoStockException;
import cn.cheakin.common.to.mq.StockDetailTo;
import cn.cheakin.common.to.mq.StockLockedTo;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.ware.dao.WareSkuDao;
import cn.cheakin.gulimall.ware.entity.WareOrderTaskDetailEntity;
import cn.cheakin.gulimall.ware.entity.WareOrderTaskEntity;
import cn.cheakin.gulimall.ware.entity.WareSkuEntity;
import cn.cheakin.gulimall.ware.feign.OrderFeignService;
import cn.cheakin.gulimall.ware.feign.ProductFeignService;
import cn.cheakin.gulimall.ware.service.WareOrderTaskDetailService;
import cn.cheakin.gulimall.ware.service.WareOrderTaskService;
import cn.cheakin.gulimall.ware.service.WareSkuService;
import cn.cheakin.gulimall.ware.vo.OrderItemVo;
import cn.cheakin.gulimall.ware.vo.OrderVo;
import cn.cheakin.gulimall.ware.vo.SkuHasStockVo;
import cn.cheakin.gulimall.ware.vo.WareSkuLockVo;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 1.库存自动解锁
     *  下单成功,库存锁定成功,接下来的业务调用失败,导致订单回滚,之前锁定的库存就要自动解锁
     * 2.订单失败
     *  库存锁定失败
     *
     *
     * 只要解锁库存的消息失败,一定要告诉服务解锁失败
     * @param to
     */
    /*@RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();
        //解锁
        //1、查询数据库关于这个订单的锁定库存信息
        //  有：证明库存锁定成功了
        //   解锁：订单情况
        //       1、没有这个订单，必须解锁库存
        //       2、有这个订单，不一定解锁库存
        //           订单状态：已取消：解锁库存
        //                   没取消：不能解锁库存
        //  没有：库存锁定失败了，库存回滚了，这种情况无需解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if (byId != null) {
            //解锁
            Long id = to.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            //TODO 远程查询订单服务，查询订单状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                //订单数据返回成功
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {
                    //订单不存在或者订单已经取消了，才能解锁库存
                    if (byId.getLockStatus() == 1) {
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            } else {
                //消息拒绝以后重新放到队列里面，让别人继续消费解锁
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        } else {
            //无需解锁
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }*/

    @Override
    public void unLockStock(StockLockedTo to) {
        System.out.println("收到解锁库存的消息");
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();
        //解锁
        //1、查询数据库关于这个订单的锁定库存信息
        //  有：证明库存锁定成功了
        //   解锁：订单情况
        //       1、没有这个订单，必须解锁库存
        //       2、有这个订单，不一定解锁库存
        //           订单状态：已取消：解锁库存
        //                   没取消：不能解锁库存
        //  没有：库存锁定失败了，库存回滚了，这种情况无需解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if (byId != null) {
            //解锁
            Long id = to.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            //TODO 远程查询订单服务，查询订单状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                //订单数据返回成功
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {
                    //订单不存在或者订单已经取消了，才能解锁库存
                    if (byId.getLockStatus() == 1) {
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                    /*channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);*/
                }
            } else {
                //消息拒绝以后重新放到队列里面，让别人继续消费解锁
                /*channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);*/
                throw new RuntimeException("远程服务失败");
            }
        } else {
            //无需解锁
            /*channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);*/
        }
    }

    private void unLockStock(Long skuId, Long wareId, Integer skuNum, Long detailId) {
        //库存解锁
        wareSkuDao.unLockStock(skuId, wareId, skuNum);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

                if (info.getCode() == 0) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }

            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 判断是否有库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            Long count = this.baseMapper.getSkuStock(skuId);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count == null ? false : count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 为某个订单锁定库存
     * 默认只要是运行时异常都会回滚
     *
     *
     * 库存解锁的场景
     * 1.下单成功，订单过期没有支付被系统自动取消、被用户手动取消都要解锁库存
     * 2.下单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁
     *    之前锁定的库存就要自动解锁
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //因为可能出现订单回滚后，库存锁定不回滚的情况，但订单已经回滚，得不到库存锁定信息，因此要有库存工作单
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);

        // 1.找到每个商品在哪个仓库都用库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map((item) -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪个仓库都有库存
            List<Long> wareIds = baseMapper.listWareIdsHasStock(item.getSkuId(), item.getCount());
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        Boolean allLock = false;
        // 2.锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            }
            //1)如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            //2)锁定失败。前面保存的工作单信息就回滚了。发送出去的消息，即使要解锁记录，由于在数据库查不到id，所有就不用解锁
            for (Long wareId : wareIds) {
                // 锁定成功就返回1，否则就是0
                Long count = baseMapper.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 0) {
                    allLock = false;
                    break;
                } else {
                    //锁定成功，保存工作单详情
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(
                            null,
                            skuId,
                            "",
                            hasStock.getNum(),
                            taskEntity.getId(),
                            wareId,
                            1);
                    /*WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity.builder()
                            .skuId(skuId)
                            .skuName("")
                            .skuNum(hasStock.getNum())
                            .taskId(taskEntity.getId())
                            .wareId(wareId)
                            .lockStatus(1).build();*/
                    wareOrderTaskDetailService.save(detailEntity);
                    //发送库存锁定消息至延迟队列
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity, detailTo);
                    lockedTo.setDetailTo(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);

                    allLock = true;
                }
            }
            if (allLock == false) {
                //当前商品锁定失败，前面锁定成功的要解锁
                throw new NoStockException(skuId);
            }

        }
        //  3.全部都是锁定成功的
        return true;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

    /**
     * 为某个订单锁定库存
     * @param wareSkuLockVo
     * @return
     */
    /*@Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        //因为可能出现订单回滚后，库存锁定不回滚的情况，但订单已经回滚，得不到库存锁定信息，因此要有库存工作单
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);

        List<OrderItemVo> itemVos = wareSkuLockVo.getLocks();
        List<SkuLockVo> lockVos = itemVos.stream().map((item) -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(item.getSkuId());
            skuLockVo.setNum(item.getCount());
            //找出所有库存大于商品数的仓库
            List<Long> wareIds = baseMapper.listWareIdsHasStock(item.getSkuId(), item.getCount());
            skuLockVo.setWareIds(wareIds);
            return skuLockVo;
        }).collect(Collectors.toList());

        for (SkuLockVo lockVo : lockVos) {
            boolean lock = true;
            Long skuId = lockVo.getSkuId();
            List<Long> wareIds = lockVo.getWareIds();
            //如果没有满足条件的仓库，抛出异常
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            }else {
                for (Long wareId : wareIds) {
                    Long count=baseMapper.lockWareSku(skuId, lockVo.getNum(), wareId);
                    if (count==0){
                        lock=false;
                    }else {
                        //锁定成功，保存工作单详情
                        WareOrderTaskDetailEntity detailEntity = WareOrderTaskDetailEntity.builder()
                                .skuId(skuId)
                                .skuName("")
                                .skuNum(lockVo.getNum())
                                .taskId(taskEntity.getId())
                                .wareId(wareId)
                                .lockStatus(1).build();
                        wareOrderTaskDetailService.save(detailEntity);
                        //发送库存锁定消息至延迟队列
                        StockLockedTo lockedTo = new StockLockedTo();
                        lockedTo.setId(taskEntity.getId());
                        StockDetailTo detailTo = new StockDetailTo();
                        BeanUtils.copyProperties(detailEntity,detailTo);
                        lockedTo.setDetailTo(detailTo);
                        rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);

                        lock = true;
                        break;
                    }
                }
            }
            if (!lock) throw new NoStockException(skuId);
        }
        return true;
    }*/

}