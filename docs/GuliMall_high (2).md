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

#### 详情页渲染 & 销售属性渲染 & sku组合切换
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

`list.html`
``` html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

	<head>
		<meta charset="UTF-8">
		<title></title>
		<link rel="stylesheet" type="text/css" href="static/item/scss/shop.css" />
		<link rel="stylesheet" type="text/css" href="static/item/scss/jd.css"/>
		<link rel="stylesheet" href="static/item/scss/header.css" />
		<link rel="stylesheet" type="text/css" href="static/item/bootstrap/css/bootstrap.css"/>
	</head>
	<body>
		<div id="max">
		<header>
			<!--品牌官方网站-->
					<div class="min">
						<ul class="header_ul_left">
							<li class="glyphicon glyphicon-home"> <a href="static/item/shouye.html" class="aa">京东首页</a></li>
							<li class="glyphicon glyphicon-map-marker"> <a href="static/item/javascript:;">北京</a>
								<ol id="beijing">
									<li style="background: red;"><a href="static/item/javascript:;" style="color: white;">北京</a></li>
									<li><a href="static/item/javascript:;">上海</a></li>
									<li><a href="static/item/javascript:;">天津</a></li>
									<li><a href="static/item/javascript:;">重庆</a></li>
									<li><a href="static/item/javascript:;">河北</a></li>
									<li><a href="static/item/javascript:;">山西</a></li>
									<li><a href="static/item/javascript:;">河南</a></li>
									<li><a href="static/item/javascript:;">辽宁</a></li>
									<li><a href="static/item/javascript:;">吉林</a></li>
									<li><a href="static/item/javascript:;">黑龙江</a></li>
									<li><a href="static/item/javascript:;">内蒙古</a></li>
									<li><a href="static/item/javascript:;">江苏</a></li>
									<li><a href="static/item/javascript:;">山东</a></li>
									<li><a href="static/item/javascript:;">安徽</a></li>
									<li><a href="static/item/javascript:;">浙江</a></li>
									<li><a href="static/item/javascript:;">福建</a></li>
									<li><a href="static/item/javascript:;">湖北</a></li>
									<li><a href="static/item/javascript:;">湖南</a></li>
									<li><a href="static/item/javascript:;">广东</a></li>
									<li><a href="static/item/javascript:;">广西</a></li>
									<li><a href="static/item/javascript:;">江西</a></li>
									<li><a href="static/item/javascript:;">四川</a></li>
									<li><a href="static/item/javascript:;">海南</a></li>
									<li><a href="static/item/javascript:;">贵州</a></li>
									<li><a href="static/item/javascript:;">云南</a></li>
									<li><a href="static/item/javascript:;">西藏</a></li>
									<li><a href="static/item/javascript:;">陕西</a></li>
									<li><a href="static/item/javascript:;">甘肃</a></li>
									<li><a href="static/item/javascript:;">青海</a></li>
									<li><a href="static/item/javascript:;">宁夏</a></li>
									<li><a href="static/item/javascript:;">新疆</a></li>
									<li><a href="static/item/javascript:;">港澳</a></li>
									<li><a href="static/item/javascript:;">台湾</a></li>
									<li><a href="static/item/javascript:;">钓鱼岛</a></li>
									<li><a href="static/item/javascript:;">海外</a></li>
								</ol>
							</li>
						</ul>
						<ul class="header_ul_right">
							<li style="border: 0;"><a href="static/item/../登录页面\index.html" class="aa">你好，请登录</a></li>
							<li><a href="static/item/../注册页面\index.html" style="color: red;">免费注册</a> |</li>
							<li><a href="static/item/javascript:;" class="aa">我的订单</a> |</li>
							<li class="jingdong"><a href="static/item/javascript:;">我的京东</a><span class="glyphicon glyphicon-menu-down">|</span>
								<ol class="jingdong_ol">
									<li><a href="static/item/javascript:;">待处理订单</a></li>
									<li><a href="static/item/javascript:;">消息</a></li>
									<li><a href="static/item/javascript:;">返修退换货</a></li>
									<li><a href="static/item/javascript:;">我的回答</a></li>
									<li><a href="static/item/javascript:;">降价商品</a></li>
									<li><a href="static/item/javascript:;">我的关注</a></li>
									<li style="width: 100%;height: 1px;background: lavender;margin-top: 15px;"></li>
									<li style="margin-top: 0;"><a href="static/item/javascript:;">我的京豆</a></li>
									<li style="margin-top: 0;"><a href="static/item/javascript:;">我的优惠券</a></li>
									<li style="margin-bottom: 10px;"><a href="static/item/javascript:;">我的白条</a></li>

								</ol>
							</li>

							<li><a href="static/item/javascript:;" class="aa">京东会员</a> |</li>
							<li><a href="static/item/javascript:;" class="aa">企业采购</a> |</li>
							<li class="fuwu"><a href="static/item/javascript:;">客户服务</a><span class="glyphicon glyphicon-menu-down"></span> |
								<ol class="fuwu_ol">
									<h6>客户</h6>
									<li><a href="static/item/javascript:;">帮助中心</a></li>
									<li><a href="static/item/javascript:;">售后服务</a></li>
									<li><a href="static/item/javascript:;">在线客服</a></li>
									<li><a href="static/item/javascript:;">意见建议</a></li>
									<li><a href="static/item/javascript:;">电话客服</a></li>
									<li><a href="static/item/javascript:;">客服邮箱</a></li>
									<li style="margin-bottom: -5px;"><a href="static/item/javascript:;">金融咨询</a></li>
									<li style="margin-bottom: -5px;"><a href="static/item/javascript:;">售全球客服</a></li>
									<h6 style="border-top: 1px dashed darkgray;height: 30px;line-height: 30px;">商户</h6>
									<li style="margin-top: -5px;"><a href="static/item/javascript:;">合作招商</a></li>
									<li style="margin-top: -5px;"><a href="static/item/javascript:;">学习中心</a></li>
									<li><a href="static/item/javascript:;">商家后台</a></li>
									<li><a href="static/item/javascript:;">京麦工作台</a></li>
									<li><a href="static/item/javascript:;">商家帮助</a></li>
									<li><a href="static/item/javascript:;">规则平台</a></li>
								</ol>
							</li>
							<li class="daohang"><a href="static/item/javascript:;">网站导航</a><span class="glyphicon glyphicon-menu-down"></span> |
								<ol class="daohang_ol">
									<li style="width: 34%;">
										<h5>特色主题</h5>
										<p>
											<a href="static/item/javascript:;">京东试用</a>
											<a href="static/item/javascript:;">京东金融</a>
											<a href="static/item/javascript:;">全球售</a>
											<a href="static/item/javascript:;">国际站</a>
										</p>
										<p>
											<a href="static/item/javascript:;">京东会员</a>
											<a href="static/item/javascript:;">京东预售</a>
											<a href="static/item/javascript:;">买什么</a>
											<a href="static/item/javascript:;">俄语站</a>
										</p>
										<p>
											<a href="static/item/javascript:;">装机大师</a>
											<a href="static/item/javascript:;">0元评测</a>
											<a href="static/item/javascript:;">定期送</a>
											<a href="static/item/javascript:;">港澳售</a>
										</p>
										<p>
											<a href="static/item/javascript:;">优惠券</a>
											<a href="static/item/javascript:;">秒杀</a>
											<a href="static/item/javascript:;">闪购</a>
											<a href="static/item/javascript:;">印尼站</a>
										</p>
										<p>
											<a href="static/item/javascript:;">京东金融科技</a>
											<a href="static/item/javascript:;">In货推荐</a>
											<a href="static/item/javascript:;">陪伴计划</a>
											<a href="static/item/javascript:;">出海招商</a>
										</p>
									</li>
									<li>
										<h5>行业频道</h5>
										<p>
											<a href="static/item/javascript:;" class="aa_2">手机</a>
											<a href="static/item/javascript:;" class="aa_2">智能数码</a>
											<a href="static/item/javascript:;" class="aa_2">玩3C</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">电脑办公</a>
											<a href="static/item/javascript:;" class="aa_2">家用电器</a>
											<a href="static/item/javascript:;" class="aa_2">京东智能</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">服装城</a>
											<a href="static/item/javascript:;" class="aa_2">美妆馆</a>
											<a href="static/item/javascript:;" class="aa_2">家装城</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">母婴</a>
											<a href="static/item/javascript:;" class="aa_2">食品</a>
											<a href="static/item/javascript:;" class="aa_2">运动户外</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">农资频道</a>
											<a href="static/item/javascript:;" class="aa_2">整车</a>
											<a href="static/item/javascript:;" class="aa_2">图书</a>
										</p>
									</li>
									<li>
										<h5>生活服务</h5>
										<p>
											<a href="static/item/javascript:;" class="aa_2">京东众筹</a>
											<a href="static/item/javascript:;" class="aa_2">白条</a>
											<a href="static/item/javascript:;" class="aa_2">京东金融APP</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">京东小金库</a>
											<a href="static/item/javascript:;" class="aa_2">理财</a>
											<a href="static/item/javascript:;" class="aa_2">智能家电</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">话费</a>
											<a href="static/item/javascript:;" class="aa_2">水电煤</a>
											<a href="static/item/javascript:;" class="aa_2">彩票</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">旅行</a>
											<a href="static/item/javascript:;" class="aa_2">机票酒店</a>
											<a href="static/item/javascript:;" class="aa_2">电影票</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">京东到家</a>
											<a href="static/item/javascript:;" class="aa_2">京东众测</a>
											<a href="static/item/javascript:;" class="aa_2">游戏</a>
										</p>
									</li>
									<li style="border: 0;">
										<h5>更多精选</h5>
										<p>
											<a href="static/item/javascript:;" class="aa_2">合作招商</a>
											<a href="static/item/javascript:;" class="aa_2">京东通信</a>
											<a href="static/item/javascript:;" class="aa_2">京东E卡</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">企业采购</a>
											<a href="static/item/javascript:;" class="aa_2">服务市场</a>
											<a href="static/item/javascript:;" class="aa_2">办公生活馆</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">乡村招募</a>
											<a href="static/item/javascript:;" class="aa_2">校园加盟</a>
											<a href="static/item/javascript:;" class="aa_2">京友邦</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">京东社区</a>
											<a href="static/item/javascript:;" class="aa_2">智能社区</a>
											<a href="static/item/javascript:;" class="aa_2">游戏社区</a>
										</p>
										<p>
											<a href="static/item/javascript:;" class="aa_2">知识产权维权</a>
											<a href="static/item/javascript:;" class="aa_2"></a>
											<a href="static/item/javascript:;" class="aa_2"></a>
										</p>
									</li>
								</ol>
							</li>
							<li class="sjjd" style="border: 0;"><a href="static/item/javascript:;" class="aa">手机京东</a>
								<div class="er">
									<div class="er_1">
										<div class="er_1_1">
											<h6><a href="static/item/#">手机京东</a></h6>
											<p>新人专享大礼包</p>

										</div>
									</div>
									<div class="er_1">
										<div class="er_1_1">
											<h6><a href="static/item/#">关注京东微信</a></h6>
											<p>微信扫一扫关注新粉最高180元惊喜礼包</p>
										</div>
									</div>
									<!--我的理财-->
									<div class="er_1" style="border: 0;">
										<img src="static/item/img/5874a555Ne8192324.jpg"/>
										<div class="er_1_1">
											<h6><a href="static/item/#">京东金融客户端</a></h6>
											<p>新人专享大礼包</p>
											<div><a href="static/item/#"><img src="static/item/img/11.png"/></a><a href="static/item/#"><img src="static/item/img/22.png"/></a></div>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
				</header>
				<nav>
				<div class="nav_min">
					<div class="nav_top">
						<div class="nav_top_one"><a href="static/item/#"><img src="static/item/img/111.png"/></a></div>
						<div class="nav_top_two"><input type="text"/><button>搜索</button></div>
						<div class="nav_top_three"><a href="static/item/../JD_Shop/One_JDshop.html">我的购物车</a><span class="glyphicon glyphicon-shopping-cart"></span>
							<div class="nav_top_three_1">
								<img src="static/item/img/44.png"/>购物车还没有商品，赶紧选购吧！
							</div>
						</div>
					</div>
					<div class="nav_down">
						<ul class="nav_down_ul">
							<li class="nav_down_ul_1" style="width: 24%;float: left;"><a href="static/item/javascript:;">全部商品分类</a>

							</li>
							<li class="ul_li"><a href="static/item/javascript:;">服装城</a></li>
							<li class="ul_li"><a href="static/item/javascript:;">美妆馆</a></li>
							<li class="ul_li"><a href="static/item/javascript:;">超市</a></li>
							<li class="ul_li" style="border-right: 1px solid lavender;"><a href="static/item/javascript:;">生鲜</a></li>
							<li class="ul_li"><a href="static/item/javascript:;">全球购</a></li>
							<li class="ul_li"><a href="static/item/javascript:;">闪购</a></li>
							<li class="ul_li" style="border-right: 1px solid lavender;"><a href="static/item/javascript:;">拍卖</a></li>
							<li class="ul_li"><a href="static/item/javascript:;">金融</a></li>
						</ul>
					</div>
				</div>
			</nav>

				</div>


			<div class="crumb-wrap">
			<div class="w">
				<div class="crumb">
					<div class="crumb-item">
						<a href="static/item/">手机</a>
					</div>
					<div class="crumb-item sep">></div>
					<div class="crumb-item">
						<a href="static/item/">手机通讯</a>
					</div>
					<div class="crumb-item sep">></div>
					<div class="crumb-item">
						<a href="static/item/">手机</a>
					</div>
					<div class="crumb-item sep">></div>
					<div class="crumb-item">
						<div class="crumb-item-one">
							华为 (HUAWEI)
							<img src="static/item/img/4a79b87a68623d4e8a73aff3e25fa99b.png" alt="" class="img" />
							<div class="crumb-item-two ">
								<div class="crumb-item-con clear">
									<ul class="con-ul">
										<li>
											<img src="static/item/img/5825a5a6Nde8ecb75.jpg" alt="" />
										</li>
										<li>
											<p>
												荣耀8青春版 全网通标配 3GB+32GB 幻海蓝
											</p>
											<p>
												￥1099.00
											</p>
										</li>
									</ul>
									<ul class="con-ul">
										<li>
											<img src="static/item/img/5919637aN271a1301.jpg" alt="" />
										</li>
										<li>
											<p>
												荣耀8青春版 全网通标配 3GB+32GB 幻海蓝
											</p>
											<p>
												￥1099.00
											</p>
										</li>
									</ul>
									<ul class="con-ul">
										<li>
											<img src="static/item/img/599a806bN9d829c1c.jpg" alt="" />
										</li>
										<li>
											<p>
												荣耀8青春版 全网通标配 3GB+32GB 幻海蓝
											</p>
											<p>
												￥1099.00
											</p>
										</li>
									</ul>
								</div>
								<div class="crumb-item-cons clear">
									<ul>
										<li>华为(huawei)</li>
										<li>小米(xiaomi)</li>
										<li>APPle</li>
										<li>魅族(meizu)</li>
										<li>锤子</li>

									</ul>
									<ul>
										<li>三星</li>
										<li>vivo</li>
										<li>飞利浦</li>
										<li>360</li>
										<li>更多>></li>

									</ul>
								</div>
							</div>

						</div>

					</div>
					<div class="crumb-item sep">></div>
					<div class="crumb-item">
						华为Mate 10
					</div>
				</div>

				<div class="contact">
					<ul class="contact-ul">
						<li>
							<a href="static/item/#">
								华为京东自营官方旗舰店
							</a>

							<span class="contact-sp">
								<span class="contact-sp1">
								JD
							</span>
							<span class="contact-sp2">
								自营
							    </span>
							</span>
						</li>
						<li>
							<a href="static/item/#">
								<img src="static/item/img/f5831b9848b32440b381bcd30a3d96c7.png" alt="" /> 联系供应商
							</a>
						</li>
						<li>
							<a href="static/item/#">
								<img src="static/item/img/81a6326edc82d343a5a8860a6970f93b.png" alt="" /> JIMI
							</a>
						</li>
						<li>
							<a href="static/item/#">
								<img src="static/item/img/a400e3d61d5645459f769b00d9f431e7.png" alt="" /> 关注店铺
							</a>
						</li>
					</ul>
					<div class="contact-one">
						<ul>
							<li>客服</li>
							<li><img src="static/item/img/f5831b9848b32440b381bcd30a3d96c7.png" alt="" />留言</li>
							<li><img src="static/item/img/81a6326edc82d343a5a8860a6970f93b.png" alt="" />JIMI智能</li>
							<li>
								<img src="static/item/img/1466134037230.jpg" class="contact-img" />
								<p>手机下单</p>
							</li>

						</ul>
						<div class="contact-two">
							<span><img src="static/item/img/a400e3d61d5645459f769b00d9f431e7.png" alt="" />进店逛逛</span>
							<span><img src="static/item/img/a400e3d61d5645459f769b00d9f431e7.png" alt="" />关注店铺</span>
						</div>
					</div>
				</div>

			</div>
		</div>
<div class="Shop">
		<div class="box">
			<div class="box-one ">
				<div class="boxx">

					<div class="imgbox">
						<div class="probox">
							<img class="img1" alt="" th:src="${item.info.skuDefaultImg}">
							<div class="hoverbox"></div>
						</div>
						<div class="showbox">
							<img class="img1" alt="" th:src="${item.info.skuDefaultImg}">
						</div>
					</div>

					<div class="box-lh">

						<div class="box-lh-one">
							<ul>
								<li th:each="img : ${item.images}" th:if="${!#strings.isEmpty(img.imgUrl)}"><img th:src="${img.imgUrl}" /></li>
							</ul>
						</div>
						<div id="left">
							< </div>
								<div id="right">
									>
								</div>

						</div>

						<div class="boxx-one">
							<ul>
								<li>
									<span>
										<img src="static/item/img/b769782fe4ecca40913ad375a71cb92d.png" alt="" />关注
									</span>
									<span>
										<img src="static/item/img/9224fcea62bfff479a6712ba3a6b47cc.png" alt="" />
										对比
									</span>
								</li>
								<li>

								</li>
							</ul>
						</div>

					</div>
					<div class="box-two">
						<div class="box-name" th:text="${item.info.skuTitle}">
							华为 HUAWEI Mate 10 6GB+128GB 亮黑色 移动联通电信4G手机 双卡双待
						</div>
						<div class="box-hide" th:text="${item.info.skuSubtitle}">预订用户预计11月30日左右陆续发货！麒麟970芯片！AI智能拍照！
							<a href="static/item/"><u></u></a>
						</div>
						<div class="box-yuyue">
							<div class="yuyue-one">
								<img src="static/item/img/7270ffc3baecdd448958f9f5e69cf60f.png" alt="" /> 预约抢购
							</div>
							<div class="yuyue-two">
								<ul>
									<li>
										<img src="static/item/img/f64963b63d6e5849977ddd6afddc1db5.png" />
										<span>190103</span> 人预约
									</li>
									<li>
										<img src="static/item/img/36860afb69afa241beeb33ae86678093.png" /> 预约剩余
										<span id="timer">

									</span>
									</li>
								</ul>
							</div>
						</div>
						<div class="box-summary clear">
							<ul>
								<li>京东价</li>
								<li>
									<span>￥</span>
									<span th:text="${#numbers.formatDecimal(item.info.price,3,2)}">4499.00</span>
								</li>
								<li>
									预约享资格
								</li>
								<li>
									<a href="static/item/">
										预约说明
									</a>
								</li>
							</ul>
						</div>
						<div class="box-wrap clear">
							<div class="box-wrap-one clear">
								<ul>
									<li>增值业务</li>
									<li><img src="static/item/img/90a6fa41d0d46b4fb0ff6907ca17c478.png" /></li>
									<li><img src="static/item/img/2e19336b961586468ef36dc9f7199d4f.png" /></li>
									<li><img src="static/item/img/1f80c3d6fabfd3418e54b005312c00b5.png" /></li>
								</ul>
							</div>
						</div>

						<div class="box-stock">
							<ul class="box-ul">
								<li>配送至</li>
								<li class="box-stock-li">
									<div class="box-stock-one">
										北京朝阳区管庄
										<img src="static/item/img/4a79b87a68623d4e8a73aff3e25fa99b.png" alt="" class="img" />
									</div>
									<div class="box-stock-two">
										<dl>
											<dt>
												<a>选择新地址</a>
												<img src="static/item/img/4a79b87a68623d4e8a73aff3e25fa99b.png" alt="" class="box-stock-two-img"/>
											</dt>
											<dd>
												<div class="box-stock-dd">
													<div class="box-stock-top">
														<div class="box-stock-div">北京</div>
														<div class="box-stock-div">朝阳区</div>
														<div class="box-stock-div">管庄</div>
													</div>
													<div class="box-stock-fot">
														<div class="box-stock-con" style="display: block;">
															<ul>
																<li>北京</li>
																<li>上海</li>
																<li>天津</li>
																<li>重庆</li>
															</ul>
														</div>
														<div class="box-stock-con">
															<ul>
																<li>朝阳区</li>
																<li>海淀区</li>
																<li>东城区</li>
																<li>西城区</li>
															</ul>
														</div>
														<div class="box-stock-con">
															<ul>
																<li>4环到5环之内</li>
																<li>管庄</li>
																<li>北苑</li>
															</ul>
														</div>

													</div>
												</div>
											</dd>
										</dl>

									</div>

								</li>
								<li>
									<span th:text="${item.hasStock?'有货':'无货'}">无货</span>， 此商品暂时售完
								</li>
							</ul>
						</div>
						<div class="box-supply">
							<ul class="box-ul">
								<li></li>
								<li>
									由<span>京东</span> 发货，并提供售后服务
								</li>
							</ul>
						</div>
						<div class="box-attr-3">
							<div class="box-attr clear" th:each="attr : ${item.saleAttr}">
								<dl>
									<dt>选择[[${attr.attrName}]]</dt>
									<dd th:each="val : ${attr.attrValues}">
										<a th:attr=" class=${#lists.contains(#strings.listSplit(val.skuIds,','),item.info.skuId.toString()) ? 'sku_attr_value checked': 'sku_attr_value'}, skus=${val.skuIds} ">
											[[${val.attrValue}]]
											<!--<img src="static/item/img/59ddfcb1Nc3edb8f1.jpg" /> 摩卡金-->
										</a>
									</dd>
								</dl>
							</div>
						</div>

						<div class="box-btns clear">
							<div class="box-btns-one">
								<input type="text" name="" id="" value="1" />
								<div class="box-btns-one1">

									<div>
										<button id="jia">
									+
									</button>
									</div>
									<div>
										<button id="jian">
										-
									</button>
									</div>

								</div>
							</div>
							<div class="box-btns-two">
								<a href="static/item/../商品分类\index.html">
									立即预约
								</a>
							</div>
							<div class="box-btns-three">
								<img src="static/item/img/e4ed3606843f664591ff1f68f7fda12d.png" alt="" /> 分享
							</div>
						</div>

						<div class="box-footer-zo">
							<div class="box-footer clear">
								<dl>
									<dt>本地活动</dt>
									<dd>
										<a href="static/item/">
											·1元500MB激活到账30元 >>
										</a>
									</dd>
								</dl>
							</div>

							<div class="box-footer">
								<dl>
									<dt>温馨提示</dt>
									<dd>·本商品不能使用 东券 京券</dd>
									<dd>·请完成预约后及时抢购！</dd>
								</dl>
							</div>
						</div>
					</div>

				</div>
			</div>
			<!--欲约抢购流程-->
			<div class="qianggoulioucheng">
				<div class="lioucheng">
					<h3>欲约抢购流程</h3>
				</div>
				<!--抢购步骤-->
				<ul class="qianggoubuzhao">
					<li>
						<img src="static/item/img/shop_03.png" />
						<dl class="buzhou">
							<dt>1.等待预约</dt>
							<dl>预约即将开始</dl>
						</dl>
					</li>
					<li>
						<img src="static/item/img/shop_04.png" />
						<dl class="buzhou">
							<dt>2.预约中</dt>
							<dl>2017-11-15 10:35 2017-11-15 23:59</dl>
						</dl>
					</li>
					<li>
						<img src="static/item/img/shop_05.png" />
						<dl class="buzhou">
							<dt>3.等待抢购</dt>
							<dl>抢购即将开始</dl>
						</dl>
					</li>
					<li>
						<img src="static/item/img/shop_06.png" />
						<dl class="buzhou">
							<dt>4.抢购中</dt>
							<dl></dl>
						</dl>
					</li>
				</ul>
			</div>

			<div class="ShopXiangqing">
				<div class="allLeft">
					<!--火热预约-->
					<div class="huoreyuyue">
						<h3>火热预约</h3>
					</div>
					<div class="dangeshopxingqing">
						<ul class="shopdange">
							<li>
								<a href="static/item/##"><img src="static/item/img/5a0afeddNb34732af.jpg" /></a>
								<p>
									<a href="static/item/##">OPPO R11s Plus 双卡双待全面屏拍照手机香槟色 全网通(6G RAM+64G ROM)标配</a>
								</p>
								<p><strong class="J-p-20015341974">￥3699.00</strong></p>
							</li>
							<li>
								<a href="static/item/##"><img src="static/item/img/5a12873eN41754123.jpg" /></a>
								<p>
									<a target="_blank" title="詹姆士（GEMRY） R19plus全网通4G 智能手机 双卡双待 6+128GB 鳄鱼纹雅致版（新品预约）" href="static/item///item.jd.com/20348283521.html">詹姆士（GEMRY） R19plus全网通4G 智能手机 双卡双待 6+128GB 鳄鱼纹雅致版（新品预约）</a>
								</p>
								<p><strong class="J-p-20348283521">￥13999.00</strong></p>
							</li>
							<li>
								<a href="static/item/##"><img src="static/item/img/59ec0131Nf239df75.jpg" /></a>
								<p>
									<a target="_blank" title="斐纳（TOMEFON） 德国家用无线无绳手持立式充电吸尘器 静音大吸力吸尘器TF-X60" href="static/item///item.jd.com/16683419775.html">斐纳（TOMEFON） 德国家用无线无绳手持立式充电吸尘器 静音大吸力吸尘器TF-X60</a>
								</p>
								<p><strong class="J-p-16683419775">￥1599.00</strong></p>
							</li>
							<li>
								<a href="static/item/##"><img src="static/item/img/59015444N27317512.jpg" /></a>
								<p>
									<a target="_blank" title="斐纳（TOMEFON） 扫地机器人德国智能导航规划全自动超薄扫地机器人吸尘器TF-D60 香槟金" href="static/item///item.jd.com/12187770381.html">斐纳（TOMEFON） 扫地机器人德国智能导航规划全自动超薄扫地机器人吸尘器TF-D60 香槟金</a>
								</p>
								<p><strong class="J-p-12187770381">￥2599.00</strong></p>
							</li>
						</ul>
					</div>
					<!--看了又看-->
					<div class="huoreyuyue">
						<h3>看了又看</h3>
					</div>
					<div class="dangeshopxingqing">
						<ul class="shopdange">
							<li>
								<a href="static/item/##"><img src="static/item/img/59e55e01N369f98f2.jpg" /></a>
								<p>
									<a target="_blank" title="华为（HUAWEI） 华为 Mate10 4G手机  双卡双待 亮黑色 全网通(6GB RAM+128GB ROM)" href="static/item///item.jd.com/17931625443.html">华为（HUAWEI） 华为 Mate10 4G手机 双卡双待 亮黑色 全网通(6GB RAM+128GB ROM)</a>
									<p><strong class="J-p-17931625443">￥4766.00</strong></p>
							</li>
							<li>
								<a href="static/item/##"><img src="static/item/img/584fcc3eNdb0ab94c.jpg" /></a>
								<p>
									<a target="_blank" title="华为 Mate 9 Pro 6GB+128GB版 琥珀金 移动联通电信4G手机 双卡双待" href="static/item///item.jd.com/3749093.html">华为 Mate 9 Pro 6GB+128GB版 琥珀金 移动联通电信4G手机 双卡双待</a>
								</p>
								<p><strong class="J-p-3749093">￥4899.00</strong></p>
							</li>
							<li>
								<!--shopjieshao-->
								<a href="static/item/##"><img src="static/item/img/59eb0df9Nd66d7585.jpg" /></a>
								<p>
									<a target="_blank" title="华为（HUAWEI） 华为 Mate10 手机 亮黑色 全网通(4+64G)标准版" href="static/item///item.jd.com/12306211773.html">华为（HUAWEI） 华为 Mate10 手机 亮黑色 全网通(4+64G)标准版</a>
								</p>
								<p><strong class="J-p-12306211773">￥4088.00</strong></p>
							</li>
							<li>
								<a href="static/item/##"><img src="static/item/img/5a002ba3N126c2f73.jpg" /></a>
								<p>
									<a target="_blank" title="斐纳（TOMEFON） 扫地机器人德国智能导航规划全自动超薄扫地机器人吸尘器TF-D60 香槟金" href="static/item///item.jd.com/12187770381.html">斐纳（TOMEFON） 扫地机器人德国智能导航规划全自动超薄扫地机器人吸尘器TF-D60 香槟金</a>
								</p>
								<p><strong class="J-p-12187770381">￥2599.00</strong></p>
							</li>
						</ul>
						<img src="static/item/img/5a084a1aNa1aa0a71.jpg" />
					</div>
				</div>
				<!--商品介绍-->
				<div class="allquanbushop">
					<ul class="shopjieshao">
						<li class="jieshoa" style="background: #e4393c;">
							<a href="static/item/##" style="color: white;">商品介绍</a>
						</li>
						<li class="baozhuang">
							<a >规格与包装</a>
						</li>
						<li class="baozhang">
							<a >售后保障</a>
						</li>
						<li class="pingjia">
							<a href="static/item/##">商品评价(4万+)</a>
						</li>
						<li class="shuoming">
							<a href="static/item/##">预约说明</a>
						</li>

					</ul>
					<button class="Lijiyuyuessss">
							立即预约
						</button>
					<ul class="shopjieshaos posi" style="display: none;">
						<li class="jieshoa" style="background: #e4393c;">
							<a href="static/item/#li1" style="color: white;">商品介绍</a>
						</li>
						<li class="baozhuang">
							<a href="static/item/#li2">规格与包装</a>
						</li>
						<li class="baozhang">
							<a href="static/item/#li3">售后保障</a>
						</li>
						<li class="pingjia">
							<a href="static/item/#li4">商品评价(4万+)</a>
						</li>
						<li class="shuoming">
							<a href="static/item/#li5">预约说明</a>
						</li>
					</ul>

					<!--商品详情-->
					<div class="huawei">
						<ul class="xuanxiangka">
								<li class="jieshoa actives" id="li1">
								<div class="shanpinsssss">
									<!--<p>
										<a href="static/item/#">品牌:华为（HUAWEI）</a>
									</p>
									<table>
										<tr>
											<td>
												<a href="static/item/##">商品名称：华为Mate 10</a>
											</td>
											<td>
												<a href="static/item/##">商品毛重：0.58kg</a>
											</td>
											<td>
												<a href="static/item/##">商品编号：5544038</a>
											</td>
											<td>
												<a href="static/item/##">商品产地：中国大陆</a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="static/item/##">系统：安卓（Android）</a>
											</td>
											<td>
												<a href="static/item/##">前置摄像头像素：800万-1599万</a>
											</td>
											<td>
												<a href="static/item/##">后置摄像头像素：2000万及以上，1200万-1999万</a>
											</td>
											<td>
												<a href="static/item/##">机身内存：128GB</a>
											</td>
										</tr>
										<tr>
											<td colspan="4">
												<a href="static/item/##">全面屏，双卡双待，指纹识别，Type-C，VoLTE，2K屏，拍照神器，支持NFC，商务手机，安全手机，分辨率10</a>
											</td>
										</tr>
									</table>-->
									<img class="xiaoguo" th:src="${descp}" th:each="descp : ${#strings.listSplit(item.desc.decript,',')}"/>
									<!--<div class="guiGebox guiGebox1">
										<div class="guiGe" th:each="group : ${item.groupAttrs}">
											<h3 th:text="${group.groupName}">主体</h3>
											<dl>
												<dt th:each="attr : ${group.attrs}">品牌</dt>
												<dd>华为(HUAWEI)</dd>
												<dt>型号</dt>
												<dd class="Ptable-tips">
													<a href="static/item/#"><i>？</i></a>
												</dd>
												<dd>ALP-AL00</dd>
												<dt>入网型号</dt>
												<dd class="Ptable-tips">
													<a href="static/item/#"><i>？</i></a>
												</dd>
												<dd>ALP-AL00</dd>
												<dt>上市年份</dt>
												<dd>2017年</dd>
												<dt>上市时间</dt>
												<dd>10月</dd>
											</dl>
										</div>
										<div class="package-list">
											<h3>包装清单</h3>
											<p>手机（含内置电池） X 1、5A大电流华为SuperCharge充电器X 1、5A USB数据线 X 1、半入耳式线控耳机 X 1、快速指南X 1、三包凭证 X 1、取卡针 X 1、保护壳 X 1</p>
										</div>
									</div>-->
								</div>
							</li>
							<li class="baozhuang actives" id="li2">
								<div class="guiGebox">
									<div class="guiGe" th:each="group : ${item.groupAttrs}">
										<h3 th:text="${group.groupName}">主体</h3>
										<dl>
											<div th:each="attr : ${group.attrs}">
												<dt th:text="${attr.attrName}">品牌</dt>
												<dd th:text="${attr.attrValue}">华为(HUAWEI)</dd>
											</div>
										</dl>
									</div>
									<div class="package-list">
										<h3>包装清单</h3>
										<p>手机（含内置电池） X 1、5A大电流华为SuperCharge充电器X 1、5A USB数据线 X 1、半入耳式线控耳机 X 1、快速指南X 1、三包凭证 X 1、取卡针 X 1、保护壳 X 1</p>
									</div>
								</div>
							</li>
							<!--包装-->
							<li class="baozhang actives" id="li3">
								<div class="oBox">
		<div class="shuoHou">
			<div class="baoZhang">
				<h2>售后保障</h2>
			</div>
		</div>
		<!--厂家服务-->
		<div class="changJia">
			<div class="lianBao">
				<span class="oImg">
					<img src="static/item/img/2017.jpg" alt="" />
					<h3>厂家服务</h3>
				</span>
				<div class="wenZi">
					本产品全国联保，享受三包服务，质保期为：一年保<br />
					如因质量问题或故障，凭厂商维修中心或特约维修点的质量检测证明，享受7日内退货，15日内换货，15日以上在保质期内享受免费保修等安保服务！<br />
					(注：如厂家在商品介绍中有售后保障的说明，则此商品按照厂家说明执行售后保障服务。)您可以查询本品牌在各地售后服务中心的练习方式<a href="static/item/#">请点击这儿查询...</a><br /><br />
				</div>
			</div>
			<div class="lianBao oCn">
				<span class="oImg">
					<img src="static/item/img/2017.jpg" alt="" />
					<h3>京东承诺</h3>
				</span>
				<div class="wenZi">
					本产品全国联保，享受三包服务，质保期为：一年保<br />
					如因质量问题或故障，凭厂商维修中心或特约维修点的质量检测证明，享受7日内退货，15日内换货，15日以上在保质期内享受免费保修等安保服务！<br />
					(注：如厂家在商品介绍中有售后保障的说明，则此商品按照厂家说明执行售后保障服务。)您可以查询本品牌在各地售后服务中心的练习方式<a href="static/item/#">请点击这儿查询...</a><br /><br /><br />
				</div>
			</div>

			<div class="lianBao ">
				<span class="oImg">
					<img src="static/item/img/2017.jpg" alt="" />
					<h3>正品行货</h3>
				</span>
				<div class="wenZi hangHuo">
					京东商城向您保证所售商品均为正品行货，京东自营商品开具机打发票或电子发票。
				</div>
			</div>
			<div class="lianBao quanGuo">
				<span class="oImg">
					<img src="static/item/img/2017-1.jpg" alt="" />
					<h3>全国联保</h3>
				</span>
				<div class="wenZi">
					凭质保证书及京东商城发票，可享受全国联保服务（奢侈品、钟表除外；奢侈品、钟表由京东联系保修，享受法定三包售后服务），与您亲临商场选购的商品享受相同的质量保证。京东商城还为您提供具有竞争力的商品价格和运费政策，请您放心购买！ <br />

注：因厂家会在没有任何提前通知的情况下更改产品包装、产地或者一些附件，本司不能确保客户收到的货物与商城图片、产地、附件说明完全一致。只能确保为原厂正货！并且保证与当时市场上同样主流新品一致。若本商城没有及时更新，请大家谅解！
				</div>
			</div>
				<!--权利声明-->
			<div class="quanLi">
				<h4>权利声明:</h4>
				<div class="jingDong">
					京东上的所有商品信息、客户评价、商品咨询、网友讨论等内容，是京东重要的经营资源，未经许可，禁止非法转载使用。<br />
	<span class="oZhu">注</span>：本站商品信息均来自于合作方，其真实性、准确性和合法性由信息拥有者（合作方）负责。本站不提供任何保证，并不承担任何法律责任。
				</div>
			</div>
			<div class="quanLi jiaGe">
				<h4>价格说明:</h4>
				<div class="jingDong">
					<span class="oZhu">京东价</span>：京东价为商品的销售价，是您最终决定是否购买商品的依据。<br />
					<span class="oZhu">划线价</span>：商品展示的划横线价格为参考价，该价格可能是品牌专柜标价、商品吊牌价或由品牌供应商提供的正品零售价（如厂商指导价、建议零售价等）或该商品在京东平台上曾经展示过的销售价；由于地区、时间的差异性和市场行情波动，品牌专柜标价、商品吊牌价等可能会与您购物时展示的不一致，该价格仅供您参考。<br />
					<span class="oZhu">折扣</span>：如无特殊说明，折扣指销售商在原价、或划线价（如品牌专柜标价、商品吊牌价、厂商指导价、厂商建议零售价）等某一价格基础上计算出的优惠比例或优惠金额；如有疑问，您可在购买前联系销售商进行咨询。<br />
					<span class="oZhu">异常问题</span>：商品促销信息以商品详情页“促销”栏中的信息为准；商品的具体售价以订单结算页价格为准；如您发现活动商品售价或促销信息有异常，建议购买前先联系销售商咨询。
				</div>
			</div>
		</div>


	</div>
							</li>
							<!--评价-->
							<li class="PINgjia actives" id="li4">
								<div class="h3">
									<h3>商品评价</h3>
								</div>
								<div class="nav">
									<div class="left">
										<p class="haoping">好评度</p>
										<p><span>100%</span></p>
									</div>
									<div class="right">
										<ul>
											<li>
												<a href="static/item/##">就是快（424）</a>
											</li>
											<li>
												<a href="static/item/##">物流很快（254） </a>
											</li>
											<li>
												<a href="static/item/##">货真价实（168）</a>
											</li>
											<li>
												<a href="static/item/##">有档次（158）</a>
											</li>
											<li>
												<a href="static/item/##">国产品牌（133）</a>
											</li>
											<li>
												<a href="static/item/##">外形美观（103）</a>
											</li>
											<li>
												<a href="static/item/##">很给力（75）</a>
											</li>
											<li>
												<a href="static/item/##">反应灵敏（68）</a>
											</li>
											<li>
												<a href="static/item/##">性价比高（60）</a>
											</li>
											<li>
												<a href="static/item/##">价格优惠（50）</a>
											</li>
											<li>
												<a href="static/item/##">功能齐全（49）</a>
											</li>
											<li style="background: gainsboro;">
												<a href="static/item/##">速度太慢（5）</a>
										</ul>
									</div>
								</div>
								<!--全部评价-->
								<div class="allpingjia">
									<ul>
										<li><a href="static/item/##">全部评价（4.2万）</a></li>
										<li><a href="static/item/##">晒图（500）</a></li>
										<li><a href="static/item/##">追拼（200+）</a></li>
										<li><a href="static/item/##">好评（4.1万）</a></li>
										<li><a href="static/item/##">中评（100+）</a></li>
										<li><a href="static/item/##">差评（100+）</a></li>
										<li><a href="static/item/##"><input type="checkbox"/>只看当前商品价格</a></li>
										<li class="imga" style="float: right;"><a href="static/item/##">推荐排序 <img src="static/item/img/animaite.png"/> </a>
										</li>
									</ul>
								</div>
								</li>
								<li class="shuoming actives" id="li5"></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<div class="headera">
			<div class="Logo-tu">
				<span><img src="static/item/img/service_items_1.png"/></span>
				<span><img src="static/item/img/service_items_2.png"/></span>
				<span><img src="static/item/img/service_items_3.png"/></span>
				<span><img src="static/item/img/service_items_4.png"/></span>
			</div>
			<div class="table">
				<dl>
					<dt><a href="static/item/##">购物指南</a></dt>
					<dd>
						<a href="static/item/##">购物流程</a>
					</dd>
					<dd>
						<a href="static/item/##">会员介绍</a>
					</dd>
					<dd>
						<a href="static/item/##">生活旅行/团购</a>
					</dd>
					<dd>
						<a href="static/item/##">常见问题</a>
					</dd>
					<dd>
						<a href="static/item/##">大家电</a>
					</dd>
					<dd>
						<a href="static/item/##">练习客服</a>
					</dd>
				</dl>
				<dl>
					<dt><a href="static/item/##">配送方式</a></dt>
					<dd>
						<a href="static/item/##">上门自提</a>
					</dd>
					<dd>
						<a href="static/item/##">211限时达</a>
					</dd>
					<dd>
						<a href="static/item/##">配送服务查询</a>
					</dd>
					<dd>
						<a href="static/item/##"></a>
					</dd>
					<dd>
						<a href="static/item/##">海外配送</a>
					</dd>
					<dd></dd>
				</dl>
				<dl>
					<dt><a href="static/item/##">支付方式</a></dt>
					<dd>
						<a href="static/item/##">货到付款</a>
					</dd>
					<dd>
						<a href="static/item/##">在线支付</a>
					</dd>
					<dd>
						<a href="static/item/##">分期付款</a>
					</dd>
					<dd>
						<a href="static/item/##">邮局汇款</a>
					</dd>
					<dd>
						<a href="static/item/##">公司转账</a>
					</dd>
					<dd></dd>
				</dl>
				<dl>
					<dt><a href="static/item/##">售后服务</a></dt>
					<dd>
						<a href="static/item/##">售后政策</a>
					</dd>
					<dd>
						<a href="static/item/##">价格保护</a>
					</dd>
					<dd>
						<a href="static/item/##">退款说明</a>
					</dd>
					<dd>
						<a href="static/item/##">返修/退换货</a>
					</dd>
					<dd>
						<a href="static/item/##">取消订单</a>
					</dd>
					<dd></dd>
				</dl>
				<dl class="dls">
					<dt><a href="static/item/##">特色服务</a></dt>
					<dd>
						<a href="static/item/##">夺宝岛</a>
					</dd>
					<dd>
						<a href="static/item/##">DIY装机</a>
					</dd>
					<dd>
						<a href="static/item/##">延保服务</a>
					</dd>
					<dd>
						<a href="static/item/##">京东E卡</a>
					</dd>
					<dd>
						<a href="static/item/##">京东通信</a>
					</dd>
					<dd>
						<a href="static/item/##">京东JD+</a>
					</dd>
				</dl>
			</div>
			<!--关于我们-->
			<div class="guanyuwomen">
				<ul>
					<li>
						<a href="static/item/##">关于我们</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">联系我们</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">联系客服</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">合作招商</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">商家帮助</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">营销中心</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">手机京东</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">友情链接</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">销售联盟</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">京东社区</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">风险检测</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">隐私政策</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">京东公益</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">English Site</a>
					</li>
					<li>|</li>
					<li>
						<a href="static/item/##">Mdeila $ IR</a>
					</li>
				</ul>
			</div>
			<!--jieshoa-->
			<p class="p1"><img src="static/item/img/56a0a994Nf1b662dc.png" />
				<a href="static/item/##"> 京公网安备 11000002000088号</a>|
				<a href="static/item/##"> 京ICP证070359号</a>|
				<a href="static/item/##"> 互联网药品信息服务资格证编号(京)-经营性-2014-0008 </a>|
				<a href="static/item/##">新出发京零 字第大120007号</a>
			</p>
			<p class="p1">
				<a href="static/item/##"> 互联网出版许可证编号新出网证(京)字150号</a>|
				<a href="static/item/##"> 出版物经营许可证</a>|
				<a href="static/item/##"> 网络文化经营许可证京网文 </a>|
				<a href="static/item/##">[2014]2148-348号 </a>|
				<a href="static/item/##"> 违法和不良信息举报电话 </a>|
				<a href="static/item/##">：4006561155 </a>
			</p>
			<p class="p1">
				<a href="static/item/##"> Copyright © 2004-2017 京东JD.com 版权所有</a>|
				<a href="static/item/##"> 消费者维权热线：4006067733 经营证照</a>|
			</p>
			<p class="p1">
				<a href="static/item/##"> 京东旗下网站：京东支付</a>|
				<a href="static/item/##"> 京东云</a>
			</p>
			<p class="p3">
				<img src="static/item/img/54b8871eNa9a7067e.png" />
				<img src="static/item/img/54b8872dNe37a9860.png" />
				<img src="static/item/img/54b8875fNad1e0c4c.png" />
				<img src="static/item/img/5698dc03N23f2e3b8.jpg" />
				<img src="static/item/img/5698dc16Nb2ab99df.jpg" />
				<img src="static/item/img/56a89b8fNfbaade9a.jpg" />
			</p>
		</div>
		<div class="Fixedbian">
			<ul>
				<li class="li1"><a class="aaa" href="static/item/##">顶部</a></li>
			</ul>
		</div>
		<div class="gouwuchexiaguo">
			<img src="static/item/img/44.png" />
			<span>购物车还没有商品，赶紧选购吧！</span>
		</div>
	</body>

	<script src="static/item/js/jquery1.9.js" type="text/javascript" charset="utf-8"></script>
	<script src="static/item/js/js.js" type="text/javascript" charset="utf-8"></script>
	<script type="text/javascript">
	</script>
	<script>
		$(".sku_attr_value").click(function () {
			// 1、点击的元素添加上自定义的属性，为了识别我们是刚才被点击的
			let skus = new Array();
			let curr = $(this).attr("skus").split(",");

			//去掉同一行的所有的checked
			$(this).parent().parent().find(".sku_attr_value").removeClass("checked");
            $(this).addClass("checked");

			$("a[class='sku_attr_value checked']").each(function () {
				skus.push($(this).attr("skus").split(","));
			});

			// 2、取出他们的交集，得到skuId
			// console.log($(skus[0]).filter(skus[i])[0]);
			let filterEle = skus[0];
			for (let i = 1; i < skus.length; i++) {
				filterEle = $(filterEle).filter(skus[i]);
			}

			location.href = "http://item.gulimall.com/" + filterEle[0] + ".html";

			return false;
		});
		$(function () {
			changeCheckedStyle();
		});

		/**
		 * 切换边框
		 * 先清楚边框，然后再给正确的加上边框
		 */
		function changeCheckedStyle() {
			$(".sku_attr_value").parent().css({"border": "solid 1px #ccc"});
			$("a[class='sku_attr_value checked']").parent().css({"border": "solid 1px red"});
		};
		
	</script>

</html>
```

