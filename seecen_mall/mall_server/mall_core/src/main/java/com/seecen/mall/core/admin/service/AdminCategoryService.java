package com.seecen.mall.core.admin.service;

import com.seecen.mall.common.entity.PmsCategory;

import java.util.List;
import java.util.Map;

/**
 * 后台商品分类服务接口
 *
 * 定义分类树查询和分类维护能力，具体业务规则由实现类负责。
 */
public interface AdminCategoryService {

    /**
     * 查询分类树（按 parentId 组织）
     */
    List<Map<String, Object>> tree();

    /**
     * 新增商品分类
     */
    void create(PmsCategory category);

    /**
     * 更新商品分类
     */
    void update(PmsCategory category);

    /**
     * 删除商品分类
     *
     * 删除前会校验：分类不存在 / 有子分类 / 有商品，三种情况都会抛出业务异常。
     */
    void delete(Long id);
}
