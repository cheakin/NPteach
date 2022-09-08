package cn.cheakin.gulimall.search.service;

import cn.cheakin.gulimall.search.vo.SearchParam;
import cn.cheakin.gulimall.search.vo.SearchResult;

/**
 * Create by botboy on 2022/09/08.
 **/
public interface MallSearchService {

    /**
     * 搜索
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果，里面包含页面需要的所有信息
     */
    SearchResult search(SearchParam param);

}
