package cn.cheakin.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Create by botboy on 2022/07/31.
 **/
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}