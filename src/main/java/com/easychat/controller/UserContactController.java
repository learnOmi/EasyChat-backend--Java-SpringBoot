package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.enums.PageSize;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.enums.UserContactStatusEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import com.easychat.service.UserContactApplyService;
import com.easychat.service.UserContactService;
import com.easychat.service.UserInfoService;
import com.easychat.utils.ArrayUtils;
import com.easychat.utils.CopyTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "联系人模块", description = "搜索用户、申请添加、处理申请、联系人管理、拉黑/删除")
@RestController
@RequestMapping("/contact")
public class UserContactController extends ABaseController{
    @Resource
    private UserContactService userContactService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    UserContactApplyService userContactApplyService;
    @Autowired
    private GroupInfoService groupInfoService;

    @Operation(summary = "搜索联系人", description = "需登录；按用户ID/群ID搜索；data 为 UserContactSearchResultDto")
    @RequestMapping("/search")
    @GlobalInterceptor
    public ResponseVO searchUser(HttpServletRequest request, @Parameter(description = "用户ID或群ID") @NotEmpty String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContactSearchResultDto resultDto = userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);

        return getSuccessResponse(resultDto);
    }

    @Operation(summary = "申请添加联系人", description = "需登录；data 返回加入方式 joinType（0:直接加入 1:需对方同意）")
    @RequestMapping("/applyAdd")
    @GlobalInterceptor
    public ResponseVO applyAdd(HttpServletRequest request,
                               @Parameter(description = "用户ID或群ID") @NotEmpty String contactId,
                               @Parameter(description = "申请附加信息") String applyInfo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        Integer joinType = userContactApplyService.applyAdd(tokenUserInfoDto, contactId, applyInfo);
        return getSuccessResponse(joinType);
    }

    @Operation(summary = "分页加载联系人申请", description = "需登录；按最后申请时间倒序；data 为 PaginationResultVO<UserContactApply>")
    @RequestMapping("/loadApply")
    @GlobalInterceptor
    public ResponseVO loadApply(HttpServletRequest request, @Parameter(description = "页码，从 1 开始") Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setOrderBy("last_apply_time desc");
        applyQuery.setReceiveUserId(tokenUserInfoDto.getUserId());
        applyQuery.setPageNo(pageNo);
        applyQuery.setPageSize(PageSize.SIZE15.getSize());
        applyQuery.setQueryContactInfo(true);
        PaginationResultVO resultVO = userContactApplyService.findPageByParam(applyQuery);

        return getSuccessResponse(resultVO);
    }

    @Operation(summary = "处理联系人申请", description = "需登录；同意/拒绝/拉黑申请人；data 为 null")
    @RequestMapping("/dealWithApply")
    @GlobalInterceptor
    public ResponseVO dealWithApply(HttpServletRequest request,
                                    @Parameter(description = "申请记录ID") @NotNull Integer applyId,
                                    @Parameter(description = "处理状态：0:待处理 1:已同意 2:已拒绝 3:已拉黑") @NotNull Integer status) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userContactApplyService.dealWithApply(tokenUserInfoDto.getUserId(), applyId, status);
        return getSuccessResponse(null);
    }

    @Operation(summary = "加载联系人列表", description = "需登录；按类型查好友或群组；data 为 List<UserContact>")
    @RequestMapping("/loadContact")
    @GlobalInterceptor
    public ResponseVO loadContact(HttpServletRequest request, @Parameter(description = "联系人类型：USER-好友 GROUP-群组") @NotNull String contactType) {
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByName(contactType);
        if (null == contactTypeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContactQuery contactQuery = new UserContactQuery();
        contactQuery.setUserId(tokenUserInfoDto.getUserId());
        contactQuery.setContactType(contactTypeEnum.getType().byteValue());
        if (UserContactTypeEnum.USER == contactTypeEnum) {
            contactQuery.setQueryContactUserInfo(true);
        } else if (UserContactTypeEnum.GROUP == contactTypeEnum) {
            contactQuery.setQueryGroupInfo(true);
            contactQuery.setExcludeMyGroup(true);
        }
        contactQuery.setOrderBy("last_update_time desc");
        contactQuery.setStatusArray(new Integer[]{
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus()
        });
        List<UserContact> contactList = userContactService.findListByParam(contactQuery);

        return getSuccessResponse(contactList);
    }

    @Operation(summary = "获取联系人信息", description = "需登录；好友返回用户信息并带 contactStatus，群组返回群名称；data 为 UserInfoVO")
    @RequestMapping("/getContactInfo")
    @GlobalInterceptor
    public ResponseVO getContactInfo(HttpServletRequest request, @Parameter(description = "用户ID或群ID") @NotNull String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfoVO userInfoVO;
        if (contactId.startsWith(UserContactTypeEnum.USER.getPrefix())) {
            UserInfo userInfo = userInfoService.getUserInfoByUserId(contactId);
            userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
            UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), contactId);
            if (userContact == null) {
                userInfoVO.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());
            } else {
                userInfoVO.setContactStatus(userContact.getStatus().intValue());
            }
        } else {
            userInfoVO = new UserInfoVO();
            UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), contactId);
            GroupInfo groupInfo = groupInfoService.getGroupInfoByGroupId(contactId);
            if (userContact != null && groupInfo != null) {
                userInfoVO.setUserId(contactId);
                userInfoVO.setNickName(groupInfo.getGroupName());
                userInfoVO.setContactStatus(UserContactStatusEnum.FRIEND.getStatus());
            }
        }


        return getSuccessResponse(userInfoVO);
    }

    @Operation(summary = "获取联系人用户详情", description = "需登录；仅好友/已删除/被拉黑关系可查；data 为 UserInfoVO")
    @RequestMapping("/getContactUserInfo")
    @GlobalInterceptor
    public ResponseVO getContactUserInfo(HttpServletRequest request, @Parameter(description = "用户ID") @NotNull String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), contactId);
        if (null == userContact || !ArrayUtils.contains(new Integer[]{
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus()
            }, userContact.getStatus().intValue())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserInfo userInfo = userInfoService.getUserInfoByUserId(contactId);
        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
        return getSuccessResponse(userInfoVO);
    }

    @Operation(summary = "删除联系人", description = "需登录；data 为 null")
    @RequestMapping("/delContact")
    @GlobalInterceptor
    public ResponseVO delContact(HttpServletRequest request, @Parameter(description = "用户ID或群ID") @NotNull String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        userContactService.removeUserContact(tokenUserInfoDto.getUserId(), contactId, UserContactStatusEnum.DEL);
        return getSuccessResponse(null);
    }

    @Operation(summary = "将联系人移入黑名单", description = "需登录；data 为 null")
    @RequestMapping("/addContact2BlackList")
    @GlobalInterceptor
    public ResponseVO addContact2BlackList(HttpServletRequest request, @Parameter(description = "用户ID或群ID") @NotNull String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        userContactService.removeUserContact(tokenUserInfoDto.getUserId(), contactId, UserContactStatusEnum.BLACKLIST);
        return getSuccessResponse(null);
    }
}
