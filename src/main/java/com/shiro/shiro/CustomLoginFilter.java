package com.shiro.shiro;

import com.shiro.common.Const;
import com.shiro.common.DeviceInfo;
import com.shiro.shiroSession.JedisUtil;
import com.shiro.utils.DeviceUtil;
import com.shiro.utils.KeyUtil;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomLoginFilter extends AccessControlFilter {
    @Autowired
    private DeviceUtil deviceUtil;

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String username = request.getParameter("username");
        String deviceType = request.getParameter("deviceType");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(deviceType)) {
            throw new LoginArgException(Const.LOGIN_ARG_ILLEGAL_EXCEPTION);
        }

        DeviceInfo deviceInfo = deviceUtil.getOrCreateDeviceInfo(username);
        String existedSessionId = deviceInfo.getByType(deviceType);

        // 踢掉上一个人
        if (!StringUtils.isEmpty(existedSessionId)) {
            byte[] sessionKey = KeyUtil.getRedisSessionKey(existedSessionId);
            byte[] cacheKey = KeyUtil.getRedisCacheKey(existedSessionId);
            jedisUtil.delete(sessionKey);
            jedisUtil.delete(cacheKey);
        }

        return true;
    }
}
