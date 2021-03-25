package com.example.demo.model;


import java.io.Serializable;

public class UserPermission implements Serializable {
    private Long userPermissionId;
    private String userPermissionName;

    public UserPermission(Long userPermissionId, String userPermissionName) {
        this.userPermissionId = userPermissionId;
        this.userPermissionName = userPermissionName;
    }

    public Long getUserPermissionId() {
        return userPermissionId;
    }

    public void setUserPermissionId(Long userPermissionId) {
        this.userPermissionId = userPermissionId;
    }

    public String getUserPermissionName() {
        return userPermissionName;
    }

    public void setUserPermissionName(String userPermissionName) {
        this.userPermissionName = userPermissionName;
    }
}
