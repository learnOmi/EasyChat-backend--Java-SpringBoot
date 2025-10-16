package com.easychat.service.impl;

import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.PageSize;
import com.easychat.mapper.UserContactMapper;
import com.easychat.service.UserContactService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
}