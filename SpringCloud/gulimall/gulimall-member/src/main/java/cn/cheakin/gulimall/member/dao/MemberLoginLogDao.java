package cn.cheakin.gulimall.member.dao;

import cn.cheakin.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 09:31:36
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
