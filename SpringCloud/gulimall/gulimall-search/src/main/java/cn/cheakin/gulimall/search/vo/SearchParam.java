package cn.cheakin.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * Create by botboy on 2022/09/08.
 **/
@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件：sort=[price/salecount/hotscore]_[desc/asc]
     */
    private String sort;

    /**
     * 过滤条件：是否显示有货, 0/1；为方便测试，就不默认1了
     */
    private Integer hasStock;

    /**
     * 过滤条件：价格区间查询, 1_500/_500/500_
     */
    private String skuPrice;

    /**
     * 品牌id,可以多选
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;

}