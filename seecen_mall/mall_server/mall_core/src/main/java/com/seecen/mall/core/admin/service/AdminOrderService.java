package com.seecen.mall.core.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

/**
 * 后台订单服务接口
 *
 * - list：分页查询所有订单，支持按订单号和状态筛选
 * - getById：查询订单详情（含商品明细）
 * - getStats：聚合已支付订单，输出总销售额、今日销售额、今日订单数和近 7 天趋势
 */
public interface AdminOrderService {

    /**
      * 分页查询订单列表
      */
    Page<Map<String, Object>> list(int page, int size, Integer payStatus, String orderNo);

    /**
      * 查询订单详情（含商品明细快照）
      */
    Map<String, Object> getById(Long orderId);

    /**
      * 聚合订单统计
      */
    Map<String, Object> getStats();
}
