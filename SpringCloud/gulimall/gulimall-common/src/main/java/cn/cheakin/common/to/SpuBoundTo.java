package cn.cheakin.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Create by botboy on 2022/07/31.
 **/
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
