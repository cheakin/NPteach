package cn.cheakin.gulimall.product.service.impl;

import cn.cheakin.gulimall.product.entity.SkuImagesEntity;
import cn.cheakin.gulimall.product.entity.SpuInfoDescEntity;
import cn.cheakin.gulimall.product.service.*;
import cn.cheakin.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;

import cn.cheakin.gulimall.product.dao.SkuInfoDao;
import cn.cheakin.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);

                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

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

        /*CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //3、远程调用查询当前sku是否参与秒杀优惠活动
            R skuSeckilInfo = seckillFeignService.getSkuSeckilInfo(skuId);
            if (skuSeckilInfo.getCode() == 0) {
                //查询成功
                SeckillSkuVo seckilInfoData = skuSeckilInfo.getData("data", new TypeReference<SeckillSkuVo>() {
                });
                skuItemVo.setSeckillSkuVo(seckilInfoData);
                if (seckilInfoData != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > seckilInfoData.getEndTime()) {
                        skuItemVo.setSeckillSkuVo(null);
                    }
                }
            }
        }, executor);*/

        //等到所有任务都完成
        // CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture, seckillFuture).get();
        CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture).get();

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

}