package com.easychat.service;

import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import java.util.List;

/**
 * 用户信息表Service
 * @author 'Tong'
 * @since 2025/09/28
 */
public interface UserInfoService {
	// 根据条件查询列表
	List<UserInfo> findListByParam(UserInfoQuery query);

	// 根据条件查询总数
	Integer findCountByParam(UserInfoQuery query);

	// 分页查询
	PaginationResultVO<UserInfo> findPageByParam(UserInfoQuery query);

	// 新增
	Integer add(UserInfo bean);

	// 批量新增
	Integer addBatch(List<UserInfo> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	// 根据UserId查询
	UserInfo getUserInfoByUserId(Integer userId);

	// 根据UserId更新
	Integer updateUserInfoByUserId(UserInfo bean, Integer userId);

	// 根据UserId删除
	Integer deleteUserInfoByUserId(Integer userId);
	// 根据Email查询
	UserInfo getUserInfoByEmail(String email);

	// 根据Email更新
	Integer updateUserInfoByEmail(UserInfo bean, String email);

	// 根据Email删除
	Integer deleteUserInfoByEmail(String email);
}