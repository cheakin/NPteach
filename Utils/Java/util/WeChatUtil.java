package com.water.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 微信接口
 * Create by CK on 2021/03/06.
 **/
@Slf4j
@Component
public class WeChatUtil {
    @Value("${WX.APP_ID}")
    private String APP_ID;
    @Value("${WX.APP_SECRET}")
    private String APP_SECRET;

    public String getOpenID(String code) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+APP_ID+"&secret="+APP_SECRET+"&js_code="+code+"&grant_type=authorization_code";
            String result = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result);
            log.info("微信登录结果 = " + result);
            Integer errcode = (Integer)jsonObject.get("errcode");
            if (jsonObject.get("openid")!=null) {
                return jsonObject.getString("openid");

            } else {
                log.info("code换取微信openId失败："+jsonObject);
                return "";
            }
        } catch (RestClientException e) {
            log.info("code换取微信openId异常："+e);
        }
        return "";
    }


}
