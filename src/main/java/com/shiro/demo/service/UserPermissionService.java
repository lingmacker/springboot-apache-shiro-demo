package com.shiro.demo.service;

import com.shiro.demo.mapper.UserPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("userPermissionService")
public class UserPermissionService {

    @Autowired
    private UserPermissionMapper userPermissionMapper;


    // shiro 相关
    public Set<String> getPermissionsByUsername(String username) {
        return userPermissionMapper.selectPermissionsByUsername(username);

    }
}
