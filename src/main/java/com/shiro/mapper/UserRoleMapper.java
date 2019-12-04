package com.shiro.mapper;

import com.shiro.model.UserRole;
import com.shiro.utils.BaseMapper;

import java.util.Set;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    // shiro 相关
    Set<String> selectRolesByUsername(String username);
}