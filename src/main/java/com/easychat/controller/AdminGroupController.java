package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-群组管理", description = "群组查询与解散（仅管理员）")
@RestController("adminGroupController")
@RequestMapping("/admin")
public class AdminGroupController extends ABaseController {
    @Resource
    private GroupInfoService groupInfoService;

    @Operation(summary = "分页查询群组", description = "仅管理员；附带群主昵称与成员数；data 为 PaginationResultVO<GroupInfo>")
    @RequestMapping("/loadGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadGroup(GroupInfoQuery query) {
        query.setOrderBy("create_time desc");
        query.setQueryGroupOwnerName(true);
        query.setQueryMemberCount(true);
        PaginationResultVO resultVO = groupInfoService.findPageByParam(query);
        return getSuccessResponse(resultVO);
    }

    @Operation(summary = "解散群组", description = "仅管理员；data 为 null")
    @RequestMapping("/dissolutionGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO dissolutionGroup(@Parameter(description = "群ID") @NotEmpty String groupId) {
        GroupInfo groupInfo = groupInfoService.getGroupInfoByGroupId(groupId);
        if (null == groupInfo) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        groupInfoService.dissolutionGroup(groupInfo.getGroupOwnerId(), groupId);
        return getSuccessResponse(null);
    }
}
