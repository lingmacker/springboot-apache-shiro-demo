package com.shiro.demo.mapper;

import com.shiro.demo.model.UserPermission;
import com.shiro.demo.utils.BaseMapper;

import java.util.Set;

public interface UserPermissionMapper extends BaseMapper<UserPermission> {

    // shiro 相关
    Set<String> selectPermissionsByUsername(String username);
}