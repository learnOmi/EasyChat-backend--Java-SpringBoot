package com.easychat.service.impl;

import com.easychat.entity.po.ChatSession;
import com.easychat.entity.query.ChatSessionQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.PageSize;
import com.easychat.mapper.ChatSessionMapper;
import com.easychat.service.ChatSessionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会话信息ServiceImpl
 * @author 'Tong'
 * @since 2026/03/13
 */
@Service("chatSessionService")
public class ChatSessionServiceImpl implements ChatSessionService {
	@Resource
	private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;

	// 根据条件查询列表
	public List<ChatSession> findListByParam(ChatSessionQuery query) {
		return this.chatSessionMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(ChatSessionQuery query) {
		return this.chatSessionMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<ChatSession> findPageByParam(ChatSessionQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<ChatSession> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(ChatSession bean) {
		return this.chatSessionMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<ChatSession> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatSessionMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<ChatSession> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatSessionMapper.insertOrUpdateBatch(listBean);
	}

	// 多条件更新
	public Integer updateByParam(ChatSession bean, ChatSessionQuery query) {
		return this.chatSessionMapper.updateByParam(bean, query);
	}

	// 多条件删除
	public Integer deleteByParam(ChatSessionQuery query) {
		return this.chatSessionMapper.deleteByParam(query);
	}

	// 根据SessionId查询
	public ChatSession getChatSessionBySessionId(String sessionId) {
		return this.chatSessionMapper.selectBySessionId(sessionId);
	}

	// 根据SessionId更新
	public Integer updateChatSessionBySessionId(ChatSession bean, String sessionId) {
		return this.chatSessionMapper.updateBySessionId(bean, sessionId);
	}

	// 根据SessionId删除
	public Integer deleteChatSessionBySessionId(String sessionId) {
		return this.chatSessionMapper.deleteBySessionId(sessionId);
	}
}