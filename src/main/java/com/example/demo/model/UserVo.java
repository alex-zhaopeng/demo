package com.example.demo.model;

import java.io.Serializable;
import java.util.List;

public class UserVo implements Serializable {
    private Integer userId;
    private String username;
    private String password;
    private String salt;//盐值
    private List<UserRole> userRoleList;

    public UserVo() {
    }

    public UserVo(Integer userId, String username, String password, String salt, List<UserRole> userRoleList) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.userRoleList = userRoleList;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
