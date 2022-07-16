package cn.cheakin.gulimall.coupon.dao;

import cn.cheakin.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 02:10:57
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
