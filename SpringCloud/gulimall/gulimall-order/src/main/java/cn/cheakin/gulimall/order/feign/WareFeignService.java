package cn.cheakin.gulimall.order.feign;

import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("/ware/waresku/hasStock")
    R getSkuHasStocks(@RequestBody List<Long> ids);

    @RequestMapping("/ware/wareinfo/fare/{addrId}")
    R getFare(@PathVariable("addrId") Long addrId);

    @RequestMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo itemVos);
}