package com.easychat.service;

import com.easychat.entity.po.ChatSession;
import com.easychat.entity.query.ChatSessionQuery;
import com.easychat.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 会话信息Service
 * @author 'Tong'
 * @since 2026/03/13
 */
public interface ChatSessionService {
	// 根据条件查询列表
	List<ChatSession> findListByParam(ChatSessionQuery query);

	// 根据条件查询总数
	Integer findCountByParam(ChatSessionQuery query);

	// 分页查询
	PaginationResultVO<ChatSession> findPageByParam(ChatSessionQuery query);

	// 新增
	Integer add(ChatSession bean);

	// 批量新增
	Integer addBatch(List<ChatSession> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<ChatSession> listBean);

	// 多条件更新
	Integer updateByParam(ChatSession bean, ChatSessionQuery query);

	// 多条件更新
	Integer deleteByParam(ChatSessionQuery query);

	// 根据SessionId查询
	ChatSession getChatSessionBySessionId(String sessionId);

	// 根据SessionId更新
	Integer updateChatSessionBySessionId(ChatSession bean, String sessionId);

	// 根据SessionId删除
	Integer deleteChatSessionBySessionId(String sessionId);
}