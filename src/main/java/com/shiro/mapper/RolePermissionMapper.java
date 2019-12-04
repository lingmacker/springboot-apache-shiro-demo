package com.shiro.mapper;

import com.shiro.model.RolePermission;
import com.shiro.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    // shiro 相关
    Set<String> selectPermissionsByRoles(@Param("roleSet") Set<String> roles);
}