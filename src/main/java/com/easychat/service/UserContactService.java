package com.easychat.service;

import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 联系人Service
 * @author 'Tong'
 * @since 2025/10/17
 */
public interface UserContactService {
	// 根据条件查询列表
	List<UserContact> findListByParam(UserContactQuery query);

	// 根据条件查询总数
	Integer findCountByParam(UserContactQuery query);

	// 分页查询
	PaginationResultVO<UserContact> findPageByParam(UserContactQuery query);

	// 新增
	Integer add(UserContact bean);

	// 批量新增
	Integer addBatch(List<UserContact> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<UserContact> listBean);

	// 根据UserIdAndContactId查询
	UserContact getUserContactByUserIdAndContactId(String userId, String contactId);

	// 根据UserIdAndContactId更新
	Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId);

	// 根据UserIdAndContactId删除
	Integer deleteUserContactByUserIdAndContactId(String userId, String contactId);
}