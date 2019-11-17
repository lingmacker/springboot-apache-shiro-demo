package com.shiro.demo.controller;

import com.shiro.demo.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;


    @RequestMapping("/userLogin")
    public Session login(String username, String password) {
        return userService.login(username, password);
    }

    @RequestMapping("/logout")
    public String logout() {
        boolean logout = userService.logout();
        if (logout) {
            return "退出登录成功";
        }
        return "退出登录失败";
    }


    @RequestMapping("/show")
    public String showSomething() {
        return "不需要权限就可以访问的页面";
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

}
