package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.UserInfoBeautyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-靓号管理", description = "靓号账号的查询、保存与删除（仅管理员）")
@RestController
@RequestMapping("/admin")
public class AdminUserInfoBeautyController extends ABaseController {
    @Resource
    private UserInfoBeautyService userInfoBeautyService;

    @Operation(summary = "分页查询靓号列表", description = "仅管理员；data 为 PaginationResultVO<UserInfoBeauty>")
    @RequestMapping("/loadBeautyAccountList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadBeautyAccountList(UserInfoBeautyQuery query) {
        query.setOrderBy("id desc");
        PaginationResultVO resultVO = userInfoBeautyService.findPageByParam(query);
        return getSuccessResponse(null);
    }

    @Operation(summary = "保存靓号账号", description = "仅管理员；有 id 为修改，无 id 为新增；data 为 null")
    @RequestMapping("/saveBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveBeautyAccount(UserInfoBeauty beauty) {
        userInfoBeautyService.saveAccount(beauty);
        return getSuccessResponse(null);
    }

    @Operation(summary = "删除靓号账号", description = "仅管理员；data 为 null")
    @RequestMapping("/delBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO delBeautyAccount(@Parameter(description = "靓号记录ID") @NotNull Integer id) {
        userInfoBeautyService.deleteUserInfoBeautyById(id);
        return getSuccessResponse(null);
    }
}