#### 异步编排优化
新建`MyThreadConfig`配置线程池
``` java
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool) {
        return new ThreadPoolExecutor(
                pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
```
新建`ThreadPoolConfigProperties`读取线程池配置
``` java
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;

}
```
可以选择加入提示
``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```
`appliamltion.properties`
``` properties
gulimall.thread.core-size=20
gulimall.thread.max-size=200
gulimall.thread.keep-alive-time=10
```

`SkuInfoServiceImpl`
``` java
@Autowired
ThreadPoolExecutor executor;

@Override
public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
    SkuItemVo skuItemVo = new SkuItemVo();

    //1、sku基本信息的获取  pms_sku_info
    CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
        SkuInfoEntity info = this.getById(skuId);
        skuItemVo.setInfo(info);
        return info;
    }, executor);

    //3、获取spu的销售属性组合
    CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
        List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
        skuItemVo.setSaleAttr(saleAttrVos);
    }, executor);

    //4、获取spu的介绍    pms_spu_info_desc
    CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
        skuItemVo.setDesc(spuInfoDescEntity);
    }, executor);

    //5、获取spu的规格参数信息
    CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
        List<SkuItemVo.SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
        skuItemVo.setGroupAttrs(attrGroupVos);
    }, executor);


    //2、sku的图片信息    pms_sku_images
    CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
        List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(imagesEntities);
    }, executor);

    //等到所有任务都完成
    CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture, seckillFuture).get();

    // 非异步编排
    /*//1、sku基本信息的获取  pms_sku_info
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
    skuItemVo.setGroupAttrs(attrGroupVos);*/

    return skuItemVo;
}
```
Controller层页需要将异常抛出

### 认证服务
#### 环境搭建
##### 服务搭建
新建`认证中心`服务`gulimall-auth-server`, 我和视频中有差异，此处仅供参考
`auth-server`的`pom.xml`
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>gulimall</artifactId>
        <groupId>cn.cheakin</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.cheakin</groupId>
    <artifactId>gulimall.auth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>gulimall-auth-server</name>
    <description>认证服务（社交登录、Oauth2.0、单点登录）</description>

    <dependencies>
        <dependency>
            <groupId>cn.cheakin</groupId>
            <artifactId>gulimall-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <exclusions>
                <exclusion>
                    <groupId>com.baomidou</groupId>
                    <artifactId>mybatis-plus-boot-starter</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- thymeleaf 模板引擎 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!--使用热加载-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

`application.yml`
``` yml
spring:
  application:
    name: gulimall-auth-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
  thymeleaf:
    cache: false
