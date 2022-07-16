package cn.cheakin.gulimall.member.dao;

import cn.cheakin.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 09:31:36
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
