package cn.cheakin.gulimall.ware.dao;

import cn.cheakin.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:22:34
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}