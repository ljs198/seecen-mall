package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体（对应 sys_user 表）
 *
 * 登录注册与权限认证
 *   - 管理员与普通用户共用这张表，通过 role 字段区分
 *   - role: ADMIN / USER
 *   - status: 1=正常, 0=禁用
 *   - deleted: 0=未删除, 1=已删除（MyBatis-Plus 逻辑删除）
 *
 * 字段说明：
 *   - id           主键，自增
 *   - username     登录名，唯一
 *   - password     BCrypt 加密后的密码（绝不存明文）
 *   - nickname     昵称
 *   - avatar       用户头像
 *   - role         角色：ADMIN / USER
 *   - status       账号状态：1=正常 0=禁用
 *   - deleted      逻辑删除：0=未删 1=已删
 *   - createTime   创建时间
 *   - updateTime   更新时间
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    /**
     * 角色：ADMIN（管理员） / USER（普通用户）
     */
    private String role;

    /**
     * 账号状态：1=正常 0=禁用
     */
    private Integer status;

    /**
     * 逻辑删除：0=未删 1=已删（MyBatis-Plus 自动过滤 deleted=1 的记录）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
