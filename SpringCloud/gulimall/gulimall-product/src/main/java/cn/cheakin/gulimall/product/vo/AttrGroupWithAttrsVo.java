package cn.cheakin.gulimall.product.vo;

import cn.cheakin.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * Create by botboy on 2022/07/31.
 **/
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
