package com.seecen.mall.core.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.OmsCartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车数据访问接口
 *
 * 继承 BaseMapper 后，可直接使用 selectList、insert、updateById、deleteById 等基础方法。
 * 业务校验（在哪个用户、库存是否足够等）放到 Service 层。
 */
@Mapper
public interface CartMapper extends BaseMapper<OmsCartItem> {
}
