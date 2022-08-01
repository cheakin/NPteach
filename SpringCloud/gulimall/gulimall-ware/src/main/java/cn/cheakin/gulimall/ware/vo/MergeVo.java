package cn.cheakin.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * Create by botboy on 2022/08/01.
 **/
@Data
public class MergeVo {

    private Long purchaseId; //整单id
    private List<Long> items;//[1,2,3,4] //合并项集合

}
