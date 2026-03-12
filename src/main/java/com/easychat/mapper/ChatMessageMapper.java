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
}