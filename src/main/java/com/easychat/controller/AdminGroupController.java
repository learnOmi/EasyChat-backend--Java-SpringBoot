package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminGroupController")
@RequestMapping("/admin")
public class AdminGroupController extends ABaseController {
    @Resource
    private GroupInfoService groupInfoService;

    @RequestMapping("/loadGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadGroup(GroupInfoQuery query) {
        query.setOrderBy("create_time desc");
        query.setQueryGroupOwnerName(true);
        query.setQueryMemberCount(true);
        PaginationResultVO resultVO = groupInfoService.findPageByParam(query);
        return getSuccessResponse(resultVO);
    }

    @RequestMapping("/dissolutionGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO dissolutionGroup(@NotEmpty String groupId) {
        GroupInfo groupInfo = groupInfoService.getGroupInfoByGroupId(groupId);
        if (null == groupInfo) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        groupInfoService.dissolutionGroup(groupInfo.getGroupOwnerId(), groupId);
        return getSuccessResponse(null);
    }
}
