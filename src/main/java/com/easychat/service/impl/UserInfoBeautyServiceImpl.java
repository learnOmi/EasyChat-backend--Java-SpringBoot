package com.easychat.service.impl;

import com.easychat.enums.PageSize;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.mapper.UserInfoBeautyMapper;import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserInfoBeautyService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 靓号表ServiceImpl
 * @author 'Tong'
 * @since 2025/09/28
 */
@Service("userInfoBeautyService")
public class UserInfoBeautyServiceImpl implements UserInfoBeautyService {
	@Resource
	private UserInfoBeautyMapper<UserInfoBeauty, UserInfoBeautyQuery> userInfoBeautyMapper;

	// 根据条件查询列表
	public List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery query) {
		return this.userInfoBeautyMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(UserInfoBeautyQuery query) {
		return this.userInfoBeautyMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<UserInfoBeauty> findPageByParam(UserInfoBeautyQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<UserInfoBeauty> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(UserInfoBeauty bean) {
		return this.userInfoBeautyMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<UserInfoBeauty> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userInfoBeautyMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<UserInfoBeauty> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userInfoBeautyMapper.insertOrUpdateBatch(listBean);
	}

	// 根据IdAndEmailAndUserId查询
	public UserInfoBeauty getUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId) {
		return this.userInfoBeautyMapper.selectByIdAndEmailAndUserId(id, email, userId);
	}

	// 根据IdAndEmailAndUserId更新
	public Integer updateUserInfoBeautyByIdAndEmailAndUserId(UserInfoBeauty bean, Integer id, String email, Integer userId) {
		return this.userInfoBeautyMapper.updateByIdAndEmailAndUserId(bean, id, email, userId);
	}

	// 根据IdAndEmailAndUserId删除
	public Integer deleteUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId) {
		return this.userInfoBeautyMapper.deleteByIdAndEmailAndUserId(id, email, userId);
	}
	// 根据UserId查询
	public UserInfoBeauty getUserInfoBeautyByUserId(Integer userId) {
		return this.userInfoBeautyMapper.selectByUserId(userId);
	}

	// 根据UserId更新
	public Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean, Integer userId) {
		return this.userInfoBeautyMapper.updateByUserId(bean, userId);
	}

	// 根据UserId删除
	public Integer deleteUserInfoBeautyByUserId(Integer userId) {
		return this.userInfoBeautyMapper.deleteByUserId(userId);
	}
	// 根据Email查询
	public UserInfoBeauty getUserInfoBeautyByEmail(String email) {
		return this.userInfoBeautyMapper.selectByEmail(email);
	}

	// 根据Email更新
	public Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean, String email) {
		return this.userInfoBeautyMapper.updateByEmail(bean, email);
	}

	// 根据Email删除
	public Integer deleteUserInfoBeautyByEmail(String email) {
		return this.userInfoBeautyMapper.deleteByEmail(email);
	}
}