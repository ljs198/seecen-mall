package com.seecen.mall.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.admin.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 后台商品管理服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductMapper productMapper;

    @Override
    public Page<PmsProduct> list(int page, int size, String keyword, Long categoryId, Integer status) {
        Page<PmsProduct> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(PmsProduct::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(PmsProduct::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(PmsProduct::getStatus, status);
        }
        wrapper.orderByDesc(PmsProduct::getCreateTime);

        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public PmsProduct getById(Long id) {
        PmsProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public void create(PmsProduct product) {
        if (product.getSalesCount() == null) {
            product.setSalesCount(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        productMapper.insert(product);
    }

    @Override
    public void update(PmsProduct product) {
        if (productMapper.selectById(product.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.updateById(product);
    }

    @Override
    public void toggleStatus(Long id) {
        PmsProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(product.getStatus() != null && product.getStatus() == 1 ? 0 : 1);
        productMapper.updateById(product);
    }
}
