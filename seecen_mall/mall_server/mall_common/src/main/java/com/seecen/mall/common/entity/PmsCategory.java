package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品分类实体（对应 pms_category 表）
 *
 * 通过 parentId 表达父子关系：parentId = 0 表示顶级分类。
 *
 * 字段说明：
 *   - id           主键，自增
 *   - name         分类名称
 *   - parentId     父分类 ID，0 表示顶级
 *   - sort         排序值，越小越靠前
 *   - icon         Element Plus 图标名称（用于后台菜单/前端展示）
 *   - deleted      逻辑删除：0=未删 1=已删（MyBatis-Plus 自动过滤）
 *   - createTime   创建时间（由 MetaObjectHandler 自动填充）
 */
@Data
public class PmsCategory implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private Integer sort;

    private String icon;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
