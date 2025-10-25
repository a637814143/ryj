package com.ryj.employment.common;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> {
    private Integer code; // 状态码 0:成功 1:失败
    private String message; // 响应消息
    private T data; // 响应数据

    // 成功响应
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200); // 使用200表示成功，与前端判断条件匹配
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 成功响应（带自定义消息）
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 失败响应
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        return result;
    }
}