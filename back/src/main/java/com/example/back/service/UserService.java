package com.example.back.service;

import com.example.back.exception.ServiceException;
import com.example.back.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.back.mapper.UserMapper;

import javax.annotation.Resource;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    //验证用户账号是否合法
    public User login(User user) {
//        根据用户名查询数据库的信息
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if(dbUser == null){
            //抛出一个自定义的异常
            throw new ServiceException("用户账号不存在！");

        }
        if (!user.getPassword().equals(dbUser.getPassword())) {
            throw new ServiceException("用户名或密码错误！");
        }

        return dbUser;
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }
}
