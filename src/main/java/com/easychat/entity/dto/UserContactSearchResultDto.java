package com.easychat.entity.dto;

import com.easychat.enums.UserContactStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "联系人搜索结果")
public class UserContactSearchResultDto {
    @Schema(description = "联系人ID")
    private String contactId;
    @Schema(description = "联系人类型")
    private String contactType;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "状态名称")
    private String statusName;
    @Schema(description = "性别 0:女 1:男")
    private Integer sex;
    @Schema(description = "地区")
    private String areaName;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        UserContactStatusEnum statusEnum = UserContactStatusEnum.getByStatus(status);
        return statusName == null ? null : statusEnum.getDesc();
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
