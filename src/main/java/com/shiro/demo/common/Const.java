package com.shiro.demo.common;

public class Const {
    // 密码hash次数
    public static final int HASH_ITERATIONS = 10;

    public static final String SHIRO_SESSION_PREFIX = "shiro_session:";

    // Redis中session超时时间，单位s
    public static final int REDIS_SESSION_EXPIRE_TIME = 600;

    public static final String SHIRO_CACHE_PREFIX = "shiro_cache:";

    // Redis中cache超时时间，单位s
    public static final int REDIS_CACHE_EXPIRE_TIME = 600;

    // 一次会话超时时间，超过时间后会自动退出登录，单位ms
    public static final int SESSION_EXPIRE_TIME = 1000 * 60 * 60 * 24;

}
