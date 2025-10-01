package com.easychat.controller;

import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.service.UserInfoBeautyService;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.UserInfoBeautyService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 靓号表Controller
 * @author 'Tong'
 * @since 2025/09/28
 */
@RestController
@RequestMapping("/userInfoBeauty")
public class UserInfoBeautyController extends ABaseController {
	@Resource
	private UserInfoBeautyService userInfoBeautyService;

	// 加载数据列表
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoBeautyQuery query) {
		return getSuccessResponse(userInfoBeautyService.findPageByParam(query));
	}

	// 新增
	@RequestMapping("add")
	public ResponseVO add(UserInfoBeauty bean) {
		this.userInfoBeautyService.add(bean);
		return getSuccessResponse(null);
	}

	// 批量新增
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfoBeauty> listBean) {
		this.userInfoBeautyService.addBatch(listBean);
		return getSuccessResponse(null);
	}

	// 批量新增或修改
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfoBeauty> listBean) {
		this.userInfoBeautyService.addOrUpdateBatch(listBean);
		return getSuccessResponse(null);
	}

	// 根据IdAndEmailAndUserId查询
	@RequestMapping("getUserInfoBeautyByIdAndEmailAndUserId")
	public ResponseVO getUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId) {
		return getSuccessResponse(this.userInfoBeautyService.getUserInfoBeautyByIdAndEmailAndUserId(id, email, userId));
	}

	// 根据IdAndEmailAndUserId更新
	@RequestMapping("updateUserInfoBeautyByIdAndEmailAndUserId")
	public ResponseVO updateUserInfoBeautyByIdAndEmailAndUserId(UserInfoBeauty bean, Integer id, String email, Integer userId) {
		this.userInfoBeautyService.updateUserInfoBeautyByIdAndEmailAndUserId(bean, id, email, userId);
		return getSuccessResponse(null);
	}

	// 根据IdAndEmailAndUserId删除
	@RequestMapping("deleteUserInfoBeautyByIdAndEmailAndUserId")
	public ResponseVO deleteUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId) {
		this.userInfoBeautyService.deleteUserInfoBeautyByIdAndEmailAndUserId(id, email, userId);
		return getSuccessResponse(null);
	}
	// 根据UserId查询
	@RequestMapping("getUserInfoBeautyByUserId")
	public ResponseVO getUserInfoBeautyByUserId(Integer userId) {
		return getSuccessResponse(this.userInfoBeautyService.getUserInfoBeautyByUserId(userId));
	}

	// 根据UserId更新
	@RequestMapping("updateUserInfoBeautyByUserId")
	public ResponseVO updateUserInfoBeautyByUserId(UserInfoBeauty bean, Integer userId) {
		this.userInfoBeautyService.updateUserInfoBeautyByUserId(bean, userId);
		return getSuccessResponse(null);
	}

	// 根据UserId删除
	@RequestMapping("deleteUserInfoBeautyByUserId")
	public ResponseVO deleteUserInfoBeautyByUserId(Integer userId) {
		this.userInfoBeautyService.deleteUserInfoBeautyByUserId(userId);
		return getSuccessResponse(null);
	}
	// 根据Email查询
	@RequestMapping("getUserInfoBeautyByEmail")
	public ResponseVO getUserInfoBeautyByEmail(String email) {
		return getSuccessResponse(this.userInfoBeautyService.getUserInfoBeautyByEmail(email));
	}

	// 根据Email更新
	@RequestMapping("updateUserInfoBeautyByEmail")
	public ResponseVO updateUserInfoBeautyByEmail(UserInfoBeauty bean, String email) {
		this.userInfoBeautyService.updateUserInfoBeautyByEmail(bean, email);
		return getSuccessResponse(null);
	}

	// 根据Email删除
	@RequestMapping("deleteUserInfoBeautyByEmail")
	public ResponseVO deleteUserInfoBeautyByEmail(String email) {
		this.userInfoBeautyService.deleteUserInfoBeautyByEmail(email);
		return getSuccessResponse(null);
	}
}