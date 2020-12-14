package com.bilibili.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "person")
    //将此实体类与配置文件的属性绑定
@Validated  //数据校验
public class Person {
    @Email(message = "邮箱格式错误")  //可以自定义错误信息
    private String email;
    private String name;
    private Integer age;
    private boolean happy;
    private Date birthdat;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;

    public Person() {
    }

    public Person(@Email(message = "邮箱格式错误") String email, String name, Integer age, boolean happy, Date birthdat, Map<String, Object> maps, List<Object> lists, Dog dog) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.happy = happy;
        this.birthdat = birthdat;
        this.maps = maps;
        this.lists = lists;
        this.dog = dog;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public Date getBirthdat() {
        return birthdat;
    }

    public void setBirthdat(Date birthdat) {
        this.birthdat = birthdat;
    }

    public Map<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Object> maps) {
        this.maps = maps;
    }

    public List<Object> getLists() {
        return lists;
    }

    public void setLists(List<Object> lists) {
        this.lists = lists;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    @Override
    public String toString() {
        return "Person{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", happy=" + happy +
                ", birthdat=" + birthdat +
                ", maps=" + maps +
                ", lists=" + lists +
                ", dog=" + dog +
                '}';
    }
}
