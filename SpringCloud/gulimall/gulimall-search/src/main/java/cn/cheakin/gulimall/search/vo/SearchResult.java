package cn.cheakin.gulimall.search.vo;

import cn.cheakin.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by botboy on 2022/09/08.
 **/
@Data
public class SearchResult {

    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Integer totalPages;

    private List<Integer> pageNavs;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVo> brands;

    /**
     * 当前查询到的结果，所有涉及到的所有分类
     */
    private List<CatalogVo> catalogs;

    /**
     * 当前查询到的结果，所有涉及到的所有属性
     */
    private List<AttrVo> attrs;


    //===========================以上是返回给页面的所有信息============================//


    /* 面包屑导航数据 */
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
