package cn.cheakin.gulimall.auth.controller;

import cn.cheakin.common.constant.AuthServerConstant;
import cn.cheakin.common.utils.R;
import cn.cheakin.common.vo.MemberResponseVo;
import cn.cheakin.gulimall.auth.feign.MemberFeignService;
import cn.cheakin.gulimall.auth.vo.SocialUser;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class OAuth2Controller {

    private final MemberFeignService memberFeignService;

    @Autowired
    public OAuth2Controller(MemberFeignService memberFeignService) {
        this.memberFeignService = memberFeignService;
    }

    @GetMapping(value = "/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {

        Map<String, Object> map = new HashMap<>(5);
        map.put("client_id", "4159591980");
        map.put("client_secret", "cd006789a55afae7c7bccb96cf6df003");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);

        //1、根据用户授权返回的code换取access_token
//        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());
//        HttpResponse post = HttpUtil.post("https://api.weibo.com/oauth2/access_token", map);
        HttpResponse response = HttpUtil.createPost("https://api.weibo.com/oauth2/access_token").form(map).execute();

        //2、处理
        if (response.getStatus() == 200) {
            //获取到了access_token,转为通用社交登录对象
            //String json = JSON.toJSONString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(response.body(), SocialUser.class);

            //知道了哪个社交用户
            //1）、当前用户如果是第一次进网站，自动注册进来（为当前社交用户生成一个会员信息，以后这个社交账号就对应指定的会员）
            //登录或者注册这个社交用户
//            System.out.println(socialUser.getAccess_token());
            //调用远程服务
            R oauthLogin = memberFeignService.oauthLogin(socialUser);
            if (oauthLogin.getCode() == 0) {
                MemberResponseVo data = oauthLogin.getData("data", MemberResponseVo.class);
                log.info("登录成功：用户信息：{}", data.toString());

                //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie, 如gulimall.com, auth.gulimall.com, order.gulimall.com
                // 发卡的时候(指定域名为父域),即使是子域系统发的卡也能让父域直接使用
                /*session.setAttribute("loginUser", data);
                Cookie cookie = new Cookie("JSESSIONID", "dada");
                cookie.setDomain("");   // 作用域默认是请求的域名
                servletResponse.addCookie(cookie);*/

                //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
                //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
//                session.setAttribute(LOGIN_USER, data);

                //2、登录成功跳回首页
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}