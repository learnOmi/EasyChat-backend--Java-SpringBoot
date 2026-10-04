package com.easychat.service;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover);

    File downloadFile(TokenUserInfoDto tokenUserInfoDto, Long fileId, Boolean showCover);

    /**
     * 处理接收方对单聊消息的送达确认（ACK）。
     * <p>仅允许单聊接收方对「发给自己的」消息回 ACK；通过条件更新（已发送→已送达）保证幂等，
     * 推进成功后向发送方推送消息状态变更通知。</p>
     * @param userId 回 ACK 的用户ID（即消息接收方）
     * @param messageId 消息ID
     */
    void ackMessage(String userId, Long messageId);
}