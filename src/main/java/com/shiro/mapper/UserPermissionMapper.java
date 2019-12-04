package com.shiro.mapper;

import com.shiro.model.UserPermission;
import com.shiro.utils.BaseMapper;

import java.util.Set;

public interface UserPermissionMapper extends BaseMapper<UserPermission> {

    // shiro 相关
    Set<String> selectPermissionsByUsername(String username);
}