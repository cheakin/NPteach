package cn.cheakin.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create by botboy on 2022/07/31.
 **/
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