server:
  port: 20000
```

启动类上使用`@EnableDiscoveryClient`和`@EnableFeignClients`
``` java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
```

验证：启动服务，能在 nacos 中发现`auth-server`服务

##### 引入静态文件
* 登录页面
  将资料里的登录页中 index.html 放到 templates 目录下并重命名为`login.html`
  + 将`src="`全部替换为`src="/static/login/`
  + 将`href="`全部替换为`href="/static/login/`
	资料中的其他静态文件上传到服务器（虚拟机）的`/mydata/nginx/html/static/login/`目录下
	将
* 注册页面
  将资料里的注册页中 index.html 放到 templates 目录下并重命名为`reg.html`，  
	+ 将`src="`全部替换为`src="/static/reg/`
  + 将`href="`全部替换为`href="/static/reg/`
  资料中的其他静态文件上传到服务器（虚拟机）的`/mydata/nginx/html/static/login/`目录下

##### 修改hosts实现域名访问
在host文件中追加
``` json
192.168.56.10 auth.gulimall.com
```

##### 配置网关转发
`gateway`的`application.yml`中添加
``` yml
- id: gulimall_auth_route
	uri: lb://gulimall-auth-server
	predicates:
		- Host=auth.gulimall.com
```

#### 验证码倒计时 & 整合短信验证码
`product`服务的`index.html`中修改登录页和注册页的地址
``` html
<li>
	<a href="http://auth.gulimall.com/login.html">你好，请登录</a>
