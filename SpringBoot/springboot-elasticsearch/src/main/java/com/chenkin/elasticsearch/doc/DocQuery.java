package com.chenkin.elasticsearch.doc;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;

/**
 * 条件查询数据
 * Create by botboy on 2022/07/02.
 **/
public class DocQuery {
    public static void main(String[] args) throws IOException {
        // 创建ES客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 创建搜索请求对象
        SearchRequest request = new SearchRequest();
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 1.条件查询
        /*sourceBuilder.query(QueryBuilders.termQuery("age", "30"));*/

        // 2.分页查询
        /*sourceBuilder.query(QueryBuilders.matchAllQuery());
        // 分页查询
        // 当前页其实索引(第一条数据的顺序号)， from
        sourceBuilder.from(0);
        // 每页显示多少条 size
        sourceBuilder.size(2);*/

        // 3.排序查询
        /*sourceBuilder.query(QueryBuilders.matchAllQuery());
        // 排序
        sourceBuilder.sort("age", SortOrder.ASC);
        // 需要的话可以加 排除
        String[] include = {"name"};
        String[] excludes = {};
        sourceBuilder.fetchSource(include, excludes);*/

        // 4.组合查询
        /*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 必须包含
        boolQueryBuilder.must(QueryBuilders.matchQuery("age", "30"));
        // 一定不含
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery("name", "zhangsan"));
        // 可能包含
        boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "男"));
        sourceBuilder.query(boolQueryBuilder);*/

        // 5.范围查询
        /*RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
        // 大于等于
        //rangeQuery.gte("30");
        // 小于等于
        rangeQuery.lte("40");
        sourceBuilder.query(rangeQuery);*/

        // 6.模糊查询
        /*sourceBuilder.query(QueryBuilders.fuzzyQuery("name","wangwu")
                .fuzziness(Fuzziness.ONE)); // 允许偏差值*/

        // 7.高亮查询
        /*TermsQueryBuilder termsQueryBuilder =
                QueryBuilders.termsQuery("name","lisi");
        sourceBuilder.query(termsQueryBuilder);
        // 构建高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>"); // 设置标签前缀
        highlightBuilder.postTags("</font>");   // 设置标签后缀
        highlightBuilder.field("name"); // 设置高亮字段
        // 设置高亮构建对象
        sourceBuilder.highlighter(highlightBuilder);*/

        // 8.最大值查询
        /*sourceBuilder.aggregation(AggregationBuilders.max("maxAge").field("age"));*/

        // 9.分组查询
        sourceBuilder.aggregation(AggregationBuilders.terms("age_groupby").field("age"));

        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response);

        // 关闭客户端
        client.close();
    }
}