package com.shiro.demo.shiroCache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component("redisCacheManager")
public class RedisCacheManager implements CacheManager {

    @Autowired
    RedisCache redisCache;

    private final ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        RedisCache cache = caches.get(s);
        if (cache != null) {
            return cache;
        }

        caches.put(s, redisCache);
        return redisCache;
    }
}
