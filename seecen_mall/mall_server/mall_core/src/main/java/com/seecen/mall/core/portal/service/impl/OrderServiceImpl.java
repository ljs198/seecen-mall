package com.seecen.mall.core.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.OmsCartItem;
import com.seecen.mall.common.entity.OmsOrder;
import com.seecen.mall.common.entity.OmsOrderItem;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.CartMapper;
import com.seecen.mall.core.admin.mapper.OrderItemMapper;
import com.seecen.mall.core.admin.mapper.OrderMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.portal.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户端订单服务实现
 *
 * 关键规则：
 *   - 创建订单必须在一个事务中完成（库存扣减、订单主记录、订单明细、购物车清理）
 *   - 总金额由后端根据当前商品价格重新计算，不信任前端
 *   - 订单明细保存商品名称和价格快照，防止后续调价影响历史订单
 *   - 支付 / 取消 / 详情 / 列表 都必须先校验订单归属当前用户
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public OmsOrder create(Long userId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new BusinessException("请选择要购买的商品");
        }

        List<OmsCartItem> cartItems = cartMapper.selectBatchIds(cartItemIds);
        if (cartItems.size() != cartItemIds.size()) {
            throw new BusinessException("购物车项不存在");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OmsOrderItem> orderItems = new ArrayList<>();

        for (OmsCartItem cartItem : cartItems) {
            if (!cartItem.getUserId().equals(userId)) {
                throw new BusinessException("购物车项不属于当前用户");
            }

            PmsProduct product = productMapper.selectById(cartItem.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException("商品不存在或已下架");
            }
            if (product.getStock() == null || product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("商品 [" + product.getName() + "] 库存不足");
            }

            // 扣减库存：
            product.setStock(product.getStock() - cartItem.getQuantity());
            productMapper.updateById(product);  //  **********  修改操作
            //

            // 收集订单明细快照
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            BigDecimal itemAmount = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);
        }

        // 写订单主记录
        OmsOrder order = new OmsOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayStatus(0);
        orderMapper.insert(order);   //  ******  新增 订单 操作
        // 写订单明细
        for (OmsOrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);  // ***** 新增订单详情操作

        }

        // 清理已下单的购物车项
        cartMapper.deleteBatchIds(cartItemIds);   // ********  删除 购物车中 用户选择的购物车项

        return order;
    }

    @Override
    @Transactional
    public void pay(Long orderId, Long userId) {
        OmsOrder order = getOwnedOrder(orderId, userId);
        if (order.getPayStatus() != 0) {
            throw new BusinessException("订单状态异常，无法支付");
        }

        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 累加商品销量
        List<OmsOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId)
        );
        for (OmsOrderItem item : items) {
            PmsProduct product = productMapper.selectById(item.getProductId());
            if (product != null) {
                Integer salesCount = product.getSalesCount() == null ? 0 : product.getSalesCount();
                product.setSalesCount(salesCount + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    @Override
    public Page<Map<String, Object>> list(Long userId, int page, int size, Integer payStatus) {
        Page<OmsOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getUserId, userId);
        if (payStatus != null) {
            wrapper.eq(OmsOrder::getPayStatus, payStatus);
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
            Map<String, Object> map = orderToMap(order);
            List<OmsOrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, order.getId())
            );
            map.put("items", enrichOrderItems(items));
            records.add(map);
        }
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public Map<String, Object> getById(Long orderId, Long userId) {
        OmsOrder order = getOwnedOrder(orderId, userId);
        Map<String, Object> result = orderToMap(order);
        List<OmsOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId)
        );
        result.put("items", enrichOrderItems(items));
        return result;
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        OmsOrder order = getOwnedOrder(orderId, userId);
        if (order.getPayStatus() != 0) {
            throw new BusinessException("只能取消待支付订单");
        }

        order.setPayStatus(2);
        orderMapper.updateById(order);

        // 恢复库存
        List<OmsOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId)
        );
        for (OmsOrderItem item : items) {
            PmsProduct product = productMapper.selectById(item.getProductId());
            if (product != null) {
                Integer stock = product.getStock() == null ? 0 : product.getStock();
                product.setStock(stock + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    /**
     * 查询订单并校验归属，避免用户访问或操作别人的订单
     */
    private OmsOrder getOwnedOrder(Long orderId, Long userId) {
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /**
     * 为订单商品明细补充商品封面图，便于前端列表展示缩略图
     */
    private List<Map<String, Object>> enrichOrderItems(List<OmsOrderItem> items) {
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

    /**
     * 生成订单编号：时间戳 + 6位随机数
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return timestamp + random;
    }

    /**
     * 将订单实体转换为前端需要的 Map
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
}
