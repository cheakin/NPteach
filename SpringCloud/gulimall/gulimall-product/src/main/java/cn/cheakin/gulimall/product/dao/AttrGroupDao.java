package cn.cheakin.gulimall.product.dao;

import cn.cheakin.gulimall.product.entity.AttrGroupEntity;
import cn.cheakin.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-15 14:27:41
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}
