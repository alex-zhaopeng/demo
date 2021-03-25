package com.example.demo.exception;

import com.example.demo.config.ReturnMessage;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class NoPermissionException {
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public ReturnMessage handleShiroException(Exception ex) {
        return ReturnMessage.failWithMsg( "无权限");
    }
    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public ReturnMessage AuthorizationException(Exception ex) {
        return ReturnMessage.failWithMsg( "权限认证失败");

    }
}
