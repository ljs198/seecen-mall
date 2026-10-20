package com.seecen.mall.core.portal.service;

import java.util.List;
import java.util.Map;

/**
 * 用户端购物车服务接口
 *
 * 提供当前用户购物车的查询、添加、修改和删除能力。
 * 业务校验在实现类里完成，Controller 只负责取当前用户 ID 后转发。
 */
public interface CartService {

    /**
     * 查询指定用户的购物车列表（附带商品展示信息）
     */
    List<Map<String, Object>> list(Long userId);

    /**
     * 添加商品到购物车（同一商品累加数量）
     */
    void add(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车项的数量或选中状态
     */
    void update(Long id, Long userId, Integer quantity, Integer checked);

    /**
     * 删除购物车项（校验归属）
     */
    void delete(Long id, Long userId);
}
