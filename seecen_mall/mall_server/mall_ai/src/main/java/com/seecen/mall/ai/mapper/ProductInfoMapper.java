package com.seecen.mall.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.PmsProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 商品查询 Mapper。
 *
 * 被商品查询工具使用，只读取 pms_product 中的上架商品信息。
 */
@Mapper
public interface ProductInfoMapper extends BaseMapper<PmsProduct> {
}