</li>
<li>
	<a href="http://auth.gulimall.com/reg.html" class="li_2">免费注册</a>
</li>
```

**设置视图映射**
`GulimallWebConfig`
``` java
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**·
     * 视图映射:发送一个请求，直接跳转到一个页面
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

         registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
```

在阿里云申请短信服务的过程，略

编写发送验证相关代码, 首先是controller层
``` java
@Controller  
@RequestMapping(value = "/sms")  
public class SmsSendController {  
  
    @Autowired  
    private SmsComponent smsComponent;  
  
    /**  
     * 提供给别的服务进行调用  
     * @param phone  
     * @param code  
     * @return  
     */  
    @GetMapping(value = "/sendCode")  
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {  
        //发送验证码  
        smsComponent.sendCode(phone,code);  
        return R.ok();  
    }  
}
```

然后是service层
``` java
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")  
@Data  
@Component  
public class SmsComponent {  
  
    private String accessKey;  
    private String secretKey;  
    private String region;  
    private String endpoint;  
    private String signName;  
    private String templateCode;  
  
  
    @SneakyThrows  
    public void sendCode(String phone, String code) {  
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()  
                .accessKeyId(accessKey)  
                .accessKeySecret(secretKey)  
                //.securityToken("<your-token>") // use STS token  
                .build());  
  
