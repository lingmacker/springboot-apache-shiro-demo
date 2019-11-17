package com.shiro.demo.utils;

import java.util.UUID;

public class SaltUtil {
    public static String generateSalt() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
