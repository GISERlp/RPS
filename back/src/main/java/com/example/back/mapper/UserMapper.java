package com.example.back.mapper;

import com.example.back.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO `user` (username,password) VALUES (#{username},#{password})")
    void insert(User user);

    // 修正SQL语法，移除多余括号，统一参数名称
    @Select("SELECT * FROM user WHERE username = #{username} ORDER BY id DESC")
    User selectByUsername(String username);
}
