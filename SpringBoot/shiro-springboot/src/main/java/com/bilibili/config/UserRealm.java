package com.bilibili.config;

import com.bilibili.pojo.User;
import com.bilibili.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的Realm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("=========授权================");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //拿到当前用户对象
        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();

        //设置当前用户权限
        info.addStringPermission(user.getPerms());

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("===============认证==========");
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        //用户名、密码（来自数据库）
        User user = userService.queryUserByName(userToken.getUsername());
        System.out.println("user = " + user);
        if (user == null){//账户不存在
            return null;    //抛出异常：UnknownAccountException
        }

        //可以选择密码加密方式（比如MD5,MD5盐值等等）
        //密码认证由shiro完成
        return new SimpleAuthenticationInfo(user, user.getPassword(), "");//可使用的资源，密码，实体对象
    }


}
