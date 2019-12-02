package com.shiro.demo.utils;

import com.shiro.demo.common.Const;
import com.shiro.demo.common.DeviceInfo;
import com.shiro.demo.shiroSession.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;


@Component("deviceUtil")
public class DeviceUtil {

    @Autowired
    private JedisUtil jedisUtil;

    public DeviceInfo getOrCreateDeviceInfo(String username) {
        byte[] deviceInfoBytes = jedisUtil.get(KeyUtil.getRedisDiviceInfoKey(username));
        if (deviceInfoBytes == null) {
            return new DeviceInfo();
        }

        String json = (String) SerializationUtils.deserialize(deviceInfoBytes);
        return JsonUtil.toObject(json, DeviceInfo.class);
    }

    public void saveDeviceInfoToRedis(String username, DeviceInfo deviceInfo) {
        byte[] deviceInfoKey = KeyUtil.getRedisDiviceInfoKey(username);
        String json = JsonUtil.toJson(deviceInfo);

        jedisUtil.set(deviceInfoKey, SerializationUtils.serialize(json));
        jedisUtil.expire(deviceInfoKey, Const.DEVICE_INFO_CACHE_EXPIRE_TIME);
    }

}
