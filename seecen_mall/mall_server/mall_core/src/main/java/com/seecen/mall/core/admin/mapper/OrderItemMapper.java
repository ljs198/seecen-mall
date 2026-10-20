package com.seecen.mall.core.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.OmsOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细数据访问接口
 *
 * 继承 BaseMapper 后可按 orderId 查询订单中的商品明细。
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OmsOrderItem> {
}
