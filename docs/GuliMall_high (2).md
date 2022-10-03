### 商品详情
#### 环境搭建
host文件中添加域名映射
``` json
192.168.56.10 item.gulimall.com
```

nginx中前面已经是使用泛域名了，就不需要再配置了

`gateway`服务的`application.yml`中配置网关
``` yml
- id: mall_host_route 
    uri: lb://mall-product
    predicates:
      - Host=gulimall.com, item.gulimall.com
```

将课件中的`shangpinxiangqing.html`复制到`product`服务的静态文件目录下，并重命名为`item.html`，且替换内容，完整内容后面会贴出
* `href="`替换为`href="static/item/`
* `src="`替换为`src="static/item/`

静态文件放在 nginx 目录下(`/mydata/nginx/html/static/item/`)实现动静分离, 

`procut`中新建`ItemController`
``` java
@Controller
public class ItemController {

    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(Long skuId) {

        return "item";
    }

}
```

`search`服务的`list.html`修改跳转地址
```html
<p class="da">
    <a th:href="|http://item.gulimall.com/${product.skuId}.html|">
        <img th:src="${product.skuImg}" class="dim">
    </a>
</p>
```

#### 模型抽取 & 规格参数 & 销售属性组合
`ItemController`
``` java
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     *
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVo vos = skuInfoService.item(skuId);
        model.addAttribute("item", vos);
        return "item";
    }

}
```
`SkuItemVo`
``` java
@ToString
@Data
public class SkuItemVo {

    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;

        private String attrName;

        //private List<String> attrValues;
        private String attrValues;
    }

    @ToString
    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;

        //private List<SpuBaseAttrVo> attrs;
        private List<Attr> attrs;
    }

    /*@ToString
    @Data
    public static class SpuBaseAttrVo {
        private String attrName;

        private String attrValue;
    }*/

}
```
`SkuInfoServiceImpl`
``` java
@Autowired
SkuImagesService skuImagesService;
@Autowired
SpuInfoDescService spuInfoDescService;
@Autowired
AttrGroupService attrGroupService;
@Autowired
SkuSaleAttrValueService skuSaleAttrValueService;

@Override
public SkuItemVo item(Long skuId) {
    SkuItemVo skuItemVo = new SkuItemVo();

    //1、sku基本信息的获取  pms_sku_info
    SkuInfoEntity info = this.getById(skuId);
    skuItemVo.setInfo(info);
    Long spuId = info.getSpuId();
    Long catalogId = info.getCatalogId();

    //2、sku的图片信息    pms_sku_images
    List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
    skuItemVo.setImages(imagesEntities);

    //3、获取spu的销售属性组合
    List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(spuId);
    skuItemVo.setSaleAttr(saleAttrVos);

    //4、获取spu的介绍    pms_spu_info_desc
    SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
    skuItemVo.setDesc(spuInfoDescEntity);

    //5、获取spu的规格参数信息
    List<SkuItemVo.SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    skuItemVo.setGroupAttrs(attrGroupVos);

    return skuItemVo;
}
```

`SkuImagesServiceImpl`
``` java
@Override
public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
    return this.baseMapper.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
}
```

