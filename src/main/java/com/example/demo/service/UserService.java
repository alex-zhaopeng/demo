package com.example.demo.service;

import com.example.demo.model.UserVo;

import java.util.List;

public interface UserService {
    /**
     * 根据用户Id查询所有的角色集合
     * @param username
     * @return
     */
    List<UserVo> findRolesByUsername(String username);

    UserVo findUserByUserUsername();



}
