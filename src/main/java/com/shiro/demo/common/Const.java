package com.shiro.demo.common;

public class Const {
    // 密码hash次数
    public static final int HASH_ITERATIONS = 10;

    public static final String SHIRO_SESSION_PREFIX = "shiro_session:";

    public static final String DEVICE_CACHE_PREFIX = "device_cache_prefix:";

    public static final String SHIRO_CACHE_PREFIX = "shiro_cache:";

    // Redis中session超时时间，单位s
    public static final int REDIS_SESSION_EXPIRE_TIME = 1000 * 60 * 60 * 24;

    // 一次会话超时时间，超过时间后会自动退出登录，单位ms
    public static final int SESSION_EXPIRE_TIME = 1000 * 60 * 60 * 24;

    // Redis中cache超时时间，单位s，不建议太大，影响权限刷新
    public static final int REDIS_CACHE_EXPIRE_TIME = 60 * 10;

    // 设备信息在redis保存时间，单位s
    public static final int DEVICE_INFO_CACHE_EXPIRE_TIME = 60 * 60 * 24;


}
