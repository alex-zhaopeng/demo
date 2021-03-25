package com.example.demo.service.impl;

import com.example.demo.dao.UserDao;
import com.example.demo.model.UserVo;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserDao userDao;

    @Override
    public List<UserVo> findRolesByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public UserVo findUserByUserUsername() {
        return null;
    }
}
