package com.easychat.service.impl;

import com.easychat.enums.PageSize;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.mapper.UserInfoMapper;import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserInfoService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户信息表ServiceImpl
 * @author 'Tong'
 * @since 2025/09/28
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	// 根据条件查询列表
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<UserInfo> findPageByParam(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	// 根据UserId查询
	public UserInfo getUserInfoByUserId(Integer userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	// 根据UserId更新
	public Integer updateUserInfoByUserId(UserInfo bean, Integer userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	// 根据UserId删除
	public Integer deleteUserInfoByUserId(Integer userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}
	// 根据Email查询
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	// 根据Email更新
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	// 根据Email删除
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}
}