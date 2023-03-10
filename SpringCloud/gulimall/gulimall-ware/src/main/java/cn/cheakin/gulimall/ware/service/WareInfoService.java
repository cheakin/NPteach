package cn.cheakin.gulimall.ware.service;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.gulimall.ware.entity.WareInfoEntity;
import cn.cheakin.gulimall.ware.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author botboy
 * @email cheakin@foxmail.com
 * @date 2022-07-16 10:22:34
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户的收货地址计算运费
     * @param addrId
     * @return
     */
    // BigDecimal getFare(Long addrId);
    FareVo getFare(Long addrId);
}

