package com.seecen.mall.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.OmsOrder;
import com.seecen.mall.common.entity.OmsOrderItem;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.OrderItemMapper;
import com.seecen.mall.core.admin.mapper.OrderMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.admin.service.AdminOrderService;
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
 * 后台订单服务实现
 *
 * 关键点：
 *   - 后台订单列表不需要用户归属校验，因为只对管理员开放
 *   - 统计只统计 pay_status = 1 的已支付订单，避免把待支付和已取消的金额算进去
 *   - 订单详情复用阶段 7 的订单明细快照，但额外补出 productImage 方便后台展示
 */
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM-dd");

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    public Page<Map<String, Object>> list(int page, int size, Integer payStatus, String orderNo) {
        Page<OmsOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        if (payStatus != null) {
            wrapper.eq(OmsOrder::getPayStatus, payStatus);
        }
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(OmsOrder::getOrderNo, orderNo.trim());
        }
        wrapper.orderByDesc(OmsOrder::getCreateTime);

        Page<OmsOrder> orderPage = orderMapper.selectPage(pageParam, wrapper);
        Page<Map<String, Object>> resultPage = new Page<>(
                orderPage.getCurrent(),
                orderPage.getSize(),
                orderPage.getTotal()
        );
        List<Map<String, Object>> records = new ArrayList<>();
        for (OmsOrder order : orderPage.getRecords()) {
            records.add(orderToMap(order));
        }
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public Map<String, Object> getById(Long orderId) {
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Map<String, Object> result = orderToMap(order);
        List<OmsOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId)
        );
        result.put("items", enrichItems(items));
        return result;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // 总订单数（已支付）
        Long paidOrderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<OmsOrder>().eq(OmsOrder::getPayStatus, 1)
        );
        dashboard.put("orderCount", paidOrderCount == null ? 0 : paidOrderCount);

        // 总销售额（已支付）
        List<OmsOrder> allPaid = orderMapper.selectList(
                new LambdaQueryWrapper<OmsOrder>().eq(OmsOrder::getPayStatus, 1)
        );
        BigDecimal totalSales = allPaid.stream()
                .map(OmsOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("totalSales", totalSales);

        // 今日
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

        // 近 7 天
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

    /**
     * 把订单实体转成前端友好的 Map
     */
    private Map<String, Object> orderToMap(OmsOrder order) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("userId", order.getUserId());
        map.put("totalAmount", order.getTotalAmount());
        map.put("payStatus", order.getPayStatus());
        map.put("payTime", order.getPayTime());
        map.put("createTime", order.getCreateTime());
        map.put("updateTime", order.getUpdateTime());
        return map;
    }

    /**
     * 为订单明细补出商品封面图
     */
    private List<Map<String, Object>> enrichItems(List<OmsOrderItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (OmsOrderItem item : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("productId", item.getProductId());
            map.put("productName", item.getProductName());
            map.put("productPrice", item.getProductPrice());
            map.put("quantity", item.getQuantity());
            PmsProduct product = productMapper.selectById(item.getProductId());
            map.put("productImage", product != null ? product.getCoverImage() : null);
            result.add(map);
        }
        return result;
    }
}
