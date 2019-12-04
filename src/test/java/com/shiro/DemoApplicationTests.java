package com.shiro;

import com.shiro.common.Const;
import org.junit.jupiter.api.Test;

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
