package com.bilibili.springcloud.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {
    //请求返回状态
    private Integer code;
    //返回消息
    private String msg;
    //请求成功时返回携带数据
    private T data;

    public CommonResult<T> fail(Integer code, String msg) {
        return new CommonResult<T>(code, msg, null);
    }

    public CommonResult<T> success() {
        return new CommonResult<T>(200, "", null);
    }

    public CommonResult<T> success(T data) {
        return new CommonResult<T>(200, "", data);
    }

}
