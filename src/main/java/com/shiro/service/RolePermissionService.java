package com.shiro.service;


import com.shiro.mapper.RolePermissionMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("rolePermissionService")
public class RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    // shiro 相关
    public Set<String> getPermissionsByRoles(Set<String> roles) {
        Set<String> permissions = new HashSet<>();

        if (CollectionUtils.isEmpty(roles))
            return permissions;

        permissions = rolePermissionMapper.selectPermissionsByRoles(roles);
        return permissions;
    }
}
