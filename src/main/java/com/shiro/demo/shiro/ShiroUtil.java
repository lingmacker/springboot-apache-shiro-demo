package com.shiro.demo.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

@Component("shiroUtil")
public class ShiroUtil {
    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }
}
