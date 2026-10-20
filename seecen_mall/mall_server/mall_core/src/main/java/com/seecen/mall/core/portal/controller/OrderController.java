package com.seecen.mall.core.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.OmsOrder;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.portal.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户端订单 Controller
 *
 * 前缀：/api/portal/orders
 * 权限：必须登录（由 SecurityConfig 中 /api/portal/** 的 authenticated 规则保证）
 *
 * 当前用户 ID 从 JwtAuthFilter 写入的 request 属性里读取。
 */
@RestController
@RequestMapping("/api/portal/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** 从购物车项创建订单 */
    @PostMapping
    public Result<OmsOrder> create(@RequestBody Map<String, List<Long>> body, HttpServletRequest request) {
        List<Long> cartItemIds = body.get("cartItemIds");
        OmsOrder order = orderService.create(getCurrentUserId(request), cartItemIds);
        return Result.success(order);
    }

    /** 模拟支付当前用户的待支付订单 */
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, HttpServletRequest request) {
        orderService.pay(id, getCurrentUserId(request));
        return Result.success(null, "支付成功");
    }

    /** 取消当前用户的待支付订单 */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        orderService.cancel(id, getCurrentUserId(request));
        return Result.success(null, "订单已取消");
    }

    /** 分页查询当前用户订单列表 */
    @GetMapping
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer payStatus,
            HttpServletRequest request) {
        Page<Map<String, Object>> result = orderService.list(
                getCurrentUserId(request), page, size, payStatus);
        return Result.success(result);
    }

    /** 查询当前用户订单详情 */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> result = orderService.getById(id, getCurrentUserId(request));
        return Result.success(result);
    }

    /**
     * 从 JwtAuthFilter 写入的 request 属性中取出当前用户 ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        Object value = request.getAttribute("currentUserId");
        if (value == null) {
            throw new IllegalStateException("未登录或登录已过期");
        }
        return (Long) value;
    }
}
