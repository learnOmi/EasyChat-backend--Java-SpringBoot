package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.GroupInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller
 * @author 'Tong'
 * @since 2025/10/17
 */
@RestController
@RequestMapping("/group")
public class GroupInfoController extends ABaseController {
	@Resource
	private GroupInfoService groupInfoService;

	// 加载数据列表
	@RequestMapping("/saveGroup")
    @GlobalInterceptor
    public ResponseVO saveGroup(HttpServletRequest request, String groupId, @NotEmpty String groupName, String groupNotice, @NotNull Integer joinType, MultipartFile avatarFile, MultipartFile avatarCover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        return getSuccessResponse(null);
    }

}