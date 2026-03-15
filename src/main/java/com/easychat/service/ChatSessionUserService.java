package com.easychat.service;

import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 会话用户Service
 * @author 'Tong'
 * @since 2026/03/13
 */
public interface ChatSessionUserService {
	// 根据条件查询列表
	List<ChatSessionUser> findListByParam(ChatSessionUserQuery query);

	// 根据条件查询总数
	Integer findCountByParam(ChatSessionUserQuery query);

	// 分页查询
	PaginationResultVO<ChatSessionUser> findPageByParam(ChatSessionUserQuery query);

	// 新增
	Integer add(ChatSessionUser bean);

	// 批量新增
	Integer addBatch(List<ChatSessionUser> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<ChatSessionUser> listBean);

	// 多条件更新
	Integer updateByParam(ChatSessionUser bean, ChatSessionUserQuery query);

	// 多条件更新
	Integer deleteByParam(ChatSessionUserQuery query);

	// 根据UserIdAndContactId查询
	ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId, String contactId);

	// 根据UserIdAndContactId更新
	Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean, String userId, String contactId);

	// 根据UserIdAndContactId删除
	Integer deleteChatSessionUserByUserIdAndContactId(String userId, String contactId);

    public void updateRedundantInfo(String contactName, String contactId);
}