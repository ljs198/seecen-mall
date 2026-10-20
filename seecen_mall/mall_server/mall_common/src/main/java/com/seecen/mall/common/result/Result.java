package com.seecen.mall.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 *
 * 示例：
 *   {"code":200,"message":"success","data":{"id":1,"name":"Tom"}}
 *
 * @param <T> data 字段类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private Integer code;
    private String  message;
    private T       data;

    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code    = code;
        this.message = message;
        this.data    = data;
    }

    // ==================== 成功 ====================

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败 ====================

    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.INTERNAL_ERROR.getCode(), ResultCode.INTERNAL_ERROR.getMessage(), null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.INTERNAL_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(ResultCode code) {
        return new Result<>(code.getCode(), code.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode code, String message) {
        return new Result<>(code.getCode(), message, null);
    }

    // ==================== 便捷判断 ====================

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}
