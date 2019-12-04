package com.shiro.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionsTest {

    @RequestMapping("/create")
    @RequiresPermissions({"user:*"})
    public String create() {
        return "成功添加一条数据";
    }

    @RequestMapping("/read")
    @RequiresPermissions(value = {"user:*", "user:read"}, logical = Logical.OR)
    public String read() {
        return "成功查看该条数据";
    }

    @RequestMapping("/update")
    @RequiresPermissions({"user:*"})
    public String update() {
        return "成功更新该条数据";
    }

    @RequestMapping("/delete")
    @RequiresPermissions({"user:*"})
    public String delete() {
        return "成功删除该条数据";
    }
}
