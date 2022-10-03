package cn.cheakin.gulimall.product.service;

import cn.cheakin.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-15 14:27:41
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);
}

