package com.chenkin.elasticsearch.doc;

import com.chenkin.elasticsearch.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * 批量插入数据
 * Create by botboy on 2022/07/02.
 **/
public class DocInsertBatch {
    public static void main(String[] args) throws IOException {
        // 创建ES客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 批量新增文档 - 请求对象
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "zhangsan"));
        request.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "lisi"));
        request.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON, "name", "wangwu"));

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);

        System.out.println("response.getTook() = " + response.getTook());
        System.out.println("response.getItems() = " + response.getItems());

        // 关闭客户端
        client.close();
    }
}
