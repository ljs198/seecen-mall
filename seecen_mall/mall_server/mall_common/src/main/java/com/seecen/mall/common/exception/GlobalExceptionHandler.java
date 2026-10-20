package com.seecen.mall.common.exception;

import com.seecen.mall.common.result.Result;
import com.seecen.mall.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * 作用：把 Controller 抛出的异常转换为统一格式的 Result JSON，
 *       避免 Spring 默认把异常渲染成 Whitelabel Error Page。
 *
 * 处理范围：
 *   1. BusinessException                —— 业务异常（自定义 code + message）
 *   2. 参数校验失败                      —— 400
 *   3. 请求方法 / 请求参数 / 请求体问题   —— 400 / 405
 *   4. 其它未预期异常                    —— 500（兜底）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** @Valid 校验失败：@RequestBody DTO */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /** @Valid 校验失败：表单绑定 */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /** @Validated 校验失败：@RequestParam / @PathVariable */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束校验失败: {}", msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /** 请求方法不支持（如用 POST 访问一个只允许 GET 的接口） */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return Result.fail(ResultCode.METHOD_NOT_ALLOWED);
    }

    /** 必填请求参数缺失 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "缺少必填参数: " + e.getParameterName();
        log.warn(msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /** 请求参数类型不匹配（如把字符串塞给 Long） */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = "参数类型错误: " + e.getName();
        log.warn(msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /** 请求体不是合法 JSON */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体不可读: {}", e.getMessage());
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    /**
     * 未登录/会话过期。
     *
     * Controller 里 getCurrentUserId(request) 等工具方法会用
     * {@code throw new IllegalStateException("未登录或登录已过期")} 抛出。
     * 这里专门捕获，转成 401 而不是 500，避免 SSE 等端点（permitAll 但仍需登录的）
     * 把"未登录"误报为服务器异常。
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<Void> handleIllegalState(IllegalStateException e) {
        if (e.getMessage() != null && e.getMessage().contains("未登录")) {
            return Result.fail(ResultCode.UNAUTHORIZED);
        }
        log.error("状态异常", e);
        return Result.fail(ResultCode.INTERNAL_ERROR);
    }

    /** 兜底：未预期的异常（最具体的 handler 都没命中时才会走这里） */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ResultCode.INTERNAL_ERROR);
    }
}
