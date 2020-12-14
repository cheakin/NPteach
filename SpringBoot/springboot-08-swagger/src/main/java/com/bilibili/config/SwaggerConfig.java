package com.bilibili.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2 //开启Swagger
public class SwaggerConfig {

    @Bean
    public Docket getDocket(Environment environment) {
        //获取项目环境
        Boolean enableFlag = false;
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile))  enableFlag=true;
        }
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                //是否启用swagger
                .enable(enableFlag) //根据当前运行环境决定是否开启
                //分组组名
                .groupName("CK")
                .select()
                //RequestHandlerSelectors,配置要扫描接口的方式
                //  basePackage()指定要扫描的包
                //  any()都扫描
                //  none()都不扫描
                //  withClassAnnotation()扫描类上的注解
                //  withMethodAnnotation()扫描方法上的注解
                //          GetMapping()、PostMapper()
//                .apis(RequestHandlerSelectors.withMethodAnnotation(GetMapping.class))
                //过滤xxx路径
                //  ant()路径
//                .paths(PathSelectors.ant("/"))
                .build();
    }

    @Bean
    public Docket getDocket1() {
        return new Docket(DocumentationType.OAS_30).groupName("A");
    }

    @Bean
    public Docket getDocket2() {
        return new Docket(DocumentationType.OAS_30).groupName("B");
    }

    @Bean
    public Docket getDocket3() {
        return new Docket(DocumentationType.OAS_30).groupName("C");
    }


    private ApiInfo apiInfo() {
        Contact contact = new Contact("作者", "主页地址", "邮箱地址");
        return new ApiInfo(
                "Api 文档",
                "Api 文档描述",
                "1.0",
                "https://space.bilibili.com/95256449/",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }

}
