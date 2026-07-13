package com.caizhilian.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一API响应格式
 * 对应文档中定义的返回格式：
 * {
 *   "code": "6100000",
 *   "message": "操作成功",
 *   "data": [...]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>("6100000", "操作成功", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>("6100000", message, data);
    }

    public static <T> ApiResult<T> error(String code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> validateError(String message) {
        return new ApiResult<>("6100010", message, null);
    }

    public static <T> ApiResult<T> networkError(String message) {
        return new ApiResult<>("6100020", message, null);
    }
}
