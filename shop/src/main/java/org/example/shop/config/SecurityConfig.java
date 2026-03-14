package org.example.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类（适配SpringBoot 3.x）
 * 放行注册、登录接口，其他接口暂时也放行（后续可根据需求调整）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置接口的安全规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF（跨站请求伪造），方便Postman测试（生产环境需根据情况开启）
                .csrf(csrf -> csrf.disable())
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行注册、登录接口，允许匿名访问
                        .requestMatchers("/user/register", "/user/login").permitAll()
                        // 其他所有请求暂时也放行（后续可改为authenticated()要求登录）
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}