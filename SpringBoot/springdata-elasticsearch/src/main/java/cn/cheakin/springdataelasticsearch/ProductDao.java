package cn.cheakin.springdataelasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Create by botboy on 2022/07/09.
 **/
@Repository
public interface ProductDao extends ElasticsearchRepository<Product, Long> {

}
