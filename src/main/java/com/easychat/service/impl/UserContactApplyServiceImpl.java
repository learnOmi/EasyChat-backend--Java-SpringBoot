package com.easychat.service.impl;

import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.mapper.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactApplyService;
import com.easychat.service.UserContactService;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联系人申请ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {
	@Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery>  userContactApplyMapper;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserContactService userContactService;

	// 根据条件查询列表
	public List<UserContactApply> findListByParam(UserContactApplyQuery query) {
		return this.userContactApplyMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(UserContactApplyQuery query) {
		return this.userContactApplyMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<UserContactApply> findPageByParam(UserContactApplyQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<UserContactApply> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(UserContactApply bean) {
		return this.userContactApplyMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<UserContactApply> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<UserContactApply> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	}

	// 根据ApplyId查询
	public UserContactApply getUserContactApplyByApplyId(Integer applyId) {
		return this.userContactApplyMapper.selectByApplyId(applyId);
	}

	// 根据ApplyId更新
	public Integer updateUserContactApplyByApplyId(UserContactApply bean, Integer applyId) {
		return this.userContactApplyMapper.updateByApplyId(bean, applyId);
	}

	// 根据ApplyId删除
	public Integer deleteUserContactApplyByApplyId(Integer applyId) {
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	}
	// 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	public UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId) {
		return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
	}

	// 根据ApplyUserIdAndReceiveUserIdAndContactId更新
	public Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean, String applyUserId, String receiveUserId, String contactId) {
		return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserIdAndContactId(bean, applyUserId, receiveUserId, contactId);
	}

	// 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	public Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId) {
		return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealWithApply(String userId, Integer applyId, Integer status) {
        UserContactApplyStatusEnum statusEnum = UserContactApplyStatusEnum.getByStatus(status);
        if (statusEnum == null || UserContactApplyStatusEnum.INIT == statusEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserContactApply applyInfo = this.userContactApplyMapper.selectByApplyId(applyId);
        if (applyInfo == null || !(userId.equals(applyInfo.getReceiveUserId()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserContactApply updateInfo = new UserContactApply();
        updateInfo.setStatus(statusEnum.getStatus().byteValue());
        updateInfo.setLastApplyTime(System.currentTimeMillis());

        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setApplyId(applyId);
        applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus().byteValue());
        Integer count = userContactApplyMapper.updateByParam(updateInfo, applyQuery);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (UserContactApplyStatusEnum.PASS.getStatus().equals(status)) {
            userContactService.addContact(applyInfo.getApplyUserId(), applyInfo.getReceiveUserId(), applyInfo.getContactId(), Integer.valueOf(applyInfo.getContactType()), applyInfo.getApplyInfo());
            return;
        }

        if (UserContactApplyStatusEnum.BLACKLIST == statusEnum) {
            Date curDate = new Date();
            UserContact userContact = new UserContact();
            userContact.setUserId(applyInfo.getApplyUserId());
            userContact.setContactId(applyInfo.getContactId());
            userContact.setContactType(applyInfo.getContactType());
            userContact.setCreateTime(curDate);
            userContact.setStatus(UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus().byteValue());
            userContact.setLastUpdateTime(curDate);
            userContactMapper.insertOrUpdate(userContact);
        }
    }
}