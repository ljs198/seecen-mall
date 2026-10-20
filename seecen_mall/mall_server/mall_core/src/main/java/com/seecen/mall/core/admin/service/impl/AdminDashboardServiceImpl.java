package com.seecen.mall.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.common.entity.OmsOrder;
import com.seecen.mall.common.entity.SysUser;
import com.seecen.mall.core.admin.mapper.OrderMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.admin.service.AdminDashboardService;
import com.seecen.mall.core.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 后台仪表盘服务实现
 *
 * 关键规则：
 *   - 订单相关指标都只统计 pay_status = 1 的已支付订单
 *   - 商品总数走 productMapper.selectCount，依赖 MyBatis-Plus 自动过滤 deleted=0
 *   - 用户总数走 userMapper.selectCount，依赖 @TableLogic 过滤
 *   - 近 7 天按"自然日" 切分，每天 sales 是当天已支付订单的 totalAmount 之和
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM-dd");

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // 商品总数
        Long productCount = productMapper.selectCount(null);
        dashboard.put("productCount", productCount == null ? 0 : productCount);

        // 用户总数
        Long userCount = userMapper.selectCount(null);
        dashboard.put("userCount", userCount == null ? 0 : userCount);

        // 已支付订单数 + 总销售额
        List<OmsOrder> allPaid = orderMapper.selectList(
                new LambdaQueryWrapper<OmsOrder>().eq(OmsOrder::getPayStatus, 1)
        );
        dashboard.put("orderCount", (long) allPaid.size());
        BigDecimal totalSales = allPaid.stream()
                .map(OmsOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("totalSales", totalSales);

        // 今日（自然日 00:00:00 ~ 23:59:59）
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<OmsOrder> todayOrders = orderMapper.selectList(
                new LambdaQueryWrapper<OmsOrder>()
                        .eq(OmsOrder::getPayStatus, 1)
                        .between(OmsOrder::getPayTime, todayStart, todayEnd)
        );
        BigDecimal todaySales = todayOrders.stream()
                .map(OmsOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("todaySales", todaySales);
        dashboard.put("todayOrders", (long) todayOrders.size());

        // 近 7 天（含今日）：倒序生成 [6,5,4,3,2,1,0] 之前到今天
        List<Map<String, Object>> recent7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            List<OmsOrder> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<OmsOrder>()
                            .eq(OmsOrder::getPayStatus, 1)
                            .between(OmsOrder::getPayTime, dayStart, dayEnd)
            );
            BigDecimal daySales = dayOrders.stream()
                    .map(OmsOrder::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("date", date.format(DATE_FMT));
            day.put("sales", daySales);
            day.put("orders", (long) dayOrders.size());
            recent7Days.add(day);
        }
        dashboard.put("recent7Days", recent7Days);

        return dashboard;
    }
}
