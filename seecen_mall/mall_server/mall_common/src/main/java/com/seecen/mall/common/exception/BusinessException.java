package com.seecen.mall.common.exception;

import com.seecen.mall.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * 用法：
 *   throw new BusinessException("用户不存在");
 *   throw new BusinessException(ResultCode.FORBIDDEN);
 *   throw new BusinessException(400, "参数错误");
 *
 * 会被 GlobalExceptionHandler 捕获并转换为统一格式的 Result JSON 返回。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public BusinessException(ResultCode code, String message) {
        super(message);
        this.code = code.getCode();
    }
}
