package com.easychat.service;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.UserContactStatusEnum;

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

    // 多条件更新
    Integer updateByParam(UserContact bean, UserContactQuery query);

    // 多条件更新
    Integer deleteByParam(UserContactQuery query);

    UserContactSearchResultDto searchContact(String userId, String contactId);

    Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo);

    void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo);

    void removeUserContact(String userId, String contactId, UserContactStatusEnum statusEnum);
}