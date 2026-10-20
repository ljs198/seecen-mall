package com.seecen.mall.core.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.OmsOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据访问接口
 *
 * 继承 BaseMapper 后可对 oms_order 执行基础 CRUD 和分页查询。
 */
@Mapper
public interface OrderMapper extends BaseMapper<OmsOrder> {
}
