package cn.cheakin.gulimall.ware.service;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.ware.entity.WareSkuEntity;
import cn.cheakin.gulimall.ware.vo.SkuHasStockVo;
import cn.cheakin.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:22:34
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 判断是否有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 为某个订单锁定库存
     * @param lockVo
     * @return
     */
//    List<LockStockResult> orderLockStock(WareSkuLockVo lockVo);
    Boolean orderLockStock(WareSkuLockVo lockVo);

}

