package com.bilibili.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

@ApiModel("用户实体类")
public class User {
    @ApiModelProperty("用户名")
    public String name;
    @ApiModelProperty("密码")
    public String password;
}
