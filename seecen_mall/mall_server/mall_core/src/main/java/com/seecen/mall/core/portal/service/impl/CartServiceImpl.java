package com.seecen.mall.core.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.common.entity.OmsCartItem;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.CartMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.portal.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端购物车服务实现
 *
 * 关键校验：
 *   - 商品必须存在且 status=1
 * - 商品库存必须 >= 本次累加后的数量
 * - 更新/删除时必须确认购物车项属于当前用户
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public List<Map<String, Object>> list(Long userId) {
        List<OmsCartItem> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .orderByDesc(OmsCartItem::getCreateTime)
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (OmsCartItem item : cartItems) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("userId", item.getUserId());
            row.put("productId", item.getProductId());
            row.put("quantity", item.getQuantity());
            row.put("checked", item.getChecked());
            row.put("createTime", item.getCreateTime());

            PmsProduct product = productMapper.selectById(item.getProductId());
            if (product != null) {
                row.put("productName", product.getName());
                row.put("productPrice", product.getPrice());
                row.put("productCoverImage", product.getCoverImage());
                row.put("productStock", product.getStock());
                row.put("productStatus", product.getStatus());
            }
            result.add(row);
        }
        return result;
    }

    @Override
    public void add(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("商品数量必须大于0");
        }

        PmsProduct product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }

        OmsCartItem existing = cartMapper.selectOne(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .eq(OmsCartItem::getProductId, productId)
        );

        if (existing != null) {
            int nextQuantity = existing.getQuantity() + quantity;
            if (nextQuantity > product.getStock()) {
                throw new BusinessException("商品库存不足");
            }
            existing.setQuantity(nextQuantity);
            existing.setChecked(1);
            cartMapper.updateById(existing);
        } else {
            OmsCartItem item = new OmsCartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setChecked(1);
            cartMapper.insert(item);
        }
    }

    @Override
    public void update(Long id, Long userId, Integer quantity, Integer checked) {
        OmsCartItem item = cartMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }

        if (quantity != null) {
            if (quantity <= 0) {
                throw new BusinessException("商品数量必须大于0");
            }
            PmsProduct product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStock() == null || quantity > product.getStock()) {
                throw new BusinessException("商品库存不足");
            }
            item.setQuantity(quantity);
        }
        if (checked != null) {
            item.setChecked(checked == 1 ? 1 : 0);
        }
        cartMapper.updateById(item);
    }

    @Override
    public void delete(Long id, Long userId) {
        OmsCartItem item = cartMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartMapper.deleteById(id);
    }
}
