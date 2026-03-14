package org.example.shop.Controller;

import org.example.shop.common.Result;
import org.example.shop.entity.User;
import org.example.shop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制层（注册、登录接口）
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 构造器注入（SpringBoot推荐方式）
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在或参数错误");
        }
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            return Result.success(loginUser);
        } else {
            return Result.error("登录失败，用户名或密码错误");
        }
    }
}