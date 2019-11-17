package com.shiro.demo.shiro;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ShiroExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public String unauthorizedExceptionHandler() {
        return "UnauthorizedException 没有授权";
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public String unauthenticatedExceptionHandler() {
        return "login";
    }
}
