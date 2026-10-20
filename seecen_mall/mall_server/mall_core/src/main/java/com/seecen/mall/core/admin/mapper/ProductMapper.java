package com.seecen.mall.core.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.PmsProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 *
 * 继承 BaseMapper<PmsProduct> 后，
 * selectList / insert / updateById / deleteById / selectById /
 * selectPage 等基础能力由 MyBatis-Plus 提供。
 */
@Mapper
public interface ProductMapper extends BaseMapper<PmsProduct> {
}
