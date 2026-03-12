package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 会话信息mapper
 * @author 'Tong'
 * @since 2026/03/13
 */
public interface ChatSessionMapper<T, P> extends BaseMapper {
	// 多条件更新
	Integer updateByParam(@Param("bean") T t, @Param("query") P p);

	// 多条件删除
	Integer deleteByParam(@Param("query") P p);

	// 根据SessionId查询
	T selectBySessionId(@Param("sessionId") String sessionId);

	// 根据SessionId更新
	Integer updateBySessionId(@Param("bean") T t, @Param("sessionId") String sessionId);

	// 根据SessionId删除
	Integer deleteBySessionId(@Param("sessionId") String sessionId);
}