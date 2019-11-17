package com.shiro.demo.shiroSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Slf4j
@Component("jedisUtil")
public class JedisUtil {

    @Autowired
    private JedisPool jedisPool;

    private Jedis getJedis() {
        Jedis jedis = null;
        if (jedisPool != null) {
            try {
                jedis = jedisPool.getResource();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return jedis;
    }

    public void set(byte[] key, byte[] value) {
        try (Jedis jedis = this.getJedis()) {
            jedis.set(key, value);
        }

    }

    public void expire(byte[] key, int i) {
        try (Jedis jedis = this.getJedis()) {
            jedis.expire(key, i);
        }
    }

    public byte[] get(byte[] key) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.get(key);
        }
    }

    public void delete(byte[] key) {
        try (Jedis jedis = this.getJedis()) {
            jedis.del(key);
        }

    }

    public Set<byte[]> getKeys(String SHIRO_SESSION_PREFIX) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.keys((SHIRO_SESSION_PREFIX + "*").getBytes());
        }
    }
}
