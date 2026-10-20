package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车项实体
 *
 * 每条记录表示某个用户购物车中的一个商品。
 * 商品名称、价格、封面、库存等展示字段来自 pms_product，
 * 本表只保存购物车自身的状态：数量和勾选。
 */
@Data
public class OmsCartItem {

    /** 购物车项主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 ID，对应 sys_user.id */
    private Long userId;

    /** 商品 ID，对应 pms_product.id */
    private Long productId;

    /** 商品购买数量 */
    private Integer quantity;

    /** 是否选中：1 选中，0 未选中 */
    private Integer checked;

    /** 加入购物车时间，由 MyBatis-Plus 自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
