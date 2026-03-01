package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 联系人mapper
 * @author 'Tong'
 * @since 2025/10/17
 */
public interface UserContactMapper<T, P> extends BaseMapper {

	// 根据UserIdAndContactId查询
	T selectByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

	// 根据UserIdAndContactId更新
	Integer updateByUserIdAndContactId(@Param("bean") T t, @Param("userId") String userId, @Param("contactId") String contactId);

	// 根据UserIdAndContactId删除
	Integer deleteByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    // 多条件更新
    Integer updateByParam(@Param("bean") T t, @Param("query") P p);

    // 多条件删除
    Integer deleteByParam(@Param("query") P p);
}