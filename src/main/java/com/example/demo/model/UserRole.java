package com.example.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UserRole
 * @Description 角色表
 * @Version 1.0
 **/
@Data
public class UserRole implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer roleId;
    private List<Role> roleList;
}
