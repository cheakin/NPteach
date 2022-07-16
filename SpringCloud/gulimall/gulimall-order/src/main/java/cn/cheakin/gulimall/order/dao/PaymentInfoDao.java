package cn.cheakin.gulimall.order.dao;

import cn.cheakin.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:17:47
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
