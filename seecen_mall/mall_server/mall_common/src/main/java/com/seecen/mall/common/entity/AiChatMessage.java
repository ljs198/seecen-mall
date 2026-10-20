package com.seecen.mall.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 聊天消息实体。
 *
 * 每条记录代表登录用户或 AI 客服在一次会话中发送的一条消息。
 *
 * 字段说明：
 *   userId    当前登录用户 ID，用于隔离不同用户的会话
 *   sessionId 会话 ID，同一轮连续对话使用同一个 sessionId
 *   role      消息角色：user 表示用户，assistant 表示 AI 客服
 *   content   消息文本内容
 */
@Data
public class AiChatMessage {

    /** 消息主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 ID。 */
    private Long userId;

    /** 会话 ID，同一轮连续对话使用同一个 sessionId。 */
    private String sessionId;

    /** 消息角色：user 表示用户，assistant 表示 AI 客服。 */
    private String role;

    /** 消息文本内容。 */
    private String content;

    /** 消息创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
