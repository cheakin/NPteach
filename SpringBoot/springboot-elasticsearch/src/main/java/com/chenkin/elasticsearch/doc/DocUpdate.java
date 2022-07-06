package com.chenkin.elasticsearch.doc;

import com.chenkin.elasticsearch.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * 修改数据
 * Create by botboy on 2022/07/02.
 **/
public class DocUpdate {
    public static void main(String[] args) throws IOException {
        // 创建ES客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 修改文档 - 请求对象
        UpdateRequest request = new UpdateRequest();
        // 设置索引及唯一性标识
        request.index("user").id("1001");
        request.doc(XContentType.JSON, "sex", "女");

        // 客户端发送请求，获取响应对象
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        // 3.打印结果信息
        System.out.println("response.getResult() = " + response.getResult());

        // 关闭客户端
        client.close();
    }
}
