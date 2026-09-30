package com.easychat.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "用户信息")
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = -720784055540385571L;

    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "性别 0:女 1:男")
    private Integer sex;
    @Schema(description = "加入类型 0:直接加入 1:同意后加入")
    private Integer joinType;
    @Schema(description = "个性签名")
    private String personalSignature;
    @Schema(description = "地区编号")
    private String areaCode;
    @Schema(description = "地区")
    private String areaName;
    @Schema(description = "登录令牌")
    private String token;
    @Schema(description = "是否管理员")
    private Boolean admin;
    @Schema(description = "联系人状态 0:非好友 1:好友 2:已删除好友 3:被好友删除 4:已拉黑好友 5:被好友拉黑")
    private Integer contactStatus;

    // getter和setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Integer getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(Integer contactStatus) {
        this.contactStatus = contactStatus;
    }
}
