package cn.cheakin.gulimall.product.service.impl;

import cn.cheakin.gulimall.product.entity.AttrEntity;
import cn.cheakin.gulimall.product.service.AttrService;
import cn.cheakin.gulimall.product.vo.AttrGroupWithAttrsVo;
import cn.cheakin.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.cheakin.common.utils.PageUtils;
import cn.cheakin.common.utils.Query;

import cn.cheakin.gulimall.product.dao.AttrGroupDao;
import cn.cheakin.gulimall.product.entity.AttrGroupEntity;
import cn.cheakin.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        // key不为空
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and((obj) ->
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key)
            );
        }
        if (catelogId == 0) {
            // Query可以把map封装为IPage
            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params),
                            wrapper);
            return new PageUtils(page);
        } else {
            // 增加id信息
            wrapper.eq("catelog_id", catelogId);

            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params),
                            wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2、查询所有属性
        return attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1、查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        AttrGroupDao baseMapper = this.getBaseMapper();
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}