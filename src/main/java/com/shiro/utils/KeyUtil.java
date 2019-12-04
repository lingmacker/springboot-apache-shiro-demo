package com.shiro.utils;

import com.shiro.common.Const;
import org.springframework.util.SerializationUtils;

public class KeyUtil {

    public static byte[] getRedisSessionKey(String sessionId) {
        return (Const.SHIRO_SESSION_PREFIX + sessionId).getBytes();
    }

    public static<K> byte[] getRedisCacheKey(K k){
        if (k instanceof String) {
            return (Const.SHIRO_CACHE_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    public static byte[] getRedisDiviceInfoKey(String username) {
        return (Const.DEVICE_CACHE_PREFIX + username).getBytes();
    }
}
