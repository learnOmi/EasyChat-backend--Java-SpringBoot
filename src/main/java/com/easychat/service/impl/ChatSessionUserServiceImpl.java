package com.easychat.service.impl;

import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.PageSize;
import com.easychat.mapper.ChatSessionUserMapper;
import com.easychat.service.ChatSessionUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会话用户ServiceImpl
 * @author 'Tong'
 * @since 2026/03/13
 */
@Service("chatSessionUserService")
public class ChatSessionUserServiceImpl implements ChatSessionUserService {
	@Resource
	private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;

	// 根据条件查询列表
	public List<ChatSessionUser> findListByParam(ChatSessionUserQuery query) {
		return this.chatSessionUserMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(ChatSessionUserQuery query) {
		return this.chatSessionUserMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<ChatSessionUser> findPageByParam(ChatSessionUserQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<ChatSessionUser> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(ChatSessionUser bean) {
		return this.chatSessionUserMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<ChatSessionUser> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatSessionUserMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<ChatSessionUser> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatSessionUserMapper.insertOrUpdateBatch(listBean);
	}

	// 多条件更新
	public Integer updateByParam(ChatSessionUser bean, ChatSessionUserQuery query) {
		return this.chatSessionUserMapper.updateByParam(bean, query);
	}

	// 多条件删除
	public Integer deleteByParam(ChatSessionUserQuery query) {
		return this.chatSessionUserMapper.deleteByParam(query);
	}

	// 根据UserIdAndContactId查询
	public ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId, String contactId) {
		return this.chatSessionUserMapper.selectByUserIdAndContactId(userId, contactId);
	}

	// 根据UserIdAndContactId更新
	public Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean, String userId, String contactId) {
		return this.chatSessionUserMapper.updateByUserIdAndContactId(bean, userId, contactId);
	}

	// 根据UserIdAndContactId删除
	public Integer deleteChatSessionUserByUserIdAndContactId(String userId, String contactId) {
		return this.chatSessionUserMapper.deleteByUserIdAndContactId(userId, contactId);
	}
}