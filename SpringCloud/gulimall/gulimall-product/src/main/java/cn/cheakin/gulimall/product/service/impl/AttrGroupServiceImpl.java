package cn.cheakin.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
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

}