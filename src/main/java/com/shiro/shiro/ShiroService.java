package com.shiro.shiro;

import com.shiro.common.DeviceInfo;
import com.shiro.utils.DeviceUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("shiroService")
public class ShiroService {

    @Autowired
    private DeviceUtil deviceUtil;

    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public boolean getLoginStatus() {
        return this.getSubject().isAuthenticated();
    }

    public Session login(String username, String password, String deviceType) {
        Subject subject = this.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);

            // 保存登录信息到设备登录信息
            String sessionId = subject.getSession().getId().toString();
            DeviceInfo deviceInfo = deviceUtil.getOrCreateDeviceInfo(username);
            deviceInfo.setByType(deviceType, sessionId);
            deviceUtil.saveDeviceInfoToRedis(username, deviceInfo);

            return subject.getSession();
        } catch (AuthenticationException e) {
            return null;
        }
    }

//    public void autoKickout(Subject subject, String username, String deviceType) {
//
//        DeviceInfo deviceInfo = deviceUtil.getOrCreateDeviceInfo(username);
//        String existedSessionId = deviceInfo.getByType(deviceType);
//
//        // 踢掉上一个人
//        if (!StringUtils.isEmpty(existedSessionId)) {
//            byte[] sessionKey = KeyUtil.getRedisSessionKey(existedSessionId);
//            byte[] cacheKey = KeyUtil.getRedisCacheKey(existedSessionId);
//            jedisUtil.delete(sessionKey);
//            jedisUtil.delete(cacheKey);
//        }
//
//        // 保存当前登录信息
//        String sessionId = subject.getSession().getId().toString();
//        deviceInfo.setByType(deviceType, sessionId);
//        deviceUtil.saveDeviceInfoToRedis(username, deviceInfo);
//    }
}
