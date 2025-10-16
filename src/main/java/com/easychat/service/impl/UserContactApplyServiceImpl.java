package com.easychat.service.impl;

import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.PageSize;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.service.UserContactApplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 联系人申请ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {
	@Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

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
}