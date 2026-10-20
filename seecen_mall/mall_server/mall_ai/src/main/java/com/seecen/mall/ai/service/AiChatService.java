package com.seecen.mall.ai.service;

import com.seecen.mall.common.entity.AiChatMessage;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * AI 聊天服务接口。
 *
 * 定义流式对话、会话列表、历史消息和删除会话能力。
 */
public interface AiChatService {

    /**
     * 流式发送消息并获取 AI 回复。
     *
     * @param userId    当前登录用户 ID
     * @param sessionId 会话 ID，传 null 或空字符串时由服务创建新会话
     * @param message   用户消息
     * @return AI 回复片段流（每个片段是模型输出的一段文本）
     */
    Flux<String> chatStream(Long userId, String sessionId, String message);

    /**
     * 查询当前用户最近会话。
     *
     * @param userId 当前登录用户 ID
     * @return 会话列表（含 sessionId / lastMessage / lastTime）
     */
    List<Map<String, Object>> getSessions(Long userId);

    /**
     * 查询当前用户指定会话的消息。
     *
     * @param userId    当前登录用户 ID
     * @param sessionId 会话 ID
     * @return 消息列表（按时间升序，最多 50 条）
     */
    List<AiChatMessage> getSessionMessages(Long userId, String sessionId);

    /**
     * 删除当前用户的指定会话。
     *
     * @param userId    当前登录用户 ID
     * @param sessionId 会话 ID
     */
    void deleteSession(Long userId, String sessionId);
}
