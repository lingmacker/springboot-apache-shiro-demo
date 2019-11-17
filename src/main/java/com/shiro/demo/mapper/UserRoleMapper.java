package com.shiro.demo.mapper;

import com.shiro.demo.model.UserRole;
import com.shiro.demo.utils.BaseMapper;

import java.util.Set;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    // shiro 相关
    Set<String> selectRolesByUsername(String username);
}