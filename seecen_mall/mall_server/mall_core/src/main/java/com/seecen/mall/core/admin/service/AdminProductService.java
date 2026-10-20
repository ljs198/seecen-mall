package com.seecen.mall.core.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsProduct;

/**
 * 后台商品管理服务接口
 *
 * - list：分页 + 关键字 / 分类 / 状态筛选
 * - getById：详情
 * - create / update：新增 / 编辑
 * - toggleStatus：上下架切换
 */
public interface AdminProductService {

    /**
     * 分页查询商品
     *
     * @param page       页码（从 1 开始）
     * @param size       每页数量
     * @param keyword    商品名称关键字（可空）
     * @param categoryId 分类 ID（可空）
     * @param status     上下架状态（可空）
     */
    Page<PmsProduct> list(int page, int size, String keyword, Long categoryId, Integer status);

    /**
     * 商品详情（不存在抛业务异常）
     */
    PmsProduct getById(Long id);

    /**
     * 新增商品（自动补默认 salesCount=0 / status=1）
     */
    void create(PmsProduct product);

    /**
     * 编辑商品（不存在抛业务异常）
     */
    void update(PmsProduct product);

    /**
     * 上下架切换
     */
    void toggleStatus(Long id);
}
