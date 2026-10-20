package com.seecen.mall.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类。
 *
 * 创建项目内共享的 ChatClient，用于向大模型发送对话请求。
 */
@Configuration
public class AiConfig {

    /**
     * 创建 ChatClient。
     *
     * @param chatModel Spring AI 根据 application.yml 自动配置的聊天模型
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
