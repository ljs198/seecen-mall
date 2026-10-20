package com.seecen.mall.core.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsProduct;

import java.util.List;
import java.util.Map;

/**
 * 用户端商品浏览服务接口
 *
 * 提供公开的商品分页、商品详情和分类树能力。
 * 本阶段所有方法均为只读，不提供新增、编辑和删除。
 */
public interface PortalProductService {

    /**
     * 分页查询上架商品
     *
     * @param page       页码（从 1 开始）
     * @param size       每页数量
     * @param keyword    商品名称关键字（可空）
     * @param categoryId 分类 ID（可空，会自动包含子分类）
     */
    Page<PmsProduct> list(int page, int size, String keyword, Long categoryId);

    /**
     * 查询上架商品详情；下架或不存在时抛业务异常
     */
    PmsProduct getById(Long id);

    /**
     * 用户端分类树（与后台共用同一张表）
     */
    List<Map<String, Object>> categoryTree();
}
