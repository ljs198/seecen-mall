package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体
 *
 * 每条记录代表当前用户的一次下单行为，订单中的商品明细保存在 oms_order_item。
 */
@Data
public class OmsOrder {

    /** 订单主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号，用于页面展示和用户查询 */
    private String orderNo;

    /** 下单用户 ID，对应 sys_user.id */
    private Long userId;

    /** 后端计算得到的订单总金额 */
    private BigDecimal totalAmount;

    /** 支付状态：0 待支付，1 已支付，2 已取消 */
    private Integer payStatus;

    /** 支付时间，未支付时为空 */
    private LocalDateTime payTime;

    /** 逻辑删除标记：0 未删除，1 已删除 */
    @TableLogic
    private Integer deleted;

    /** 下单时间，由 MyBatis-Plus 自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 最后更新时间，由 MyBatis-Plus 自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
