package com.shiro.demo;

import com.shiro.demo.common.Const;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {

        String s =  Const.SHIRO_CACHE_PREFIX + "2222";
        int i = s.indexOf(Const.SHIRO_CACHE_PREFIX);

        String substring = s.substring(i);
        System.out.println(i);
        System.out.println(substring);

    }

}
