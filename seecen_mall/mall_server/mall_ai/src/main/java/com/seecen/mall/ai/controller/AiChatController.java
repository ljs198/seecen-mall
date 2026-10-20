package com.seecen.mall.ai.controller;

import com.seecen.mall.ai.dto.ChatRequest;
import com.seecen.mall.ai.service.AiChatService;
import com.seecen.mall.common.entity.AiChatMessage;
import com.seecen.mall.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 智能客服控制器。
 *
 * 提供当前登录用户的流式聊天和会话记录接口。
 *
 * 权限：必须登录（由 SecurityConfig 中 /api/portal/** 的 authenticated 规则保证）。
 * 当前用户 ID 从 JwtAuthFilter 写入的 request 属性里读取，与现有 OrderController 等保持一致。
 */
@Slf4j
@RestController
@RequestMapping("/api/portal/ai")
@RequiredArgsConstructor
public class AiChatController {

    /** SSE 连接超时：60 秒。 */
    private static final long SSE_TIMEOUT_MS = 60_000L;

    private final AiChatService aiChatService;

    /**
     * 使用缓存线程池异步执行 SSE 推送，避免阻塞 Web MVC 工作线程。
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 发送聊天消息并通过 SSE 流式返回 AI 回复。
     *
     * 流程：
     *   1. 生成或确认 sessionId
     *   2. 先推送 { sessionId } 让前端绑定会话
     *   3. 订阅 AI 流，把每段文本包装成 { content, sessionId } 推送
     *   4. 流结束推送 [DONE] 标记，前端据此关闭 loading
     *
     * @param request 聊天请求
     * @param http    当前 HTTP 请求，用于从属性读取当前用户 ID
     * @return SSE 连接
     */
    @PostMapping(value = "/chat", produces = "text/event-stream")
    public SseEmitter chat(@RequestBody ChatRequest request, HttpServletRequest http) {
        Long userId = getCurrentUserId(http);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        executor.execute(() -> {
            String sessionId = (request.getSessionId() == null || request.getSessionId().isEmpty())
                    ? UUID.randomUUID().toString()
                    : request.getSessionId();
            String finalSessionId = sessionId;
            try {
                // 1. 先把 sessionId 推给前端
                emitter.send(SseEmitter.event().data(Map.of("sessionId", finalSessionId)));
                // 2. 订阅 AI 流
                aiChatService.chatStream(userId, finalSessionId, request.getMessage())
                        .subscribe(
                                chunk -> sendChunk(emitter, finalSessionId, chunk),
                                error -> sendError(emitter),
                                () -> sendDone(emitter)
                        );
            } catch (Exception e) {
                log.error("AI chat init error", e);
                sendError(emitter);
            }
        });

        return emitter;
    }

    /**
     * 查询当前用户最近会话。
     *
     * @param http 当前 HTTP 请求
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public Result<List<Map<String, Object>>> getSessions(HttpServletRequest http) {
        Long userId = getCurrentUserId(http);
        return Result.success(aiChatService.getSessions(userId));
    }

    /**
     * 查询当前用户指定会话消息。
     *
     * @param sessionId 会话 ID
     * @param http      当前 HTTP 请求
     * @return 消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<AiChatMessage>> getMessages(@PathVariable String sessionId, HttpServletRequest http) {
        Long userId = getCurrentUserId(http);
        return Result.success(aiChatService.getSessionMessages(userId, sessionId));
    }

    /**
     * 删除当前用户指定会话。
     *
     * @param sessionId 会话 ID
     * @param http      当前 HTTP 请求
     * @return 空结果
     */
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId, HttpServletRequest http) {
        Long userId = getCurrentUserId(http);
        aiChatService.deleteSession(userId, sessionId);
        return Result.success();
    }

    /**
     * 推送一段 AI 回复内容。
     */
    private void sendChunk(SseEmitter emitter, String sessionId, String chunk) {
        try {
            emitter.send(SseEmitter.event().data(Map.of("content", chunk, "sessionId", sessionId)));
        } catch (IOException e) {
            log.error("SSE send error", e);
        }
    }

    /**
     * 推送错误提示并关闭 SSE。
     */
    private void sendError(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().data(Map.of("error", "AI 服务暂时不可用")));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    /**
     * 推送完成标记并关闭 SSE。
     */
    private void sendDone(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().data("[DONE]"));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    /**
     * 从 JwtAuthFilter 写入的 request 属性中取出当前用户 ID。
     * 与现有 OrderController 等保持一致。
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        Object value = request.getAttribute("currentUserId");
        if (value == null) {
            throw new IllegalStateException("未登录或登录已过期");
        }
        return ((Number) value).longValue();
    }
}
