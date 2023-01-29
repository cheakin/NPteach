package cn.cheakin.gulimall.auth.controller;

import cn.cheakin.common.constant.AuthServerConstant;
import cn.cheakin.common.exception.BizCodeEnum;
import cn.cheakin.common.utils.R;
import cn.cheakin.gulimall.auth.feign.ThirdPartFeignService;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * Create by botboy on 2022/10/07.
 **/
@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 发送一个请求直接跳到一个页面。
     * SpringMVC viewcontroller: 将请求和页面映射过来
     */
    /*@GetMapping(value = "/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/reg.html")
    public String regPage() {
        return "reg";
    }*/



    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        //1、接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StrUtil.isEmpty(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //2、验证码的再次效验 redis.存key-phone,value-code
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        //存入redis，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                redisStorage, 10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone, codeNum);

        return R.ok();
    }

}
