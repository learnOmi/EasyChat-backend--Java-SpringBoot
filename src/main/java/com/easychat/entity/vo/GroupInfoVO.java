package com.easychat.entity.vo;

import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "群组信息")
public class GroupInfoVO {
    @Schema(description = "群组信息")
    private GroupInfo groupInfo;
    @Schema(description = "群成员列表")
    private List<UserContact> userContactList;

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<UserContact> getUserContactList() {
        return userContactList;
    }

    public void setUserContactList(List<UserContact> userContactList) {
        this.userContactList = userContactList;
    }
}
