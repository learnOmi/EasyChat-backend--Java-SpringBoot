package com.easychat.service.impl;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.*;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.GroupInfoMapper;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.mapper.UserContactMapper;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.service.UserContactService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 联系人ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

	// 根据条件查询列表
	public List<UserContact> findListByParam(UserContactQuery query) {
		return this.userContactMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(UserContactQuery query) {
		return this.userContactMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<UserContact> findPageByParam(UserContactQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<UserContact> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(UserContact bean) {
		return this.userContactMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<UserContact> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<UserContact> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	}

	// 根据UserIdAndContactId查询
	public UserContact getUserContactByUserIdAndContactId(String userId, String contactId) {
		return this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
	}

	// 根据UserIdAndContactId更新
	public Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId) {
		return this.userContactMapper.updateByUserIdAndContactId(bean, userId, contactId);
	}

	// 根据UserIdAndContactId删除
	public Integer deleteUserContactByUserIdAndContactId(String userId, String contactId) {
		return this.userContactMapper.deleteByUserIdAndContactId(userId, contactId);
	}

    public UserContactSearchResultDto searchContact(String userId, String contactId) {
        UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (typeEnum == null) {
            return null;
        }

        UserContactSearchResultDto resultDto = new UserContactSearchResultDto();
        switch (typeEnum) {
            case USER:
                UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
                if (userInfo == null) {
                    return null;
                }
                resultDto = CopyTools.copy(userInfo, UserContactSearchResultDto.class);
                break;
            case GROUP:
                GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
                if (groupInfo == null) {
                    return null;
                }
                resultDto.setNickName(groupInfo.getGroupName());
                break;
        }
        resultDto.setContactType(typeEnum.toString());
        resultDto.setContactId(contactId);
        if (userId.equals(contactId)) {
            resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            return resultDto;
        }

        UserContact userContact = this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
        resultDto.setStatus(Integer.valueOf(userContact == null ? null : userContact.getStatus()));
        return resultDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) {
        UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (null == typeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String applyUserId = tokenUserInfoDto.getUserId();
        applyInfo = StringTools.isEmpty(applyInfo) ? String.format(Constants.APPLY_INFO_TEMPLATE, tokenUserInfoDto.getNickName()) : applyInfo;
        Long curTime = System.currentTimeMillis();
        Integer joinType = null;
        String receiveUserId = contactId;
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(applyUserId, contactId);
        if (userContact != null && UserContactStatusEnum.BLACKLIST_BE.getStatus().byteValue() == userContact.getStatus()) {
            throw new BusinessException("对方已将你拉黑！");
        }

        if (UserContactTypeEnum.GROUP == typeEnum) {
            GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
            if (groupInfo == null || GroupStatusEnum.DISSOLUTION.getStatus().byteValue() == groupInfo.getStatus()) {
                throw new BusinessException("该群不存在或已解散！");
            }
            receiveUserId = groupInfo.getGroupOwnerId();
            joinType = Integer.valueOf(groupInfo.getJoinType());
        } else {
            UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
            if (userInfo == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            joinType = Integer.valueOf(userInfo.getJoinType());
        }

        if (JoinTypeEnum.JOIN.getType() == joinType) {

            return joinType;
        }

        UserContactApply dbApply = this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
        if (dbApply != null) {
            UserContactApply contactApply = new UserContactApply();
            contactApply.setApplyUserId(applyUserId);
            contactApply.setContactType(typeEnum.getType().byteValue());
            contactApply.setContactId(contactId);
            contactApply.setReceiveUserId(receiveUserId);
            contactApply.setLastApplyTime(curTime);
            contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus().byteValue());
            contactApply.setApplyInfo(applyInfo);
            this.userContactApplyMapper.insert(contactApply);
        } else {
            UserContactApply contactApply = new UserContactApply();
            contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus().byteValue());
            contactApply.setLastApplyTime(curTime);
            contactApply.setApplyInfo(applyInfo);
            this.userContactApplyMapper.updateByApplyId(contactApply, dbApply.getApplyId());
        }

        if (dbApply == null || !(UserContactApplyStatusEnum.INIT.getStatus().byteValue() == dbApply.getStatus())) {

        }

        return joinType;
    }
}