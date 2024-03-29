package cn.cheakin.gulimall.thirdparty;

import cn.cheakin.gulimall.thirdparty.component.SmsComponent;
import com.aliyun.oss.OSSClient;
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


    @Autowired
    SmsComponent smsComponent;

    @Test
    void sendCode() {
        // 提示没有访问权限，是因为没有申请对应的模板
        smsComponent.sendCode("13888888888", "2022");
    }

}
