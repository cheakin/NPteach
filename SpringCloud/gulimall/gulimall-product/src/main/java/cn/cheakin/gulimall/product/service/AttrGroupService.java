package cn.cheakin.gulimall.product.service;

import cn.cheakin.gulimall.product.vo.AttrGroupWithAttrsVo;
import cn.cheakin.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-15 14:27:41
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

