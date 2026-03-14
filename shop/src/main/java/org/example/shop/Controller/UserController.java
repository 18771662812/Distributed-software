package org.example.shop.Controller;

import org.example.shop.common.Result;
import org.example.shop.dto.LoginResponse;
import org.example.shop.entity.User;
import org.example.shop.service.UserService;
import org.example.shop.util.TokenUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败，用户名已存在或参数错误");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.error("登录失败，用户名或密码错误");
        }

        LoginResponse response = new LoginResponse(
                TokenUtils.generateToken(loginUser),
                new LoginResponse.UserInfo(
                        loginUser.getId(),
                        loginUser.getUsername(),
                        loginUser.getPhone(),
                        loginUser.getEmail()
                )
        );
        return Result.success(response);
    }
}
