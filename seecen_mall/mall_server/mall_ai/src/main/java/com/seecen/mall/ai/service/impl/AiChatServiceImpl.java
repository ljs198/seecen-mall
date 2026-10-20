package com.seecen.mall.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.ai.function.OrderFunction;
import com.seecen.mall.ai.function.ProductFunction;
import com.seecen.mall.ai.mapper.AiChatMessageMapper;
import com.seecen.mall.ai.service.AiChatService;
import com.seecen.mall.common.entity.AiChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI 聊天服务实现。
 *
 * 负责保存聊天记录、构建上下文、调用大模型和管理会话历史。
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private static final String SYSTEM_PROMPT = """
            你是软萌又靠谱的虚拟商品客服小助手🥰
            【工具使用小规则（一定要遵守哦）】
            
            1. 宝子问商品相关问题，比如：
            “有啥宝贝？”“都上架了哪些好物？”“给我看看商品～”
            “XX 多少钱呀？”“XX 还有库存吗？”“XX 是什么东西？”
            👉 要先调用 getProductInfo 工具。商品名称留空 = 查询全部上架宝贝。
            2. 如果宝子给了订单号，想查订单情况（“我的订单”“订单 XX”“看看 XX 订单咋样了”）
            👉 先调用 getOrderStatus 工具。
            3. 宝子想查订单但没给订单号，要软软地问对方要订单号，不许瞎猜回答。
            
            【回答小守则】
            ❌ 不许乱编商品价格、库存、订单状态、订单金额这些数据哦。
            ✅ 回答只能参考工具返回的信息；工具查不到的商品 / 订单，就告诉宝子 “暂时没有这个信息哒”。
            日常闲聊可以直接回消息，不用调用工具。
            回答简短一点，2～3 句话搞定就好啦。
            """;

    private static final String FALLBACK_REPLY = "抱歉，AI 服务暂时不可用，请稍后再试。";

    private final AiChatMessageMapper chatMessageMapper;

    /**
     * 项目内共享的 ChatClient。
     *
     * 关键点：ChatClient.Builder 是有状态的，defaultSystem / defaultTools 每次调用都会追加，
     * 不能在 chatStream() 里反复 build，否则第二次请求会出现
     * "Multiple tools with the same name (getOrderStatus, getProductInfo)"。
     * 正确做法是在构造函数中一次性 build 出来，整个应用共用同一个 ChatClient。
     */
    private final ChatClient chatClient;

    public AiChatServiceImpl(AiChatMessageMapper chatMessageMapper,
                             ChatClient.Builder chatClientBuilder,
                             ProductFunction productFunction,
                             OrderFunction orderFunction) {
        this.chatMessageMapper = chatMessageMapper;
        // 一次性 build：ChatClient.Builder 是有状态的，整个应用共用一个 ChatClient。
        // 如果在 chatStream() 里反复 defaultTools，会触发
        // "Multiple tools with the same name" IllegalStateException。
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(productFunction, orderFunction)
                .build();
    }

    /**
     * 流式处理用户消息。
     *
     * 流程：保存用户消息 -> 加载最近 50 条历史 -> 流式调用模型 -> 流结束时保存完整 AI 回复。
     */
    @Override
    public Flux<String> chatStream(Long userId, String sessionId, String message) {
        String currentSessionId = (sessionId == null || sessionId.isEmpty())
                ? UUID.randomUUID().toString()
                : sessionId;

        // 1. 保存用户消息
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setUserId(userId);
        userMsg.setSessionId(currentSessionId);
        userMsg.setRole("user");
        userMsg.setContent(message);
        chatMessageMapper.insert(userMsg);

        // 2. 构建上下文
        List<Message> messages = buildMessages(userId, currentSessionId);
        StringBuilder fullReply = new StringBuilder();

        // 3. 流式调用（直接用构造函数里 build 好的 chatClient，不再重新 build）
        return chatClient.prompt()
                .messages(messages)
                .stream()
                .content()
                .map(chunk -> {
                    fullReply.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> saveAssistantMessage(userId, currentSessionId, fullReply.toString()))
                .doOnError(e -> {
                    log.error("AI chat stream error", e);
                    saveAssistantMessage(userId, currentSessionId, FALLBACK_REPLY);
                })
                .onErrorResume(e -> Flux.just(FALLBACK_REPLY));
    }

    /**
     * 查询当前用户最近 10 个会话。
     */
    @Override
    public List<Map<String, Object>> getSessions(Long userId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getUserId, userId)
                .orderByDesc(AiChatMessage::getCreateTime);
        List<AiChatMessage> all = chatMessageMapper.selectList(wrapper);

        // LinkedHashMap 保留"最近消息优先"的插入顺序
        Map<String, AiChatMessage> sessionMap = new LinkedHashMap<>();
        for (AiChatMessage msg : all) {
            sessionMap.putIfAbsent(msg.getSessionId(), msg);
        }

        return sessionMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sessionId", e.getKey());
                    map.put("lastMessage", e.getValue().getContent());
                    map.put("lastTime", e.getValue().getCreateTime());
                    return map;
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 查询当前用户指定会话最近 50 条消息。
     */
    @Override
    public List<AiChatMessage> getSessionMessages(Long userId, String sessionId) {
        return getRecentMessages(userId, sessionId, 50);
    }

    /**
     * 删除当前用户指定会话。
     */
    @Override
    public void deleteSession(Long userId, String sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getUserId, userId)
                .eq(AiChatMessage::getSessionId, sessionId);
        chatMessageMapper.delete(wrapper);
    }

    /**
     * 构建发送给大模型的上下文消息。
     */
    private List<Message> buildMessages(Long userId, String sessionId) {
        List<AiChatMessage> history = getRecentMessages(userId, sessionId, 50);
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        for (AiChatMessage msg : history) {
            if ("user".equals(msg.getRole())) {
                messages.add(new UserMessage(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        return messages;
    }

    /**
     * 保存 AI 回复。
     */
    private void saveAssistantMessage(Long userId, String sessionId, String content) {
        if (content == null || content.isEmpty()) {
            return;
        }
        AiChatMessage assistantMsg = new AiChatMessage();
        assistantMsg.setUserId(userId);
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(content);
        chatMessageMapper.insert(assistantMsg);
    }

    /**
     * 查询最近 N 条历史消息，并按时间升序返回。
     */
    private List<AiChatMessage> getRecentMessages(Long userId, String sessionId, int limit) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getUserId, userId)
                .eq(AiChatMessage::getSessionId, sessionId)
                .orderByDesc(AiChatMessage::getCreateTime)
                .last("LIMIT " + limit);
        List<AiChatMessage> messages = chatMessageMapper.selectList(wrapper);
        Collections.reverse(messages);
        return messages;
    }
}
