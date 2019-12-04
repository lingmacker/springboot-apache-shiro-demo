package com.shiro.controller;

import com.shiro.service.UserService;
import com.shiro.shiro.ShiroService;
import com.shiro.shiroSession.JedisUtil;
import com.shiro.utils.KeyUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    ShiroService shiroService;

    @RequestMapping("/userLogin")
    public String login(String username, String password, String deviceType) {
        return userService.login(username, password, deviceType);
    }

    @RequestMapping("/logout")
    public String logout() {
        boolean logout = userService.logout();

        if (logout) {
            return "退出登录成功";
        }
        return "退出登录失败";
    }

    @RequestMapping("/isLogin")
    public boolean showSomething() {
        return shiroService.getLoginStatus();
    }

    @RequestMapping("/admin")
    @RequiresRoles("admin")
    public String admin() {
        return "admin 页面";
    }

    @RequestMapping(value = "/")
    @RequiresAuthentication
    public String index() {
        return "这里是主页";
    }

    @Autowired
    JedisUtil jedisUtil;

    @RequestMapping("/cache")
    public Object getCache() {
        Subject subject = shiroService.getSubject();
        String s = subject.getSession().getId().toString();
        byte[] redisCacheKey = KeyUtil.getRedisCacheKey(s);
        byte[] bytes = jedisUtil.get(redisCacheKey);
        return SerializationUtils.deserialize(bytes);
    }
}
