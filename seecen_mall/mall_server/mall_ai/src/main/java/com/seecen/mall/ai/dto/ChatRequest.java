package com.seecen.mall.ai.dto;

import lombok.Data;

/**
 * AI 聊天请求 DTO。
 *
 * 前端发送消息时携带 sessionId 和 message。sessionId 留空时由后端创建新会话。
 */
@Data
public class ChatRequest {

    /** 会话 ID，为空时由后端创建新会话。 */
    private String sessionId;

    /** 用户输入的消息内容。 */
    private String message;
}
