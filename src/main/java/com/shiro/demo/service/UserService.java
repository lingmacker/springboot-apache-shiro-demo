package com.shiro.demo.service;

import com.shiro.demo.common.Const;
import com.shiro.demo.mapper.UserMapper;
import com.shiro.demo.model.User;
import com.shiro.demo.shiro.ShiroUtil;
import com.shiro.demo.utils.SaltUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
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
    private ShiroUtil shiroUtil;


    // shiro 相关
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);

    }

    public Session login(String username, String password) {
        Subject subject = shiroUtil.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
            return subject.getSession();
        } catch (AuthenticationException e) {
            return null;
        }

    }

    public boolean logout() {
        Subject subject = shiroUtil.getSubject();
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