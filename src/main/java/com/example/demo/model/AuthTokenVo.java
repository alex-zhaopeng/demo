package com.example.demo.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @ClassName AuthTokenVo
 * @Description
 * @Version 1.0
 **/
public class AuthTokenVo implements AuthenticationToken {
    private String token;
    public AuthTokenVo(String token){
        this.token = token;
    }

    @Override
    public Object getPrincipal(){
        return token;
    }

    @Override
    public Object getCredentials(){
        return token;
    }
}
