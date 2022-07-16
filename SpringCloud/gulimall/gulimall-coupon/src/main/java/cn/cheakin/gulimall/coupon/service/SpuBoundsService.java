package cn.cheakin.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 02:10:57
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

