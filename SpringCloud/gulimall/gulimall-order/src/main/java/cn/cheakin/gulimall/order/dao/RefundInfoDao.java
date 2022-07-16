package cn.cheakin.gulimall.order.dao;

import cn.cheakin.gulimall.order.entity.RefundInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:17:47
 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}
