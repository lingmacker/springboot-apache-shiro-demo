package com.shiro.demo.controller;

import com.shiro.demo.model.User;
import com.shiro.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class AddUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/addUser")
//    @RequiresRoles("admin")
    public User addUser(@RequestParam("username") String username, @RequestParam("password") String passwod) {
        return userService.manageAddUser(username, passwod);
    }

}
