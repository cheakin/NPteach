package cn.cheakin.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * δΈι’εε
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 02:10:57
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

