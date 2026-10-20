package com.seecen.mall.core.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.PmsCategory;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.CategoryMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.portal.service.PortalProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端商品浏览服务实现
 *
 * - list：固定 status=1 + 关键字/分类（包含子分类）筛选
 * - getById：下架或不存在抛业务异常
 * - categoryTree：复用分类数据组装树
 */
@Service
@RequiredArgsConstructor
public class PortalProductServiceImpl implements PortalProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    /**
     *
     * Page：
     * 当前页码
     * 记录数
     * 当前页的记录
     * 总记录数
     *
     *
     * @param page       页码（从 1 开始）
     * @param size       每页数量
     * @param keyword    商品名称关键字（可空）
     * @param categoryId 分类 ID（可空，会自动包含子分类）
     * @return
     */
    @Override
    public Page<PmsProduct> list(int page, int size, String keyword, Long categoryId) {
        Page<PmsProduct> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();
        // 用户端只看上架商品
        wrapper.eq(PmsProduct::getStatus, 1);

        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(PmsProduct::getName, keyword.trim());
        }
        if (categoryId != null) {
            // 包含当前分类 + 所有后代子分类的商品
            List<Long> categoryIds = getDescendantIds(categoryId);
            categoryIds.add(categoryId);
            wrapper.in(PmsProduct::getCategoryId, categoryIds);
        }

        wrapper.orderByDesc(PmsProduct::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public PmsProduct getById(Long id) {
        PmsProduct product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        return product;
    }

    @Override
    public List<Map<String, Object>> categoryTree() {
        List<PmsCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().orderByAsc(PmsCategory::getSort)
        );

        Map<Long, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();

        // 第一遍：每个分类建一个节点（含 children 空列表）
        for (PmsCategory c : allCategories) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", c.getId());
            node.put("name", c.getName());
            node.put("parentId", c.getParentId());
            node.put("sort", c.getSort());
            node.put("icon", c.getIcon());
            node.put("children", new ArrayList<>());
            nodeMap.put(c.getId(), node);
        }

        // 第二遍：按 parentId 挂到父节点下
        for (PmsCategory c : allCategories) {
            Map<String, Object> node = nodeMap.get(c.getId());
            if (c.getParentId() == null || c.getParentId() == 0L) {
                roots.add(node);
            } else {
                Map<String, Object> parent = nodeMap.get(c.getParentId());
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(node);
                } else {
                    roots.add(node);
                }
            }
        }

        return roots;
    }

    /**
     * 递归获取一个分类下所有后代分类的 ID
     */
    private List<Long> getDescendantIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        List<PmsCategory> children = categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().eq(PmsCategory::getParentId, parentId)
        );
        for (PmsCategory child : children) {
            result.add(child.getId());
            result.addAll(getDescendantIds(child.getId()));
        }
        return result;
    }
}
