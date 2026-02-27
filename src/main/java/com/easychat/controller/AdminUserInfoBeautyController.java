package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.UserInfoBeautyService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminUserInfoBeautyController extends ABaseController {
    @Resource
    private UserInfoBeautyService userInfoBeautyService;

    @RequestMapping("/loadBeautyAccountList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadBeautyAccountList(UserInfoBeautyQuery query) {
        query.setOrderBy("id desc");
        PaginationResultVO resultVO = userInfoBeautyService.findPageByParam(query);
        return getSuccessResponse(null);
    }

    @RequestMapping("/saveBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveBeautyAccount(UserInfoBeauty beauty) {
        userInfoBeautyService.saveAccount(beauty);
        return getSuccessResponse(null);
    }

    @RequestMapping("/delBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO delBeautyAccount(@NotNull Integer id) {
        userInfoBeautyService.deleteUserInfoBeautyById(id);
        return getSuccessResponse(null);
    }
}
