package com.ryj.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standardised wrapper for HTTP responses returned by the REST controllers.
 *
 * @param <T> type of the payload that is being returned.
 */
public class ApiResponse<T> {

    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return failure(500, message, null);
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return failure(code, message, null);
    }

    public static <T> ApiResponse<T> failure(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
