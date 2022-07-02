package com.chenkin;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;

import java.io.IOException;

/**
 * ES 客户端
 * Create by botboy on 2022/07/02.
 **/
public class esClient {

    public static void main(String[] args) throws IOException {
//        new TransportClient(); // 已经不推荐使用

        // 创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 关闭客户端
        esClient.close();

    }
}
