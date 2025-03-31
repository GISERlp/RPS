package com.example.back.controller;

import cn.hutool.core.util.StrUtil;
import com.example.back.dto.Result;
import com.example.back.service.UserService;
import com.example.back.entity.User;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@CrossOrigin(origins = "*")
@RestController
public class WebController {
    @Resource
    UserService userService;

    @GetMapping("/hello")
    public Result hello() {return Result.success("success");}

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if(StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            return  Result.error("error:数据输入不合法");
        }
        user = userService.login(user);
        return Result.success("登陆成功！");
    }

}