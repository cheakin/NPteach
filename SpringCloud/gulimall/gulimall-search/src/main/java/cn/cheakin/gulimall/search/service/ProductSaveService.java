package cn.cheakin.gulimall.search.service;

import cn.cheakin.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * Create by botboy on 2022/08/18.
 **/
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
