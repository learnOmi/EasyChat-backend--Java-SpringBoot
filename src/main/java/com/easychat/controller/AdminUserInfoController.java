package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-用户管理", description = "用户查询、状态变更与强制下线（仅管理员）")
@RestController
@RequestMapping("/admin")
public class AdminUserInfoController extends ABaseController {
    @Resource
    private UserInfoService userInfoService;

    @Operation(summary = "分页查询用户", description = "仅管理员；按创建时间倒序；data 为 PaginationResultVO<UserInfo>")
    @RequestMapping("/loadUser")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUser(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("create_time desc");
        PaginationResultVO resultVO = userInfoService.findPageByParam(userInfoQuery);
        return getSuccessResponse(null);
    }

    @Operation(summary = "修改用户状态", description = "仅管理员；data 为 null")
    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO updateUserStatus(@Parameter(description = "状态：0:禁用 1:正常") @NotNull Integer status,
                                       @Parameter(description = "用户ID") @NotEmpty String userId) {
        userInfoService.updateUserStatus(status, userId);
        return getSuccessResponse(null);
    }

    @Operation(summary = "强制用户下线", description = "仅管理员；data 为 null")
    @RequestMapping("/forceOffLine")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO forceOffLine(@Parameter(description = "用户ID") @NotEmpty String userId) {
        userInfoService.forceOffLine(userId);
        return getSuccessResponse(null);
    }
}
