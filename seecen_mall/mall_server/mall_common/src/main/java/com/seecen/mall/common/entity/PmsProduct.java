package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体（对应 pms_product 表）
 *
 * 保存商品基础管理信息（名称、售价、原价、库存、状态等），
 * 供管理后台维护。本阶段不实现图片上传，coverImage 字段手动填写。
 *
 * 字段说明：
 *   - id            主键，自增
 *   - name          商品名称
 *   - categoryId    所属分类 ID
 *   - price         当前售价（BigDecimal，避免 double 精度问题）
 *   - originalPrice 原价（划线价）
 *   - stock         当前库存
 *   - coverImage    封面图地址
 *   - description   简要描述
 *   - detail        详情内容
 *   - status        上下架：1=上架 0=下架
 *   - salesCount    累计销量
 *   - deleted       逻辑删除标记
 *   - createTime    创建时间（自动填充）
 *   - updateTime    更新时间（自动填充）
 */
@Data
public class PmsProduct implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String coverImage;

    private String description;

    private String detail;

    private Integer status;

    private Integer salesCount;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
