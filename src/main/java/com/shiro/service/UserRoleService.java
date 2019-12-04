package com.shiro.service;


import com.shiro.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("userRoleService")
public class UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    // shiro 相关
    public Set<String> getRolesByUsername(String username) {
//        System.out.println("从数据库中获取授权数据");

        return userRoleMapper.selectRolesByUsername(username);
    }
}
