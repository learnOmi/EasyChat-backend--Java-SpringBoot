package com.easychat.controller;

import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.service.UserInfoService;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.UserInfoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户信息表Controller
 * @author 'Tong'
 * @since 2025/09/28
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {
	@Resource
	private UserInfoService userInfoService;

	// 加载数据列表
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query) {
		return getSuccessResponse(userInfoService.findPageByParam(query));
	}

	// 新增
	@RequestMapping("add")
	public ResponseVO add(UserInfo bean) {
		this.userInfoService.add(bean);
		return getSuccessResponse(null);
	}

	// 批量新增
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean) {
		this.userInfoService.addBatch(listBean);
		return getSuccessResponse(null);
	}

	// 批量新增或修改
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfo> listBean) {
		this.userInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponse(null);
	}

	// 根据UserId查询
	@RequestMapping("getUserInfoByUserId")
	public ResponseVO getUserInfoByUserId(Integer userId) {
		return getSuccessResponse(this.userInfoService.getUserInfoByUserId(userId));
	}

	// 根据UserId更新
	@RequestMapping("updateUserInfoByUserId")
	public ResponseVO updateUserInfoByUserId(UserInfo bean, Integer userId) {
		this.userInfoService.updateUserInfoByUserId(bean, userId);
		return getSuccessResponse(null);
	}

	// 根据UserId删除
	@RequestMapping("deleteUserInfoByUserId")
	public ResponseVO deleteUserInfoByUserId(Integer userId) {
		this.userInfoService.deleteUserInfoByUserId(userId);
		return getSuccessResponse(null);
	}
	// 根据Email查询
	@RequestMapping("getUserInfoByEmail")
	public ResponseVO getUserInfoByEmail(String email) {
		return getSuccessResponse(this.userInfoService.getUserInfoByEmail(email));
	}

	// 根据Email更新
	@RequestMapping("updateUserInfoByEmail")
	public ResponseVO updateUserInfoByEmail(UserInfo bean, String email) {
		this.userInfoService.updateUserInfoByEmail(bean, email);
		return getSuccessResponse(null);
	}

	// 根据Email删除
	@RequestMapping("deleteUserInfoByEmail")
	public ResponseVO deleteUserInfoByEmail(String email) {
		this.userInfoService.deleteUserInfoByEmail(email);
		return getSuccessResponse(null);
	}
}