`AttrGroupServiceImpl`
``` java
@Override
public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
    //1、查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
    AttrGroupDao baseMapper = this.getBaseMapper();
    return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
}
```
`AttrGroupDao`
``` java
List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
```
`AttrGroupDao.xml`
``` xml
<!--返回集合里面元素的类型， 只要有嵌套属性就要封装自定义结果-->
<resultMap id="spuItemAttrGroupVo" type="cn.cheakin.gulimall.product.vo.SkuItemVo$SpuItemAttrGroupVo">
    <result property="groupName" column="attr_group_name"/>
    <collection property="attrs" ofType="cn.cheakin.gulimall.product.vo.Attr">
        <result property="attrId" column="attr_id"/>
        <result property="attrName" column="attr_name"/>
        <result property="attrValue" column="attr_value"/>
    </collection>
</resultMap>
<select id="getAttrGroupWithAttrsBySpuId" resultMap="spuItemAttrGroupVo">
    SELECT product.spu_id,
            pag.attr_group_id,
            pag.attr_group_name,
            product.attr_id,
            product.attr_name,
            product.attr_value
    FROM pms_product_attr_value product
              LEFT JOIN pms_attr_attrgroup_relation paar ON product.attr_id = paar.attr_id
              LEFT JOIN pms_attr_group pag ON paar.attr_group_id = pag.attr_group_id
    WHERE product.spu_id = #{spuId}
      AND pag.catelog_id = #{catalogId}
</select>
```
`GulimallProductApplicationTests`(单元测试), 获取spu的规格参数信息 
``` java
@Autowired
AttrGroupService attrGroupService;
@Test
public void testAttrGroupService() {
    List<SkuItemVo.SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(13L, 225L);
    System.out.println("attrGroupWithAttrsBySpuId = " + attrGroupWithAttrsBySpuId);
}
```

`SkuSaleAttrValueServiceImpl`
``` java
@Override
public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
    SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
    return baseMapper.getSaleAttrBySpuId(spuId);
}
```
`SkuSaleAttrValueDao`
``` java
List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);
```
`SkuSaleAttrValueDao.xml`
``` xml
<select id="getSaleAttrBySpuId" resultType="cn.cheakin.gulimall.product.vo.SkuItemVo$SkuItemSaleAttrVo">
    SELECT
        ssav.attr_id attr_id,
        ssav.attr_name attr_name,
        group_concat( DISTINCT ssav.attr_value ) attr_values
    FROM
        pms_sku_info info
            LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id = info.sku_id
    WHERE
        info.spu_id = #{spuId}
    GROUP BY
        ssav.attr_id,
        ssav.attr_name
</select>
```
`GulimallProductApplicationTests`(单元测试), 获取spu的销售属性组合
``` java
@Autowired
SkuSaleAttrValueService skuSaleAttrValueService;
@Test
public void testSkuSaleAttrValueService() {
    List<SkuItemVo.SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueService.getSaleAttrBySpuId(13L);
    System.out.println("saleAttrBySpuId = " + saleAttrBySpuId);
}
```

#### 详情页渲染 & 销售属性渲染
`SkuItemVo`
``` java
private boolean hasStock = true;
```

`SkuItemVo`
``` java
@Data
public static class SkuItemSaleAttrVo {
    private Long attrId;

    private String attrName;

    //private List<String> attrValues;
    //private String attrValues;
    private List<AttrValueWithSkuIdVO> attrValues;
}
```
`AttrValueWithSkuIdVO`
``` java
@Data
public class AttrValueWithSkuIdVO {

    private String attrValue;

    private String skuIds;

}
```
`SkuSaleAttrValueDao.xml`
``` xml
<resultMap id="skuItemSaleAttrVo" type="cn.cheakin.gulimall.product.vo.SkuItemVo$SkuItemSaleAttrVo">
    <result column="attr_id" property="attrId"></result>
    <result column="attr_name" property="attrName"></result>
    <collection property="attrValues" ofType="cn.cheakin.gulimall.product.vo.AttrValueWithSkuIdVO">
        <result column="attr_value" property="attrValue"></result>
        <result column="sku_ids" property="skuIds"></result>
    </collection>
</resultMap>
<select id="getSaleAttrBySpuId" resultMap="skuItemSaleAttrVo">
    SELECT
        ssav.attr_id attr_id,
        ssav.attr_name attr_name,
        ssav.attr_value,
        group_concat( DISTINCT info.sku_id ) sku_ids
    FROM
        pms_sku_info info
            LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id = info.sku_id
    WHERE
        info.spu_id = #{spuId}
    GROUP BY
        ssav.attr_id,
        ssav.attr_name,
        ssav.attr_value
</select>
```


#### sku组合切换

#### 异步编排优化

### 认证服务
### 购物车
### 消息队列
### 订单服务
### 分布式事务
### 订单服务
### 支付
### 订单服务
### 秒杀服务

# 谷粒商城-集群篇(cluster)