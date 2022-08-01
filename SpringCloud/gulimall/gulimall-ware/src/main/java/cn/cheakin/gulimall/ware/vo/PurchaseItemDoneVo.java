package cn.cheakin.gulimall.ware.vo;

import lombok.Data;

/**
 * Create by botboy on 2022/08/02.
 **/
@Data
public class PurchaseItemDoneVo {

    //{itemId:1,status:4,reason:""}
    private Long itemId;
    private Integer status;
    private String reason;

}
