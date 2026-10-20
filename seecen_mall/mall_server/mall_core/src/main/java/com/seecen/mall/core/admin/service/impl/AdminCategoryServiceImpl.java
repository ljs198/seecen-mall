package com.seecen.mall.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.common.entity.PmsCategory;
import com.seecen.mall.common.entity.PmsProduct;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.mapper.CategoryMapper;
import com.seecen.mall.core.admin.mapper.ProductMapper;
import com.seecen.mall.core.admin.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台商品分类服务实现
 *
 * - tree()：查全部分类 → 转节点 Map → 二次循环建立父子关系
 * - delete()：删除前检查子分类、商品占用，业务规则在这里集中维护
 */
@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper   productMapper;

    @Override
    public List<Map<String, Object>> tree() {
        List<PmsCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().orderByAsc(PmsCategory::getSort)
        );

        Map<Long, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();

        // 第一遍：每个分类建一个节点（含 children 空列表）
        for (PmsCategory c : all) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", c.getId());
            node.put("name", c.getName());
            node.put("parentId", c.getParentId());
            node.put("sort", c.getSort());
            node.put("icon", c.getIcon());
            node.put("createTime", c.getCreateTime());
            node.put("children", new ArrayList<>());
            nodeMap.put(c.getId(), node);
        }

        // 第二遍：按 parentId 挂到父节点下；parentId=0 或找不到父节点的当作顶级
        for (PmsCategory c : all) {
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

    @Override
    public void create(PmsCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.insert(category);
    }

    @Override
    public void update(PmsCategory category) {
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }

        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<PmsCategory>().eq(PmsCategory::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<PmsProduct>().eq(PmsProduct::getCategoryId, id)
        );
        if (productCount > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }

        categoryMapper.deleteById(id);
    }
}