        // Configure the Client  
        AsyncClient client = AsyncClient.builder()  
                .region(region) // Region ID  
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)                .credentialsProvider(provider)  
                //.serviceConfiguration(Configuration.create()) // Service-level configuration  
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.                .overrideConfiguration(  
                        ClientOverrideConfiguration.create()  
                                .setEndpointOverride(endpoint)  
                        //.setConnectTimeout(Duration.ofSeconds(30))  
                )  
                .build();  
  
        // Parameter settings for API request  
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()  
                .signName(signName)  
                .templateCode(templateCode)  
                .phoneNumbers(phone)  
                .templateParam("{\"code\": " + code + "}")  
                // Request-level configuration rewrite, can set Http request parameters, etc.  
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))                .build();  
  
        // Asynchronously get the return value of the API request  
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);  
        // Synchronously get the return value of the API request  
        SendSmsResponse resp = response.get();  
        System.out.println(new Gson().toJson(resp));  
        // Asynchronous processing of return values  
        /*response.thenAccept(resp -> {            System.out.println(new Gson().toJson(resp));        }).exceptionally(throwable -> { // Handling exceptions            System.out.println(throwable.getMessage());            return null;        });*/  
        // Finally, close the client        client.close();  
    }  
  
}
```

##### 验证码防刷校验
需要在`auth`服务中远程调用第三方服务, 新建feign的远程调用接口
``` java
@FeignClient("gulimall-third-party")  
public interface ThirdPartFeignService {  
  
