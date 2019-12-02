package com.shiro.demo.shiro;

import com.shiro.demo.utils.JsonUtil;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomLoginFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("request" + JsonUtil.toJson(request));

//        Subject subject = this.getSubject(request, response);
//        boolean authenticated = subject.isAuthenticated();
//        Object principal = subject.getPrincipal();
//        System.out.println(JsonUtil.toJson(principal));
//
//        PrincipalCollection principals = subject.getPrincipals();
//        PrincipalCollection previousPrincipals = subject.getPreviousPrincipals();
//        Session session = subject.getSession();
//        String s = JsonUtil.toJson("loginFilter session" + session);
//        System.out.println(s);


        return true;
    }
}
