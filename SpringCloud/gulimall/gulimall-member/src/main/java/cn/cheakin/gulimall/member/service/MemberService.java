package cn.cheakin.gulimall.member.service;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.member.entity.MemberEntity;
import cn.cheakin.gulimall.member.exception.PhoneException;
import cn.cheakin.gulimall.member.exception.UsernameException;
import cn.cheakin.gulimall.member.vo.MemberUserLoginVo;
import cn.cheakin.gulimall.member.vo.MemberUserRegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 09:31:36
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberUserRegisterVo vo);

    void checkPhoneUnique(String phone) throws PhoneException;

    void checkUserNameUnique(String userName) throws UsernameException;

    MemberEntity login(MemberUserLoginVo vo);
}

