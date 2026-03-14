package org.example.shop.service.impl;

import org.example.shop.entity.User;
import org.example.shop.mapper.UserMapper;
import org.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

/**
 * 用户业务层实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 密码加密器（BCrypt是SpringSecurity推荐的加密方式）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册逻辑
     */
    @Override
    public boolean register(User user) {
        // 1. 参数校验
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return false;
        }

        // 2. 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }

        // 3. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 设置时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 5. 保存用户信息
        return userMapper.insert(user) > 0;
    }

    /**
     * 用户登录逻辑
     */
    @Override
    public User login(String username, String password) {
        // 1. 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }

        // 2. 根据用户名查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }

        // 3. 密码校验（明文密码与加密密码比对）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // 4. 登录成功，返回用户信息（注意：密码不返回给前端，这里可以封装VO，简化版先直接返回）
        user.setPassword(null); // 清空密码，避免泄露
        return user;
    }
}