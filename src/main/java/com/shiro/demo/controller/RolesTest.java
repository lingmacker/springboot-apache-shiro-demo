package com.shiro.demo.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RolesTest {

    @RequestMapping("/backend")
    @RequiresRoles("admin")
    @RequiresPermissions("user:*")
    public String backend() {
        return "这里是后台管理页面";
    }

    @RequestMapping("/portal")
    @RequiresRoles(value = {"admin", "user"}, logical = Logical.OR)
    public String portal() {
        return "这里是前台页面";
    }
}
