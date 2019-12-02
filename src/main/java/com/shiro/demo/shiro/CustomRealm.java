package com.shiro.demo.shiro;

import com.shiro.demo.model.User;
import com.shiro.demo.service.RolePermissionService;
import com.shiro.demo.service.UserPermissionService;
import com.shiro.demo.service.UserRoleService;
import com.shiro.demo.service.UserService;
import com.shiro.demo.utils.JsonUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        String username = user.getUsername();

        Set<String> roles = userRoleService.getRolesByUsername(username);
        Set<String> permisseions = userPermissionService.getPermissionsByUsername(username);
        permisseions.addAll(rolePermissionService.getPermissionsByRoles(roles));

        info.addRoles(roles);
        info.addStringPermissions(permisseions);

        System.out.println("从数据库获取权限数据:"+ JsonUtil.toJson(info));

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String) token.getPrincipal();
        User user = userService.getUserByUsername(username);

        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getClass().getName());
    }
}
