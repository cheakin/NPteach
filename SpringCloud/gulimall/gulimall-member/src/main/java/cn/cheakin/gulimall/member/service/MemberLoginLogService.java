package cn.cheakin.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 09:31:36
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

