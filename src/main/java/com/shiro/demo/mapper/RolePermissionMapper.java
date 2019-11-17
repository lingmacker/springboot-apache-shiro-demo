package com.shiro.demo.mapper;

import com.shiro.demo.model.RolePermission;
import com.shiro.demo.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Set;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    // shiro 相关
    Set<String> selectPermissionsByRoles(@Param("roleSet") Set<String> roles);
}