    @GetMapping(value = "/sms/sendCode")  
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);  
  
}
```

在`auth`服务的`LoginController`中编写发送验证码的接口, 用到了redis, 所以需要在xml中引入依赖
``` java
@ResponseBody  
@GetMapping(value = "/sms/sendCode")  
public R sendCode(@RequestParam("phone") String phone) {  
  
    //1、接口防刷  
    String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);  
    if (!StrUtil.isEmpty(redisCode)) {  
        //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码  
        long currentTime = Long.parseLong(redisCode.split("_")[1]);  
        if (System.currentTimeMillis() - currentTime < 60000) {  
            //60s内不能再发  
            return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());  
        }  
    }  
  
    //2、验证码的再次效验 redis.存key-phone,value-code  
    int code = (int) ((Math.random() * 9 + 1) * 100000);  
    String codeNum = String.valueOf(code);  
    String redisStorage = codeNum + "_" + System.currentTimeMillis();  
  
    //存入redis，防止同一个手机号在60秒内再次发送验证码  
    stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,  
            redisStorage, 10, TimeUnit.MINUTES);  
  
    thirdPartFeignService.sendCode(phone, codeNum);  
  
    return R.ok();  
}


```
在`common`服务中新增常量
``` java
public class AuthServerConstant {  
    public static final String SMS_CODE_CACHE_PREFIX = "sms:code:";  
//    public static final String LOGIN_USER = "loginUser";  
}
```
在`common`服务中新增异常枚举
``` java
SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
```

##### 注册接口 & 异常机制 & MD5&盐&BCrypt
`auth`服务中的`LoginController`
``` java
/**  
 * TODO: 重定向携带数据：利用session原理，将数据放在session中。  
 * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉  
 * TODO：分布下session问题  
 * RedirectAttributes：重定向也可以保留数据，不会丢失  
 * 用户注册  
 *  
 * @return  
 */  
