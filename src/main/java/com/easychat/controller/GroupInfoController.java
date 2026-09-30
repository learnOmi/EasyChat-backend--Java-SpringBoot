package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.GroupInfoVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.GroupStatusEnum;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.UserContactStatusEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import com.easychat.service.UserContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.catalina.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller
 * @author 'Tong'
 * @since 2025/10/17
 */
@Tag(name = "群组模块", description = "创建/修改群、我的群列表、群详情、成员管理、退群、解散群")
@RestController
@RequestMapping("/group")
@Validated
public class GroupInfoController extends ABaseController {
	@Resource
	private GroupInfoService groupInfoService;
    @Resource
    private UserContactService userContactService;

	@Operation(summary = "创建/修改群组", description = "需登录；groupId 为空时新建，否则修改；可上传群头像与封面；data 为 null")
	@RequestMapping("/saveGroup")
    @GlobalInterceptor
    public ResponseVO saveGroup(HttpServletRequest request,
                                @Parameter(description = "群ID，新增时为空") String groupId,
                                @Parameter(description = "群名称") @NotEmpty String groupName,
                                @Parameter(description = "群公告") String groupNotice,
                                @Parameter(description = "加入方式：0:直接加入 1:管理员同意后加入") @NotNull Integer joinType,
                                @Parameter(description = "群头像文件") MultipartFile avatarFile,
                                @Parameter(description = "群头像封面文件") MultipartFile avatarCover) throws IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupId(groupId);
        groupInfo.setGroupOwnerId(tokenUserInfoDto.getUserId());
        groupInfo.setGroupName(groupName);
        groupInfo.setGroupNotice(groupNotice);
        groupInfo.setJoinType(joinType.byteValue());
        this.groupInfoService.saveGroup(groupInfo, avatarFile, avatarCover);

        return getSuccessResponse(null);
    }

    @Operation(summary = "加载我创建的群组", description = "需登录；按创建时间倒序；data 为 List<GroupInfo>")
    @RequestMapping("/loadMyGroup")
    @GlobalInterceptor
    public ResponseVO loadMyGroup(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
        groupInfoQuery.setGroupOwnerId(tokenUserInfoDto.getUserId());
        groupInfoQuery.setOrderBy("create_time desc");
        List<GroupInfo> groupInfos = this.groupInfoService.findListByParam(groupInfoQuery);

        return getSuccessResponse(groupInfos);
    }

    @Operation(summary = "获取群详情", description = "需登录且群成员；附带成员数；data 为 GroupInfo")
    @RequestMapping("/getGroupInfo")
    @GlobalInterceptor
    public ResponseVO getGroupInfo(HttpServletRequest request, @Parameter(description = "群ID") @NotEmpty String groupId) {
        GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        Integer memberCount = this.userContactService.findCountByParam(userContactQuery);
        groupInfo.setMemberCount(memberCount);

        return getSuccessResponse(groupInfo);
    }

    private GroupInfo getGroupDetailCommon(HttpServletRequest request, String groupId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContact userContact = this.userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), groupId);
        if (null == userContact || !(UserContactStatusEnum.FRIEND.getStatus().byteValue() == userContact.getStatus())) {
            throw new BusinessException("你不在群聊或者群聊不存在");
        }

        GroupInfo groupInfo = this.groupInfoService.getGroupInfoByGroupId(groupId);
        if (null == groupInfo || !(GroupStatusEnum.NORMAL.getStatus().byteValue() == groupInfo.getStatus())) {
            throw new BusinessException("群聊不存在或已解散");
        }
        return groupInfo;
    }

    @Operation(summary = "获取聊天所需群信息", description = "需登录且群成员；返回群信息与成员联系人列表；data 为 GroupInfoVO")
    @RequestMapping("/getGroupInfo4Chat")
    @GlobalInterceptor
    public ResponseVO getGroupInfo4Chat(HttpServletRequest request, @Parameter(description = "群ID") @NotEmpty String groupId) {
        GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        userContactQuery.setQueryUserInfo(true);
        userContactQuery.setOrderBy("create_time asc");
        List<UserContact> userContacts = this.userContactService.findListByParam(userContactQuery);
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        groupInfoVO.setGroupInfo(groupInfo);
        groupInfoVO.setUserContactList(userContacts);

        return getSuccessResponse(groupInfoVO);
    }

    @Operation(summary = "群成员批量添加/移除", description = "需登录且为群主；data 为 null")
    @RequestMapping("/addOrRemoveGroupUser")
    @GlobalInterceptor
    public ResponseVO addOrRemoveGroupUser(HttpServletRequest request,
                                           @Parameter(description = "群ID") @NotEmpty String groupId,
                                           @Parameter(description = "选中联系人ID，多个以逗号分隔") @NotEmpty String selectContacts,
                                           @Parameter(description = "操作类型：0:添加 1:移除") @NotNull Integer opType) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.groupInfoService.addOrRemoveGroupUser(tokenUserInfoDto, groupId, selectContacts, opType);
        return getSuccessResponse(null);
    }

    @Operation(summary = "退出群聊", description = "需登录；data 为 null")
    @RequestMapping("/leaveGroup")
    @GlobalInterceptor
    public ResponseVO leaveGroup(HttpServletRequest request, @Parameter(description = "群ID") @NotEmpty String groupId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        groupInfoService.leaveGroup(tokenUserInfoDto.getUserId(), groupId, MessageTypeEnum.LEAVE_GROUP);
        return getSuccessResponse(null);
    }

    @Operation(summary = "解散群聊", description = "需登录且为群主；data 为 null")
    @RequestMapping("/dissolutionGroup")
    @GlobalInterceptor
    public ResponseVO dissolutionGroup(HttpServletRequest request, @Parameter(description = "群ID") @NotEmpty String groupId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        groupInfoService.dissolutionGroup(tokenUserInfoDto.getUserId(), groupId);
        return getSuccessResponse(null);
    }
}