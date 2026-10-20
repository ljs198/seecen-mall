package com.seecen.mall.core.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.portal.service.PortalProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户端商品浏览 Controller
 *
 * 前缀：/api/portal
 * 权限：商品浏览接口（products / categories）公开；其它 portal 接口仍需登录
 */
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalProductController {

    private final PortalProductService portalProductService;

    /** 用户端分页查询上架商品（公开） */
    @GetMapping("/products")
    public Result<Page<PmsProduct>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(portalProductService.list(page, size, keyword, categoryId));
    }

    /** 用户端查询上架商品详情（公开，下架返回业务错误） */
    @GetMapping("/products/{id}")
    public Result<PmsProduct> getById(@PathVariable Long id) {
        return Result.success(portalProductService.getById(id));
    }

    /** 用户端分类树（公开） */
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> categoryTree() {
        return Result.success(portalProductService.categoryTree());
    }
}
