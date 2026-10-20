package com.seecen.mall.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 聊天消息 Mapper。
 *
 * 用于保存、查询和删除当前登录用户的聊天记录。
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}
