package com.seecen.mall.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.OmsOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 订单查询 Mapper。
 *
 * 被订单查询工具使用，根据订单号读取订单状态。
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OmsOrder> {
}
