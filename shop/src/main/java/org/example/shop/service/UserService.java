package org.example.shop.service;

import org.example.shop.entity.User;

/**
 * 用户业务层接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param user 用户信息（用户名、密码必填）
     * @return 注册是否成功
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(String username, String password);
}