@PostMapping(value = "/register")  
public String register(@Valid UserRegisterVo vos, BindingResult result, RedirectAttributes attributes) {  
    //如果有错误回到注册页面  
    if (result.hasErrors()) {  
        Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));  
        attributes.addFlashAttribute("errors", errors);  
        //效验出错回到注册页面  
        //return "redirect:reg.html";   // 用户注册时时发送的post请求，而路径映射转发默认时使用get方式  
        //转发的话用户在刷新页面时会重新提交请求  
        /*model.addAttribute("errors", errors);  
        return "reg"; */        //return "redirect:reg.html";   //这样会重定向到ip下  
        return "redirect:http://auth.gulimall.com/reg.html";  
    }  
  
    //1、效验验证码  
    String code = vos.getCode();  
  
    //获取存入Redis里的验证码  
    String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());  
    if (!StrUtil.isEmpty(redisCode)) {  
        //截取字符串  
        if (code.equals(redisCode.split("_")[0])) {  
            //删除验证码;令牌机制  
            stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());  
            //验证码通过，真正注册，调用远程服务进行注册  
            R register = memberFeignService.register(vos);  
            if (register.getCode() == 0) {  
                //成功  
                return "redirect:http://auth.gulimall.com/login.html";  
            } else {  
                //失败  
                Map<String, String> errors = new HashMap<>();  
                errors.put("msg", register.getData("msg", new TypeReference<String>() {  
                }));  
                attributes.addFlashAttribute("errors", errors);  
                return "redirect:http://auth.gulimall.com/reg.html";  
            }  
        } else {  
            //效验出错回到注册页面  
            Map<String, String> errors = new HashMap<>();  
            errors.put("code", "验证码错误");  
            attributes.addFlashAttribute("errors", errors);  
            return "redirect:http://auth.gulimall.com/reg.html";  
        }  
    } else {  
        //效验出错回到注册页面  
        Map<String, String> errors = new HashMap<>();  
        errors.put("code", "验证码错误");  
        attributes.addFlashAttribute("errors", errors);  
        return "redirect:http://auth.gulimall.com/reg.html";  
    }  
}
```
`auth`服务新建`UserRegisterVo`
``` java
@Data  
public class UserRegisterVo {  
  
