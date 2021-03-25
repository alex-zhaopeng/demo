package com.example.demo.dao;

import com.example.demo.model.UserVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {

    public List<UserVo> findUserByUsername(String username);

}
