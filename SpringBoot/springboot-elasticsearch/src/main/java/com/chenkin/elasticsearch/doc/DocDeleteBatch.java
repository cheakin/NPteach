package com.chenkin.elasticsearch.doc;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * 批量删除数据
 * Create by botboy on 2022/07/02.
 **/
public class DocDeleteBatch {
    public static void main(String[] args) throws IOException {
        // 创建ES客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 批量删除文档 - 请求对象
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index("user").id("1002"));
        request.add(new DeleteRequest().index("user").id("1003"));
        request.add(new DeleteRequest().index("user").id("1004"));

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println("response.getTook() = " + response.getTook());
        System.out.println("response.getItems() = " + response.getItems());

        // 关闭客户端
        client.close();
    }
}
