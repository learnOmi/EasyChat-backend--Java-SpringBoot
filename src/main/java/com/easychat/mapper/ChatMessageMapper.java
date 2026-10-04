package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 聊天消息表mapper
 * @author 'Tong'
 * @since 2026/03/13
 */
public interface ChatMessageMapper<T, P> extends BaseMapper {
	// 多条件更新
	Integer updateByParam(@Param("bean") T t, @Param("query") P p);

	// 多条件删除
	Integer deleteByParam(@Param("query") P p);

	// 根据MessageId查询
	T selectByMessageId(@Param("messageId") Long messageId);

	// 根据MessageId更新
	Integer updateByMessageId(@Param("bean") T t, @Param("messageId") Long messageId);

	// 根据MessageId删除
	Integer deleteByMessageId(@Param("messageId") Long messageId);

	/**
	 * 根据客户端幂等ID查询消息（依赖唯一索引 uk_client_msg_id）
	 * @param clientMessageId 客户端幂等ID
	 * @return 命中的消息，未命中返回 null
	 */
	T selectByClientMessageId(@Param("clientMessageId") String clientMessageId);

	/**
	 * 条件更新消息：仅当当前 status 等于 fromStatus 时才更新，返回影响行数
	 * 用于上传闸门判重：影响行数=1 表示本次推进成功，=0 表示已被并发请求抢先推进
	 * @param t 待更新的字段（如 status）
	 * @param messageId 消息ID
	 * @param fromStatus 期望的当前状态
	 * @return 影响行数
	 */
	Integer updateStatusByMessageIdAndStatus(@Param("bean") T t, @Param("messageId") Long messageId, @Param("fromStatus") Byte fromStatus);
}