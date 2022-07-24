package cn.cheakin.gulimall.thirdparty;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class GulimallTirdPartyTests {

    @Autowired
    OSSClient ossClient;

    @Test
    void testUpload() {
        String filePath= "D:\\图片\\girl.jpg";

        ossClient.putObject("gulimall-cheakin", "girl2.jpg", new File(filePath));

        ossClient.shutdown();

        System.out.println("上传完成...");
    }

}
