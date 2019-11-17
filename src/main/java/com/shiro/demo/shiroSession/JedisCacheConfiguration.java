package com.shiro.demo.shiroSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableCaching
public class JedisCacheConfiguration extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private  String host;

    @Value("${spring.redis.port}")
    private  int port;

    @Value("${spring.redis.timeout}")
    private  int timeout;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private  int maxWait;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private  int maxIdle;

    @Value("${spring.redis.jedis.pool.minidle}")
    private  int minIdle;

    @Bean("jedisPool")
    public  JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        config.setMaxIdle(maxIdle);

        return new JedisPool(config, host, port, timeout);
    }

}

