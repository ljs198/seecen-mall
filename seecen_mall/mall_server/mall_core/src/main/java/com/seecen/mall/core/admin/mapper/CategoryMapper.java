package com.seecen.mall.core.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.PmsCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper
 *
 * 继承 BaseMapper<PmsCategory> 后，
 * selectList / insert / updateById / deleteById / selectById 等
 * 基础 CRUD 由 MyBatis-Plus 提供。
 *
 * 本阶段不写 XML，所有查询通过 LambdaQueryWrapper 拼装。
 */
@Mapper
public interface CategoryMapper extends BaseMapper<PmsCategory> {
}
