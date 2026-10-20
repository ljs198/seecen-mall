package com.seecen.mall.core.portal.controller;

import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.portal.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户端购物车 Controller
 *
 * 前缀：/api/portal/cart
 * 权限：必须登录（由 SecurityConfig 中 /api/portal/** 的 authenticated 规则保证）
 *
 * 当前用户 ID 从 JwtAuthFilter 写入的 request 属性里读取，
 * 避免被前端伪造。
 */
@RestController
@RequestMapping("/api/portal/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** 查询当前用户购物车 */
    @GetMapping
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        return Result.success(cartService.list(getCurrentUserId(request)));
    }

    /** 添加商品到当前用户购物车 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.add(getCurrentUserId(request), productId, quantity);
        return Result.success(null, "已加入购物车");
    }

    /** 更新购物车项的数量或选中状态 */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer quantity = body.get("quantity") == null ? null : Integer.valueOf(body.get("quantity").toString());
        Integer checked = body.get("checked") == null ? null : Integer.valueOf(body.get("checked").toString());
        cartService.update(id, getCurrentUserId(request), quantity, checked);
        return Result.success();
    }

    /** 删除当前用户购物车项 */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        cartService.delete(id, getCurrentUserId(request));
        return Result.success(null, "删除成功");
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
