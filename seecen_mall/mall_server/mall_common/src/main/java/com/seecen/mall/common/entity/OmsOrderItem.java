package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细实体
 *
 * 每条记录代表一个订单中的一个商品，并保存下单时的商品名称和价格快照，
 * 保证历史订单不因商品改名或调价而变化。
 */
@Data
public class OmsOrderItem {

    /** 订单明细主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属订单 ID，对应 oms_order.id */
    private Long orderId;

    /** 商品 ID，对应 pms_product.id */
    private Long productId;

    /** 下单时的商品名称快照 */
    private String productName;

    /** 下单时的商品单价快照 */
    private BigDecimal productPrice;

    /** 本商品购买数量 */
    private Integer quantity;
}
