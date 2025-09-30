package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 靓号表mapper
 * @author 'Tong'
 * @since 2025/09/28
 */
public interface UserInfoBeautyMapper<T, P> extends BaseMapper {

	// 根据IdAndEmailAndUserId查询
	T selectByIdAndEmailAndUserId(@Param("id") Integer id, @Param("email") String email, @Param("userId") Integer userId);

	// 根据IdAndEmailAndUserId更新
	Integer updateByIdAndEmailAndUserId(@Param("bean") T t, @Param("id") Integer id, @Param("email") String email, @Param("userId") Integer userId);

	// 根据IdAndEmailAndUserId删除
	Integer deleteByIdAndEmailAndUserId(@Param("id") Integer id, @Param("email") String email, @Param("userId") Integer userId);

	// 根据UserId查询
	T selectByUserId(@Param("userId") Integer userId);

	// 根据UserId更新
	Integer updateByUserId(@Param("bean") T t, @Param("userId") Integer userId);

	// 根据UserId删除
	Integer deleteByUserId(@Param("userId") Integer userId);

	// 根据Email查询
	T selectByEmail(@Param("email") String email);

	// 根据Email更新
	Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

	// 根据Email删除
	Integer deleteByEmail(@Param("email") String email);
}