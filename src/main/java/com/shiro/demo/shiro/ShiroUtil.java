package com.shiro.demo.shiro;

import com.shiro.demo.common.DeviceInfo;
import com.shiro.demo.shiroSession.JedisUtil;
import com.shiro.demo.utils.DeviceUtil;
import com.shiro.demo.utils.KeyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("shiroUtil")
public class ShiroUtil {

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private DeviceUtil deviceUtil;

    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public void autoKickout(Subject subject, String username, String deviceType) {

        DeviceInfo deviceInfo = deviceUtil.getOrCreateDeviceInfo(username);
        String existedSessionId = deviceInfo.getByType(deviceType);

        // 踢掉上一个人
        if (!StringUtils.isEmpty(existedSessionId)) {
            byte[] sessionKey = KeyUtil.getRedisSessionKey(existedSessionId);
            byte[] cacheKey = KeyUtil.getRedisCacheKey(existedSessionId);
            jedisUtil.delete(sessionKey);
            jedisUtil.delete(cacheKey);
        }

        // 保存当前登录信息
        String sessionId = subject.getSession().getId().toString();
        deviceInfo.setByType(deviceType, sessionId);
        deviceUtil.saveDeviceInfoToRedis(username, deviceInfo);
    }
}