    @NotEmpty(message = "用户名不能为空")  
    @Length(min = 6, max = 19, message="用户名长度在6-18字符")  
    private String userName;  
  
    @NotEmpty(message = "密码必须填写")  
    @Length(min = 6,max = 18,message = "密码必须是6—18位字符")  
    private String password;  
  
    @NotEmpty(message = "手机号不能为空")  
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")  
    private String phone;  
  
    @NotEmpty(message = "验证码不能为空")  
    private String code;  
  
}
```
`auth`服务的远程调用接口
``` java
@FeignClient("gulimall-member")  
public interface MemberFeignService {  
  
    @PostMapping(value = "/member/member/register")  
    R register(@RequestBody UserRegisterVo vo); 
}
```

`member`服务需要有相对应的接口
`MemberController`
``` java
@Data

public class MemberUserRegisterVo {

private String userName;

private String password;

private String phone;

}
```
`member`服务中新建
``` java
@Data  
public class MemberUserRegisterVo {  
  
    private String userName;  
  
    private String password;  
  
    private String phone;  
  
}
```
MemberServiceImpl
``` java
@Resource  
private MemberLevelDao memberLevelDao;

@Override  
public void register(MemberUserRegisterVo vo) {  
    MemberEntity memberEntity = new MemberEntity();  
  
    //设置默认等级  
    MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();  
    memberEntity.setLevelId(levelEntity.getId());  
  
    //设置其它的默认信息  
    //检查用户名和手机号是否唯一。感知异常，异常机制  
    checkPhoneUnique(vo.getPhone());  
    checkUserNameUnique(vo.getUserName());  
  
    memberEntity.setNickname(vo.getUserName());  
    memberEntity.setUsername(vo.getUserName());  
    //密码进行MD5加密  
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();  
    String encode = bCryptPasswordEncoder.encode(vo.getPassword());  
    memberEntity.setPassword(encode);  
    memberEntity.setMobile(vo.getPhone());  
    memberEntity.setGender(0);  
    memberEntity.setCreateTime(new Date());  
  
    //保存数据  
    this.baseMapper.insert(memberEntity);  
}

@Override  
public void checkPhoneUnique(String phone) throws PhoneException {  
  
    Integer phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));  
  
    if (phoneCount > 0) {  
        throw new PhoneException();  
    }  
  
}  
  
@Override  
public void checkUserNameUnique(String userName) throws UsernameException {  
  
    Integer usernameCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));  
  
    if (usernameCount > 0) {  
        throw new UsernameException();  
    }  
}
```
MemberLevelDao.xml
``` sql
SELECT * FROM ums_member_level WHERE default_status = 1
```


![[Pasted image 20230130224421.png]]

common服务的BizCodeEnum中新增异常消息
``` java

```

### 购物车
### 消息队列
### 订单服务
### 分布式事务
### 订单服务
### 支付
### 订单服务
### 秒杀服务

# 谷粒商城-集群篇(cluster)

