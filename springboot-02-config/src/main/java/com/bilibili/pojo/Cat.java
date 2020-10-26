package com.bilibili.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "cat")    //yaml数据绑定
public class Cat {
    private String firstName;
    private String age;

    public Cat() {
    }

    public Cat(String firstName, String age) {
        this.firstName = firstName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "firstName='" + firstName + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
