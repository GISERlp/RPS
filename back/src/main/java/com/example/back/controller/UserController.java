package com.example.back.controller;

import com.example.back.dto.Result;
import com.example.back.entity.User;
import com.example.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.insertUser(user);
        return Result.success("success");
    }
}
