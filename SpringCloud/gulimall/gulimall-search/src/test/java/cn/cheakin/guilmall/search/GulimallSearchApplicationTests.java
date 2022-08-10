package cn.cheakin.guilmall.search;

import cn.cheakin.gulimall.search.GulimallSearchApplication;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Create by botboy on 2022/08/10.
 **/
@SpringBootTest
public class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println("client = " + client);
    }
}
