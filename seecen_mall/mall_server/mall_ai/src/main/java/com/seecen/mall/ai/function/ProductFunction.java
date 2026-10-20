package com.seecen.mall.ai.function;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.ai.mapper.ProductInfoMapper;
import com.seecen.mall.common.entity.PmsProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 商品查询工具。
 *
 * 当用户询问商品价格、库存或简介时，AI 可以调用此工具查询真实商品数据。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFunction {

    private final ProductInfoMapper productInfoMapper;

    /**
     * 查询上架商品。
     *
     * productName 为空字符串（或 null）时返回全部上架商品；否则按名称关键字模糊匹配。
     * 最多返回 5 条。
     *
     * @param productName 商品名称关键字，可空（空表示"列出全部上架商品"）
     * @return 商品信息列表（最多 5 条）
     */
    @Tool(description = "查询上架商品信息（名称、价格、库存、描述）。"
            + "商品名称关键字可空：传空字符串时返回全部上架商品，传关键字时按名称模糊匹配。最多 5 条。")
    public List<ProductInfo> getProductInfo(
            @ToolParam(description = "商品名称关键字，为空字符串时返回全部上架商品", required = false) String productName) {
        log.info("AI tool getProductInfo called, productName='{}'", productName);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsProduct::getStatus, 1);
        // 只有 productName 非空时才加 like 条件；空字符串 LIKE '%%' 会全表扫描且语义不清晰
        if (productName != null && !productName.isBlank()) {
            wrapper.like(PmsProduct::getName, productName);
        }
        wrapper.last("LIMIT 5");
        List<PmsProduct> products = productInfoMapper.selectList(wrapper);
        return products.stream()
                .map(p -> new ProductInfo(
                        p.getName(),
                        p.getPrice() != null ? p.getPrice().doubleValue() : 0d,
                        p.getStock() != null ? p.getStock() : 0,
                        p.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * 返回给 AI 的商品数据结构。
     *
     * @param name        商品名称
     * @param price       商品价格
     * @param stock       商品库存
     * @param description 商品描述
     */
    public record ProductInfo(String name, double price, int stock, String description) {
    }
}
