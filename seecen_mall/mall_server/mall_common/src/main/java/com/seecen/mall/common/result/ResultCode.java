package com.seecen.mall.common.result;

import lombok.Getter;

/**
 * 业务响应码枚举
 *
 * 编码区间约定（与 HTTP 状态码语义保持一致）：
 *   2xx —— 成功
 *   4xx —— 客户端错误（参数、权限、未登录等）
 *   5xx —— 服务端错误
 */
@Getter
public enum ResultCode {

    SUCCESS                    (200, "success"),
    BAD_REQUEST                (400, "请求参数不合法"),
    UNAUTHORIZED               (401, "未登录或登录已过期"),
    FORBIDDEN                  (403, "无权限访问"),
    NOT_FOUND                  (404, "资源不存在"),
    METHOD_NOT_ALLOWED         (405, "请求方法不支持"),

    INTERNAL_ERROR             (500, "服务器内部错误"),
    BUSINESS_ERROR             (500, "业务异常");

    private final Integer code;
    private final String  message;

    ResultCode(Integer code, String message) {
        this.code    = code;
        this.message = message;
    }
}
