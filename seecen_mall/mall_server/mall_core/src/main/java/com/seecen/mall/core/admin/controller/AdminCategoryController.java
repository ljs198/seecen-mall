package com.seecen.mall.core.admin.controller;

import com.seecen.mall.common.entity.PmsCategory;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.admin.service.AdminCategoryService;
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
 * 后台商品分类管理 Controller
 *
 * 前缀：/api/admin/categories
 * 权限：必须 ROLE_ADMIN（由 SecurityConfig 统一要求）
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    /** 查询分类树 */
    @GetMapping
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(adminCategoryService.tree());
    }

    /** 新增分类 */
    @PostMapping
    public Result<Void> create(@RequestBody PmsCategory category) {
        adminCategoryService.create(category);
        return Result.success(null, "创建成功");
    }

    /** 更新分类 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PmsCategory category) {
        category.setId(id);
        adminCategoryService.update(category);
        return Result.success(null, "更新成功");
    }

    /** 删除分类 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return Result.success(null, "删除成功");
    }
}
