package org.example.shop.common;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> {
    // 响应码（200成功，500失败）
    private Integer code;
    // 响应消息
    private String msg;
    // 响应数据
    private T data;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功（无数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 失败
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}