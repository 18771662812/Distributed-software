package org.example.shop.mapper;

import org.apache.ibatis.annotations.*;
import org.example.shop.entity.User;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 插入用户
     * @param user 用户
     * @return 影响行数
     */
    @Insert("INSERT INTO user (username, password, phone, email, create_time, update_time) VALUES (#{username}, #{password}, #{phone}, #{email}, #{createTime}, #{updateTime})")
    int insert(User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
}