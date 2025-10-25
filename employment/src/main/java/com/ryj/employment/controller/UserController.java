package com.ryj.employment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ryj.employment.common.Result;
import com.ryj.employment.entity.User;
import com.ryj.employment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }
    
    @GetMapping("/{userId}")
    public User getById(@PathVariable Integer userId) {
        return userService.getById(userId);
    }
    
    // 用户登录 - 使用POST方法处理登录请求
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        try {
            if (user == null || user.getUsername() == null || user.getPassword() == null) {
                return Result.fail("用户名或密码不能为空");
            }
            
            // 添加查询条件
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("username", user.getUsername()).
                    eq("password", user.getPassword());
            // 根据条件，查询出一个用户
            User loginUser = userService.getOne(qw);
            if (loginUser != null) { // 用户名密码同时都对， 找到了用户信息
                loginUser.setPassword(null);  // 去掉密码
                return Result.success(loginUser);
            } else {
                return Result.fail("用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("登录失败，请稍后重试");
        }
    }
    
    // 处理GET请求，提供友好提示
    @GetMapping("/login")
    public Result<String> loginGet() {
        return Result.fail("请使用POST方法访问此接口进行登录。您可以访问 /login.html 测试登录功能。");
    }
    
    // 用户注册 - 使用POST方法
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            // 参数校验
            if (user == null || user.getUsername() == null || user.getPassword() == null) {
                return Result.fail("用户名和密码不能为空");
            }
            
            // 检查用户名是否已存在
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("username", user.getUsername());
            User existingUser = userService.getOne(qw);
            if (existingUser != null) {
                return Result.fail("用户名已存在");
            }
            
            // 设置默认值
            user.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
            user.setState(1); // 1表示正常状态
            user.setPhoneState(0); // 0表示未认证
            user.setEmailState(0); // 0表示未认证
            user.setUserGroup("user"); // 默认普通用户组
            
            // 保存用户
            boolean saveResult = userService.save(user);
            if (saveResult) {
                // 注册成功，隐藏密码
                User savedUser = userService.getOne(qw);
                savedUser.setPassword(null);
                return Result.success(savedUser);
            } else {
                return Result.fail("注册失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("注册过程中发生错误");
        }
    }
    
    // 根据用户组查询用户 - 使用GET方法和路径参数
    @GetMapping("/group/{userGroup}")
    public Result<List<User>> queryByUserGroup(@PathVariable String userGroup) {
        try {
            if (userGroup == null) {
                return Result.fail("用户组不能为空");
            }
            
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("user_group", userGroup);
            // 查询到的可能是多个用户信息， 所以需要List来存储数据
            List<User> list = userService.list(qw);
            // 去掉密码
            for (User u : list) {
                u.setPassword(null);
            }
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询用户失败，请稍后重试");
        }
    }
}