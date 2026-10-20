package com.seecen.mall.core.admin.service;

import java.util.Map;

/**
 * 后台仪表盘服务接口
 *
 * 仪表盘不维护独立的统计表，所有指标都从商品表和订单表实时聚合。
 */
public interface AdminDashboardService {

    /**
      * 获取仪表盘统计数据
      *
      * @return 包含商品总数、订单总数、总销售额、今日销售额、今日订单数、近 7 天趋势
      */
    Map<String, Object> getDashboard();
}
