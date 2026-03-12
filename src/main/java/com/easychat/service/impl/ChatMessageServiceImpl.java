package com.easychat.service.impl;

import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.PageSize;
import com.easychat.mapper.ChatMessageMapper;
import com.easychat.service.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天消息表ServiceImpl
 * @author 'Tong'
 * @since 2026/03/13
 */
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {
	@Resource
	private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;

	// 根据条件查询列表
	public List<ChatMessage> findListByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<ChatMessage> findPageByParam(ChatMessageQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<ChatMessage> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(ChatMessage bean) {
		return this.chatMessageMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	}

	// 多条件更新
	public Integer updateByParam(ChatMessage bean, ChatMessageQuery query) {
		return this.chatMessageMapper.updateByParam(bean, query);
	}

	// 多条件删除
	public Integer deleteByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.deleteByParam(query);
	}

	// 根据MessageId查询
	public ChatMessage getChatMessageByMessageId(Long messageId) {
		return this.chatMessageMapper.selectByMessageId(messageId);
	}

	// 根据MessageId更新
	public Integer updateChatMessageByMessageId(ChatMessage bean, Long messageId) {
		return this.chatMessageMapper.updateByMessageId(bean, messageId);
	}

	// 根据MessageId删除
	public Integer deleteChatMessageByMessageId(Long messageId) {
		return this.chatMessageMapper.deleteByMessageId(messageId);
	}
}