package com.example.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Role implements Serializable {
    private Integer userRoleId;
    private String userRoleName;
    private List<UserPermission> userPermissions;
}
