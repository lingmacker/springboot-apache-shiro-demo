package com.shiro.service;

import com.shiro.common.Const;
import com.shiro.mapper.UserMapper;
import com.shiro.model.User;
import com.shiro.shiro.ShiroService;
import com.shiro.utils.SaltUtil;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShiroService shiroService;

    // shiro 相关
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);

    }

    public String login(String username, String password, String deviceType) {
        Session login = shiroService.login(username, password, deviceType);
        if (login == null) {
            return "登录失败";
        }
        return "登录成功";

    }

    public boolean logout() {
        Subject subject = shiroService.getSubject();

        try {
            subject.logout();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User manageAddUser(String username, String passwod) {
        User user = new User();
        String salt = SaltUtil.generateSalt();
        String hashedPassword = new Sha256Hash(passwod, salt, Const.HASH_ITERATIONS).toString();

        user.setUsername(username);
        user.setSalt(salt);
        user.setPassword(hashedPassword);

        int rowCount = userMapper.insert(user);

        if (rowCount > 0) {
            return user;
        }

        return null;
    }
}
