package com.shiro.demo.mapper;

import com.shiro.demo.model.User;
import com.shiro.demo.utils.BaseMapper;

public interface UserMapper extends BaseMapper<User> {

    // shiro 相关
    User selectByUsername(String username);
}