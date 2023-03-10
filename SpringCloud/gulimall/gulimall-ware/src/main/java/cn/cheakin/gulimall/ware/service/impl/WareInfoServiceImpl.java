package cn.cheakin.gulimall.ware.service.impl;

import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;
import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.ware.dao.WareInfoDao;
import cn.cheakin.gulimall.ware.entity.WareInfoEntity;
import cn.cheakin.gulimall.ware.feign.MemberFeignService;
import cn.cheakin.gulimall.ware.service.WareInfoService;
import cn.cheakin.gulimall.ware.vo.FareVo;
import cn.cheakin.gulimall.ware.vo.MemberAddressVo;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wareInfoEntityQueryWrapper.eq("id",key).or()
                    .like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    /*@Override
    public BigDecimal getFare(Long addrId) {
        R info = memberFeignService.info(addrId);
        MemberAddressVo data = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            String phone = data.getPhone();
            //取电话号的最后两位作为邮费
            String fare = phone.substring(phone.length() - 2, phone.length());
            return new BigDecimal(fare);
        }
        return null;
    }*/

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R info = memberFeignService.info(addrId);
        if (info.getCode() == 0) {
            MemberAddressVo address = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
            });
            fareVo.setAddress(address);
            String phone = address.getPhone();
            //取电话号的最后两位作为邮费
            String fare = phone.substring(phone.length() - 2, phone.length());
            fareVo.setFare(new BigDecimal(fare));
        }
        return fareVo;
    }

}