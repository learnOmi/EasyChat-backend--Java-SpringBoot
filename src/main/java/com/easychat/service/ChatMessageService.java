package com.easychat.service;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 聊天消息表Service
 * @author 'Tong'
 * @since 2026/03/13
 */
public interface ChatMessageService {
	// 根据条件查询列表
	List<ChatMessage> findListByParam(ChatMessageQuery query);

	// 根据条件查询总数
	Integer findCountByParam(ChatMessageQuery query);

	// 分页查询
	PaginationResultVO<ChatMessage> findPageByParam(ChatMessageQuery query);

	// 新增
	Integer add(ChatMessage bean);

	// 批量新增
	Integer addBatch(List<ChatMessage> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<ChatMessage> listBean);

	// 多条件更新
	Integer updateByParam(ChatMessage bean, ChatMessageQuery query);

	// 多条件更新
	Integer deleteByParam(ChatMessageQuery query);

	// 根据MessageId查询
	ChatMessage getChatMessageByMessageId(Long messageId);

	// 根据MessageId更新
	Integer updateChatMessageByMessageId(ChatMessage bean, Long messageId);

	// 根据MessageId删除
	Integer deleteChatMessageByMessageId(Long messageId);

    MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto);
}