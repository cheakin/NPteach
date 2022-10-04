package cn.cheakin.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create by botboy on 2022/10/04.
 **/
@ConfigurationProperties(prefix = "gulimall.thread")
//@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;

}
