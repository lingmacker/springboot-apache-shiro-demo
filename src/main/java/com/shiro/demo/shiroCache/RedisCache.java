package com.shiro.demo.shiroCache;

import com.shiro.demo.common.Const;
import com.shiro.demo.shiroSession.JedisUtil;
import com.shiro.demo.utils.JsonUtil;
import com.shiro.demo.utils.KeyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public V get(K k) throws CacheException {

        byte[] key = KeyUtil.getRedisCacheKey(k);

        byte[] value = jedisUtil.get(key);
        if (value == null)
            return null;


        System.out.println("从redis中获取授权数据:"+ JsonUtil.toJson(SerializationUtils.deserialize(value))+ "------key:"+JsonUtil.toJson(k));

        return (V) SerializationUtils.deserialize(value);
    }

    @Override
    public V put(K k, V v) throws CacheException {

        if (k == null || v == null) {
            return null;
        }

        byte[] key = KeyUtil.getRedisCacheKey(k);
        byte[] value = SerializationUtils.serialize(v);

        jedisUtil.set(key, value);
        jedisUtil.expire(key, Const.REDIS_CACHE_EXPIRE_TIME);

        return v;
    }

    @Override
    public V remove(K k) throws CacheException {

        if (k == null) {
            return null;
        }

        byte[] key = KeyUtil.getRedisCacheKey(k);
        byte[] value = jedisUtil.get(key);

        jedisUtil.delete(key);

        return (V) SerializationUtils.deserialize(value);
    }

    @Override
    public void clear() throws CacheException {
        Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        keys.forEach(key -> {
            jedisUtil.delete(key);
        });

    }

    @Override
    public int size() {
        Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);

        return CollectionUtils.size(keys);
    }

    @Override
    public Set<K> keys() {
        Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);
        Set<K> vs = new HashSet<>();

        if (CollectionUtils.isEmpty(keys)) {
            return vs;
        }

        keys.forEach(key -> {
            Object o = SerializationUtils.deserialize(key);
            if (o instanceof String) {
                String string = ((String) o).replace(Const.SHIRO_CACHE_PREFIX, "");
                vs.add((K) string);
            } else {
                vs.add((K) SerializationUtils.deserialize(key));
            }

        });

        return vs;
    }

    @Override
    public Collection<V> values() {
        Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);

        Set<V> values = new HashSet<>();
        if (CollectionUtils.isEmpty(keys)) {
            return values;
        }

        keys.forEach(key -> {
            V value = (V) SerializationUtils.deserialize(jedisUtil.get(key));
            values.add(value);
        });

        return values;
    }
}
