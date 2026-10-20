package com.seecen.mall.core.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.admin.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台商品管理 Controller
 *
 * 前缀：/api/admin/products
 * 权限：必须 ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /** 分页查询商品 */
    @GetMapping
    public Result<Page<PmsProduct>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminProductService.list(page, size, keyword, categoryId, status));
    }

    /** 商品详情 */
    @GetMapping("/{id}")
    public Result<PmsProduct> getById(@PathVariable Long id) {
        return Result.success(adminProductService.getById(id));
    }

    /** 新增商品 */
    @PostMapping
    public Result<Void> create(@RequestBody PmsProduct product) {
        adminProductService.create(product);
        return Result.success(null, "创建成功");
    }

    /** 编辑商品 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PmsProduct product) {
        product.setId(id);
        adminProductService.update(product);
        return Result.success(null, "更新成功");
    }

    /** 切换上下架 */
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        adminProductService.toggleStatus(id);
        return Result.success(null, "状态已更新");
    }
}
