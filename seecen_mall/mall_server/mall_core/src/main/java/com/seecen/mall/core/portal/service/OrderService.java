package com.seecen.mall.core.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.OmsOrder;

import java.util.List;
import java.util.Map;

/**
 * 用户端订单服务接口
 *
 * 定义普通用户创建订单、支付订单、取消订单和查询订单的业务能力。
 */
public interface OrderService {

    /**
     * 根据当前用户选中的购物车项创建订单。
     * 整个流程在事务中完成：扣减库存 -> 写入订单主记录 -> 写入订单明细 -> 清理已下单购物车项。
     */
    OmsOrder create(Long userId, List<Long> cartItemIds);

    /** 模拟支付待支付订单（校验归属 + 校验状态 + 累加销量） */
    void pay(Long orderId, Long userId);

    /** 分页查询当前用户的订单列表 */
    Page<Map<String, Object>> list(Long userId, int page, int size, Integer payStatus);

    /** 查询当前用户的订单详情 */
    Map<String, Object> getById(Long orderId, Long userId);

    /** 取消待支付订单并恢复库存 */
    void cancel(Long orderId, Long userId);
}
