package com.shiro.mapper;

import com.shiro.model.User;
import com.shiro.utils.BaseMapper;

public interface UserMapper extends BaseMapper<User> {
    // shiro 相关
    User selectByUsername(String username);
}